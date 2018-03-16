import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;
import java.lang.*;

public class lab3part4_client {
	
	static int lastAck = 0;
	
	public static void setAckNum(int ackNum){
		lab3part4_client.lastAck = ackNum;
	}

	public static void main(String[] args) {
		try {
	
			
			// define a (client socket)
			Socket socket = new Socket("localhost", 9876);
			
			
			
			BufferedReader socket_bf = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			
			
            DataOutputStream socket_dos = new DataOutputStream(
					socket.getOutputStream());
            
            Scanner scr = new Scanner(System.in);
            System.out.println("number of packets");
            int noPackets = scr.nextInt();									//store user input in noPackets
            socket_dos.write(noPackets);									//send to server
            
            
            System.out.println("probability of error");
            int probError = scr.nextInt();
            socket_dos.write(probError);
            
            
            System.out.println("window size");
            int wSize = scr.nextInt();
            
            System.out.println("Timeout length");
            int timeOut = scr.nextInt();
            
            int sent = 1;
            
            long [] timer = new long [wSize];
            Thread thread=new Thread(new listener(socket));
            thread.start();
           int done =0;
        	while (sent <= noPackets)
            {
            	int first = sent;
            	int n = 0;
            	while(n<wSize){
            		socket_dos.write(sent);
            		timer[n]=System.currentTimeMillis();
            		System.out.print("Packet number sent:");
                	System.out.print(sent);
                	System.out.println();
                	n++;
                	
                	sent++;
                	if(sent>noPackets){
                		done =1;
                		break;
                	}
            	}
            	
            	
            	n = 0;
            	
            	while(n<wSize){
            		int out = 1;
            		while(System.currentTimeMillis()-timer[n]<timeOut){
            			if(lab3part4_client.lastAck>=first+n){
            				out=0;
            				n = lab3part4_client.lastAck-first;
            				System.out.println(lab3part4_client.lastAck);
            				System.out.print("Packet number acknowledged:");
                        	System.out.print(n+first);
                        	System.out.println();
                        	n++;
            				break;
            			}
            		}
            		if(out==1){
            			System.out.println("TIMEOUT");
            			break;
            		}
            		
            	}
            	sent = first + n;
            }
     
            socket.close();
            scr.close();

		} catch (Exception e) {
			e.printStackTrace();
		}


	}
}