import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.*;


public class Client {
	private static final int Buffer = 1024;
	private static final int Port = 6789;
	private static final String Hostname = "localhost";
	static int SequenceNumber = 1;
	static int window_size = 5;

	
	public static void main(String args[]) throws Exception {
		
		//create array to story sequence numbers of NACK 
		ArrayList<Integer> failedACK_Array = new ArrayList<Integer>();	
		// create a socket
		DatagramSocket socket = new DatagramSocket();
		socket.setSoTimeout(1000);
		
		// Read the local file and store the data in a String object
		BufferedReader user_input = new BufferedReader (new FileReader("message.txt"));
		String Str = user_input.readLine();
		
		//Counter to send multiple times the data for demonstration purposes
		for (int counter = 0; counter<window_size; counter ++) {
			boolean timedOut = true;
			
			// infinite client loop
			while (timedOut) {
				
				// create a byte array for sending and receiving data, storing the info into Buffer
				byte [] sendData = new byte [Buffer];
				byte [] receiveData = new byte [Buffer];

				
				// get IP 
				InetAddress IPAddress = InetAddress.getByName(Hostname);
				
				//transformation and preparation of the message to be sent
				sendData = Str.getBytes();
				System.out.println("Sending packet: " + Str +" with sequence number: " + SequenceNumber);
				
				//attach a sequence number to the message 
		        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		        DataOutputStream dos = new DataOutputStream(outputStream);
		        dos.writeInt(SequenceNumber ++);
		        dos.writeUTF(Str);
		        
		        byte [] concatData = outputStream.toByteArray( );
		        sendData = concatData;
				
				try {
					
					// Send the UDP packet to the server
					DatagramPacket packet = new DatagramPacket(sendData, sendData.length, IPAddress, Port);
					socket.send(packet);
					
					//Receive packet from server
					DatagramPacket received = new DatagramPacket(receiveData, receiveData.length);
					socket.receive(received);
					
					//get the message from the server
					String ackstr = new String (received.getData());
					System.out.println( "FROM SERVER:" + ackstr );
					
					// If we receive and ACK stop the while loop
					timedOut = false;
					
					
				} catch ( SocketTimeoutException exception ){
					// when try block fails to receive ACK, manage the error with catch block to retransmit
					System.out.println( "Timeout, resending packet" );
					//store the sequence number of the NACKs into an array
					failedACK_Array.add(SequenceNumber); 
					SequenceNumber--; 
				}
				
			}

	
		}	
		 // find the first NACK (min num) so it could be used as starting point for new retransmission
        int n = failedACK_Array.size();
        int min = failedACK_Array.get(0);
        // loop to find minimum from ArrayList
        for (int i = 0; i < n; i++) {
            if (failedACK_Array.get(i) < min) {
                min = failedACK_Array.get(i);
                            }
         //This part of the code was not successful, its my attempt to recreate the GO-Back-N   
        }
        min = min-1;
        System.out.println("First NACK was packet number: " + min);
		
		socket.close();
	}
		
	}


