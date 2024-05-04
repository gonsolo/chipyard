FREQUENCY = 50
STRATEGY = TIMING
RISCV = /home/gonsolo/work/chipyard/.conda-env/riscv-tools
FIRESIM_ENV_SOURCED = 1
FIRESIM = ../sims/firesim
PLATFORM = rhsresearch_nitefury_ii
TARGET_PROJECT = firesim
DESIGN = FireSim
TARGET_CONFIG = FireSimRocket1GiBDRAMConfig
PLATFORM_CONFIG = BaseNitefuryConfig
QUINTUPLET = $(PLATFORM)-$(TARGET_PROJECT)-$(DESIGN)-$(TARGET_CONFIG)-$(PLATFORM_CONFIG)
SV = $(FIRESIM)/sim/generated-src/$(PLATFORM)/$(QUINTUPLET)/$(DESIGN)-generated.sv
MCS = $(FIRESIM)/platforms/$(PLATFORM)/NiteFury-and-LiteFury-firesim/Sample-Projects/Project-0/cl_$(QUINTUPLET)/Nitefury-II/mcs/out.mcs

all: sv
sv: $(SV)
$(SV) $(MCS):
	$(MAKE) -C ../sims/firesim/sim RISCV=$(RISCV) FIRESIM_ENV_SOURCED=$(FIRESIM_ENV_SOURCED) PLATFORM=$(PLATFORM) TARGET_PROJECT=$(TARGET_PROJECT) DESIGN=$(DESIGN) TARGET_CONFIG=$(TARGET_CONFIG) PLATFORM_CONFIG=$(PLATFORM_CONFIG) replace-rtl

clean:
	rm -rf project project.cache *.log *.jou $(MCS) $(SV)

.PHONY: all clean sv

# How to make a mcs (buried deep in the make command above):
# vivado -mode batch -source top.tcl -tclargs $(FREQUENCY) $(STRATEGY)
