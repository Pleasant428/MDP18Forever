#include <ArduinoPIDLibrary.h>
#include <EnableInterrupt.h>
#include <DualVNH5019MotorShield.h>

#define LEFT_PULSE 3 // LEFT M1 Pulse
#define RIGHT_PULSE 11 // RIGHT M2 Pulse

const int MOVE_MAX_SPEED = 230;
const int MOVE_MIN_SPEED = 170;
const int TURN_MAX_SPEED = 190;
const int ROTATE_MAX_SPEED = 110;
const int TURN_TICKS_L = 792;
const int TURN_TICKS_R = 789;
const int TENCM_TICKS = 577;
const double DIST_WALL_CENTER_BOX = 2.27;
const double kp = 7.35, ki = 1.25, kd = 0;

int TENCM_TICKS_OFFSET = 0;

double tick_R = 0;
double tick_L = 0;
double speed_O = 0;
double previous_tick_R = 0;
double previous_error = 0;

DualVNH5019MotorShield md;
PID myPID(&tick_R, &speed_O, &tick_L, kp, ki, kd, REVERSE);

//--------------------------Motor Codes-------------------------------
void setupMotorEncoder() {
  md.init();
  pinMode(LEFT_PULSE, INPUT);
  pinMode(RIGHT_PULSE, INPUT);
  enableInterrupt(LEFT_PULSE, leftMotorTime, CHANGE);
  enableInterrupt(RIGHT_PULSE, rightMotorTime, CHANGE);
}

void stopMotorEncoder() {
  disableInterrupt(LEFT_PULSE);
  disableInterrupt(RIGHT_PULSE);
}

void setupPID() {
  myPID.SetMode(AUTOMATIC);
  myPID.SetOutputLimits(-370, 370);
  myPID.SetSampleTime(5);
}

void moveForward(int distance) {
  initializeTick();
  initializeMotor_Start();
  int distance_ticks = cmToTicks(distance);
  double currentSpeed = 0;
  if (distance < 30) {
    currentSpeed = MOVE_MIN_SPEED;
  } else {
    currentSpeed = MOVE_MAX_SPEED;
  }
  double offset = 0;
  long last_tick_R = 0;
  while (tick_R < distance_ticks || tick_L < distance_ticks) {
    if ((tick_R - last_tick_R) >= 10 || tick_R == 0) {
      offset += 0.1;
      last_tick_R = tick_R;
    }
    if (myPID.Compute()) {
      if (offset >= 1 && tick_R != 0)
        md.setSpeeds(currentSpeed + speed_O, currentSpeed - speed_O);
      else
        md.setSpeeds(offset * (currentSpeed + speed_O), offset * (currentSpeed - speed_O));
    }
  }
  initializeMotor_End();
  if (getLastFrontIR2() != 99 && getLastFrontIR2() <= 40 && distance == 10) {
    double changeInFrontIR2 = getLastFrontIR2() - getFrontIR2();
    if (distance - changeInFrontIR2 > 4) {
      moveForward_Pure(3);
      incrementTenCmTicksOffset();
    } else if (distance - changeInFrontIR2 < -4) {
      moveBackwards(3);
      decrementTenCmTicksOffset();
    }
  }
}

void moveForward_Pure(int distance) {
  initializeTick();
  initializeMotor_Start();
  int distance_ticks = cmToTicks(distance);
  double currentSpeed = 0;
  if (distance < 30) {
    currentSpeed = MOVE_MIN_SPEED;
  } else {
    currentSpeed = MOVE_MAX_SPEED;
  }
  double offset = 0;
  long last_tick_R = 0;
  while (tick_R < distance_ticks || tick_L < distance_ticks) {
    if ((tick_R - last_tick_R) >= 10 || tick_R == 0) {
      offset += 0.1;
      last_tick_R = tick_R;
    }
    if (myPID.Compute()) {
      if (offset >= 1 && tick_R != 0)
        md.setSpeeds(currentSpeed + speed_O, currentSpeed - speed_O);
      else
        md.setSpeeds(offset * (currentSpeed + speed_O), offset * (currentSpeed - speed_O));
    }
  }
  initializeMotor_End();
}

void moveBackwards(long distance) {
  initializeTick();
  initializeMotor_Start();
  distance = cmToTicks(distance);
  double currentSpeed = 0;
  if (distance < 30) {
    currentSpeed = MOVE_MIN_SPEED;
  } else {
    currentSpeed = MOVE_MAX_SPEED;
  }
  double offset = 0;
  long last_tick_R = 0;
  while (tick_R < distance || tick_L < distance) {
    if ((tick_R - last_tick_R) >= 10 || tick_R == 0) {
      offset += 0.1;
      last_tick_R = tick_R;
    }
    if (myPID.Compute()) {
      if (offset >= 1 && tick_R != 0)
        md.setSpeeds(-(currentSpeed + speed_O), -(currentSpeed - speed_O));
      else
        md.setSpeeds(-(offset * (currentSpeed + speed_O)), -(offset * (currentSpeed - speed_O)));
    }
  }
  initializeMotor_End();
}

void turnLeft() {
  initializeTick();
  initializeMotor_Start();
  clearLastFrontIR2();
  double currentSpeed = TURN_MAX_SPEED;
  double offset = 0;
  while (tick_R < TURN_TICKS_L || tick_L < TURN_TICKS_L) {
    if (myPID.Compute())
      md.setSpeeds(-(currentSpeed + speed_O), currentSpeed - speed_O);
  }
  initializeMotor_End();
}

void turnRight() {
  initializeTick();
  initializeMotor_Start();
  clearLastFrontIR2();
  double currentSpeed = TURN_MAX_SPEED;
  double offset = 0;

  while (tick_R < (TURN_TICKS_R + 10) || tick_L < (TURN_TICKS_R + 10)) {
    //    offset = computePID();
    if (myPID.Compute())
      md.setSpeeds((currentSpeed + speed_O), -(currentSpeed - speed_O));
  }
  initializeMotor_End();
}

void rotateLeft(long distance) {
  initializeTick();
  initializeMotor_Start();
  double currentSpeed = ROTATE_MAX_SPEED;
  while (tick_R < distance || tick_L < distance) {
    if (myPID.Compute())
      md.setSpeeds(-(currentSpeed + speed_O), currentSpeed - speed_O);
  }
  initializeMotor_End();
}

void rotateRight(long distance) {
  initializeTick();
  initializeMotor_Start();
  double currentSpeed = ROTATE_MAX_SPEED;
  while (tick_R < distance || tick_L < distance) {
    if (myPID.Compute())
      md.setSpeeds((currentSpeed + speed_O), -(currentSpeed - speed_O));
  }
  initializeMotor_End();
}

void alignRight() {
  delay(10);
  double diff = getRightIR1() - getRightIR2();
  while (abs(diff) >= 0.4) {
    if (diff > 0) {
      rotateLeft(abs(diff * 5));
    } else {
      rotateRight(abs(diff * 5));
    }
    delay(20);
    diff = getRightIR1() - getRightIR2();
  }
  delay(10);
}

void alignFront() {
  // Check if able to align front
  delay(10);
  double diff_dis;
  double f1 = getFrontIR1();
  double f2 = getFrontIR2();
  double f3 = getFrontIR3();
  if (getFrontIR1_Block() != getFrontIR3_Block()) {
    if (f1 < f2 && f1 < f3)
    {
      diff_dis = getFrontIR1() - DIST_WALL_CENTER_BOX;
    }
    else if (f2 < f3)
    {
      diff_dis = getFrontIR2() - DIST_WALL_CENTER_BOX;
    }
    else
    {
      diff_dis = getFrontIR3() - DIST_WALL_CENTER_BOX;
    }
    if (diff_dis > 0) {
      if (abs(diff_dis) > 4)
        incrementTenCmTicksOffset();
      moveForward(abs(diff_dis));
    } else {
      if (abs(diff_dis) > 4)
        decrementTenCmTicksOffset();
      moveBackwards(abs(diff_dis));
    }
    return;
  }

  //Align parallel to front
  double diff = getFrontIR1() - getFrontIR3();
  while (abs(diff) >= 0.4) {
    if (diff > 0) {
      rotateLeft(abs(diff * 5));
    } else {
      rotateRight(abs(diff * 5));
    }
    delay(10);
    diff = getFrontIR1() - getFrontIR3();
  }

  //Move to center of 3x3
  diff_dis = getFrontIR1() - DIST_WALL_CENTER_BOX;
  if (diff_dis > 0) {
    if (abs(diff_dis) > 4)
      incrementTenCmTicksOffset();
    moveForward(abs(diff_dis));
  } else {
    if (abs(diff_dis) > 4)
      decrementTenCmTicksOffset();
    moveBackwards(abs(diff_dis));
  }

  //Align parallel to front
  delay(10);
  diff = getFrontIR1() - getFrontIR3();
  while (abs(diff) >= 0.4) {
    if (diff > 0) {
      rotateLeft(abs(diff * 5));
    } else {
      rotateRight(abs(diff * 5));
    }
    delay(10);
    diff = getFrontIR1() - getFrontIR3();
  }
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
  speed_O = 0;
  previous_tick_R = 0;
}

void initializeMotor_Start() {
  md.setSpeeds(0, 0);
  md.setBrakes(0, 0);
}

void initializeMotor_End() {
  md.setSpeeds(0, 0);
  md.setBrakes(400, 400);
  delay(20);
}

void incrementTenCmTicksOffset() {
  TENCM_TICKS_OFFSET += 5;
}

void decrementTenCmTicksOffset() {
  TENCM_TICKS_OFFSET -= 5;
}

long ticksToCm(long ticks) {
  double ret = (double)ticks * 10.0 / (TENCM_TICKS + TENCM_TICKS_OFFSET);
  return ret;
}

long cmToTicks(long cm) {
  double ret = (double)cm * (TENCM_TICKS + TENCM_TICKS_OFFSET) / 10.0;
  return ret;
}
