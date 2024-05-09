FREQUENCY = 50
STRATEGY = TIMING
RISCV = # Not needed, use spike-git package
FIRESIM_ENV_SOURCED = 1
FIRESIM = ../sims/firesim
PLATFORM = rhsresearch_nitefury_ii
TARGET_PROJECT = firesim
DESIGN = FireSim
TARGET_CONFIG = FireSimRocket1GiBDRAMConfig
PLATFORM_CONFIG = BaseNitefuryConfig
QUINTUPLET = $(PLATFORM)-$(TARGET_PROJECT)-$(DESIGN)-$(TARGET_CONFIG)-$(PLATFORM_CONFIG)

# This is the SOC as System Verilog in one file
SV = $(FIRESIM)/sim/generated-src/$(PLATFORM)/$(QUINTUPLET)/$(DESIGN)-generated.sv

# The driver is a x86-64 binary that loads the linux kernel and a base image and runs the FPGA via PCI-Express.
DRIVER =$(FIRESIM)/sim/output/$(PLATFORM)/$(QUINTUPLET)/FireSim-rhsresearch_nitefury_ii

#all: out.mcs $(DRIVER)
all: $(DRIVER)

driver: $(DRIVER)
$(DRIVER) $(SV):
	$(MAKE) -j $(shell nproc) -C $(FIRESIM)/sim RISCV=$(RISCV) FIRESIM_ENV_SOURCED=$(FIRESIM_ENV_SOURCED) PLATFORM=$(PLATFORM) TARGET_PROJECT=$(TARGET_PROJECT) DESIGN=$(DESIGN) TARGET_CONFIG=$(TARGET_CONFIG) PLATFORM_CONFIG=$(PLATFORM_CONFIG) replace-rtl

# Vivado MCS file: This is the bitstream the FPGA is programmed with
out.mcs: $(DRIVER)
	vivado -mode batch -source top.tcl -tclargs $(FREQUENCY) $(STRATEGY)

# The permissions have to be set for non-root users: sudo chmod a+rw /dev/xdma0_*
check_fingerprint:
	$(DRIVER) +permissive +bus=08:00.0 +check-fingerprint +permissive-off +prog0=none

FIREMARSHAL = ../software/firemarshal
FIRECHIP = $(FIREMARSHAL)/images/firechip
BR_BASE = $(FIRECHIP)/br-base
BASE_IMG = $(BR_BASE)/br-base.img
BIN_DWARF = $(BR_BASE)/br-base-bin-dwarf
BASE_BIN = $(BR_BASE)/br-base-bin


distro: $(BASE_BIN) $(BASE_IMG)
$(BASE_BIN) $(BASE_IMG):
	cd $(FIREMARSHAL); ./marshal -v build br-base.json
clean_distro_kernel:
	rm -f $(BASE_BIN)

run_simulation:
	$(DRIVER) +permissive   +macaddr0=00:12:6D:00:00:02 +blkdev0=$(BASE_IMG) +niclog0=niclog0 +blkdev-log0=blkdev-log0  +trace-select=1 +trace-start=0 +trace-end=-1 +trace-output-format=0 +dwarf-file-name=$(BIN_DWARF) +autocounter-readrate=0 +autocounter-filename-base=AUTOCOUNTERFILE  +print-start=0 +print-end=-1 +linklatency0=6405 +netbw0=200 +shmemportname0=default  +domain=0x0000 +bus=0x08 +device=0x00 +function=0x0 +bar=0x0 +pci-vendor=0x10ee +pci-device=0x903f +permissive-off +prog0=$(BASE_BIN)

WORKLOADS = $(FIRESIM)/deploy/workloads

POWEROFF_IMG = $(WORKLOADS)/linux-poweroff/poweroffnode.ext2
make_poweroff:
	$(MAKE) -C $(WORKLOADS) linux-poweroff
run_poweroff: make_poweroff
	$(DRIVER) +permissive   +macaddr0=00:12:6D:00:00:02 +blkdev0=$(POWEROFF_IMG) +niclog0=niclog0 +blkdev-log0=blkdev-log0  +trace-select=1 +trace-start=0 +trace-end=-1 +trace-output-format=0 +dwarf-file-name=$(BIN_DWARF) +autocounter-readrate=0 +autocounter-filename-base=AUTOCOUNTERFILE  +print-start=0 +print-end=-1 +linklatency0=6405 +netbw0=200 +shmemportname0=default  +domain=0x0000 +bus=0x08 +device=0x00 +function=0x0 +bar=0x0 +pci-vendor=0x10ee +pci-device=0x903f +permissive-off +prog0=$(BASE_BIN)

BORG_IMG = $(WORKLOADS)/borg/borgnode.ext2
make_borg:
	$(MAKE) -C $(WORKLOADS) borg
run_borg: make_borg
	$(DRIVER) +permissive   +macaddr0=00:12:6D:00:00:02 +blkdev0=$(BORG_IMG) +niclog0=niclog0 +blkdev-log0=blkdev-log0  +trace-select=1 +trace-start=0 +trace-end=-1 +trace-output-format=0 +dwarf-file-name=$(BIN_DWARF) +autocounter-readrate=0 +autocounter-filename-base=AUTOCOUNTERFILE  +print-start=0 +print-end=-1 +linklatency0=6405 +netbw0=200 +shmemportname0=default  +domain=0x0000 +bus=0x08 +device=0x00 +function=0x0 +bar=0x0 +pci-vendor=0x10ee +pci-device=0x903f +permissive-off +prog0=$(BASE_BIN)

c: clean
clean:
	rm -rf project project.cache *.log *.jou $(SV) $(DRIVER) check_fingerprint

.PHONY: all c clean clean_distro_kernel distro driver make_borg make_poweroff run_poweroff run_simulation

