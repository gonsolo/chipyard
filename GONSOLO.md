# Notes about working with Chipyard

## Overview
Three things are needed to successfully run a simulation on a Nitefury II:
1. A bitstream that is flashed onto Nitefury: firesim.mcs or firesim.bit
2. The simulator: FireSim-rhsresearch_nitefury_ii
3. A working Linux environment: linux-uniform0-br-base[-bin|.img]
TODO: How to make them.

## Conda
```bash
eval "$(/home/gonsolo/miniforge3/bin/conda shell.bash hook)"
conda init
conda init --reverse $SHELL
conda deactivate
```
Sometimes conda is commented out in .bashrc

## If build-setup.sh step 9 fails:
```bash
sudo chmod ga+r /boo/vmlinuz-6.5.0-17-generic
```

## Environment
```bash
source ./env.sh
cd sims/firesim
source ./sourceme-manager.sh
```

## Manual setup for firesim enumeratefpgas
```bash
cd ~/.ssh; ssh-agent -s > AGENT_VARS; source AGENT_VARS; ssh-add firesim.pem
source ./envsh; cd sims/firesim; source sourcme-manager.sh
insmod xdma; modprobe xvsec
sudo chmod 777 /sys/bus/pci/rescan
chmod 666 /dev/xdma0_user
chmod 666 /dev/xdma0_h2c_0 
chmod 666 /dev/xdma0_c2h_0 
```

## Once: Generate database manually as Nitefury doesn't survive programming without reboot
```bash
sudo /home/gonsolo/FIRESIM_RUNS_DIR/enumerate_fpgas_staging/scripts/generate-fpga-db.py --bitstream ../rhsresearch_nitefury_ii/firesim.bit --driver ../FireSim-rhsresearch_nitefury_ii --out-db-json /opt/firesim-db.json --vivado-bin /tools/Xilinx/Vivado_Lab/2023.2/bin/vivado_lab --hw-server-bin /tools/Xilinx/Vivado_Lab/2023.2/bin/hw_server
```

Values from above when run manually:

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

## firesim infraetup: Comment flash_fpga atline 875:
/home/gonsolo/work/chipyard/sims/firesim/deploy/runtools/run_farm_deploy_managers.py:    def infrasetup_instance(self, uridir: str) -> None:

## XMDA test with Nitefury flashed with RHSResearch Project-0
```bash
sudo ./dma_to_device -d /dev/xdma0_h2c_0 -f ../tests/data/datafile0_4K.bin -s 1024 -a 0 -c 1
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
