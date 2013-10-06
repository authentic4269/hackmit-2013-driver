int statusLED = 2; //the pin you connect your status LED to
int desiredBaud = 4;//index of your desired baud rate 4 is 9600
char* desiredPin = "1234";//change to the pin of your choice

unsigned int baudLookup[] = 
{
1200,  //code 1
2400,  //code 2
4800,  //code 3
9600,  //code 4
19200, //code 5
38400, //code 6
57600  //code 7
}; 

void setup()
{
  pinMode(statusLED, OUTPUT);
  digitalWrite(statusLED, HIGH);
  
  //this block of code searches every possible baud rate until a 
  //working rate is found (blinking briefly between searches)
  int myBaud = 1;
  while(!checkBT2Sstatus(myBaud)) {
     digitalWrite(statusLED, LOW);
     myBaud++; 
     delay(200);
     if(myBaud > 7) myBaud=1;//reset baud and keep searching
     digitalWrite(statusLED, HIGH);
  }
  //blink fast to show baud found
  for(int i =0; i <10; i++)
  {
     digitalWrite(statusLED, LOW);
     delay(100);
     digitalWrite(statusLED, HIGH);
     delay(100);
  }
  
  delay(1000);//wait a second
  
  //blink number that corresponds to baud
  for(int i =0; i <myBaud; i++)
  {
     digitalWrite(statusLED, LOW);
     delay(200);
     digitalWrite(statusLED, HIGH);
     delay(500);
  }
  
  digitalWrite(statusLED, LOW); //we found the baud, now to configure

  //this is where you put your desired configuration
  //                   startingBaud   newBaud         PIN      
  if(!configureBT2S(myBaud,       desiredBaud, desiredPin))
  {
  digitalWrite(statusLED, LOW);
  while(true);//device failed to configure
  }; 
  
  digitalWrite(statusLED, HIGH); //SUCCESS!
  
}

void loop()
{
  digitalWrite(statusLED, LOW); //fast blink means success
  delay(100);
  digitalWrite(statusLED, HIGH);
  delay(100);
}


 

boolean configureBT2S(uint8_t startingBaud, uint8_t endingBaud, char* myPin)
{
  boolean myReturn = false;

  digitalWrite(statusLED, HIGH);//our device is attached and configured correctly
 delay(500); 
  Serial.print("AT+PIN");
  Serial.print(myPin[0]);Serial.print(myPin[1]);Serial.print(myPin[2]);Serial.print(myPin[3]);
  
  delay(1500);//give it half a tick just to be sure its ready
  while(Serial.available()>0)Serial.read();//clear the buffer
  
  Serial.print("AT+BAUD");
  Serial.print(endingBaud, DEC);

delay(1500);//let it reset
if(checkBT2Sstatus(endingBaud)) myReturn = true; //final check

return myReturn;
}

boolean checkBT2Sstatus(uint8_t baud)
{
  Serial.begin(baudLookup[baud-1]);//pulls baud from baudLookup
  delay(500);//give it half a tick just to be sure its ready
  while(Serial.available()>0)Serial.read();//clear the buffer
  delay(1500);//wait for reset to complet
  
  
    
  Serial.print("AT");//checks to see if BT2S is connected, and set to proper baud
 
for(int i = 0;Serial.available()<2 && i <100; i++ ) delay(12);//give as much as 1.2 seconds for a response 

if(Serial.read()=='O' && Serial.read()=='K') return true;
else return false;

}
