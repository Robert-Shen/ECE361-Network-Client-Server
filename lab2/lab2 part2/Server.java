package Part2Server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;


public class Server {


	public static void main(String[] args) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		System.out.println("Server starts");
		try{
			
			Socket client;
			Socket client2;	
			
			ServerSocket socket = new ServerSocket(1995);
			
			client=socket.accept();
	        		
			
			String connectl="Welcome to the Server";
			
			BufferedReader socket_reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			DataOutputStream writer = new DataOutputStream(client.getOutputStream());
			writer.writeBytes(connectl + "\r\n");
			
			while(true){
				
			
		   String Filename=socket_reader.readLine();
		   
		   //defining a file with a specific name and checking if it exists:
		   File file=new File(Filename);
		   	
			
			
			//Transfer Files
			if (file.exists())
			{
				writer.writeBytes("File Transfer Starts" + "\r\n");
			    //writer.writeByte(1997);
			    writer.writeBytes("1997"+"\r\n");
			    ServerSocket socket2 = new ServerSocket(1997);
			    System.out.println("Socket2 build");
			    client2=socket2.accept();
				
			    System.out.println("Socket2 opened");
			    FileInputStream fis= new FileInputStream(file);
				DataOutputStream writer2 = new DataOutputStream(client2.getOutputStream());
                
				
				byte[] buffer = new byte[1024];
				int len;
					//writer.write(buffer);
					//fis.read
				do{
					len=fis.read(buffer);
					if(len==-1)
						break;
					writer2.write(buffer,0,len);				
				}
				while(len!=-1);
			    
				writer2.writeBytes("Socket 2 port 1997 is closed!"+"\r\n");
                client2.close();
                socket2.close();
                System.out.println("Server"+client2.isClosed());
				fis.close();
			System.out.println(file.length());
				//writer.writeBytes("File Transfer Completed" + "\r\n");
			}
				
					
				
			
		    //Report Error to the client
			else
				writer.writeBytes("File does not exist" + "\r\n");

				
			}	
			//String sendmessage=reader.readLine();
			//if(sendmessage.equalsIgnoreCase("quit"))
				//break;}
			//socket.close();
		}catch(Exception e){e.getStackTrace();}
		
	}
	
	
	
}
