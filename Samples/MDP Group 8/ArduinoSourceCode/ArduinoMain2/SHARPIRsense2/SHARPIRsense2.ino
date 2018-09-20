#include "DualVNH5019MotorShield.h"

#define IR A0

DualVNH5019MotorShield md;

//sensors
float front_sensor1 = 0;
float front_sensor2 = 0;
float left_sensor = 0;

unsigned short disf1 = 0;
unsigned short disf2 = 0;
unsigned short disf3 = 0;
unsigned short disL1 = 0;
unsigned short ps1_median = 0;
unsigned short ps2_median = 0;
unsigned short ps3_median = 0;
unsigned short ps4_median = 0;
int A[51];
int B[51];
int C[51];
int D[51];
int O[4];


int partitionA(int A[51],int start,int last)
{
  int pIndex, pivot, j;
  unsigned short temp;
  pivot = A[last];
  pIndex = start - 1;
  for(j=start; j<=last; j++)
  {
    if(A[j]<pivot)
    {
      pIndex++;
      temp = A[pIndex];
      A[pIndex] = A[j];
      A[j] = temp;
    }
  }
  pIndex++;
  temp = A[pIndex];
  A[pIndex] = A[j];
  A[j] = temp;
  return pIndex;
}

void quicksortA(int A[51], int start, int last)
{
  int pIndex;
  if(start < last)
  {
    int pIndex = partitionA(A, start, last);
    quicksortA(A, start, pIndex-1);
    quicksortA(A, pIndex+1, last);
  }
}

int partitionB(int B[51],int start,int last)
{
  int pIndex, pivot, j;
  unsigned short temp;
  pivot = B[last];
  pIndex = start - 1;
  for(j=start; j<=last; j++)
  {
    if(B[j]<pivot)
    {
      pIndex++;
      temp = B[pIndex];
      B[pIndex] = B[j];
      B[j] = temp;
    }
  }
  pIndex++;
  temp = B[pIndex];
  B[pIndex] = B[j];
  B[j] = temp;
  return pIndex;
}

void quicksortB(int B[51], int start, int last)
{
  int pIndex;
  if(start < last)
  {
    int pIndex = partitionB(B, start, last);
    quicksortB(B, start, pIndex-1);
    quicksortB(B, pIndex+1, last);
  }
}

int partitionC(int C[51],int start,int last)
{
  int pIndex, pivot, j;
  unsigned short temp;
  pivot = C[last];
  pIndex = start - 1;
  for(j=start; j<=last; j++)
  {
    if(C[j]<pivot)
    {
      pIndex++;
      temp = C[pIndex];
      C[pIndex] = C[j];
      C[j] = temp;
    }
  }
  pIndex++;
  temp = C[pIndex];
  C[pIndex] = C[j];
  C[j] = temp;
  return pIndex;
}

void quicksortC(int C[51], int start, int last)
{
  int pIndex;
  if(start < last)
  {
    int pIndex = partitionC(C, start, last);
    quicksortC(C, start, pIndex-1);
    quicksortC(C, pIndex+1, last);
  }
}

void ps1_reading()
{
  for(int x=0; x<51 ; x++)
  {
    A[x] = analogRead(A0);
    delay(2);
  }
  quicksortA(A,0,25);
  Serial.print("Sensor ADC: ");
  Serial.println(A[25]);
  ps1_median = A[25];
}


void ps2_reading()
{
  for(int x=0; x<51 ; x++)
  {
    B[x] = analogRead(A1);
    delay(2); 
  }
  quicksortB(B,0,25);
  Serial.print("Sensor ADC: ");
  Serial.println(B[25]);
  ps2_median = B[25];
}

void ps3_reading()
{
  for(int x=0; x<51 ; x++)
  {
    C[x] = analogRead(A2);
    delay(2); 
  }
  quicksortC(C,0,25);
  Serial.print("Sensor ADC: ");
  Serial.println(B[25]);
  ps3_median = C[25];
}

void sensors()
{
  disf1 = (5485/(ps1_median-17))-2;
  disf2 = (5485/(ps2_median-17))-2;
  disf3 = (5485/(ps2_median-17))-2;
  disL1 = (10975/(ps2_median-14))-2;
  delay(300);
}

void obstacle()
{
  O[0] = 0;
  O[1] = 0;
  O[2] = 0;
  O[3] = 0;
  
  while(disf1 < 15)
  {
    O[0] = 1;
    break;
  }
  while(disf2 < 15)
  {
    O[1] = 1;
    break;
  }
    while(disf3 < 15)
  {
    O[2] = 1;
    break;
  }
}

void printdistance()
{
  Serial.print("Distance front1: ");
  Serial.print(disf1);
  Serial.println("cm");
  Serial.print("Distance front2: ");
  Serial.print(disf2);
  Serial.println("cm");
  Serial.print("Distance front3: ");
  Serial.print(disf3);
  Serial.println("cm");
  Serial.println("Obstacle position ");
  Serial.print(O[0]);
  Serial.print(" , ");
  Serial.print(O[1]);
  Serial.print(" , ");
  Serial.print(O[2]);
  Serial.print(" , ");
  Serial.println(O[3]);

  delay(50);
}

void setup() 
{
  Serial.begin(9600);
  md.init();
}

void loop() 
{
  ps1_reading();
  ps2_reading();
  ps3_reading();
  sensors();
  obstacle();
  printdistance();
  
}
