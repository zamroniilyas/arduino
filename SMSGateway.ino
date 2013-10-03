#include <SoftwareSerial.h>
#include <String.h>
 
SoftwareSerial mySerial(7, 8);
 
void setup(){
  Serial.println("Sedang mengkonfigurasi Arduino");
  Serial.begin(19200);    // the GPRS baud rate 

  Serial.println("Sedang mengkonfigurasi Modem GSM Shield");  
  mySerial.begin(19200);  // the GPRS baud rate   

  Serial.println("Board Arduino siap di gunakan");
  delay(500);
  
}
 
void loop(){
  if (Serial.available())
    switch(Serial.read()){
      case 's':
        SendTextMessage();
        break;
      case 'p':
        DialVoiceCall();
        break;
      case 'i':
        inbox();
        break;
      case 'd':
        deleteSMS();
        break;
  } 
  
//  //modem listener ?
//  if (mySerial.available()){
//    Serial.write(mySerial.read());
//    delay(100);
//    mySerial.println("AT+CMGR=1");    
//  }
}
 
//kirim sms
void SendTextMessage(){
    Serial.println("BoardArduino sedang mengiriim sms");
    mySerial.print("AT+CMGF=1\r");    //Because we want to send the SMS in text mode
    delay(100);
    mySerial.println("AT + CMGS = \"+NOPE\"");//send sms message, be careful need to add a country code before the cellphone number
    delay(100);
    mySerial.println("A test message! jika terkirim mohon di bales");//the content of the message
    delay(100);
    mySerial.println((char)26);//the ASCII code of the ctrl+z is 26
    delay(100);
    mySerial.println();
    Serial.println("BoardArduino mengirim sms selesai");    
}
 
///nelpon
void DialVoiceCall(){
    Serial.println("BoardArduino sedang memanggil");    
    mySerial.println("ATD +NOPE;");//dial the number
    delay(100);
    mySerial.println();
    Serial.println("BoardArduino selesai memanggil");    
}

//receive sms
void receiveSMS(){
  if(mySerial.available()){
    int i=0;
    Serial.println("Ada sms masuk ke-"+i);
    i++;
    delay(100);
    
    mySerial.println("AT+CMGR=1");
  }
}

void inbox(){
  mySerial.println("AT+CMGL=ALL");
}

void deleteSMS(){
  mySerial.println("AT+CMGD=1");
}
