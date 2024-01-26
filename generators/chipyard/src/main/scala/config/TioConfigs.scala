package chipyard

import org.chipsalliance.cde.config.{Config}
import freechips.rocketchip.diplomacy.{AsynchronousCrossing}

// ------------------------------
// Config for Tio accelerator
// ------------------------------

class TioConfig extends Config(
  new tio.WithTio ++
  new freechips.rocketchip.subsystem.WithNBigCores(1) ++
  new chipyard.config.AbstractConfig)

//class GCDTLRocketConfig extends Config(
//  new chipyard.example.WithGCD(useAXI4=false, useBlackBox=false) ++          // Use GCD Chisel, connect Tilelink
//  new freechips.rocketchip.subsystem.WithNBigCores(1) ++
//  new chipyard.config.AbstractConfig)

//class InitZeroRocketConfig extends Config(
//  new chipyard.example.WithInitZero(0x88000000L, 0x1000L) ++   // add InitZero
//  new freechips.rocketchip.subsystem.WithNBigCores(1) ++
//  new chipyard.config.AbstractConfig)


