import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public class lab3_client {

	public static void main(String[] args) {
		try {
			// define a (client socket)
			Socket socket = new Socket("localhost", 1995);
			BufferedReader socket_bf = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			
			DataInputStream InputData=new DataInputStream (socket.getInputStream());
			
		
            DataOutputStream socket_dos = new DataOutputStream(
					socket.getOutputStream());
            
            Scanner scr = new Scanner(System.in);							//get user input
            int noPackets = scr.nextInt();									//store user input in noPackets
            socket_dos.write(noPackets);									//send to server
            
            int sent = 1;
            
            while (sent <= noPackets)
            {
            	
            	socket_dos.write(sent);
            	
            	System.out.print("Packet number sent:");
            	System.out.print(sent);
            	System.out.println();
            	
            	while(socket_bf.read() != sent){
            		//System.out.println("Wait");
            	}
            	
            	System.out.print("Packet number acknowledged:");
            	System.out.print(sent);
            	System.out.println();
            	
            	sent = sent+1;
            }
     
            socket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}


	}
	
	
}
