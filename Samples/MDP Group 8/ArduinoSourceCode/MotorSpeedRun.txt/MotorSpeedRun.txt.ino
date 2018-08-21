#include <EnableInterrupt.h>

#include "DualVNH5019MotorShield.h"
#include "RunningMedian.h"
#include "EnableInterrupt.h"

//Rotate Left 778 
//Rotate Right 777

#define leftMotor 13 // LEFT MOTOR M1 Pulse B
#define m1PulseA 11 // LEFT MOTOR M1 Pulse A 
#define rightMotor 5 // RIGHT MOTOR M2 Pulse B
#define m2PulseA 3 // RIGHT MOTOR M2 Pulse A

#define front_middle_sensor_pin 0 // front-middle sensor, MDP BOARD PIN PS0
#define front_left_sensor_pin 2   //front-left sensor , MDP BOARD PIN PS2
#define front_right_sensor_pin 5  //front-right sensor , MDP BOARD PIN PS5
#define left_sensor_pin 1         //left long range sensor , MDP BOARD PIN PS1
#define right_fronthug_sensor_pin 4  //right-front sensor , MDP BOARD PIN PS4
#define right_backhug_sensor_pin 3   //right-back sensor , MDP BOARD PIN PS3

#define maxSpeed 380

#define sensorErrorAllowance 0.1
#define distanceErrorAllowance 0.2

#define distanceToWall 10.6 // 10.8  
#define distanceAwayWall 11.8 // 1 CM buffer range between distanceToWall and distanceAwayWall  // 12
#define distanceFromSideWall 11.5 

DualVNH5019MotorShield md;

double timeWidthM1; // Left Motor 1 square wave time width
double timeWidthM2; // Right Motor 1 square wave time width

String motorM1SpeedString; //Left Motor
String motorM2SpeedString; //Right Motor


volatile int countLeft, countRight; // Number of ticks for Left and Right motor
int alignmentCount = 0; 
int motorM1Speed;
int motorM2Speed;
int Speed; // Speed for Motor to be set.
int tickError;
int sensorError; // Difference (-) between two sensors depending on direction
int stringIndex = 0;
int numOfGrid = 0;
int avgCount = 0;
int Integral = 0;
int Derivative = 0;
int previousError = 0;
int leftSpeed = 0;
int rightSpeed = 0;

String stringToSend;
String command = "";
char character;
char c;
char test;
const char delim[2] = "|"; //Delimiter for the string to decode
  
boolean aligning = false; // False = Send Sensor Data, True = No sending of Sensor data
boolean fastestPath = false; // False = Send Sensor Data, True = No sending of Sensor data
boolean calibrateBEx = true;


void setup()
{
  Serial.begin(9600);
  countLeft = 0, countRight = 0;
  md.init();  // Motor Driver Shield initialization
  pinMode(leftMotor, INPUT);
  pinMode(rightMotor, INPUT);
  enableInterrupt(leftMotor, leftMotorInc, CHANGE);  // Measure the high / low (change) of the left motor pulses
  enableInterrupt(rightMotor, rightMotorInc, CHANGE); // Measure the high / low (change) of the right motor pulses
  calibrateBeforeExploration();  
} 

void loop()
{
//    moveForward(496, 350);
//    delay(500);
//  calibrateIfFoundCorner();
//    rotateLeft(765, 300);
//    delay(1000);
//  turnRight();
//  delay(1000);
//    turnRight();
//    delay(1000);
//  turnBackward();
//  delay(1000);
//  turnRight();
//  delay(1000);
//   md.setSpeeds(0, 400);
//   calculatingTimeWidthOfMotor2();
//    moveForwardGrid(1);
//    delay(1000);
// alignFrontAngle();
//  calibrateEveryMove();
//      moveForwardGrid(1);
//    md.setSpeeds(400, 0);
//        delay(1000);
//        rotateRight(788,380);
//        delay(500);
//        delay(500);
//        moveForward(540, 380);
//        delay(1000);
//       float distance1 = getMedianDistance(left_sensor_pin, 20150);
//       Serial.print("Left sensor");
//       Serial.println(distance1);
//     float distance2 = getMedianDistance(right_fronthug_sensor_pin, 1080);
//     Serial.print("Right Front Hug sensor ");
//     Serial.println(distance2);
//     float distance4 = getMedianDistance(right_backhug_sensor_pin, 1080);
//     Serial.print("Right Back Hug Sensor");
//     Serial.println(distance4);
//      float distance5 = getMedianDistance(front_left_sensor_pin, 1080);
//      Serial.print("Front Left:  ");
//      Serial.println(distance5);
//      float distance6 = getMedianDistance(front_middle_sensor_pin, 1080);
//      Serial.print("Front Middle: ");
//      Serial.println(distance6);
//      float distance7 = getMedianDistance(front_right_sensor_pin, 1080);
//      Serial.print("Front Right: ");
//      Serial.println(distance7);
//      readSensors();
//      delay(1000);
//      delay(1000);
//      rotateLeft(752, 380);
//      delay(1000);
//      (790, 380);
//      delay(1000);
//      moveForward(545, 380);
//      delay(500);
      while (Serial.available()){
      character = Serial.read();
      if (character == '\n' || character == '\0')
        break;
      else {
        command+= character;
        delay(4);
      }
     }
     
     while (command.length() > stringIndex){
      while(command[stringIndex] != '\n' && command[stringIndex] != '\0'){
        switch(command[stringIndex]){
          //Received a command from Algo / Rpi
          case 'A': while (command[stringIndex] != '|'){
                      stringIndex++;
                    }
                    stringIndex++;
                    while (command[stringIndex] != '|'){
                      stringIndex++;
                    }
                    break;
          //Move Forward by Grid
          //Arduino|Algorithm|L|Nil Arduino|Algorithm|F|01
             case 'F': if (!fastestPath){
                       //MoveForwardGrid 2 grids = 1150; 3 grids = 1750; 4 grids = 2350;
                      if (command[++stringIndex] == '|'){ // Hitting the delimiter 
                        stringIndex++; // Therefore increment by 1 to move to next position
                        if (command[stringIndex] == '0'){
                        numOfGrid = command[++stringIndex] - 48;
                        moveForwardGrid(numOfGrid);
                        }
                        else {
                          stringIndex++;
                          moveForwardGrid(10 + (command[stringIndex] - 48));  
                        }
                      }
                      calibrateEveryMove();
                    }
                     else { 
                      // For fastest Path Arduino|Algorithm|X|F01 No calibration allowed
                      if (command[++stringIndex] == '0'){
                        numOfGrid = command[++stringIndex] - 48;
                        moveForwardGrid(numOfGrid);
                        }
                        else {
                          stringIndex++;
                          moveForwardGrid(10 + (command[stringIndex] - 48));
                        }
                   }
                    break;
           case 'L': turnLeft();
                    if (!fastestPath){
                     calibrateEveryMove();
                     //calibrate side / front if possible as well
                    }
                     break;
           case 'R': turnRight();
                    if (!fastestPath){
                     calibrateEveryMove();
                    }
                     break;
           case 'X': fastestPath = true; // X is sent before all commands for fastest Path
                     stringIndex++; // Hitting delimiter and move on to next command on the long string |F01LF04R etc.
                     break;
           case 'S': readSensors();
                     break;
           case 'T': turnBackward();
                    if (!fastestPath){
                     calibrateEveryMove();
                    } 
                     break;
           case 'C':  
                      alignFrontAngle();
                      delay(25);
                      rotateLeft(772, 300); //777 without decceleration // 653
                      alignSideAngle();
                      alignmentCount = 0;
                      delay(25);
                     readSensors();
                     break;
           case 'W': flushForward();
                     break;
                    
        } // End of Switch Case
        
        //While not end of String
        stringIndex++;
        
      }
        fastestPath = false; 
        stringIndex++;
        //End of String 
        //Reset the entire command to empty and start from index 0
        
      }
      stringIndex = 0;
      command = ""; 
}
     

void leftMotorInc(){
  countLeft++;
}

void rightMotorInc(){
  countRight++;
  // Serial.print("Right Count: ");
  // Serial.println(countRight);
}

void resetMotorInc(){
  countLeft = 0;
  countRight = 0;
}

void calculatingTimeWidthOfMotor1(){
  timeWidthM1 = pulseIn(leftMotor, HIGH);
  countLeft++;
  Serial.println(timeWidthM1 * 2);
}

void calculatingTimeWidthOfMotor2(){
  timeWidthM2 = pulseIn(rightMotor, HIGH);
  countRight++;
  Serial.println(timeWidthM2 * 2);
}


double tuneWithPID(){
  int error = (countLeft - countRight); 
  Integral = Integral + error;
  Derivative = error - previousError;
  return 80 * error + 12/13*Integral + 4/10*Derivative; //20 //30 // 70 // Integral = 10/11 // 4/10
}

double tuneWithP(){
  int error = (countLeft - countRight);
  return 11*error;
}

void moveForward(int distance, int Speed){ // Distance to travel
  resetMotorInc();
  while (countLeft < distance || countRight < distance){
    tickError = tuneWithPID();
    md.setSpeeds((Speed) - tickError, (Speed) + tickError);
    previousError = tickError;
  }
  md.setBrakes(400,400);
}

void moveForward1Grid(int distance, int Speed){ // Distance to travel 
  Speed = 350;
  resetMotorInc();
  while (countLeft < distance || countRight < distance){
    tickError = tuneWithPID();
    md.setSpeeds((Speed) - tickError, (Speed) + tickError);
  }
  md.setBrakes(400,400);
}

void moveBackward(int distance, int Speed){ // Distance to travel 
  resetMotorInc();
  while (countLeft < distance || countRight < distance){
    tickError = tuneWithPID();
    md.setSpeeds(-(Speed - tickError), -(Speed + tickError));
  }
  md.setBrakes(400,400);
}


void rotateRight(int distance, int Speed) { // 3325 for 360 degree
  resetMotorInc();
  while(countLeft < distance || countRight < distance){
  tickError = tuneWithPID(); //if positive, left is faster than right
  md.setSpeeds(Speed - tickError, -(Speed + tickError));  // Left, Right
//  md.setSpeeds(Speed, -Speed);
  }
  md.setBrakes(389,389);
  delay(75);
//      Serial.print("Left count: ");
//      Serial.println(countLeft);
//      Serial.print("Right count: ");
//      Serial.println(countRight);
//  Serial.println("Algorithm|Arduino|FinishedAction|Nil");
}


void rotateLeft(int distance, int Speed) { // 3325 for 360 degree
  resetMotorInc();
  while(countLeft<distance || countRight < distance){
  tickError = tuneWithPID(); //if positive, left is faster than right
  md.setSpeeds(-(Speed - tickError) , (Speed + tickError));  // Left, Right
  }
  md.setBrakes(400,400);
  delay(75);
  
}

void turnBackward(){
  rotateLeft(1587,300); //1650 a bit off so 1645 from 1655 22 march
  md.setBrakes(400,400);
  delay(75);
//  turnBackwardCorrection(1605);
  if (!fastestPath && !aligning){ // Tell Algo that robot is ready by sending readSensor if during exploration
    calibrateEveryMove();
    delay(50);
    readSensors();
    delay(20);
  }
  else{ // For fastest Path
    delay(150);
    Serial.println("Algorithm|Arduino|FinishedAction|Nil");
  }
  
}

void turnLeft() {
   rotateLeft(772, 300); //777 without decceleration // 653
   md.setBrakes(400,400);
   delay(75);
//   Serial.print("Left Count: ");
//   Serial.println(countLeft);
//   Serial.print("Right Count: ");
//   Serial.println(countRight);
//   md.setBrakes(389, 389);
//   turnLeftCorrection(819);
//   Serial.print("After Left Count: ");
//   Serial.println(countLeft);
//   Serial.print("After Right Count: ");
//   Serial.println(countRight);
   if (!fastestPath && !aligning){ // Tell Algo that robot is ready by sending readSensor if during exploration
     calibrateEveryMove();
     delay(50);
     readSensors();
     delay(20);
   }
   else{ // For fastest Path
     delay(150);
     Serial.println("Algorithm|Arduino|FinishedAction|Nil");
   }
//  rotateLeft(762,380);
//  md.setBrakes(400, 400);
//  if (!fastestPath && !aligning){
//     calibrateEveryMove();
//     delay(150);
//     readSensors();
//   }
//   else{
//     delay(75);
//     Serial.println("Algorithm|Arduino|FinishedAction|Nil");
//  }
}


void turnRight() {
   rotateRight(760, 300); //777
   md.setBrakes(400,400);
   delay(75);
//   md.setBrakes(389, 389);
//     Serial.print("Left count: ");
//      Serial.println(countLeft);
//      Serial.print("Right count: ");
//      Serial.println(countRight);
//     turnRightCorrection(825);
//     delay(10);
//     Serial.print("After Left count: ");
//     Serial.println(countLeft);
//     Serial.print("After Right count: ");
//     Serial.println(countRight);
 
   if (!fastestPath && !aligning){ // Tell Algo that robot is ready by sending readSensor if during exploration
     calibrateEveryMove();
     delay(50);
     readSensors();
     delay(20);
     
   }
   else{ // For fastest Path
     delay(150);
     Serial.println("Algorithm|Arduino|FinishedAction|Nil");
  }
}

float readSensor(int IRpin, int model) {
  float sensorValue = analogRead(IRpin);
  float distance;
  if (model == 1080)  //for 10-80cm sensor
    distance = 12343.85 * pow(sensorValue, -1.15); // 10-80cm // 8.50 is directly infront 
    //pin 0:f < 22 is 1 grid away < 32 is 2 grid away < 42 is 3 grid away 
    //pin 2: f<22 if 1 grid away <32 if 2 grid away <42 if 3 grid away
    //pin5: f<22 if 1 grid away <32 if 2 grid away <42 if 3 grid away
    //pin 4: f<23 if 1 grid away <35 if 2 grid away <47 if 3 grid 
    //pin 3: f<22 if 1 grid away <32 if 2 grid away <42 if 3 grid 
    //pin 1: f<24 if 1 grid away <33 if 2 grid away <43 if 3 grid <55 if 4 grid <75 if 5 grid (fluctuate abit) 
    
  if (model == 20150) //for 20-150cm sensor
    distance = 30431 * pow(sensorValue, -1.169); //20-150cm
    
  return distance;
}


float getMedianDistance(int IRpin, int model) {   //read each sensor ~5msec
  RunningMedian samples = RunningMedian(21);
  for (int i = 0; i < 21; i ++)
    samples.add(readSensor(IRpin, model));

  if (IRpin == right_fronthug_sensor_pin) // Right Back Hug sensor more accurate than the front, therefore need offset front.
    return samples.getMedian() - 0.6; //1.2 //0.2 
  if (IRpin == front_right_sensor_pin)
    return samples.getMedian() + 0.3;  //0.1 // Left sensor is more accurate so offset needed to be added to right sensor.
    
  if (IRpin == left_sensor_pin) { //model == 20150
    if (samples.getMedian() < 20)       return samples.getMedian();
    else if (samples.getMedian() < 27)  return samples.getMedian();
    else if (samples.getMedian() < 42)  return samples.getMedian();
    else if (samples.getMedian() < 48)  return samples.getMedian();
    else                                return samples.getMedian();
  }
  return samples.getMedian();
}

/*Front Left Sensor converting distance to grid*/
/*8 MARCH THURSDAY 11:03AM CAN*/
int getObstacleGridsAwayFL() {   //front left sensor // pin 2 
  int distance = getMedianDistance(front_left_sensor_pin, 1080);
  if (distance < 15)        return 1;  
  else if (distance < 26)   return 2;
  else return -1;
}

/*Front Right Sensor converting distance to grid*/
int getObstacleGridsAwayFR() {   //front right sensor // pin 5
  int distance = getMedianDistance(front_right_sensor_pin, 1080);
  if (distance < 15)        return 1;
  else if (distance < 26)   return 2;
  else return -1;
}

/*Front Middle Sensor converting distance to grid*/
int getObstacleGridsAwayFM() {   //front middle sensor // pin 0 
  int distance = getMedianDistance(front_middle_sensor_pin, 1080);
  if (distance < 16)        return 1; // 15 - 16
  else if (distance < 28)   return 2; // 26 - 28
  else return -1;
}

/*Right Hug front Sensor converting distance to grid*/
int getObstacleGridsAwayRHF() {   //right hug front sensor pin 4
  int distance = getMedianDistance(right_fronthug_sensor_pin, 1080);
  if (distance < 15)        return 1;
  else if (distance < 23)   return 2;
  else return -1;
}

/*Right Hug Back Sensor converting distance to grid*/
int getObstacleGridsAwayRHB() {   //right hug back sensor pin 3
  int distance = getMedianDistance(right_backhug_sensor_pin, 1080);
  if (distance < 15)         return 1;
  else if (distance < 23)   return 2;
  else return -1;
}

/*Left side Long Range Sensor converting distance to grid*/
int getObstacleGridsAwayL() {   //left sensor // pin 1
 int distance = getMedianDistance(left_sensor_pin, 20150);
  if (distance < 22)   return 1; //22 low 
  else if (distance < 27)   return 2; //27 low
  else if (distance < 36)   return 3; //36 low
  else if (distance < 46)   return 4; //46 low
  else return -1;
}

/*Sensor Reading from left to right in a circle*/
void readSensors() {      //cost ~24msec to execute
//  if (!calibrateBEx){ //If it is not calibrateBEx then read sensor
    stringToSend += "Algorithm|Arduino|SensorData|"; 
    stringToSend += getObstacleGridsAwayL();
    stringToSend += ",";
    stringToSend += getObstacleGridsAwayFL();
    stringToSend += ",";
    stringToSend += getObstacleGridsAwayFM();
    stringToSend += ",";
    stringToSend += getObstacleGridsAwayFR();
    stringToSend += ",";
    stringToSend += getObstacleGridsAwayRHF();
    stringToSend += ",";
    stringToSend += getObstacleGridsAwayRHB();
    Serial.println(stringToSend);
    stringToSend = "";
  }
//  else{
//    calibrateBEx = false;
//  }


/*Aligning parallel against side wall maintaining certain distance*/
void alignSideAngle(){
  aligning = true;
  Speed = 60; // original 82
  resetMotorInc();
  if (sideCanAlign()){
  while ((sensorError = getMedianDistance(right_backhug_sensor_pin, 1080) - getMedianDistance(right_fronthug_sensor_pin, 1080)) >= sensorErrorAllowance){
    // Robot Tilted right, Rotate Left until parellel to right wall
    tickError = tuneWithP();
    md.setSpeeds(-(Speed - tickError), (Speed+tickError));
    }
  
  resetMotorInc();
  
  while ((sensorError = getMedianDistance(right_backhug_sensor_pin, 1080) - getMedianDistance(right_fronthug_sensor_pin, 1080)) <= -sensorErrorAllowance){
    // Robot Tilted right, Rotate Left until parellel to right wall
    tickError = tuneWithP();
    md.setSpeeds((Speed - tickError), -(Speed+tickError));

    }
  
  }
  md.setBrakes(400,400);
  delay(75); //Just Added
  if (getMedianDistance(right_backhug_sensor_pin, 1080) > (distanceFromSideWall + 1) || getMedianDistance(right_backhug_sensor_pin, 1080) < (distanceFromSideWall - 1.5) ) { //if distance of robot side from wall too near or too far, turn and do front alignment.
    rotateRight(760,300);
    distanceFromWall();
    delay(75);
    rotateLeft(772,300);
  }
  if (sideCanAlign()){
    resetMotorInc();
    while ((sensorError = getMedianDistance(right_backhug_sensor_pin, 1080) - getMedianDistance(right_fronthug_sensor_pin, 1080)) >= sensorErrorAllowance){
      // Robot Tilted right, Rotate Left until parellel to right wall
      tickError = tuneWithP();
      md.setSpeeds(-(Speed - tickError), (Speed+tickError));
      }
    
    resetMotorInc();
    
    while ((sensorError = getMedianDistance(right_backhug_sensor_pin, 1080) - getMedianDistance(right_fronthug_sensor_pin, 1080)) <= -sensorErrorAllowance){
      // Robot Tilted right, Rotate Left until parellel to right wall
      tickError = tuneWithP();
      md.setSpeeds((Speed - tickError), -(Speed+tickError));
      }
    }
  md.setBrakes(400,400);
  delay(75); //Just Added
  aligning = false;
  
}

/*Aligning parallel against side wall maintaining certain distance*/
void alignFrontAngle(){
  alignmentCount++;
  if (alignmentCount > 2){  //Change from 1 Changed 20th March
    return;
  }
  Speed = 60;
  resetMotorInc();
  if (frontCanAlign()){
  while ((sensorError = getMedianDistance(front_left_sensor_pin, 1080) - getMedianDistance(front_right_sensor_pin,1080)) <= -sensorErrorAllowance){
    // Robot tilted right, rotate left
    tickError = tuneWithPID();
    md.setSpeeds(-(Speed - tickError), (Speed+tickError));
    
  }
  resetMotorInc();
  while ((sensorError = getMedianDistance(front_left_sensor_pin, 1080) - getMedianDistance(front_right_sensor_pin,1080)) >= sensorErrorAllowance){
    // Robot tilted left, rotate right
    tickError = tuneWithPID();
    md.setSpeeds((Speed - tickError), -(Speed + tickError));

    }
  }
  md.setBrakes(400,400);
  delay(75); //Just added
  if (getMedianDistance(front_left_sensor_pin, 1080) > distanceToWall + 1.8 + distanceErrorAllowance || getMedianDistance(front_left_sensor_pin, 1080) < distanceToWall - 1.8 - distanceErrorAllowance)
      distanceFromWall();
  
  
//  delay(50); //just commmented
}


void distanceFromWall(){ 
  /* Whenever too close to wall / too far from wall*/
  resetMotorInc();
  while (getMedianDistance(front_left_sensor_pin, 1080) <= distanceToWall - 0.5 || getMedianDistance(front_right_sensor_pin,1080) <= distanceToWall - 0.5){
    moveBackward(70, 80); // 40 Speed Works // 60 also works // Intially 65 Changed by me on Tuesday Night 20th March
    delay(15); //let motor continously run for 0.001 sec before procceeding with next instruction
  }
  resetMotorInc();
  while (getMedianDistance(front_left_sensor_pin, 1080) >= distanceToWall + 1.5 || getMedianDistance(front_right_sensor_pin, 1080) >= distanceToWall + 1.5){
    moveForward(130,80); 
    delay(15);
    
   }
  
  md.setBrakes(400,400);
  delay(75); //Just added
  alignFrontAngle();
}

boolean frontCanAlign() { 
  /* Make sure both sensor returns 1 to 
     show they are the same distance from front*/
  if ( (getObstacleGridsAwayFL() == 1) && (getObstacleGridsAwayFR() == 1) ){
    return true;
  }
  else
    return false;
}

boolean sideCanAlign() {
  /* Make sure both sensor returns 1 to 
     show they are the same distance from side*/
  if ( (getObstacleGridsAwayRHF() == 1) && (getObstacleGridsAwayRHB() == 1) )
    return true;
  else
    return false;
}

boolean leftSideCanAlign(){
  if (getObstacleGridsAwayL() == 1){
    return true;
  }
  else 
    return false;
}

void uTurn(){
  if (leftSideCanAlign() && sideCanAlign() && frontCanAlign()){
    turnBackward();
  }
}


void calibrateBeforeExploration(){ 
   //Face South for Start calibration
   if (frontCanAlign() && sideCanAlign()){
    rotateRight(760, 300); //777
    alignFrontAngle();
    delay(1000);
    rotateLeft(772, 300); //777 without decceleration // 653
    alignFrontAngle();
    delay(1000);
    rotateLeft(772, 300); //777 without decceleration // 653
    delay(100);
    alignmentCount = 0;
    alignSideAngle();
   }
}

void calibrateIfFoundCorner(){  
  // Called after every movement to calibrate 
    // alignmentCount = 0;  
    alignFrontAngle();
    alignmentCount = 0;  
    alignSideAngle();
    
}

void calibrateAgainstRightWall(){
    alignSideAngle();
}

void calibrateAgainstFrontWall(){
    alignFrontAngle();
}

void calibrateEveryMove(){
  if (frontCanAlign() && sideCanAlign()){   
  calibrateIfFoundCorner();
  }
  if (frontCanAlign()){
  calibrateAgainstFrontWall();
  }
  if (sideCanAlign()){
  calibrateAgainstRightWall();
  }
}


void moveForwardGrid(int numOfGrids){
  if (numOfGrids > 1 ){
    moveForward(20,115);
    moveForward(493 + 600 *(numOfGrids - 1), 380); // 538 on sunday night // 538 velcro battery  // 545 without velcro battery
    md.setBrakes(400,400);
     if (!fastestPath && !aligning){
         calibrateEveryMove();
         delay(50); // Small delay for sensor reading
         readSensors();
       }
     else{
      // For fastest Path
      delay(100);
      Serial.println("Algorithm|Arduino|FinishedAction|Nil");
     }
  }
  else {
    //Move forward by 1 grid
    moveForward(20, 115);
    moveForward1Grid(494, 350); //539 //424 //426
    md.setBrakes(400,400);
    delay(75);
    // // Serial.print("Left count: ");
    // // Serial.println(countLeft);
    // // Serial.print("Right count: ");
    // // Serial.println(countRight);
    // moveForwardCorrection(580);
    // Serial.print("After Left count: ");
    // Serial.println(countLeft);
    // Serial.print("After Right count: ");
    // Serial.println(countRight);
    if (!fastestPath){
     calibrateEveryMove();
     delay(50); // Small delay for sensor reading
     readSensors();
    }
    else{
     // For fastest Path
     delay(100);
     Serial.println("Algorithm|Arduino|FinishedAction|Nil");
    }
  }

}



// void moveForwardCorrection(int distance) {
//   Speed = 80; //125
//   while ( countRight < distance && countLeft < distance ) {    //run until either one wheel reaches the tick
//     tickError = tuneWithPID();
//     Serial.println(tickError);
//     md.setSpeeds(Speed - tickError, Speed + tickError);
//     delay(10);  
//     md.setBrakes(400, 400);
//     delay(5);
//   }
 
//   while (countLeft < distance ) {
//     md.setSpeeds(0, Speed);
//     delay(10);  
//     md.setBrakes(400, 400);
//     delay(5);
//   }
//   md.setBrakes(400, 400);
//   while (countRight < distance) {
//     md.setSpeeds(Speed, 0);
//     delay(10);  
//     md.setBrakes(400, 400);
//     delay(5);
//   }
//   md.setBrakes(400, 400);
  
// }


//void turnBackwardCorrection(int distance){
//  Speed = 100;
//   while ( countRight < distance && countLeft < distance ) {    //run until either one wheel reaches the tick
//     tickError = tuneWithP();
//     md.setSpeeds(-(Speed - tickError), (Speed + tickError) );
//     delay(15);  
//     md.setBrakes(400, 400);
//     delay(5);
//   }
//
//   while (countLeft < distance ) {
//     md.setSpeeds(-Speed, 0);
//     delay(15); 
//     md.setBrakes(400, 400);
//     delay(5);
//   }
//
//   while (countRight < distance) {
//     md.setSpeeds(0, Speed);
//     delay(15); //20
//     md.setBrakes(400, 400);
//     delay(5);
//   }
//}


void flushForward(){
  while (!frontCanAlign()){
    moveForward(120, 300);
  }
  md.setBrakes(400,400);
  readSensors();
}

 void turnLeftCorrection(int distance) {
   leftSpeed = 120;
   rightSpeed = 100;
   while ( countRight < distance && countLeft < distance ) {    //run until either one wheel reaches the tick
     tickError = tuneWithP();
     md.setSpeeds(-(leftSpeed - tickError), (rightSpeed + tickError) );
     delay(15);  
     md.setBrakes(400, 400);
     delay(5);
   }

   while (countLeft < distance) {
     md.setSpeeds(-leftSpeed, 0);
     delay(15); 
     md.setBrakes(400, 400);
     delay(5);
   }

   while (countRight < distance) {
     md.setSpeeds(0, rightSpeed);
     delay(15); //20
     md.setBrakes(400, 400);
     delay(5);
   }
 }
//
 void turnRightCorrection(int distance) {
   leftSpeed = 120;
   rightSpeed = 100;
   while ( countRight < distance && countLeft < distance ) {    //run until either one wheel reaches the tick
     tickError = tuneWithP();
     md.setSpeeds((leftSpeed - tickError), -(rightSpeed + tickError) );
     delay(15);
     md.setBrakes(400, 400);
     delay(20);
   }
   while (countLeft < distance ) {
     md.setSpeeds(leftSpeed, 0);
     delay(15);
     md.setBrakes(400, 400);
     delay(5);
   }

   while (countRight < distance) {
     md.setSpeeds(0, -rightSpeed);
     delay(15);
     md.setBrakes(400, 400);
     delay(5);
   }
 }
//
//
//
//
//
//
//
//

