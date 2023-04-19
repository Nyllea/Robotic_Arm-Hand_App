#include <Servo.h>

Servo servo;
int currentAngle = 0;

void setup() {
  servo.attach(8);
  servo.write(currentAngle);
  Serial.begin(9600);
}


void loop() 
{

  int convertedValue;
   // If there is a data stored in the serial receive buffer and read it
  if(Serial.available()){
    convertedValue = Serial.read() - '0';
    convertedValue = (int) ((convertedValue * 2) + 95);

/*
    if (currentAngle < convertedValue) {
       for(int angle = currentAngle; angle < convertedValue; angle++) {                                  
        servo.write(angle);               
        delay(15);                   
       }
    } else {
       for(int angle = currentAngle; angle > convertedValue; angle--) {                                  
        servo.write(angle);               
        delay(15);                   
       }
    }
    currentAngle = convertedValue;
*/    

    servo.write(convertedValue);
  }
}
