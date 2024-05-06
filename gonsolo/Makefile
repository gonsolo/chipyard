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
	$(MAKE) -j $(shell nproc) -C ../sims/firesim/sim RISCV=$(RISCV) FIRESIM_ENV_SOURCED=$(FIRESIM_ENV_SOURCED) PLATFORM=$(PLATFORM) TARGET_PROJECT=$(TARGET_PROJECT) DESIGN=$(DESIGN) TARGET_CONFIG=$(TARGET_CONFIG) PLATFORM_CONFIG=$(PLATFORM_CONFIG) replace-rtl

# Vivado MCS file: This is the bitstream the FPGA is programmed with
out.mcs: $(DRIVER)
	vivado -mode batch -source top.tcl -tclargs $(FREQUENCY) $(STRATEGY)

# The permissions have to be set for non-root users: sudo chmod a+rw /dev/xdma0_*
check_fingerprint:
	$(DRIVER) +permissive +bus=08:00.0 +check-fingerprint +permissive-off +prog0=none

c: clean
clean:
	rm -rf project project.cache *.log *.jou $(SV) $(DRIVER) check_fingerprint

.PHONY: all c clean driver

