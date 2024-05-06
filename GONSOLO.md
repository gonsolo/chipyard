# BORG Graphics -- Build your own graphics hardware

## Makefile-based workflow for Arch Linux

### Prerequisites

- [Nitefury II](https://github.com/RHSResearchLLC/NiteFury-and-LiteFury?tab=readme-ov-file), a $150 FPGA. (Running on [verilator](https://www.veripool.org/verilator) works too but running Linux on it is much too slow).
- [Java](https://archlinux.org/packages/extra/x86_64/jre-openjdk-headless).
- [Spike](https://aur.archlinux.org/packages/spike-git).
- Boot with `pci=nomsi` for XDMA to work.
- Vivado from [AMD](https://www.xilinx.com/support/download.html) or [Arch](https://aur.archlinux.org/packages/vivado-lab-edition) for building the bitstream and flashing the Nitefury II.

### Steps

1. ``` git clone -b gonsolo git@github.com:gonsolo/chipyard.git```
2. ```git submodule update --init generators/bar-fetchers generators/boom generators/caliptra-aes-acc generators/constellation generators/diplomacy generators/fft-generator generators/hardfloat generators/hwacha generators/ibex generators/icenet generators/mempress generators/rocc-acc-utils generators/rocket-chip generators/rocket-chip-blocks generators/rocket-chip-inclusive-cache generators/shuttle generators/riscv-sodor generators/testchipip sims/firesim tools/cde tools/dsptools tools/fixedpoint tools/rocket-dsp-utils```
3. ```cd sims/firesim && git submodule update --init platforms/rhsresearch_nitefury_ii/NiteFury-and-LiteFury-firesim```
4. ```cd gonsolo; make```

This results in a driver in `sims/firesim/platforms/rhsresearch_nitefury_ii/NiteFury-and-LiteFury-firesim/Sample-Projects/Project-0/cl_rhsresearch_nitefury_ii-firesim-FireSim-FireSimRocket1GiBDRAMConfig-BaseNitefuryConfig/driver/FireSim-rhsresearch_nitefury_ii` and a FPGA bitstream `out.mcs`.

### TODO

1. TODO: Program FPGA with mcs.
2. TODO: Generate RISC-V Linux and filesystem image to run driver.
3. TODO: Generate workload.

## Custom forks

- gonsolo/chipyard forked from ucb-bar/chipyard
- gonsolo/testchipip forked from ucb/bar/testchipip
- gonsolo/firesim forked from firesim/firesim
- gonsolo/NiteFury-and-LiteFury-firesim forked from firesim/NiteFury-and-LiteFury-firesim
- gonsolo/dma_ip_drivers forked from Xilinx/dma_ip_drivers

## Overview
The following notes are for running a full system simulation of a RISC-V system on the latest Arch Linux distribution.

A few things are needed to successfully run a simulation on a [Nitefury II](https://www.amazon.com/dp/B0B9FMBF6C):

1. A bitstream that is flashed onto Nitefury: ```firesim.mcs``` or ```firesim.bit```.

   What ```firesim buildbitstream``` does:
   - replace_rtl
     - In sims/firesim/sim: ```make PLATFORM=rhsresearch_nitefury_ii TARGET_PROJECT=firesim DESIGN=FireSim TARGET_CONFIG=FireSimRocket1GiBDRAMConfig PLATFORM_CONFIG=BaseNitefuryConfig replace-rtl```
   - build_driver
     - In sims/firesim/sim: ```make PLATFORM=rhsresearch_nitefury_ii TARGET_PROJECT=firesim DESIGN=FireSim TARGET_CONFIG=FireSimRocket1GiBDRAMConfig PLATFORM_CONFIG=BaseNitefuryConfig driver```
   - ```build-bitstream.sh --cl_dir bla --frequency 50 --strategy TIMING --board rhsresearch_nitefury_ii```

   Made from ```project.tcl```. TODO.

2. A working XDMA (and XVSEC) driver.

   The simulator from 3. talks to the FPGA via PCI Express with this driver.
   
   It is made from my [repository](https://github.com/gonsolo/dma_ip_drivers/tree/gonsolo) which updated AMD's XDMA
   drivers for kernel 6.8.7.

   - The bitstream from 1. has to be flashed onto the Nitefury II. Otherwise the xdma module will fail to load.
   - The kernel has to be booted with kernel parameter ```pci=nomsi'''. Otherwise the xdma module will fail to load.
   - In branch ```gonsolo```, cd to ```xdma/xdma``` and type ```sudo make -j 10 install```. You should end up
     with a ```/lib/modules/.../xdma/xdma.ko```.
   - ```sudo insmod /lib/modules/6.8.7-arch1-1/xdma/xdma.ko poll_mode=1 interrupt_mode=2```. Check lsmod and dmesg
     whether probing failed. It *must* not!
   - Cd to ```xvsec``` and type ```sudo make install```. ```sudo modprobe xvsec```. Check dmesg and lsmod.
     Not sure whether xvsec is actually needed.

4. The simulator: FireSim-rhsresearch_nitefury_ii.

   Resides in ```sims/firesim/sim```. TODO.

   The executable takes the linux kernel and the filesystem image, opens a few bridges via PCI Express to the Nitefury
   and boot the system.

   It is also built from ```firesim buildbitstream``` in 1.

6. A working RISC-V Linux environment:

   Resides in ```software/firemarshal```. TODO.

   * Kernel: linux-uniform0-br-base-bin
   * Filesystem: linux-uniform0-br-base.img

## Old random notes

> Serial: Digilent/210249BAC8DB
> BDF: 08:00.0
> Fingerprint: 0x46697265

## Check fingerprint
```bash
./FireSim-rhsresearch_nitefury_ii +permissive +bus=08:00.0 +check-fingerprint +permissive-off +prog0=none
```

## Write fingerprint
1181315685 is 0x46697265
```bash
./FireSim-rhsresearch_nitefury_ii +permissive +bus=08:00.0 +write-fingerprint=1181315685 +permissive-off +prog0=none
```

## /opt/firesime-db.json
```json
[ { "uid": "Digilent/210249BAC8DB", "device": "xc7a200t_0", "bdf": "08:00.0" } ]
```

## Run simulation manually
```bash
gdb -x gonsolo ./FireSim-rhsresearch_nitfury_ii
```

gonsolo:
```bash
break main
run +permissive   +macaddr0=00:12:6D:00:00:02 +blkdev0=linux-uniform0-br-base.img +niclog0=niclog0 +blkdev-log0=blkdev-log0  +trace-select=1 +trace-start=0 +trace-end=-1 +trace-output-format=0 +dwarf-file-name=linux-uniform0-br-base-bin-dwarf +autocounter-readrate=0 +autocounter-filename-base=AUTOCOUNTERFILE  +print-start=0 +print-end=-1 +linklatency0=6405 +netbw0=200 +shmemportname0=default  +domain=0x0000 +bus=0x08 +device=0x00 +function=0x0 +bar=0x0 +pci-vendor=0x10ee +pci-device=0x903f +permissive-off +prog0=linux-uniform0-br-base-bin
```

## If lspci shows memory disabled:
```bash
setpci -s 08:00.0 COMMAND=0x2
```

Enable MSI (not needed) ```setpci -s 08:00.0 4a.w=1```

## If XDMA config bar fails
### Boot with MSI disabled by setting in grub
```pci=nomsi```

### Build custom bitstream with memory set to 64 bit and prefetchable
/home/gonsolo/work/chipyard/sims/firesim/platforms/rhsresearch_nitefury_ii/NiteFury-and-LiteFury-firesim/Sample-Projects/Project-0/cl_firesim/Nitefury-II/project/project.tcl:
```tcl
CONFIG.axil_master_64bit_en {true}
CONFIG.axil_master_prefetchable {true}
```
