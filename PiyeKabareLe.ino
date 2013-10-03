void setup(){
  Serial.begin(19200);
}

void loop(){
  if(Serial.available()){
    switch(Serial.read()){
      case'a':
        Serial.println("Piye skripsimu le?");
        break;
      case'b':
        Serial.println("Gorong mari pak, hehe :D");
        break;
      case'c':
        Serial.println("Payah age ndang di marikno le!");
        break;
      case'd':
        Serial.println("Engge pak");
        break;        
      default:
        Serial.println("No Respon");
        break;      
    }
  }
}
