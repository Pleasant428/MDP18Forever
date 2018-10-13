#include <MsTimer2.h>
#include <ArduinoPIDLibrary.h>
#include <EnableInterrupt.h>
#include <DualVNH5019MotorShield.h>

const int LEFT_PULSE = 3; // LEFT M1 Pulse
const int RIGHT_PULSE = 11; // RIGHT M2 Pulse
const int MOVE_MAX_SPEED = 230;
const int MOVE_MIN_SPEED = 210;
const int TURN_MAX_SPEED = 190;
const int ROTATE_MAX_SPEED = 110;
const int TURN_TICKS_L = 792;
const int TURN_TICKS_R = 789;
const int TENCM_TICKS = 577; // Remember to change in conversion lib too
const double DIST_WALL_CENTER_BOX = 2.27;
//const double kp = 6.73, ki = 1.25, kd = 0; // OPTIMAL
//const double kp = 7.25, ki = 1.05, kd = 0; //THIS
const double kp = 7.35, ki = 1.25, kd = 0;
//const double kp = 4.25, ki = 0.3, kd = 0;
//double kp = 0, ki = 0, kd = 0;
//const double kp = 15, ki = 0, kd = 0;

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

void moveForward(long distance) {
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
    //    offset = computePID();
    if ((tick_R - last_tick_R) >= 10 || tick_R == 0) {
      offset += 0.1;
      last_tick_R = tick_R;
    }
    if (myPID.Compute()) {
      if (offset >= 1)
        md.setSpeeds(currentSpeed + speed_O, currentSpeed - speed_O);
      else
        md.setSpeeds(offset * (currentSpeed + speed_O), offset * (currentSpeed - speed_O));
    }
  }
  initializeMotor_End();
  //Serial.print("R: ");
  //Serial.print(tick_R);
  //Serial.print(" L: ");
  //Serial.print(tick_L);
  //Serial.print(" S: ");
  //Serial.println(speed_O);
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
    //    offset = computePID();
    if ((tick_R - last_tick_R) >= 10 || tick_R == 0) {
      offset += 0.1;
      last_tick_R = tick_R;
    }
    if (myPID.Compute()) {
      if (offset >= 1)
        md.setSpeeds(-(currentSpeed + speed_O), -(currentSpeed - speed_O));
      else
        md.setSpeeds(-(offset * (currentSpeed + speed_O)), -(offset * (currentSpeed - speed_O)));
    }
  }
  initializeMotor_End();
}


//int moveForwardTillWall() {
//  initializeTick();
//  initializeMotor_Start();
//  double currentSpeed = MOVE_MAX_SPEED;
//  double offset = 0;
//
//  while (1) {
//    if (getFrontIR1() <= (DIST_WALL_CENTER_BOX + 2.6) || getFrontIR2() <= (DIST_WALL_CENTER_BOX + 3.3)  || getFrontIR3() <= (DIST_WALL_CENTER_BOX + 2.3) ) {
//      break;
//    }
//    //    offset = computePID();
//    if (myPID.Compute())
//      md.setSpeeds(currentSpeed, ceil(currentSpeed - speed_O));
//
//  }
//  //  md.setSpeeds(0, 0);
//  int tick_save = (tick_R + tick_L) / 2;
//  tick_save = ticksToCm(tick_save);
//  initializeMotor_End();
//  return tick_save;
//}

void turnLeft() {
  initializeTick();
  initializeMotor_Start();
  double currentSpeed = TURN_MAX_SPEED;
  double offset = 0;

  while (tick_R < TURN_TICKS_L || tick_L < TURN_TICKS_L) {
    //    offset = computePID();
    if (myPID.Compute())
      md.setSpeeds(-(currentSpeed + speed_O), currentSpeed - speed_O);
  }
  initializeMotor_End();
}

//void turnLeft(int degree) {
//  initializeTick();
//  initializeMotor_Start();
//  double currentSpeed = TURN_MAX_SPEED;
//  double offset = 0;
//
//  int turn_degree = ceil(ceil(TURN_TICKS / 90.0) * (degree * 0.98));
//  while (tick_R < turn_degree || tick_L < turn_degree) {
//    //    offset = computePID();
//    if (myPID.Compute())
//      md.setSpeeds(-currentSpeed, currentSpeed - speed_O);
//  }
//  initializeMotor_End();
//}

void turnRight() {
  initializeTick();
  initializeMotor_Start();
  double currentSpeed = TURN_MAX_SPEED;
  double offset = 0;

  while (tick_R < (TURN_TICKS_R + 10) || tick_L < (TURN_TICKS_R + 10)) {
    //    offset = computePID();
    if (myPID.Compute())
      md.setSpeeds((currentSpeed + speed_O), -(currentSpeed - speed_O));
  }
  initializeMotor_End();
}

//void turnRight(int degree) {
//  initializeTick();
//  initializeMotor_Start();
//  double currentSpeed = TURN_MAX_SPEED;
//  double offset = 0;
//
//  int turn_degree = ceil(ceil(TURN_TICKS / 90.0) * (degree * 0.98));
//  while (tick_R < (turn_degree) || tick_L < (turn_degree)) {
//    //    offset = computePID();
//    if (myPID.Compute())
//      md.setSpeeds(currentSpeed, -(currentSpeed - speed_O));
//  }
//  initializeMotor_End();
//}

void rotateLeft(long distance) {
  initializeTick();
  initializeMotor_Start();
  double currentSpeed = ROTATE_MAX_SPEED;
  double offset = 0;

  while (tick_R < distance || tick_L < distance) {
    //    offset = computePID();
    if (myPID.Compute())
      md.setSpeeds(-(currentSpeed + speed_O), currentSpeed - speed_O);
  }
  initializeMotor_End();
}

void rotateRight(long distance) {
  initializeTick();
  initializeMotor_Start();
  double currentSpeed = ROTATE_MAX_SPEED;
  double offset = 0;

  while (tick_R < distance || tick_L < distance) {
    //    offset = computePID();
    if (myPID.Compute())
      md.setSpeeds((currentSpeed + speed_O), -(currentSpeed - speed_O));
  }
  initializeMotor_End();
}

void alignRight() {
  delay(100);
  double diff = getRightIR1() - getRightIR2();
  while (abs(diff) >= 0.4) {
    if (diff > 0) {
      rotateLeft(abs(diff * 5));
    } else {
      rotateRight(abs(diff * 5));
    }
    delay(200);
    diff = getRightIR1() - getRightIR2();
  }
  delay(100);
}

void alignFront() {
  delay(100);
  double diff_dis;
  double f1 = getFrontIR1();
  double f2 = getFrontIR2();
  double f3 = getFrontIR3();
  if (getFrontIR1_Block() != getFrontIR3_Block() || getFrontIR3_Block() != getFrontIR2_Block() || getFrontIR1_Block() != getFrontIR2_Block()) {
    if (f1 < f2) {
      if (f1 < f3) {
        diff_dis = getFrontIR1() - (DIST_WALL_CENTER_BOX - 0.5);
      } else {
        diff_dis = getFrontIR3() - DIST_WALL_CENTER_BOX;
      }
    } else {
      if (f2 < f3) {
        diff_dis = getFrontIR2() - DIST_WALL_CENTER_BOX;
      } else {
        diff_dis = getFrontIR3() - (DIST_WALL_CENTER_BOX - 0.5);
      }
    }
    if (diff_dis > 0)
      moveForward(abs(diff_dis));
    else
      moveBackwards(abs(diff_dis));
    return;
  }
  double diff = getFrontIR1() - getFrontIR3();
  //  Serial.println(diff);
  while (abs(diff) >= 0.4) {
    if (diff > 0) {
      rotateLeft(abs(diff * 5));
    } else {
      rotateRight(abs(diff * 5));
    }
    delay(200);
    diff = getFrontIR1() - getFrontIR3();
  }
  delay(200);
  diff_dis = getFrontIR2() - (DIST_WALL_CENTER_BOX  - 0.5);
  if (diff_dis > 0)
    moveForward(abs(diff_dis));
  else
    moveBackwards(abs(diff_dis));
  delay(200);
  diff = getFrontIR1() - getFrontIR3();
  while (abs(diff) >= 0.4) {
    if (diff > 0) {
      rotateLeft(abs(diff * 5));
    } else {
      rotateRight(abs(diff * 5));
    }
    delay(200);
    diff = getFrontIR1() - getFrontIR3();
  }
  delay(100);
}

void followRightWallTillWall() {
  alignRight();
  while (1) {
    if (getFrontIR1() <= (DIST_WALL_CENTER_BOX + 2.6) || getFrontIR2() <= (DIST_WALL_CENTER_BOX + 3.3)  || getFrontIR3() <= (DIST_WALL_CENTER_BOX + 2.3) ) {
      break;
    }
    moveForward(10);
    alignRight();
  }
  alignFront();
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




//--------------------------PID Codes-------------------------------
//double computePID() {
//  double error, integral;
//
//  error = tick_R - tick_L;
//  integral = previous_error + error;
//
//  double p = kp * error;
//  double i = ki * integral;
//  double d = kd * (previous_tick_R - tick_R);
//  double pid = p + i + d;
//
//  previous_tick_R = tick_R;
//
//  return pid;
//}


//--------------------------Check List Codes-------------------------------
//void checkList_A6() {
//  int dist = 150;
//  int dist_moved = moveForwardTillWall();
//  dist = dist - dist_moved;
//  turnLeft();
//  moveForward(30);
//  turnRight();
//  moveForward(40);
//  dist = dist - 40;
//  turnRight();
//  moveForward(30);
//  turnLeft();
//  moveForward(dist);
//}
//
//int moveForwardTillWallA7() {
//  initializeTick();
//  initializeMotor_Start();
//  double currentSpeed = MOVE_MAX_SPEED;
//  double offset = 0;
//  while (1) {
//    if (getFrontIR1() <= (DIST_WALL_CENTER_BOX + 10.6) || getFrontIR2() <= (DIST_WALL_CENTER_BOX + 11.3)  || getFrontIR3() <= (DIST_WALL_CENTER_BOX + 10.3) ) {
//      break;
//    }
//    //    offset = computePID();
//    if (myPID.Compute())
//      md.setSpeeds(currentSpeed, currentSpeed - speed_O);
//  }
//  md.setSpeeds(0, 0);
//  int tick_save = (tick_R + tick_L) / 2;
//  tick_save = ticksToCm(tick_save);
//  initializeMotor_End();
//  return tick_save;
//}
//
//void checkList_A7() {
//  int dist = 150;
//  int dist_moved = moveForwardTillWallA7();
//  dist = dist - dist_moved;
//  turnRight(45);
//  moveForward(38);
//  turnLeft(45);
//  moveForward(10);
//  turnLeft(45);
//  moveForward(28);
//  dist = dist - 54;
//  turnRight(45);
//  moveForward(dist-10);
//}
