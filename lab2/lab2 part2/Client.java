package Part2Client;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.lang.Thread;


public class Client {

	
	public static void main(String[] args) {
		try {
			// define a (client socket)
			Socket socket = new Socket("localhost", 1995);
			BufferedReader socket_bf = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			DataInputStream InputData=new DataInputStream (socket.getInputStream());
			
		
            DataOutputStream socket_dos = new DataOutputStream(
					socket.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            
        	//Response Message from Server
        	String str = socket_bf.readLine();
            System.out.println(str);
            
         
            
            while(true){
            	String Userinput = reader.readLine();
          	    

            	socket_dos.writeBytes(Userinput+"\r\n");
            	
    			
            	//Server Response
            	
            	str = socket_bf.readLine();
            	
            	if(str.equalsIgnoreCase("File does not exist")) {
            		 System.out.println(str);

            	}
            	
            	
               
            	
            	else
            	{
            		
            		str = socket_bf.readLine();
        
            		int portNUmber=Integer.parseInt(str);
            		System.out.println("Data connection is Port "+portNUmber);
            		Socket socket2 = new Socket("localhost", portNUmber);
            		
            		DataInputStream InputData2=new DataInputStream (socket2.getInputStream());
            		BufferedReader socket_bf2 = new BufferedReader(
        					new InputStreamReader(socket2.getInputStream()));
            		
            		FileOutputStream fos= new FileOutputStream(Userinput);
 
            		int len;
            		byte[] buffer = new byte[1024];
            	  do{
            		 if(InputData2.available()!=0)
            		 {            		  
            		  len=InputData2.read(buffer); 
            		  fos.write(buffer,0,len); 
            		 }
            		 
            	  	} 
            		  while(InputData2.available()!=0);
            	 // if(socket2.isClosed())
            	 
            	  
                 //System.out.println(socket2.isClosed());
            	
            	  //System.out.println("hehe"+socket2.isOutputShutdown());
            	  
            	 // System.out.println("done receiving data."); 
            	// i++;
            	 // System.out.println((socket_bf2.ready()));
                   System.out.println("Check if the socket is closed: (Null for not available) " + socket_bf2.readLine()); 
            	  
            	  fos.close();   
            	           	  
            	  
            	  File file=new File(Userinput);
             	  System.out.println("Current file's length: " +file.length() + " btyes"); 
                  System.out.println("Whether the input stream still returns bytes: (-1 or 0 as No)" +InputData2.read(buffer));
            	  
                   socket2.close();
            	  
            	  
            	   }
            	if(str.equalsIgnoreCase("quit"))
    				break;	
            }
            
            
            socket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}


	}
}
