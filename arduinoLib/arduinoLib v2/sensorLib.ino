#include <RunningMedian.h>

#define FIR1 A0
#define FIR2 A4
#define FIR3 A2

#define RIR1 A3
#define RIR2 A5
#define LIR1 A1

const int MAX_SMALL_SENSOR = 80;
const int MAX_BIG_SENSOR = 150;
const int NUM_SAMPLES_MEDIAN = 15;

double frontIR1_Diffs[] = {5.50, 13.95, 23.30, 35.00};
double frontIR2_Diffs[] = {5.70, 15.15, 25.75, 39.00};
double frontIR3_Diffs[] = {5.05, 13.85, 22.50, 36.00};


double rightIR1_Diffs[] = {6.90, 17.05, 27.75, 41.00};
double rightIR2_Diffs[] = {7.5, 17.75, 29.75, 47.00};

double leftIR1_Diffs[] = {20.25, 24.15, 32.00, 40.55, 49.00, 56.00};

double frontIR1_Value = 0, frontIR2_Value = 0, frontIR3_Value = 0;
int  frontIR1_Block = 0, frontIR2_Block = 0, frontIR3_Block = 0;
double rightIR1_Value = 0, rightIR2_Value = 0, leftIR1_Value = 0;
int rightIR1_Block = 0, rightIR2_Block = 0, leftIR1_Block = 0;

double lastFrontIR2_Value = 99;

RunningMedian frontIR1_Median = RunningMedian(NUM_SAMPLES_MEDIAN);
RunningMedian frontIR2_Median = RunningMedian(NUM_SAMPLES_MEDIAN);
RunningMedian frontIR3_Median = RunningMedian(NUM_SAMPLES_MEDIAN);
RunningMedian rightIR1_Median = RunningMedian(NUM_SAMPLES_MEDIAN);
RunningMedian rightIR2_Median = RunningMedian(NUM_SAMPLES_MEDIAN);
RunningMedian leftIR1_Median = RunningMedian(NUM_SAMPLES_MEDIAN);

void setupSensorInterrupt() {
  //  ADCSRA &= ~(bit (ADPS0) | bit (ADPS1) | bit (ADPS2)); // clear prescaler bits
  //  //  ADCSRA |= bit (ADPS0) | bit (ADPS2);// 32  prescaler
  //  ADCSRA |= bit (ADPS2); // 16  prescaler
}

void readSensors() {
  readFrontSensor_1();
  readFrontSensor_2();
  readFrontSensor_3();
  readRightSensor_1();
  readRightSensor_2();
  readLeftSensor_1();
}


double getFrontIR1() {
  for (int n = 0; n < NUM_SAMPLES_MEDIAN; n++) {
    readFrontSensor_1();
  }
  return frontIR1_Value;
}
double getFrontIR2() {
  for (int n = 0; n < NUM_SAMPLES_MEDIAN; n++) {
    readFrontSensor_2();
  }
  return frontIR2_Value;
}
double getFrontIR3() {
  for (int n = 0; n < NUM_SAMPLES_MEDIAN; n++) {
    readFrontSensor_3();
  }
  return frontIR3_Value;
}
double getRightIR1() {
  for (int n = 0; n < NUM_SAMPLES_MEDIAN; n++) {
    readRightSensor_1();
  }
  return rightIR1_Value;
}
double getRightIR2() {
  for (int n = 0; n < NUM_SAMPLES_MEDIAN; n++) {
    readRightSensor_2();
  }
  return rightIR2_Value;
}
double getLeftIR1() {
  for (int n = 0; n < NUM_SAMPLES_MEDIAN; n++) {
    readLeftSensor_1();
  }
  return leftIR1_Value;
}

int getFrontIR1_Block() {
  return frontIR1_Block;
}
int getFrontIR2_Block() {
  return frontIR2_Block;
}
int getFrontIR3_Block() {
  return frontIR3_Block;
}
int getRightIR1_Block() {
  return rightIR1_Block;
}
int getRightIR2_Block() {
  return rightIR2_Block;
}
int getLeftIR1_Block() {
  return leftIR1_Block;
}

double getLastFrontIR2() {
  return lastFrontIR2_Value;
}

void clearLastFrontIR2() {
  lastFrontIR2_Value = 99;
}

void readFrontSensor_1() {
  int raw = analogRead(FIR1);
  //  Serial.print(" FS1: ");
  //  Serial.print(raw);
  double convertedDistance;
  if (raw < 50) {
    convertedDistance = MAX_SMALL_SENSOR;
  } else {
    convertedDistance = -0.086 * raw + 47.319;
    //    convertedDistance = 4800.0 / (raw - 20.0);
  }
  if (convertedDistance < 0)
    convertedDistance = 0;
  frontIR1_Median.add(convertedDistance);
  if (frontIR1_Median.getCount() >= NUM_SAMPLES_MEDIAN) {
    if (abs(frontIR1_Median.getHighest() - frontIR1_Median.getLowest()) > 40) {
      frontIR1_Value = MAX_SMALL_SENSOR;
    } else {
      frontIR1_Value = frontIR1_Median.getMedian();
    }
  }
  for (int m = 0; m < 3; m++) {
    if (frontIR1_Value <= frontIR1_Diffs[m]) {
      frontIR1_Block = m + 1;
      return;
    }
  }
  frontIR1_Block = 9;
}

void readFrontSensor_2() {
  int raw = analogRead(FIR2);
  //  Serial.print(" FS2: ");
  //  Serial.print(raw);
  double convertedDistance;
  if (raw < 5) {
    convertedDistance = MAX_SMALL_SENSOR;
  } else {
    convertedDistance = -0.0846 * raw + 46.224;
    //convertedDistance = 4800.0 / (raw - 20.0);
  }
  if (convertedDistance < 0)
    convertedDistance = 0;
  frontIR2_Median.add(convertedDistance);
  if (frontIR2_Median.getCount() >= NUM_SAMPLES_MEDIAN) {
    lastFrontIR2_Value = frontIR2_Value;
    if (abs(frontIR2_Median.getHighest() - frontIR2_Median.getLowest()) > 40) {
      frontIR2_Value = MAX_SMALL_SENSOR;
    } else {
      frontIR2_Value = frontIR2_Median.getMedian();
    }
  }
  for (int m = 0; m < 3; m++) {
    if (frontIR2_Value <= frontIR2_Diffs[m]) {
      frontIR2_Block = m + 1;
      return;
    }
  }
  frontIR2_Block = 9;
}

void readFrontSensor_3() {
  int raw = analogRead(FIR3);
  //  Serial.print(" FS3: ");
  //  Serial.print(raw);
  double convertedDistance;
  if (raw < 5) {
    convertedDistance = MAX_SMALL_SENSOR;
  } else {
    convertedDistance = -0.0862 * raw + 47.23;
    //    convertedDistance = 4800.0 / (raw - 20.0);
  }
  if (convertedDistance < 0)
    convertedDistance = 0;
  frontIR3_Median.add(convertedDistance);
  if (frontIR3_Median.getCount() >= NUM_SAMPLES_MEDIAN) {
    if (abs(frontIR3_Median.getHighest() - frontIR3_Median.getLowest()) > 40) {
      frontIR3_Value = MAX_SMALL_SENSOR;
    } else {
      frontIR3_Value = frontIR3_Median.getMedian();
    }
  }
  for (int m = 0; m < 3; m++) {
    if (frontIR3_Value <= frontIR3_Diffs[m]) {
      frontIR3_Block = m + 1;
      return;
    }
  }
  frontIR3_Block = 9;
}

void readRightSensor_1() {
  int raw = analogRead(RIR1);
  //  Serial.print(" RS1: ");
  //  Serial.print(raw);
  double convertedDistance;
  if (raw < 5) {
    convertedDistance = MAX_SMALL_SENSOR;
  } else {
    convertedDistance = -0.0847 * raw + 46.691;
    //    convertedDistance = 4800.0 / (raw - 20.0);
  }
  if (convertedDistance < 0)
    convertedDistance = 0;
  rightIR1_Median.add(convertedDistance);
  if (rightIR1_Median.getCount() >= NUM_SAMPLES_MEDIAN) {
    if (abs(rightIR1_Median.getHighest() - rightIR1_Median.getLowest()) > 40) {
      rightIR1_Value = MAX_SMALL_SENSOR;
    } else {
      rightIR1_Value = rightIR1_Median.getMedian();
    }
  }
  for (int m = 0; m < 3; m++) {
    if (rightIR1_Value <= rightIR1_Diffs[m]) {
      rightIR1_Block = m + 1;
      return;
    }
  }
  rightIR1_Block = 9;
}

void readRightSensor_2() {
  int raw = analogRead(RIR2);
  //  Serial.print(" RS2: ");
  //  Serial.print(raw);
  double convertedDistance;
  if (raw < 5) {
    convertedDistance = MAX_SMALL_SENSOR;
  } else {
    convertedDistance = -0.0847 * raw + 46.3;
    //    convertedDistance = 4800.0 / (raw - 20.0);
  }
  if (convertedDistance < 0)
    convertedDistance = 0;
  rightIR2_Median.add(convertedDistance);
  if (rightIR2_Median.getCount() >= NUM_SAMPLES_MEDIAN) {
    if (abs(rightIR2_Median.getHighest() - rightIR2_Median.getLowest()) > 40) {
      rightIR2_Value = MAX_SMALL_SENSOR;
    } else {
      rightIR2_Value = rightIR2_Median.getMedian();
    }
  }
  for (int m = 0; m < 3; m++) {
    if (rightIR2_Value <= rightIR2_Diffs[m]) {
      rightIR2_Block = m + 1;
      return;
    }
  }
  rightIR2_Block = 9;
}

void readLeftSensor_1() {
  int raw = analogRead(LIR1);
  //  Serial.print(" LS1: ");
  //  Serial.println(raw);
  double convertedDistance;
  if (raw < 5) {
    convertedDistance = MAX_BIG_SENSOR;
  } else {
    convertedDistance = -0.1362 * raw + 73.904;
    //    convertedDistance = 9462.0 / (raw - 16.92);
  }
  if (convertedDistance < 0)
    convertedDistance = 0;
  leftIR1_Median.add(convertedDistance);
  if (leftIR1_Median.getCount() >= NUM_SAMPLES_MEDIAN) {
    if (abs(leftIR1_Median.getHighest() - leftIR1_Median.getLowest()) > 40) {
      leftIR1_Value = MAX_BIG_SENSOR;
    } else {
      leftIR1_Value = leftIR1_Median.getMedian();
    }
  }
  for (int m = 0; m < 6; m++) {
    if (leftIR1_Value <= leftIR1_Diffs[m]) {
      leftIR1_Block = m + 1;
      return;
    }
  }
  leftIR1_Block = 9;
}


