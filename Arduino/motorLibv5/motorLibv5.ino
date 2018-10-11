#include <RunningMedian.h>

#include <SharpIR.h>

#include <PID_v1.h>

#include <EnableInterrupt.h>
#include <DualVNH5019MotorShield.h>

const int LEFT_PULSE = 3; // LEFT M1 Pulse A
const int RIGHT_PULSE = 11; // RIGHT M2 Pulse A
const double MAX_SPEED = 250;
const double TURN_MAX_SPEED = 160;
const double START_SPEED = 25;
const double SPEED_STEP = 30;
const double SPEED_STEP_TICKS = 15;
const double TURN_TICKS = 780;

double tick_R = 0;
double tick_L = 0;
double diff_LR = 0;
double speed_O = 0.0;

double lastFrontIRValue = -1;
//double kp = 3.5, ki = 1.6, kd = 0;
double kp = 2.6, ki = 1.6, kd = 0;
//double kp = 0.001, ki = 0.1, kd = 0.001;

DualVNH5019MotorShield md;
PID myPID(&tick_R, &speed_O, &tick_L, kp, ki, kd, DIRECT);
SharpIR frontIR_L(SharpIR::GP2Y0A02YK0F, A0);
SharpIR frontIR_R(SharpIR::GP2Y0A02YK0F, A1);
SharpIR rightIR_1(SharpIR::GP2Y0A21YK0F, A5);
SharpIR rightIR_2(SharpIR::GP2Y0A21YK0F, A4);
RunningMedian frontIR_Median = RunningMedian(15);
RunningMedian rightIR1_Median = RunningMedian(15);
RunningMedian rightIR2_Median = RunningMedian(15);

void setup() {
  md.init();
  Serial.begin(9600);
  setupMotorEncoder();
  myPID.SetMode(AUTOMATIC);
  myPID.SetOutputLimits(-320, 320);
  myPID.SetSampleTime(1);
  moveForward(500000);
//  turnRight();
//  turnRight();
  moveForward(5000);
//  turnLeft();
//  turnLeft();

}


void loop() {
  //  Serial.print("Front IR: ");
  //  Serial.print(readFrontSensor());
  //  Serial.print(" Right IR 1: ");
  //  Serial.print(readRightSensor_1());
  //  Serial.print(" Right IR 2: ");
  //  Serial.println(readRightSensor_2());
}


void setupMotorEncoder() {
  md.init();
  pinMode(LEFT_PULSE, INPUT);
  pinMode(RIGHT_PULSE, INPUT);
  enableInterrupt(LEFT_PULSE, leftMotorTime, CHANGE);
  enableInterrupt(RIGHT_PULSE, rightMotorTime, CHANGE);
}

void leftMotorTime() {
  tick_L++;
  diff_LR = tick_L - tick_R;
}

void rightMotorTime() {
  tick_R++;
  diff_LR = tick_L - tick_R;
}

void moveForward(double distance) {
  initializeTick();
  initializeSpeed();
  initializeMotor_Start();
  double currentSpeed = START_SPEED;
  double lastTick_R = 0.0;
  while (tick_R < distance || tick_L < distance) {
    double irDistance = readFrontSensor();
    //    Serial.print("IR: ");
    //    Serial.print(irDistance);
    if (irDistance <= 20) {
      initializeMotor_End();
      break;
      //RETURN VALUE HERE
    }
    if (distance - tick_R < 200 && distance > 200) {
      currentSpeed = currentSpeed * 0.5;
    } else if (((tick_R - lastTick_R > SPEED_STEP_TICKS) && (currentSpeed < MAX_SPEED)) || currentSpeed == START_SPEED) {
      currentSpeed = currentSpeed + SPEED_STEP;
      lastTick_R = tick_R;
    }
    if (myPID.Compute()) {
      md.setSpeeds(currentSpeed - speed_O, currentSpeed + speed_O);
      //      md.setSpeeds(currentSpeed, currentSpeed + speed_O);
    }
    Serial.print("R: ");
    Serial.print(tick_R);
    Serial.print(" L: ");
    Serial.print(tick_L);
    Serial.print(" O: ");
    Serial.println(speed_O);
    //    Serial.print(" S: ");
    //    Serial.println(currentSpeed);
    //    Serial.print("D(L-R): ");
    //    Serial.println(tick_L - tick_R);
  }
  initializeMotor_End();
  clearFrontSensor();
}

void turnRight() {
  initializeTick();
  initializeSpeed();
  initializeMotor_Start();
  double currentSpeed = START_SPEED;
  double lastTick_R = 0.0;
  while (tick_R < TURN_TICKS || tick_L < TURN_TICKS) {
    if (((tick_R - lastTick_R > SPEED_STEP_TICKS) && (currentSpeed < TURN_MAX_SPEED)) || currentSpeed == START_SPEED) {
      currentSpeed = currentSpeed + SPEED_STEP;
      lastTick_R = tick_R;
    }
    if (myPID.Compute()) {
      md.setSpeeds(-(currentSpeed - speed_O), currentSpeed + speed_O);
      //      md.setSpeeds(-currentSpeed, currentSpeed + speed_O);
    }
    //    Serial.print("R: ");
    //    Serial.print(tick_R);
    //    Serial.print(" L: ");
    //    Serial.print(tick_L);
    //    Serial.print(" O: ");
    //    Serial.print(speed_O);
    //    Serial.print(" S: ");
    //    Serial.println(currentSpeed);
    //    Serial.print("D(L-R): ");
    //    Serial.println(tick_L - tick_R);
  }

  initializeMotor_End();
}

void turnLeft() {
  initializeTick();
  initializeSpeed();
  initializeMotor_Start();
  double currentSpeed = START_SPEED;
  double lastTick_R = 0.0;
  while (tick_R < TURN_TICKS || tick_L < TURN_TICKS) {
    if (((tick_R - lastTick_R > SPEED_STEP_TICKS) && (currentSpeed < TURN_MAX_SPEED)) || currentSpeed == START_SPEED) {
      currentSpeed = currentSpeed + SPEED_STEP;
      lastTick_R = tick_R;
    }
    if (myPID.Compute()) {
      md.setSpeeds(currentSpeed - speed_O, -(currentSpeed + speed_O));
      //      md.setSpeeds(currentSpeed, -(currentSpeed + speed_O));
    }
    //    Serial.print("R: ");
    //    Serial.print(tick_R);
    //    Serial.print(" L: ");
    //    Serial.print(tick_L);
    //    Serial.print(" O: ");
    //    Serial.print(speed_O);
    //    Serial.print(" S: ");
    //    Serial.println(currentSpeed);
    //    Serial.print("D(L-R): ");
    //    Serial.println(tick_L - tick_R);
  }
  initializeMotor_End();
}

void initializeTick() {
  tick_R = 0;
  tick_L = 0;
}

void initializeSpeed() {
  speed_O = 0;
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

double readFrontSensor() {
  long irDistance_R = frontIR_R.getDistance();
  long irDistance_L = frontIR_L.getDistance();
  frontIR_Median.add(min(irDistance_R, irDistance_L));
  if (abs(frontIR_Median.getHighest() - frontIR_Median.getLowest()) > 50) {
    return 150;
  }
  return frontIR_Median.getMedian();
}

double readRightSensor_1() {
  long irDistance = rightIR_1.getDistance();
  rightIR1_Median.add(irDistance);
  if (abs(rightIR1_Median.getHighest() - rightIR1_Median.getLowest()) > 40) {
    return 80;
  }
  return rightIR1_Median.getMedian();
}

double readRightSensor_2() {
  long irDistance = rightIR_2.getDistance();
  rightIR2_Median.add(irDistance);
  if (abs(rightIR2_Median.getHighest() - rightIR2_Median.getLowest()) > 40) {
    return 80;
  }
  return rightIR2_Median.getMedian();
}



void clearFrontSensor() {
  frontIR_Median.clear();
}

