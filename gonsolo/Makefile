FREQUENCY = 50
STRATEGY = TIMING
#RISCV = /home/gonsolo/work/chipyard/.conda-env/riscv-tools
RISCV =
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

DRIVER =$(FIRESIM)/sim/output/$(PLATFORM)/$(QUINTUPLET)/FireSim-rhsresearch_nitefury_ii

all: driver

driver: $(DRIVER)
$(DRIVER):
	$(MAKE) -j $(shell nproc) -C ../sims/firesim/sim RISCV=$(RISCV) FIRESIM_ENV_SOURCED=$(FIRESIM_ENV_SOURCED) PLATFORM=$(PLATFORM) TARGET_PROJECT=$(TARGET_PROJECT) DESIGN=$(DESIGN) TARGET_CONFIG=$(TARGET_CONFIG) PLATFORM_CONFIG=$(PLATFORM_CONFIG) replace-rtl

# Vivado MCS file: This is the bitstream the FPGA is programmed with
out.mcs: $(DRIVER)
	vivado -mode batch -source top.tcl -tclargs $(FREQUENCY) $(STRATEGY)

clean:
	rm -rf project project.cache *.log *.jou $(SV) $(DRIVER)

.PHONY: all clean driver mcs sv

