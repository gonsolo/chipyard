FREQUENCY = 50
STRATEGY = TIMING
RISCV = # Not needed, use spike-git package
FIRESIM_ENV_SOURCED = 1
FIRESIM = ../sims/firesim
PLATFORM = rhsresearch_nitefury_ii
TARGET_PROJECT = firesim
DESIGN = FireSim
#TARGET_CONFIG = FireSimRocket1GiBDRAMConfig
TARGET_CONFIG = BorgConfig
PLATFORM_CONFIG = BaseNitefuryConfig
QUINTUPLET = $(PLATFORM)-$(TARGET_PROJECT)-$(DESIGN)-$(TARGET_CONFIG)-$(PLATFORM_CONFIG)

# This is the SOC as System Verilog in one file
SV = $(FIRESIM)/sim/generated-src/$(PLATFORM)/$(QUINTUPLET)/$(DESIGN)-generated.sv

# The driver is a x86-64 binary that loads the linux kernel and a base image and runs the FPGA via PCI-Express.
DRIVER =$(FIRESIM)/sim/output/$(PLATFORM)/$(QUINTUPLET)/FireSim-rhsresearch_nitefury_ii

# Vivado MCS file: This is the bitstream the FPGA is programmed with
MCS = out.mcs

# The device tree
DTS = $(FIRESIM)/sim/generated-src/$(PLATFORM)/$(QUINTUPLET)/firesim.firesim.FireSim.BorgConfig.dts
edit_dts:
	vi $(DTS)
ls_dts:
	ls -lh $(DTS)

all: out.mcs $(DRIVER)

driver: $(DRIVER)
$(SV) $(DRIVER):
	$(MAKE) -j $(shell nproc) -C $(FIRESIM)/sim RISCV=$(RISCV) FIRESIM_ENV_SOURCED=$(FIRESIM_ENV_SOURCED) PLATFORM=$(PLATFORM) TARGET_PROJECT=$(TARGET_PROJECT) DESIGN=$(DESIGN) TARGET_CONFIG=$(TARGET_CONFIG) PLATFORM_CONFIG=$(PLATFORM_CONFIG) replace-rtl

mcs: $(MCS)
$(MCS): $(DRIVER)
	ln -sf $(FIRESIM)/platforms/$(PLATFORM)/NiteFury-and-LiteFury-firesim/Sample-Projects/Project-0/cl_$(QUINTUPLET)/Nitefury-II/project/project.srcs .
	cp $(FIRESIM)/platforms/$(PLATFORM)/NiteFury-and-LiteFury-firesim/Sample-Projects/Project-0/cl_firesim/common/HDL/CodeBlinker.v project.srcs/sources_1/imports/HDL/
	cp ../sims/firesim/platforms/rhsresearch_nitefury_ii/NiteFury-and-LiteFury-firesim/Sample-Projects/Project-0/cl_firesim/Nitefury-II/project/project.srcs/sources_1/imports/HDL/firesim_wrapper.v project.srcs/sources_1/imports/HDL/
	cp ../sims/firesim/platforms/rhsresearch_nitefury_ii/NiteFury-and-LiteFury-firesim/Sample-Projects/Project-0/cl_firesim/Nitefury-II/project/project.srcs/sources_1/imports/HDL/dna_reader.v project.srcs/sources_1/imports/HDL/
	cp ../sims/firesim/platforms/rhsresearch_nitefury_ii/NiteFury-and-LiteFury-firesim/Sample-Projects/Project-0/cl_firesim/Nitefury-II/project/project.srcs/sources_1/imports/HDL/user_efuse.v project.srcs/sources_1/imports/HDL/

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

xdma:
	sudo rmmod xdma
	sudo modprobe xdma poll_mode=1 interrupt_mode=2
	sudo chmod a+rw /dev/xdma0_*

DRIVER_FLAGS = +permissive +macaddr0=00:12:6D:00:00:02 +niclog0=niclog0 +blkdev-log0=blkdev-log0 +trace-select=1 +trace-start=0 +trace-end=-1 +trace-output-format=0 +dwarf-file-name=$(BIN_DWARF) +autocounter-readrate=0 +autocounter-filename-base=AUTOCOUNTERFILE +print-start=0 +print-end=-1 +linklatency0=6405 +netbw0=200 +shmemportname0=default +domain=0x0000 +bus=0x08 +device=0x00 +function=0x0 +bar=0x0 +pci-vendor=0x10ee +pci-device=0x903f +permissive-off +prog0=$(BASE_BIN)

run_simulation:
	$(DRIVER) $(DRIVER_FLAGS) +blkdev0=$(BASE_IMG)

WORKLOADS = $(FIRESIM)/deploy/workloads

POWEROFF_IMG = $(WORKLOADS)/linux-poweroff/poweroffnode.ext2
make_poweroff:
	$(MAKE) -C $(WORKLOADS) linux-poweroff
run_poweroff: make_poweroff
	$(DRIVER) $(DRIVER_FLAGS) +blkdev0=$(POWEROFF_IMG)

BORG_IMG = $(WORKLOADS)/borg/borgnode.ext2
make_borg:
	$(MAKE) -C $(WORKLOADS) borg
run_borg: make_borg
	$(DRIVER) $(DRIVER_FLAGS) +blkdev0=$(BORG_IMG)

BORG_TIO_IMG = $(WORKLOADS)/borg-tio/borgtionode.ext2
make_borg_tio:
	$(MAKE) -C $(WORKLOADS) borg-tio
run_borg_tio: make_borg_tio
	$(DRIVER) $(DRIVER_FLAGS) +blkdev0=$(BORG_TIO_IMG)

c: clean
clean:
	rm -rf project project.cache *.log *.jou $(SV) $(DRIVER) check_fingerprint $(MCS) project.srcs/sources_1/imports/HDL/* $(FIRESIM)/sim/generated-src

.PHONY: all c clean clean_distro_kernel distro driver edit_dts make_borg make_borg_tio make_poweroff mcs run_poweroff run_simulation xdma

