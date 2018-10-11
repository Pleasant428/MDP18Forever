#include <PID_AutoTune_v0.h>
#include <PID_v1.h>
#include <EnableInterrupt.h>
#include <DualVNH5019MotorShield.h>

const int LEFT_PULSE = 3; // LEFT M1 Pulse A
const int RIGHT_PULSE = 11; // RIGHT M2 Pulse A
const double MAX_SPEED = 100;

unsigned long previousTime_R = 0;
unsigned long previousTime_L = 0;

unsigned long cycleTime_R = 0;
unsigned long cycleTime_L = 0;

double currentRPM_R = 0.0;
double currentRPM_L = 0.0;
double currentDifference = 0.0;

double tick_R = 0;
double tick_L = 0;
double tick_LRDifference = 0;
double tick_LRDifference_Target = 0;

double targetRPM = 0.0;

double speed_R = 0.0;
double speed_L = 0.0;

DualVNH5019MotorShield md;
PID myPID_T(&currentRPM_R, &speed_R, &targetRPM, 2, 5, 0, P_ON_M, DIRECT);
PID myPID_S(&currentRPM_L, &speed_L, &targetRPM, 2, 5, 0, P_ON_M, DIRECT);


void setup() {
  md.init();
  Serial.begin(9600);
  setupMotorEncoder();
  myPID_T.SetMode(AUTOMATIC);
  myPID_T.SetOutputLimits(0, 350);
  myPID_T.SetSampleTime(20);
  myPID_S.SetMode(AUTOMATIC);
  myPID_S.SetOutputLimits(0, 350);
  myPID_S.SetSampleTime(20);
  moveForward(10000);
}


void loop() {
  //  Serial.print("R: ");
  //  Serial.println(currentRPM_R);
  //  Serial.print("L: ");
  //  Serial.println(currentRPM_L);

}

void setupMotorEncoder() {
  pinMode(LEFT_PULSE, INPUT);
  pinMode(RIGHT_PULSE, INPUT);
  enableInterrupt(LEFT_PULSE, leftMotorTime, CHANGE);
  enableInterrupt(RIGHT_PULSE, rightMotorTime, CHANGE);
}

void leftMotorTime() {
  unsigned long t = micros();
  cycleTime_L = t - previousTime_L;
  previousTime_L = t;
  currentRPM_L = (1 / ((cycleTime_L * 2.0 * 562.25) / 1000000)) * 60;
  tick_L++;
  tick_LRDifference = tick_L - tick_R;
}

void rightMotorTime() {
  unsigned long t = micros();
  cycleTime_R = t - previousTime_R;
  previousTime_R = t;
  currentRPM_R = (1 / ((cycleTime_R * 2.0 * 562.25) / 1000000)) * 60;
  tick_R++;
  tick_LRDifference = tick_L - tick_R;
}

void moveForward(double distance) {
  initializeTime();
  initializeTick();
  initializeSpeed();
  double lastTick_R = 0;
  targetRPM = 30;
  while (tick_R < distance || tick_L < distance) {
    if (tick_R - lastTick_R > 10) {
      targetRPM = targetRPM + 10;
      lastTick_R = tick_R;
    }
    if (myPID_T.Compute() == true) {
      md.setM2Speed((int)speed_R);
    }
    if (myPID_S.Compute() == true) {
      md.setM1Speed((int)speed_L);
    }
    Serial.print("R: ");
    Serial.println(tick_R);
    Serial.print("L: ");
    Serial.println(tick_L);
  }
  md.setBrakes(400, 400);

}

void initializeTime() {
  unsigned long t = micros();
  previousTime_R = t;
  previousTime_L = t;
}

void initializeTick() {
  tick_R = 0;
  tick_L = 0;
  tick_LRDifference = 0;
}

void initializeSpeed() {
  speed_R = 0;
  speed_L = 0;
  targetRPM = 0;
}


