import java.net.*;
import java.io.*;
public class Lab1_practical3 {

	public static void main(String[] args) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		System.out.println("Hello World");
		try{
			ServerSocket server = new ServerSocket(9883);
			//Socket socket = new Socket("localhost",9876);
			Socket socket;
			socket = server.accept();
			
			DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
			String str1 = "Welcome";
			writer.writeBytes(str1  + "\r\n");
			
			
			BufferedReader socket_reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			//DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
			
			while(true){
				
				String str = socket_reader.readLine();
				System.out.println("Client: " + str);
			
			
				str = reader.readLine();
				writer.writeBytes(str  + "\r\n");
			
				if(str.equalsIgnoreCase("quit"))
					break;
           	 	}
			
			socket.close();
			server.close();
		}catch(Exception e){e.getStackTrace();}
	}
}