#include "EnableInterrupt.h"
#include "DualVNH5019MotorShield.h"

DualVNH5019MotorShield md;

const int M1A = 3;
const int M1B = 5;
const int M2A = 11;
const int M2B = 13;

const int TICKS_FOR_RAMP = 100;
const int SPEED = 300;
const int DURATION_1CM = 266;
const int DURATION_90 = 373;
const int DURATION_180 = 810;
const int DURATION_360 = 1574;

const double KP = 10;
const double KI = 0.1;
const double KD = 0.025;

double m1ticks = 0;
double m2ticks = 0;
double previous_m1ticks = 0;

double previous_error = 0;

double m1pid_value_percentage = 0;
double m1pid_value = 0;
double m1pid_previous = 0;
double m1error = 0;
double m1previous_error = 0;
double m1previous_error2 = 0;

double m2pid_value_percentage = 0;
double m2pid_value = 0;
double m2pid_previous = 0;
double m2error = 0;
double m2previous_error = 0;
double m2previous_error2 = 0;

void m1edge() 
{
  m1ticks++;
}

void m2edge() 
{
  m2ticks++;
}

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  Serial.println("Motor RPM:");
  enableInterrupt(M1B, m1edge, RISING);
  //enableInterrupt(M1B, m1edge, CHANGE);
  enableInterrupt(M2B, m2edge, RISING);
  //enableInterrupt(M2B, m2edge, CHANGE);
  md.init();
}

void loop() {
  robotForward(10000);

}

void robotStart()
{
  md.setM1Speed(SPEED);
  md.setM2Speed(SPEED);
}

void robotStop() 
{
  md.setM1Speed(0);
  md.setM2Speed(0);
}

void robotForward(double duration) { 
  double pid;
  
  m1ticks = 0;
  m2ticks = 0;
  previous_m1ticks = 0;
  previous_error = 0;

  while (m1ticks < min(TICKS_FOR_RAMP, duration)) {
    for (int i = 0; i <= SPEED; i +=50) {
      pid = computePID();
      md.setSpeeds ((i + pid), (i - pid));
      Serial.println(i);
      delay(30);
    }
  }
  while (m1ticks > TICKS_FOR_RAMP && m1ticks <= duration) {
    pid = computePID();
    md.setSpeeds ((SPEED + pid), (SPEED - pid));
    Serial.print("m1ticks");
    Serial.println(m1ticks);
    Serial.print("m2ticks");
    Serial.println(m2ticks);
  }

  while(1){
    md.setBrakes(400, 400);
  }
}

void robotRight(double duration) { 
  double pid;
  
  m1ticks = 0;
  m2ticks = 0;
  previous_m1ticks = 0;
  previous_error = 0;

  while (m1ticks < min(TICKS_FOR_RAMP, duration)) {
    for (int i = 0; i <= SPEED; i +=50) {
      pid = computePID();
      md.setSpeeds (-(i + pid), (i - pid));
      Serial.println(i);
      delay(30);
    }
  }
  while (m1ticks > TICKS_FOR_RAMP && m1ticks <= duration) {
    pid = computePID();
    md.setSpeeds (-(SPEED + pid), (SPEED - pid));
    Serial.print("m1ticks");
    Serial.println(m1ticks);
    Serial.print("m2ticks");
    Serial.println(m2ticks);
  }

  while(1){
    md.setBrakes(400, 400);
  }
}

void robotLeft(double duration) { 
  double pid;
  
  m1ticks = 0;
  m2ticks = 0;
  previous_m1ticks = 0;
  previous_error = 0;

  while (m1ticks < min(TICKS_FOR_RAMP, duration)) {
    for (int i = 0; i <= SPEED; i +=50) {
      pid = computePID();
      md.setSpeeds ((i + pid), -(i - pid));
      Serial.println(i);
      delay(30);
    }
  }
  while (m1ticks > TICKS_FOR_RAMP && m1ticks <= duration) {
    pid = computePID();
    md.setSpeeds ((SPEED + pid), -(SPEED - pid));
    Serial.print("m1ticks");
    Serial.println(m1ticks);
    Serial.print("m2ticks");
    Serial.println(m2ticks);
  }

  while(1){
    md.setBrakes(400, 400);
  }
}

double computePID() {
  double kp, ki, kd, p, i, d, pid, error, integral;

  kp = KP;
  ki = KI;
  kd = KD;

  error = m1ticks - m2ticks;
  integral = previous_error + error;
  
  p = kp * error;
  i = ki * integral;
  d = kd * (previous_m1ticks - m1ticks);
  pid = p + i + d;
  
  previous_m1ticks = m1ticks;

  return pid;
}

