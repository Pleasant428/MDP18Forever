
void setup() {
  setupSerialConnection();
  setupMotorEncoder();
  setupSensorInterrupt();
  setupPID();
  delay(20);

}

boolean readSomething = false;
void loop() {
  delay(2);
  if (!Serial) {
    Serial.println("Waiting for connection");
  }
  //  getFrontIR1();
  //  getFrontIR3();
//  returnSensorReading_Raw();
  if (Serial.available() > 0) {
    char command = '0';
    int value = -1;
    char arr[15];
    int c = 0;
    char hold = Serial.read();
    if (hold == '[') {
      Serial.println('R');
      Serial.flush();
      delay(1);
      hold = Serial.read();
      while (hold != ']') {
        arr[c] = hold;
        c ++;
        hold = Serial.read();
        delay(2);
      }
    } else {
      while (Serial.available() > 0) {
        if ((char)Serial.peek() == '[' )
          return;
        Serial.println("DISCARDING");
        Serial.read();
        delay(1);
        return;
      }
    }
    command = arr[8];
    char value_ca[c - 10 + 1];
    for (int g = 10; g < c; g++) {
      value_ca[g - 10] = arr[g];
    }
    value_ca[c - 10] = '\0';
    value = atoi(value_ca);
    //    Serial.println(command);
    //    Serial.println(value);

    // Alg|Ard|0|{1-10} (Steps) [Alg|Ard|0|3]
    //2nd Character of the Array is the Command

    // 0 : FORWARD
    // 1: TURN_LEFT
    // 2: TURN_RIGHT
    // 3: BACKWARD
    // 4: ALIGN_FRONT
    // 5: ALIGN_RIGHT
    // 6: SEND_SENSORS

    switch (command) {
      case '0':
        moveForward(value * 10);
        delay(10);
        returnSensorReading_Raw();
        break;
      case '1':
        for (int k = 0; k < value; k++) {
          turnLeft();
        }
        delay(10);
        returnSensorReading_Raw();
        break;
      case '2':
        for (int k = 0; k < value; k++) {
          turnRight();
        }
        delay(10);
        returnSensorReading_Raw();
        break;
      case '3':
        moveBackwards(value * 10);
        delay(10);
        returnSensorReading_Raw();
        break;
      case '4':
        alignFront();
        returnSensorReading_Raw();
        break;
      case '5':
        alignRight();
        returnSensorReading_Raw();
        break;
      case 'S':
        returnSensorReading_Raw();
        break;
      case 'R':
        returnSensorReading_Raw();
        break;
      case 'W':
        moveForwardFast(value * 10);
        returnSensorReading_Raw();
        break;
      case 'A':
        for (int k = 0; k < value; k++) {
          turnLeft();
        }
        delay(5);
        returnSensorReading_Raw();
        break;
      case 'X':
        moveBackwardsFast(value * 10);
        returnSensorReading_Raw();
        break;
      case 'D':
        for (int k = 0; k < value; k++) {
          turnRight();
        }
        delay(5);
        returnSensorReading_Raw();
        break;
    }

  }
}



//--------------------------Serial Codes-------------------------------
void setupSerialConnection() {
  Serial.begin(9600);
  while (!Serial);
}

void returnSensorReading_Raw() {
  Serial.print("Ard|Alg|S|1:");
  Serial.print(getFrontIR1());
  Serial.print(":");
  Serial.print(getFrontIR1_Block());
  Serial.print(",2:");
  Serial.print(getFrontIR2());
  Serial.print(":");
  Serial.print(getFrontIR2_Block());
  Serial.print(",3:");
  Serial.print(getFrontIR3());
  Serial.print(":");
  Serial.print(getFrontIR3_Block());
  Serial.print(",4:");
  Serial.print(getRightIR1());
  Serial.print(":");
  Serial.print(getRightIR1_Block());
  Serial.print(",5:");
  Serial.print(getRightIR2());
  Serial.print(":");
  Serial.print(getRightIR2_Block());
  Serial.print(",6:");
  Serial.print(getLeftIR1());
  Serial.print(":");
  Serial.println(getLeftIR1_Block());
  //  Serial.println("Ard|And|S|");
  Serial.flush();
}


void returnSensorReading() {
  Serial.print("Ard|Alg|S|1:");
  Serial.print((int)getFrontIR1());
  Serial.print(":");
  Serial.print(getFrontIR1_Block());
  Serial.print(",2:");
  Serial.print((int)getFrontIR2());
  Serial.print(":");
  Serial.print(getFrontIR2_Block());
  Serial.print(",3:");
  Serial.print((int)getFrontIR3());
  Serial.print(":");
  Serial.print(getFrontIR3_Block());
  Serial.print(",4:");
  Serial.print((int)getRightIR1());
  Serial.print(":");
  Serial.print(getRightIR1_Block());
  Serial.print(",5:");
  Serial.print((int)getRightIR2());
  Serial.print(":");
  Serial.print(getRightIR2_Block());
  Serial.print(",6:");
  Serial.print((int)getLeftIR1());
  Serial.print(":");
  Serial.print(getLeftIR1_Block());
  Serial.print("\n");
  //  Serial.println("Ard|And|S|");
  Serial.flush();
}
