package tio

import chisel3._
import chisel3.util._
import chisel3.experimental.{IntParam, BaseModule}
import freechips.rocketchip.amba.axi4._
import freechips.rocketchip.subsystem.BaseSubsystem
import org.chipsalliance.cde.config.{Parameters, Field, Config}
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.regmapper.{HasRegMap, RegField}
import freechips.rocketchip.tilelink._
import freechips.rocketchip.util.UIntIsOneOf

case class TioParams(
  address: BigInt = 0x4000,
  width: Int = 32)

case object TioKey extends Field[Option[TioParams]](None)

class TioIO(val w: Int) extends Bundle {
  val clock = Input(Clock())
  val reset = Input(Bool())
  val input_ready = Output(Bool())
  val input_valid = Input(Bool())
  val x = Input(UInt(w.W))
  val y = Input(UInt(w.W))
  val output_ready = Input(Bool())
  val output_valid = Output(Bool())
  val tio = Output(UInt(w.W))
  val busy = Output(Bool())
}

trait TioTopIO extends Bundle {
  val tio_busy = Output(Bool())
}

trait HasTioIO extends BaseModule {
  val w: Int
  val io = IO(new TioIO(w))
}

class TioMMIOChiselModule(val w: Int) extends Module
  with HasTioIO
{
  val s_idle :: s_run :: s_done :: Nil = Enum(3)

  val state = RegInit(s_idle)
  val tmp   = Reg(UInt(w.W))
  val tio   = Reg(UInt(w.W))

  io.input_ready := state === s_idle
  io.output_valid := state === s_done
  io.tio := tio

  when (state === s_idle && io.input_valid) {
    state := s_run
  } .elsewhen (state === s_run && tmp === 0.U) {
    state := s_done
  } .elsewhen (state === s_done && io.output_ready) {
    state := s_idle
  }

  when (state === s_idle && io.input_valid) {
    tio := io.x
    tmp := io.y
  } .elsewhen (state === s_run) {
    when (tio > tmp) {
      tio := tio - tmp
    } .otherwise {
      tmp := tmp - tio 
    }
  }

  io.busy := state =/= s_idle
}

trait TioModule extends HasRegMap {
  val io: TioTopIO

  implicit val p: Parameters
  def params: TioParams
  val clock: Clock
  val reset: Reset

  // How many clock cycles in a PWM cycle?
  val x = Reg(UInt(params.width.W))
  val y = Wire(new DecoupledIO(UInt(params.width.W)))
  val tio = Wire(new DecoupledIO(UInt(params.width.W)))
  val status = Wire(UInt(2.W))

  val impl = Module(new TioMMIOChiselModule(params.width))

  impl.io.clock := clock
  impl.io.reset := reset.asBool

  impl.io.x := x
  impl.io.y := y.bits
  impl.io.input_valid := y.valid
  y.ready := impl.io.input_ready

  tio.bits := impl.io.tio
  tio.valid := impl.io.output_valid
  impl.io.output_ready := tio.ready

  status := Cat(impl.io.input_ready, impl.io.output_valid)
  io.tio_busy := impl.io.busy

  regmap(
    0x00 -> Seq(
      RegField.r(2, status)), // a read-only register capturing current status
    0x04 -> Seq(
      RegField.w(params.width, x)), // a plain, write-only register
    0x08 -> Seq(
      RegField.w(params.width, y)), // write-only, y.valid is set on write
    0x0C -> Seq(
      RegField.r(params.width, tio))) // read-only, tio.ready is set on read
}
// DOC include end: Tio instance regmap

// DOC include start: Tio router
class TioTL(params: TioParams, beatBytes: Int)(implicit p: Parameters)
  extends TLRegisterRouter(
    params.address, "tio", Seq("ucbbar,tio"),
    beatBytes = beatBytes)(
      new TLRegBundle(params, _) with TioTopIO)(
      new TLRegModule(params, _, _) with TioModule)

trait CanHavePeripheryTio { this: BaseSubsystem =>
  private val portName = "tio"

  val tio_busy = p(TioKey) match {
    case Some(params) => {
      val tio = {
        val tio = pbus { LazyModule(new TioTL(params, pbus.beatBytes)(p)) }
        pbus.coupleTo(portName) { tio.node := TLFragmenter(pbus.beatBytes, pbus.blockBytes) := _ }
        tio 
      }
      val pbus_io = pbus { InModuleBody {
        val busy = IO(Output(Bool()))
        busy := tio.module.io.tio_busy
        busy
      }}
      val tio_busy = InModuleBody {
        val busy = IO(Output(Bool())).suggestName("tio_busy")
        busy := pbus_io
        busy
      }
      Some(tio_busy)
    }
    case None => None
  }
}

class WithTio() extends Config((site, here, up) => {
  case TioKey => Some(TioParams())
})
