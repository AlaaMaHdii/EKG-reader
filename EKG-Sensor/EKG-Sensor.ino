#include <SPI.h>

SPISettings settings(8000000, MSBFIRST, SPI_MODE0);


// Find delay fra frekvens
long findDelayMicros(long frequency){
  long microsecondsSIunit = pow(10, 6);
  long nyquistShannonFrequency = (frequency * 2);
  return microsecondsSIunit / nyquistShannonFrequency; // 1200mikrosekund 
}

// Input frekvens i Hz her.
const long sampling = 400; // 400Hz
const long delayLoop = findDelayMicros(sampling);

void setup() {
    Serial.begin(115200);
    SPI.begin();
    // Begynd SPI forbindelsen til EKG sensor
    SPI.beginTransaction(settings);
    pinMode(10, OUTPUT);
    digitalWrite(10, HIGH);

    // Apparently first measurement will be invalid
    // So we perform one, without displaying it anywhere
    getECGADC();
}

void loop(){
    measureAndSend();
    delayMicroseconds(delayLoop);
  }

int getECGADC(){
    digitalWrite(10, LOW);
    // Spec sheet siger 1 microsecond for ADC til at tænde.
    int sample = SPI.transfer16(0x00);
    digitalWrite(10, HIGH);
    return sample;
}

void measureAndSend(){
   int sample = getECGADC();
   Serial.write(sample & 0xFF); // bitwise operation til at få lowerbyte
   Serial.write(sample >> 8); // shift dem til venstre for at få higherbyte
}
