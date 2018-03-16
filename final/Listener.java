import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public class Listener implements Runnable {

	public Socket socket;
	
	public Listener(Socket socket) {
		this.socket=socket;
	}
	
	@Override
	public void run() {
		try {
			
				BufferedReader socket_bf = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));
				
				String ack;
				
				while(true){
					
						 ack = socket_bf.readLine();
						Final.update(ack);
			}
			
			
		} catch (Exception e) {
			//e.printStackTrace();
		}
		
		
	}
	
}