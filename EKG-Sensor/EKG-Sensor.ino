#include <TimerOne.h>
#include <SPI.h>

SPISettings settings(8000000, MSBFIRST, SPI_MODE0);


// Input frekvens i Hz her.
const long frequency = 400; // 400Hz


// Find delay fra frekvens
long findDelayMicros(long frequency){
  long microsecondsSIunit = pow(10, 6);
  long nyquistShannonFrequency = (frequency * 2);
  return microsecondsSIunit / nyquistShannonFrequency; // 1200mikrosekund 
}

void setup() {
    Timer1.initialize(findDelayMicros(frequency)); // intialisere timeren
    Timer1.attachInterrupt(measureAndSend);  // Timer1 skal kalde på measureAndSend funktionen på interrupts. I dette tilfælde bliver der kørt measureAndSend hvert 1200 mikrosekund
    // Start serie forbindelse på en høj nok baudrate
    Serial.begin(115200); // Burde kun at være 56700, men pga. TODO: dynamisk sampling rate så skal der være plads til lidt mere.   
    // Desuden bruger den højere baud rate meget mindre tid og cpu-cyklusser.
    SPI.begin();
    // Begynd SPI forbindelsen til EKG sensor
    SPI.beginTransaction(settings);
    pinMode(10, OUTPUT);
    digitalWrite(10, HIGH);
}

void loop(){
  
}

int getECGADC(){
    digitalWrite(10, LOW);
    int sample = SPI.transfer16(0x00);
    digitalWrite(10, HIGH);
    return sample;
}

void measureAndSend(){
   int sample = getECGADC();
   Serial.write(highByte (sample));
   Serial.write(lowByte (sample));
   Serial.println(sample);
}
