const int analogIn = A2;
cosnt int digitalPower = 8;

void setup() {
  Serial.begin(9600);  
  pinMode(digitalPower, OUTPUT);
  digitalWrite(digitalPower, HIGH);
}

void loop() {
  Serial.println(analogRead(analogIn));
}
