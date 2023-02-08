import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.charset.Charset;
import java.util.*;


public class Server {
	
	private static final int Buffer = 1024;
	private static final int Port = 6789;
	static int expectedSequenceNumber = 1;
	
	public static void main(String[] args) throws IOException {
		
		// create a socket for the server
		DatagramSocket serverSocket = new DatagramSocket( Port );
		
		//set up byte arrays for sending and receiving data
        byte[] receiveData = new byte[ Buffer ];
        byte[] dataForSend = new byte[ Buffer ];
        
        System.out.println("Server is running");
        
        //infinite loop to check for connections
        while (true) {
        	// get the packet from the client
        	DatagramPacket received = new DatagramPacket( receiveData, receiveData.length );
          	serverSocket.receive( received );
       
          	// extract message from client
          	
          	ByteArrayInputStream bais = new ByteArrayInputStream(receiveData);
          	DataInputStream dis = new DataInputStream(bais);
          	int sequenceNumber = dis.readInt();
          	String Str = dis.readUTF();
          	
          	// for demo purpose only, create a 50% chance that ACK is not sent to client
            Random random = new Random( );
            int chance = random.nextInt( 100 );
          	
            //validate received sequence number if matches with expected packet
          	if (sequenceNumber == expectedSequenceNumber) {
              	receiveData = received.getData();
             
              	
    			//Simultaion of packet loss
                if( ((chance % 2) == 0) ) {
                               
                	System.out.println("FROM CLIENT: "  + Str + " - sequence number:" + sequenceNumber);
               	 
               	 //get ip and port from client
                    InetAddress IPAddress = received.getAddress();
                    int port = received.getPort();
                    
                    String ack = "ACK - MESSAGE WITH SEQUENCE NUMBER " +sequenceNumber+ " RECEIVED";
                    dataForSend = ack.getBytes();
                    
                    // Send ACK back to the client
                    DatagramPacket packet = new DatagramPacket( dataForSend, dataForSend.length, IPAddress, port );
                    serverSocket.send( packet ); 
                    expectedSequenceNumber++;
               } else {
               	System.out.println( "Failed to ACK packet with sequence number: " + sequenceNumber);
               	// client will re-send the packet as timer will expire on his side
               } 
          	} 
         }

     }

}
   
