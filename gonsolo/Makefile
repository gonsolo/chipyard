FREQUENCY = 50
STRATEGY = TIMING
RISCV=/home/gonsolo/work/chipyard/.conda-env/riscv-tools 
FIRESIM_ENV_SOURCED=1 
SV = ../sims/firesim/sim/generated-src/rhsresearch_nitefury_ii/rhsresearch_nitefury_ii-firesim-FireSim-FireSimRocket1GiBDRAMConfig-BaseNitefuryConfig/FireSim-generated.sv

all: out.mcs
out.mcs: $(SV)
	vivado -mode batch -source top.tcl -tclargs $(FREQUENCY) $(STRATEGY)

$(SV):
	$(MAKE) -C ../sims/firesim/sim RISCV=$(RISCV) FIRESIM_ENV_SOURCED=$(FIRESIM_ENV_SOURCED) PLATFORM=rhsresearch_nitefury_ii TARGET_PROJECT=firesim DESIGN=FireSim TARGET_CONFIG=FireSimRocket1GiBDRAMConfig PLATFORM_CONFIG=BaseNitefuryConfig replace-rtl

clean:
	rm -rf project project.cache *.log *.jou out.mcs out.bin out.prm $(SV)

.PHONY:  clean
