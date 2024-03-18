#include <stdio.h>
#include "mmio.h"

enum {
	TIO_STATUS	= 0x4000,
	TIO_X		= 0x4004,
	TIO_Y		= 0x4008,
	TIO_TIO		= 0x400C,
	TIO_DMA_STATE	= 0x4010,
	TIO_BLA_STATE	= 0x4020
};

unsigned int tio_ref(unsigned int x, unsigned int y) {
	while (y != 0) {
		if (x > y)
			x = x - y;
		else
			y = y - x;
	}
	return x;
}

void check_address(uint32_t address, uint32_t value) {
	uint32_t dma_value = reg_read32(address);
	if (dma_value == value) {
		printf("%x Received correct DMA value: %u.\n", address, dma_value);
	} else {
		printf("%x Received wrong DMA value: %u.\n", address, dma_value);
	}
}

uint8_t get_dma_state() {
	//return reg_read8(TIO_DMA_STATE) & 0b111;
	return reg_read8(TIO_DMA_STATE);
}

uint8_t get_bla_state() {
	return reg_read8(TIO_BLA_STATE);
}

void set_bla_state(uint8_t value) {
	reg_write8(TIO_BLA_STATE, value);
}

int main(void) {
	//uint32_t result, ref, x = 20, y = 15;

	// wait for peripheral to be ready
	//while ((reg_read8(TIO_STATUS) & 0x2) == 0) /* nix */ ;

	//reg_write32(TIO_X, x);
	//reg_write32(TIO_Y, y);

	// wait for peripheral to complete
	//while ((reg_read8(TIO_STATUS) & 0x1) == 0) /* nix */ ;

	//result = reg_read32(TIO_TIO);
	//ref = tio_ref(x, y);

	//printf("Bla state: %d.\n", get_bla_state());
	//printf("Setting Bla state to 1.\n");
	//set_bla_state(1);
	//for(int i = 0; i < 12; i++ )
	//	printf("Bla state: %d.\n", get_bla_state());

	//printf("DMA state: %d.\n", get_dma_state());
	//printf("Setting DMA state to 1.\n");
	//reg_write8(TIO_DMA_STATE, 1);
	//printf("DMA state: %d.\n", get_dma_state());
	printf("DMA state: %d.\n", get_dma_state());
	printf("DMA state: %d.\n", get_dma_state());
	printf("DMA finished.\n");
	uint32_t address_start = 0x88000000;
	uint32_t dma_size = 0x1000;
	uint32_t cache_block_bytes = 8;

	check_address(address_start, 666);					// First
	check_address(address_start + dma_size - cache_block_bytes, 666);	// Last
	check_address(address_start - cache_block_bytes, 0);			// One before
	check_address(address_start + dma_size, 0);				// One after

	//if (result != ref) {
	//	printf("Hardware result %d does not match reference value %d.\n", result, ref);
	//	return 1;
	//}
	//printf("Hardware result %d is correct for TIO\n.", result);
	return 0;
}