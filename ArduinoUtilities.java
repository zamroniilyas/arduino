/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bejoarduino.arduino;


import bejoarduino.panel.KoneksiRxTx;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.TooManyListenersException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

/**
 *
 * @author Chromatics
 */
public class ArduinoUtilities implements SerialPortEventListener {
    
    KoneksiRxTx window = null;
    private Enumeration port = null;
    private HashMap portMap = new HashMap();
    private CommPortIdentifier portIdentifier = null;
    private SerialPort serialPort = null;
    private InputStream inPut = null;
    private OutputStream outPut = null;
    private boolean serialConnected = false;
    final static int TIMEOUT = 2000;
    
    int nilaiBaud = 19200; // Nilai Baud Rate
    int nilaiData = javax.comm.SerialPort.DATABITS_8; // Nilai DATABITS
    int nilaiStop = javax.comm.SerialPort.STOPBITS_1; // Nilai STOPBITS
    int nilaiParity = javax.comm.SerialPort.PARITY_NONE; // Nilai PARITY
    int nilaiFlow = javax.comm.SerialPort.FLOWCONTROL_NONE; // Nilai FLOWCONTROL
    
    String log="";
    String dataIn = "";
    String statusPort = "";
    private char dataSerial = ' '; // Untuk menampung input dari serial port 
    

    
    public String getTanggal(){
        Date date = new Date();
        DateFormat df1 = DateFormat.getDateInstance(DateFormat.SHORT, new Locale("in", "ID"));    
        String tgl = df1.format(date)+" | "+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
        
        return tgl;
    }
    
    public DefaultComboBoxModel cekSerialPort() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        try {
            port = CommPortIdentifier.getPortIdentifiers();

            
            log=getTanggal()+" | ArduinoUtilities sedang mengecek port serial yang tersedia \n";
            window.textLog.setText(log);
            while (port.hasMoreElements()) {
                CommPortIdentifier curPort = (CommPortIdentifier) port.nextElement();
                if (curPort.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                    model.addElement(curPort.getName());
                    portMap.put(curPort.getName(), curPort);
                }
            }          
            
            log+=getTanggal()+" | ArduinoUtilities sudah selesai mengecek port\n";
            window.textLog.setText(log);
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }        
        return model;
    }
    
    private String connectedPort="";
    public void connect(String selectedPort) {
        
        log+=getTanggal()+" | ArduinoUtilities sedang membuka port "+selectedPort+"\n";
        window.textLog.setText(log);
        
        portIdentifier = (CommPortIdentifier) portMap.get(selectedPort);
        CommPort commPort = null;
        try {
            commPort = portIdentifier.open(null, TIMEOUT);
            serialPort = (SerialPort) commPort;
            setConnected(true);
            connectedPort=selectedPort;
            statusConnect = "Disconnect";
            
            log+=getTanggal()+" | ArduinoUtilities berhasil membuka port "+selectedPort+"\n";
        window.textLog.setText(log);
        } catch (PortInUseException e) {
            statusPort = selectedPort + " is in use. (" + e.toString() + ")";
            JOptionPane.showMessageDialog(null, statusPort);
        } catch (Exception e) {
            statusPort = "Failed to open " + selectedPort + "(" + e.toString() + ")";
            JOptionPane.showMessageDialog(null, statusPort);
        }
    }
 
    String statusConnect="";
    public void disconnect() {
        if (serialPort!=null) {
            try {
                
                log+=getTanggal()+" | ArduinoUtility sedang menutup port ("+connectedPort+")\n";
                window.textLog.setText(log);
                
                serialPort.removeEventListener();
                serialPort.close();
                inPut.close();
                setConnected(false);
                
                log+=getTanggal()+" | ArduinoUtility berhasil menutup port ("+connectedPort+")\n";
                window.textLog.setText(log);
                                
                JOptionPane.showMessageDialog(null, "Port ("+connectedPort+") berhasil di tutup");                
            } catch (Exception e) {
                statusPort = "Failed to close " + serialPort.getName() + "(" + e.toString() + ")";
                JOptionPane.showMessageDialog(null, statusPort);
            }
        }         
    }

    public boolean initIOStream() throws UnsupportedCommOperationException {
        boolean successful = false;
        try {
            inPut = serialPort.getInputStream();
            outPut = serialPort.getOutputStream();
            
            log+=getTanggal()+" | ArduinoUtilities sedang mengkonfiguri arduino("+connectedPort+") dengan : \n"
                +"Baud rate (Bits per second) : "+nilaiBaud+"\n"  
                +"Data bits : "+nilaiData+"\n"  
                +"Parity : "+nilaiStop+"\n" 
                +"Stop bits : "+nilaiStop+"\n"
                +"Flow Control : "+nilaiFlow+"\n";   
            window.textLog.setText(log);
            
            serialPort.setSerialPortParams(nilaiBaud, nilaiData, nilaiStop, nilaiParity);
            serialPort.setFlowControlMode(nilaiFlow);
            // Menerima pemberitahuan jika ada data pada terminal
            serialPort.notifyOnDataAvailable(true);            
            
            successful = true;
            
            log+=getTanggal()+" | ArduinoUtility berhasil mengkonfigurasi arduino ("+connectedPort+")\n";
            window.textLog.setText(log);
            return successful;
        } catch (IOException e) {
            statusPort = "I/O Streams failed to open. (" + e.toString() + ")";
            JOptionPane.showMessageDialog(null, statusPort);
            return successful;
        }
    }

    public void sendChar(Byte a){
        try {
            outPut.write(a);
            outPut.flush();
        } catch (IOException | NullPointerException ex) {
            log=ex.toString();
            window.textRespon.setText(log);
        }
    }
    
    public void kirimData(String str){
        log+=getTanggal()+" | ArduinoUtility sedang menulis ke arduino, pesan : "+str;
        window.textLog.setText(log);
        for(int i=0; i<str.length();i++){
            sendChar((byte)str.charAt(i));
            log+=str.charAt(i)+" = "+(byte)str.charAt(i)+"\n";
        }                
        log+=getTanggal()+" | ArduinoUtility selesai menulis ke arduino\n";
        window.textLog.setText(log);
    }
    
    public void initListener() {
        try {
            log+=getTanggal()+" | ArduinoUtility sedang menambahkan listiner pada serialoPort ("+serialPort.getName()+")\n";
            window.textLog.setText(log);
            
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
            
            log+=getTanggal()+" | ArduinoUtility selesai menambahkan listiner\n";
            window.textLog.setText(log);
        } catch (TooManyListenersException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }
    }
    
    
    @Override
    public void serialEvent(SerialPortEvent evt) {
        if (evt.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                log+=getTanggal()+ " | Ada data masuk\n";
                window.textLog.setText(log);
                if(dataSerial != '\n'){
                    
                    dataSerial = (char) inPut.read();
                    dataIn = dataIn + String.valueOf(dataSerial);
                    
                    System.out.print(dataSerial);
                    String dt =+dataSerial+"";
//                    window.textRespon.setText("fuuuuuuuuuuuuuck"+"");
//                    System.out.print("Data serial : "+dataSerial);
                    window.textRespon.setText(dataIn);
//                    System.out.print("\t");
//                    System.out.print(dataSerial);
//                    System.out.print("\n");
                }else{
//                    window.txtDataIn.setText(dataIn);
                    dataSerial=' ';
//                    dataIn="";
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, ex.toString());
            }
        }
    }

    public String getDataIn() {
        return dataIn;
    }

    public boolean getConnected() {
        return serialConnected;
    }

    public void setConnected(boolean serialConnected) {
        this.serialConnected = serialConnected;
    }
    
    public char getDataSerial() {
        return dataSerial;
    }

    public ArduinoUtilities(KoneksiRxTx window) {
        this.window = window;
    }        
}
