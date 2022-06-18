#include <TimerOne.h>
#include <SPI.h>


// Input frekvens i Hz her.
const long frequency = 400; // 400Hz


// automatisk beregning
const long microsecondsSIunit = pow(10, 6);
const long nyquistShannonFrequency = (frequency * 2);
const long tSampleInMicros = microsecondsSIunit / nyquistShannonFrequency; //1000000;  Sample tid i mikrosekunder
//const long tSampleInMicros = 1200;

SPISettings settings(8000000, MSBFIRST, SPI_MODE0);

void setup() {
    Timer1.initialize(tSampleInMicros);          // intialisere timeren
    Timer1.attachInterrupt(measureAndSend);      // Timer1 skal kalde på measureAndSend funktionen på interrupts. I dette tilfælde bliver der kørt measureAndSend hvert 1200 mikrosekund
    // Start serie forbindelse på en høj nok baudrate
    Serial.begin(115200); // Burde kun at være 56700, men pga. dynamisk sampling rate så skal der være plads til lidt mere.   
    // Desuden bruger den højere baud rate meget mindre tid og cpu-cyklusser.
    SPI.begin();
    // Begynd SPI forbindelsen til EKG sensor
    SPI.beginTransaction(settings);
    pinMode(10, OUTPUT);
    digitalWrite(10, HIGH);
}

void loop(){
  //implementere ændringer
  
}

void convertADCtoVoltage(){

}


int getECGADC(){
    digitalWrite(10, LOW);
    int sample = SPI.transfer16(0x00);
    digitalWrite(10, HIGH);
    return sample;
}

void measureAndSend(){
   int sample = getECGADC();
   Serial.println(sample);
}
