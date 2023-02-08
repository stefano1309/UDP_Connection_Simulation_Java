import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.*;


public class ServerP {
	private static final int BUFFER_SIZE = 1024;
	private static final int PORT = 6789;
	private static final String HOSTNAME = "localhost";
	static int BASE_SEQUENCE_NUMBER = 1;
	static int window_size = 5;

	
	public static void main(String args[]) throws Exception {
		ArrayList<Integer> failedACK_Array = new ArrayList<Integer>();	
		// create a socket
		DatagramSocket socket = new DatagramSocket();
		socket.setSoTimeout(1000);
		
		// the message to be sent
		BufferedReader user_input = new BufferedReader (new FileReader("message.txt"));
		String Str = user_input.readLine();
		
		for (int counter = 0; counter<window_size; counter ++) {
			boolean timedOut = true;
			
			while (timedOut) {
				
				// create a byte array for sending and receiving data
				byte [] sendData = new byte [BUFFER_SIZE];
				byte [] receiveData = new byte [BUFFER_SIZE];

				
				// get IP server
				InetAddress IPAddress = InetAddress.getByName(HOSTNAME);
				
				//get byte data for message
				sendData = Str.getBytes();
				System.out.println("Sending packet: " + Str +"with seuqnce number: " + BASE_SEQUENCE_NUMBER);
				
		        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		        DataOutputStream dos = new DataOutputStream(outputStream);
		        dos.writeInt(BASE_SEQUENCE_NUMBER ++);
		        dos.writeUTF(Str);
		        
		        byte [] concatData = outputStream.toByteArray( );
		        sendData = concatData;
				
				try {
					
					// Send the UDP packet to the server
					DatagramPacket packet = new DatagramPacket(sendData, sendData.length, IPAddress, 6789);
					socket.send(packet);
					
					//Receive packet from server
					DatagramPacket received = new DatagramPacket(receiveData, receiveData.length);
					socket.receive(received);
					
					//get the message from the server
					
					String ackstr = new String (received.getData());
					System.out.println( "FROM SERVER:" + ackstr );
					// If we receive an ack, stop the while loop
					timedOut = false;
					
					
				} catch ( SocketTimeoutException exception ){
					// If we don't get an ack, prepare to resend sequence number
					System.out.println( "Timeout, resending packet" );
					failedACK_Array.add(BASE_SEQUENCE_NUMBER); 
					BASE_SEQUENCE_NUMBER--; 
				}
				
			}

	
		}	
		 // store the length of the ArrayList in variable n
        int n = failedACK_Array.size();
        int min = failedACK_Array.get(0);
        // loop to find minimum from ArrayList
        for (int i = 0; i < n; i++) {
            if (failedACK_Array.get(i) < min) {
                min = failedACK_Array.get(i);
                min = min-1;
            }
        }
        System.out.println("the min is: " + min);
		
		socket.close();
	}
		
	}


