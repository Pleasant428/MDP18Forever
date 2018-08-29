#include <PID_v1.h>

#include <PID_AutoTune_v0.h>

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
double speed_O = 0.0;

double kp = 4, ki = 0.5, kd = 0.005;

DualVNH5019MotorShield md;
//AutoPID myPID(&tick_R, &tick_L, &speed_O, -350, 350, 4.005, 0.5, 0.005);
PID myPID(&currentRPM_R, &speed_O, &currentRPM_L, kp, ki, kd, DIRECT);
PID_ATune aTune(&currentRPM_R, &speed_O);


void setup() {
  md.init();
  Serial.begin(9600);
  setupMotorEncoder();
  myPID.SetMode(AUTOMATIC);
  myPID.SetOutputLimits(-350, 350);
  myPID.SetSampleTime(10);
  //  myPID.setTimeStep(1);
  //  myPID.reset();
  //  moveForward(5000);
  tunePID();
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
  double currentSpeed = 25;
  double lastTick_R = 0.0;
  while (tick_R < distance || tick_L < distance) {
    if (((tick_R - lastTick_R > 20) && (currentSpeed < MAX_SPEED)) || currentSpeed == 25) {
      currentSpeed = currentSpeed + 25;
      lastTick_R = tick_R;
    }
    if (myPID.Compute())
      md.setSpeeds(currentSpeed - speed_O, currentSpeed + speed_O);
    Serial.print("R: ");
    Serial.println(tick_R);
    Serial.print("L: ");
    Serial.println(tick_L);
    Serial.print("O: ");
    Serial.println(speed_O);
    //    Serial.print("S: ");
    //    Serial.println(currentSpeed);
  }
  md.setBrakes(400, 400);

}

void tunePID() {
  myPID.SetMode(MANUAL);
  initializeTime();
  initializeTick();
  initializeSpeed();
  speed_O = 0;
  aTune.SetOutputStep(100);
  aTune.SetLookbackSec(20);
  while (1) {
    byte val = (aTune.Runtime());
    Serial.print("RT: ");
    Serial.println(val);
    if (val != 0) {
      kp = aTune.GetKp();
      ki = aTune.GetKi();
      kd = aTune.GetKd();
      Serial.print("P: ");
      Serial.println(kp);
      Serial.print("I: ");
      Serial.println(ki);
      Serial.print("D: ");
      Serial.println(kd);
      myPID.SetTunings(kp, ki, kd);
      break;
    }
    //    myPID.Compute();
    md.setSpeeds(150 - speed_O, 150 + speed_O);
    Serial.print("O: ");
    Serial.println(speed_O);
  }
  aTune.Cancel();
  md.setSpeeds(0, 0);
  myPID.SetMode(AUTOMATIC);

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
  speed_O = 0;
}

void initializeRPM() {
  md.setSpeeds(0, 0);
  currentRPM_R = 0.0;
  currentRPM_L = 0.0;
}


