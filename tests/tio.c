#include <stdio.h>
#include "mmio.h"

enum {
TIO_STATUS = 0x4000,
TIO_X = 0x4004,
TIO_Y = 0x4008,
TIO_TIO = 0x400C
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

int main(void)
{
  uint32_t result, ref, x = 20, y = 15;

  // wait for peripheral to be ready
  while ((reg_read8(TIO_STATUS) & 0x2) == 0) ;

  reg_write32(TIO_X, x);
  reg_write32(TIO_Y, y);

  // wait for peripheral to complete
  while ((reg_read8(TIO_STATUS) & 0x1) == 0) ;

  result = reg_read32(TIO_TIO);
  ref = tio_ref(x, y);

  if (result != ref) {
    printf("Hardware result %d does not match reference value %d\n", result, ref);
    return 1;
  }
  printf("Hardware result %d is correct for TIO\n", result);
  return 0;
}
