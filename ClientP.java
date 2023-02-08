import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.*;


public class ClientP {
	private static final int BUFFER_SIZE = 1024;
	private static final int PORT = 6789;
	private static final String HOSTNAME = "localhost";
	static int BASE_SEQUENCE_NUMBER = 1;
	static int window_size = 5;

	
	public static void main(String args[]) throws Exception {
			
		// create a socket
		DatagramSocket socket = new DatagramSocket();
		socket.setSoTimeout(1000);
		
		// the message to be sent
		BufferedReader user_input = new BufferedReader (new FileReader("message.txt"));
		String Str = user_input.readLine();
				
			boolean timedOut = true;
			
			while (timedOut) {
				for (int counter = 0; counter<window_size; counter ++) {
					
					// create a byte array for sending and receiving data
					byte [] sendData = new byte [BUFFER_SIZE];
										
					// get IP server
					InetAddress IPAddress = InetAddress.getByName(HOSTNAME);
					
					//get byte data for message
					sendData = Str.getBytes();
					System.out.println("Sending packet: " + Str);
					
			        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
			        DataOutputStream dos = new DataOutputStream(outputStream);
			        dos.writeInt(BASE_SEQUENCE_NUMBER ++);
			        dos.writeUTF(Str);
			        
			        byte [] concatData = outputStream.toByteArray( );
			        sendData = concatData;
					// Send the UDP packet to the server
					DatagramPacket packet = new DatagramPacket(sendData, sendData.length, IPAddress, 6789);
					socket.send(packet);
					
				}
								
				try {
					
					byte [] receiveData = new byte [BUFFER_SIZE];	
					//Receive packet from server
					DatagramPacket received = new DatagramPacket(receiveData, receiveData.length);
					socket.receive(received);
					
					//get the message from the server
					//int returnMessage = ByteBuffer.wrap( received.getData( ) ).getInt();
					String ackstr = new String (received.getData());
					System.out.println( "FROM SERVER:" + ackstr );
					// If we receive an ack, stop the while loop
					timedOut = false;
					
					
				} catch ( SocketTimeoutException exception ){
					// If we don't get an ack, prepare to resend sequence number
					System.out.println( "Timeout, resending packet" );
					BASE_SEQUENCE_NUMBER--;
				}
			}

	
			
		socket.close();
	}
		
	}


