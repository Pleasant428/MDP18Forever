#include <AutoPID.h>
#include <EnableInterrupt.h>
#include <DualVNH5019MotorShield.h>

const int LEFT_PULSE = 3; // LEFT M1 Pulse A
const int RIGHT_PULSE = 11; // RIGHT M2 Pulse A
const double MAX_SPEED = 250;
const double START_SPEED = 25;
const double SPEED_STEP = 30;
const double SPEED_STEP_TICKS = 15;


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

DualVNH5019MotorShield md;
AutoPID myPID(&tick_R, &tick_L, &speed_O, -350, 350, 4, 0.1, 0.025);
//AutoPID myPID(&currentRPM_R, &currentRPM_L, &speed_O, -350, 350, 1.86, 0.04, 0);


void setup() {
  md.init();
  Serial.begin(9600);
  setupMotorEncoder();
  myPID.setTimeStep(1);
  myPID.reset();
  moveForward(5000);

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
  initializeRPM();
  double currentSpeed = START_SPEED;
  double lastTick_R = 0.0;
  while (tick_R < distance || tick_L < distance) {
    if (((tick_R - lastTick_R > SPEED_STEP_TICKS) && (currentSpeed < MAX_SPEED)) || currentSpeed == START_SPEED) {
      currentSpeed = currentSpeed + SPEED_STEP;
      lastTick_R = tick_R;
    }
    myPID.run();
    md.setSpeeds(currentSpeed - speed_O, currentSpeed + speed_O);
    Serial.print("R: ");
    Serial.print(tick_R);
    Serial.print(" L: ");
    Serial.print(tick_L);
    Serial.print(" O: ");
    Serial.print(speed_O);
    Serial.print(" S: ");
    Serial.println(currentSpeed);
  }
  myPID.stop();
  md.setSpeeds(0, 0);
  md.setBrakes(400, 400);
  initializeTime();
  initializeTick();
  initializeSpeed();
  initializeRPM();
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


