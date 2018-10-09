
const double tenDivideTenCMTicks = 10.0 / TENCM_TICKS;
const double tenCMTicksDivideTen = TENCM_TICKS / 10.0;

long ticksToCm(long ticks) {
  double ret = (double)ticks * tenDivideTenCMTicks;
  return ret;
}

long cmToTicks(long cm) {
  double ret = (double)cm * tenCMTicksDivideTen;
  return ret;
}



