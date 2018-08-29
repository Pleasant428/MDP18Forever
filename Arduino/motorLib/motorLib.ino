#include <DualVNH5019MotorShield.h>
#include <EnableInterrupt.h>

#define MAX_SPEED 400
#define LEFT_PULSE 3 // LEFT M1 Pulse A 
#define RIGHT_PULSE 11 // RIGHT M2 Pulse A

DualVNH5019MotorShield md;

volatile int countLeft, countRight;
volatile boolean calibrationMotorLeft, calibrationMotorRight;

int integral = 0;
int derivative = 0;
int previousDifference = 0;

void setup() {
  md.init();
  Serial.begin(9600);
  while (!Serial);
  resetMotorCount();
  setupMotorEncoder();
  //  calibrateMotor();
  moveForward(20000, 50);
  //  turnRight();
}

void loop() {
}

void resetMotorCount() {
  countLeft = 0;
  countRight = 0;
}

void setupMotorEncoder() {
  pinMode(LEFT_PULSE, INPUT);
  pinMode(RIGHT_PULSE, INPUT);
  enableInterrupt(LEFT_PULSE, incrementLeftMotorCount, CHANGE);
  enableInterrupt(RIGHT_PULSE, incrementRightMotorCount, CHANGE);
  md.setSpeeds(0, 0);
}

void incrementLeftMotorCount() {
//  Serial.print("L: ");
//  Serial.println(countLeft);
  countLeft++;
}

void incrementRightMotorCount() {
//  Serial.print("R: ");
//  Serial.println(countRight);
  countRight++;
}

void calibrateMotor() {
  Serial.println("Start - Motor Calibration");
  int calibrationMotorSpeed = 50;
  resetMotorCount();
  calibrationMotorLeft = false;
  calibrationMotorRight = false;
  enableInterrupt(LEFT_PULSE, leftCalibrateDone, CHANGE);
  enableInterrupt(RIGHT_PULSE, rightCalibrateDone, CHANGE);
  while (calibrationMotorLeft == false || calibrationMotorRight == false) {
    if (calibrationMotorLeft == false)
      md.setM1Speed(calibrationMotorSpeed);
    if (calibrationMotorRight == false)
      md.setM2Speed(calibrationMotorSpeed);
  }
  delay(100);
  while (countLeft != countRight) {
    if (countLeft > countRight) {
      calibrationMotorRight = false;
      while (calibrationMotorRight == false)
        md.setM2Speed(calibrationMotorSpeed);
    }
    if (countRight > countLeft) {
      calibrationMotorLeft = false;
      while (calibrationMotorLeft == false)
        md.setM1Speed(calibrationMotorSpeed);
    }
  }
  resetMotorCount();
  Serial.println("Done - Motor Calibration");
  setupMotorEncoder();
}

void leftCalibrateDone() {
  md.setM1Brake(400);
  calibrationMotorLeft = true;
}

void rightCalibrateDone() {
  md.setM2Brake(400);
  calibrationMotorRight = true;
}

void moveForward(int distance, int Speed) {
  resetMotorCount();
  while (countLeft < distance || countRight < distance) {
    double adjustments = tunePID();
    Serial.print("PID Value: ");
    Serial.println(adjustments);
    md.setSpeeds(Speed - adjustments, Speed + adjustments);
    previousDifference = adjustments;
  }
  md.setBrakes(400, 400);
}

void turnRight() {
  int distance = 808;
  int Speed = 200;
  resetMotorCount();
  while (countLeft < distance || countRight < distance) {
    double adjustments = tunePID();
    Serial.print("PID Value: ");
    Serial.println(adjustments);
    md.setSpeeds(Speed - adjustments, -(Speed + adjustments));
    previousDifference = adjustments;
  }
  md.setBrakes(400, 400);
}

double tunePID() {
  int difference = (countLeft - countRight);
  integral = integral + previousDifference;
  previousDifference = difference;
  return ((0.6) * difference) + ((0.1) * integral);
}

