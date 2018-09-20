#include <MsTimer2.h>
#include <RunningMedian.h>
#include <SharpIR.h>
#include <EnableInterrupt.h>
#include "DualVNH5019MotorShield.h"


const int NUM_SAMPLES_MEDIAN = 7;
const int LEFT_PULSE = 3; // LEFT M1 Pulse
const int RIGHT_PULSE = 11; // RIGHT M2 Pulse
const int MAX_SPEED = 250;
const int TURN_MAX_SPEED = 200;
const int START_SPEED = 50;
const int SPEED_STEP = 10;
const int SPEED_STEP_TICKS = 30;
const int TURN_TICKS = 745;
const int TENCM_TICKS = 560;
const int MAX_SMALL_SENSOR = 80;
const int MAX_BIG_SENSOR = 150;
const int DIST_WALL_CENTER_BOX = 12;
//const double kp = 2.6, ki = 1.6, kd = 0;
//const double kp = 2.95, ki = 1.3, kd = 0.0005;
const double kp = 2.95, ki = 1.3, kd = 0.00006; // OPTIMAL
//const double kp = 1.95, ki = 1.3, kd = 0.0001; 
//const double kp = 10, ki = 0.1, kd = 0.025; 
//const double kp = 2.85, ki = 1.9, kd = 0; 
//const double kp = 10, ki = 0.1, kd = 0.025;

DualVNH5019MotorShield md;
long tick_R = 0;
long tick_L = 0;
long previous_tick_R = 0;
double previous_error = 0;

SharpIR frontIR_1(SharpIR::GP2Y0A21YK0F, A0);
SharpIR frontIR_2(SharpIR::GP2Y0A21YK0F, A4);
SharpIR frontIR_3(SharpIR::GP2Y0A21YK0F, A2);
SharpIR rightIR_1(SharpIR::GP2Y0A21YK0F, A3);
SharpIR rightIR_2(SharpIR::GP2Y0A21YK0F, A5);
SharpIR leftIR_1(SharpIR::GP2Y0A02YK0F, A1);
RunningMedian frontIR1_Median = RunningMedian(NUM_SAMPLES_MEDIAN);
RunningMedian frontIR2_Median = RunningMedian(NUM_SAMPLES_MEDIAN);
RunningMedian frontIR3_Median = RunningMedian(NUM_SAMPLES_MEDIAN);
RunningMedian rightIR1_Median = RunningMedian(NUM_SAMPLES_MEDIAN);
RunningMedian rightIR2_Median = RunningMedian(NUM_SAMPLES_MEDIAN);
RunningMedian leftIR_1_Median = RunningMedian(NUM_SAMPLES_MEDIAN);

long frontIR1_Value = 0, frontIR2_Value = 0, frontIR3_Value = 0;
long rightIR1_Value = 0, rightIR2_Value = 0, leftIR1_Value = 0;



void setup() {
  md.init();
  setupSerialConnection();
  setupMotorEncoder();
  setupSensorInterrupt();
  delay(10);
  //  alignRight();
  //  moveForwardTillWall();
  //  alignFront();
  //  turnLeft();
  //  alignRight();
  moveForwardTillWall();
  //  turnLeft();
  //  turnLeft();
  //  alignRight();
  //  moveForward(15 * TENCM_TICKS);
  //  turnLeft();
  //  turnLeft();
  //  turnLeft();
  //  turnLeft();
  //
  //  turnLeft();
  //  turnLeft();
  //  turnLeft();
  //  turnLeft();
  //
  //  turnLeft();
  //  turnLeft();
  //  turnLeft();
  //  turnLeft();






  //  turnLeft();
  //  moveForward(4000);
  //  moveForward(4000);
}

void loop() {
  //  Serial.print("F: ");
  //  Serial.print(frontIR1_Value);
  //  Serial.print(", ");
  //  Serial.print(frontIR2_Value);
  //  Serial.print(", ");
  //  Serial.print(frontIR3_Value);
  //  Serial.println("");
  //  Serial.print("R: ");
  //  Serial.print(rightIR1_Value);
  //  Serial.print(", ");
  //  Serial.print(rightIR2_Value);
  //  Serial.print(",L: ");
  //  Serial.println(leftIR1_Value);

  //  Serial.print(", ");
  //  Serial.print("S: ");
  //  Serial.print(frontIR3_Value);
  Serial.print(" R: ");
  Serial.print(tick_R);
  Serial.print(" L: ");
  Serial.println(tick_L);


}


//--------------------------Serial Codes-------------------------------
void setupSerialConnection() {
  Serial.begin(9600);
}

//--------------------------Timer Codes-------------------------------
void setupSensorInterrupt() {
  MsTimer2::set(50, readSensors); // 50ms period
  MsTimer2::start();
  delay(1000);
}

//--------------------------Motor Codes-------------------------------
void setupMotorEncoder() {
  md.init();
  pinMode(LEFT_PULSE, INPUT);
  pinMode(RIGHT_PULSE, INPUT);
  enableInterrupt(LEFT_PULSE, leftMotorTime, CHANGE);
  enableInterrupt(RIGHT_PULSE, rightMotorTime, CHANGE);
}

void moveForward(long distance) {
  initializeTick();
  initializeMotor_Start();
  double currentSpeed = START_SPEED;
  double offset = 0;
  long last_R = 0;
  while (tick_R < distance || tick_L < distance) {
    if (tick_R < (long)(0.5 * distance) || currentSpeed == START_SPEED || last_R == 0) {
      if (((tick_R - last_R) > SPEED_STEP_TICKS && currentSpeed < MAX_SPEED) || currentSpeed == START_SPEED || last_R == 0) {
        last_R = tick_R;
        currentSpeed = currentSpeed + SPEED_STEP;
      }
    }
    if (tick_R > (long)(0.85 * distance)) {
      if ((tick_R - last_R) > SPEED_STEP_TICKS) {
        last_R = tick_R;
        currentSpeed = currentSpeed - SPEED_STEP;
      }
    }
    offset = computePID();
    md.setSpeeds(currentSpeed + offset, currentSpeed - offset);
    Serial.print("S: ");
    Serial.print(currentSpeed);
    Serial.print(" R: ");
    Serial.print(tick_R);
    Serial.print(" L: ");
    Serial.print(tick_L);
    Serial.print(" O: ");
    Serial.println(offset);
  }
  initializeMotor_End();
}


void moveBackwards(long distance) {
  initializeTick();
  initializeMotor_Start();
  double currentSpeed = START_SPEED;
  double offset = 0;
  long last_R = 0;
  while (tick_R < distance || tick_L < distance) {
    if (tick_R < (long)(0.5 * distance) || currentSpeed == START_SPEED || last_R == 0) {
      if (((tick_R - last_R) > SPEED_STEP_TICKS && currentSpeed < MAX_SPEED) || currentSpeed == START_SPEED || last_R == 0) {
        last_R = tick_R;
        currentSpeed = currentSpeed + SPEED_STEP;
      }
    }
    if (tick_R > (long)(0.85 * distance)) {
      if ((tick_R - last_R) > SPEED_STEP_TICKS) {
        last_R = tick_R;
        currentSpeed = currentSpeed - SPEED_STEP;
      }
    }
    offset = computePID();
    md.setSpeeds(-(currentSpeed + offset), -(currentSpeed - offset));
    Serial.print("S: ");
    Serial.print(currentSpeed);
    Serial.print(" R: ");
    Serial.print(tick_R);
    Serial.print(" L: ");
    Serial.print(tick_L);
    Serial.print(" O: ");
    Serial.println(offset);
  }
  initializeMotor_End();
}


void moveForwardTillWall() {
  initializeTick();
  initializeMotor_Start();
  double currentSpeed = START_SPEED;
  double offset = 0;
  long last_R = 0;
  while (1) {
    if (frontIR1_Value <= DIST_WALL_CENTER_BOX || frontIR2_Value <= DIST_WALL_CENTER_BOX  || frontIR3_Value <= DIST_WALL_CENTER_BOX ) {
      break;
    }
    if (tick_R < (long)(0.5 * 800) || currentSpeed == START_SPEED || last_R == 0) {
      if (((tick_R - last_R) > SPEED_STEP_TICKS && currentSpeed < MAX_SPEED) || currentSpeed == START_SPEED || last_R == 0) {
        last_R = tick_R;
        currentSpeed = currentSpeed + SPEED_STEP;
      }
    }
    offset = computePID();
    md.setSpeeds(currentSpeed + offset, currentSpeed - offset);
    Serial.print("S: ");
    Serial.print(currentSpeed);
    Serial.print(" R: ");
    Serial.print(tick_R);
    Serial.print(" L: ");
    Serial.print(tick_L);
    Serial.print(" O: ");
    Serial.println(offset);
    //    Serial.println(micros());
  }
  md.setSpeeds(0, 0);
  initializeMotor_End();
}

void turnLeft() {
  initializeTick();
  initializeMotor_Start();
  double currentSpeed = START_SPEED;
  double offset = 0;
  long last_R = 0;
  while (tick_R < TURN_TICKS || tick_L < TURN_TICKS) {
    if (tick_R < (long)(0.5 * TURN_TICKS) || currentSpeed == START_SPEED || last_R == 0) {
      if (((tick_R - last_R) > SPEED_STEP_TICKS && currentSpeed < TURN_MAX_SPEED) || currentSpeed == START_SPEED || last_R == 0) {
        last_R = tick_R;
        currentSpeed = currentSpeed + SPEED_STEP;
      }
    }
    if (tick_R > (long)(0.85 * TURN_TICKS)) {
      if ((tick_R - last_R) > SPEED_STEP_TICKS) {
        last_R = tick_R;
        currentSpeed = currentSpeed - SPEED_STEP;
      }
    }
    offset = computePID();
    md.setSpeeds(-(currentSpeed + offset), currentSpeed - offset);
    Serial.print("S: ");
    Serial.print(currentSpeed);
    Serial.print(" R: ");
    Serial.print(tick_R);
    Serial.print(" L: ");
    Serial.print(tick_L);
    Serial.print(" O: ");
    Serial.println(offset);
  }
  initializeMotor_End();
}

void turnRight() {
  initializeTick();
  initializeMotor_Start();
  double currentSpeed = START_SPEED;
  double offset = 0;
  long last_R = 0;
  while (tick_R < TURN_TICKS || tick_L < TURN_TICKS) {
    if (tick_R < (long)(0.5 * TURN_TICKS) || currentSpeed == START_SPEED || last_R == 0) {
      if (((tick_R - last_R) > SPEED_STEP_TICKS && currentSpeed < TURN_MAX_SPEED) || currentSpeed == START_SPEED || last_R == 0) {
        last_R = tick_R;
        currentSpeed = currentSpeed + SPEED_STEP;
      }
    }
    if (tick_R > (long)(0.85 * TURN_TICKS)) {
      if ((tick_R - last_R) > SPEED_STEP_TICKS) {
        last_R = tick_R;
        currentSpeed = currentSpeed - SPEED_STEP;
      }
    }
    offset = computePID();
    md.setSpeeds(currentSpeed + offset, -(currentSpeed - offset));
    Serial.print("S: ");
    Serial.print(currentSpeed);
    Serial.print(" R: ");
    Serial.print(tick_R);
    Serial.print(" L: ");
    Serial.print(tick_L);
    Serial.print(" O: ");
    Serial.println(offset);
  }
  initializeMotor_End();
}

void rotateLeft(long distance) {
  initializeTick();
  initializeMotor_Start();
  double currentSpeed = START_SPEED;
  double offset = 0;
  long last_R = 0;
  while (tick_R < distance || tick_L < distance) {
    if (last_R == 0) {
      currentSpeed = currentSpeed + SPEED_STEP;
    }
    //    if (tick_R < (long)(0.5 * TURN_TICKS) || currentSpeed == START_SPEED) {
    //      if (((tick_R - last_R) > SPEED_STEP_TICKS && currentSpeed < TURN_MAX_SPEED) || currentSpeed == START_SPEED) {
    //        last_R = tick_R;
    //        currentSpeed = currentSpeed + SPEED_STEP;
    //      }
    //    }
    //    if (tick_R > (long)(0.85 * distance)) {
    //      if ((tick_R - last_R) > SPEED_STEP_TICKS) {
    //        last_R = tick_R;
    //        currentSpeed = currentSpeed - SPEED_STEP;
    //      }
    //    }
    offset = computePID();
    md.setSpeeds(-(currentSpeed + offset), currentSpeed - offset);
    Serial.print("S: ");
    Serial.print(currentSpeed);
    Serial.print(" R: ");
    Serial.print(tick_R);
    Serial.print(" L: ");
    Serial.print(tick_L);
    Serial.print(" O: ");
    Serial.println(offset);
  }
  initializeMotor_End();
}

void rotateRight(long distance) {
  initializeTick();
  initializeMotor_Start();
  double currentSpeed = START_SPEED;
  double offset = 0;
  long last_R = 0;
  while (tick_R < distance || tick_L < distance) {
    if (last_R == 0) {
      currentSpeed = currentSpeed + SPEED_STEP;
    }
    //    if (tick_R < (long)(0.5 * TURN_TICKS) || currentSpeed == START_SPEED) {
    //      if (((tick_R - last_R) > SPEED_STEP_TICKS && currentSpeed < TURN_MAX_SPEED) || currentSpeed == START_SPEED) {
    //        last_R = tick_R;
    //        currentSpeed = currentSpeed + SPEED_STEP;
    //      }
    //    }
    //    if (tick_R > (long)(0.85 * distance)) {
    //      if ((tick_R - last_R) > SPEED_STEP_TICKS) {
    //        last_R = tick_R;
    //        currentSpeed = currentSpeed - SPEED_STEP;
    //      }
    //    }
    offset = computePID();
    md.setSpeeds(currentSpeed + offset, -(currentSpeed - offset));
    Serial.print("S: ");
    Serial.print(currentSpeed);
    Serial.print(" R: ");
    Serial.print(tick_R);
    Serial.print(" L: ");
    Serial.print(tick_L);
    Serial.print(" O: ");
    Serial.println(offset);
  }
  initializeMotor_End();
}

void alignRight() {
  Serial.println("Start");
  delay(1000);
  long diff = rightIR1_Value - rightIR2_Value;
  Serial.print("D: ");
  Serial.println(diff);
  while (diff != 0) {
    if (diff > 0) {
      rotateLeft(abs(diff * 6));
    } else {
      rotateRight(abs(diff * 6));
    }
    diff = rightIR1_Value - rightIR2_Value;
    Serial.print("D: ");
    Serial.println(diff);
    delay(400);
  }


}

void alignFront() {
  Serial.println("Start");
  delay(1000);
  long diff = frontIR1_Value - frontIR2_Value;
  Serial.print("D: ");
  Serial.println(diff);
  while (diff != 0) {
    if (diff > 0) {
      rotateLeft(abs(diff * 6));
    } else {
      rotateRight(abs(diff * 6));
    }
    diff = rightIR1_Value - rightIR2_Value;
    Serial.print("D: ");
    Serial.println(diff);
    delay(400);
  }
  long diff_dis = frontIR1_Value - 11;
  if (diff_dis > 0)
    moveForward((TENCM_TICKS / 10) * diff_dis);
  else
    moveBackwards((TENCM_TICKS / 10) * diff_dis);
  delay(400);


}


void leftMotorTime() {
  tick_L++;
}

void rightMotorTime() {
  tick_R++;
}

void initializeTick() {
  tick_R = 0;
  tick_L = 0;
  previous_tick_R = 0;
}

void initializeMotor_Start() {
  md.setSpeeds(0, 0);
  md.setBrakes(0, 0);
}

void initializeMotor_End() {
  md.setSpeeds(0, 0);
  md.setBrakes(400, 400);
  delay(50);
}

//--------------------------PID Codes-------------------------------
double computePID() {
  double error, integral;

  error = tick_R - tick_L;
  integral = previous_error + error;

  double p = kp * error;
  double i = ki * integral;
  double d = kd * (previous_tick_R - tick_R);
  double pid = p + i + d;

  previous_tick_R = tick_R;

  return pid;
}


//--------------------------Sensor Codes-------------------------------
void readSensors() {
  readFrontSensor_1();
  readFrontSensor_2();
  readFrontSensor_3();
  readRightSensor_1();
  readRightSensor_2();
  readLeftSensor_1();
}


void readFrontSensor_1() {
  long irDistance = frontIR_1.getDistance();
  frontIR1_Median.add(irDistance);
  if (frontIR1_Median.getCount() >= NUM_SAMPLES_MEDIAN) {
    if (abs(frontIR1_Median.getHighest() - frontIR1_Median.getLowest()) > 40) {
      frontIR1_Value = MAX_SMALL_SENSOR;
    } else {
      frontIR1_Value = frontIR1_Median.getMedian();
    }
  }
}

void readFrontSensor_2() {
  long irDistance = frontIR_2.getDistance();
  frontIR2_Median.add(irDistance);
  if (frontIR2_Median.getCount() >= NUM_SAMPLES_MEDIAN) {
    if (abs(frontIR2_Median.getHighest() - frontIR2_Median.getLowest()) > 40) {
      frontIR2_Value = MAX_SMALL_SENSOR;
    } else {
      frontIR2_Value = frontIR2_Median.getMedian();
    }
  }
}

void readFrontSensor_3() {
  long irDistance = frontIR_3.getDistance();
  frontIR3_Median.add(irDistance);
  if (frontIR3_Median.getCount() >= NUM_SAMPLES_MEDIAN) {
    if (abs(frontIR3_Median.getHighest() - frontIR3_Median.getLowest()) > 40) {
      frontIR3_Value = MAX_SMALL_SENSOR;
    } else {
      frontIR3_Value = frontIR3_Median.getMedian();
    }
  }
}

void readRightSensor_1() {
  long irDistance = rightIR_1.getDistance();
  rightIR1_Median.add(irDistance);
  if (rightIR1_Median.getCount() >= NUM_SAMPLES_MEDIAN) {
    if (abs(rightIR1_Median.getHighest() - rightIR1_Median.getLowest()) > 40) {
      rightIR1_Value = MAX_SMALL_SENSOR;
    } else {
      rightIR1_Value = rightIR1_Median.getMedian();
    }
  }
}

void readRightSensor_2() {
  long irDistance = rightIR_2.getDistance();
  rightIR2_Median.add(irDistance);
  if (rightIR2_Median.getCount() >= NUM_SAMPLES_MEDIAN) {
    if (abs(rightIR2_Median.getHighest() - rightIR2_Median.getLowest()) > 40) {
      leftIR1_Value = MAX_SMALL_SENSOR;
    } else {
      rightIR2_Value = rightIR2_Median.getMedian();
    }
  }
}

void readLeftSensor_1() {
  long irDistance = leftIR_1.getDistance();
  leftIR_1_Median.add(irDistance);
  if (leftIR_1_Median.getCount() >= NUM_SAMPLES_MEDIAN) {
    if (abs(leftIR_1_Median.getHighest() - leftIR_1_Median.getLowest()) > 40) {
      leftIR1_Value = MAX_BIG_SENSOR;
    } else {
      leftIR1_Value = leftIR_1_Median.getMedian();
    }
  }
}
