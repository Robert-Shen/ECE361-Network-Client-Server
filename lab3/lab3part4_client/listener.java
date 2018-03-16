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

public class listener implements Runnable{
	Socket soc;
	
	public listener (Socket socket){
		soc = socket;
	}
	
	public void run(){
		try{
		BufferedReader socket_bf = new BufferedReader(
				new InputStreamReader(soc.getInputStream()));
		
		int a;
		
		while(true){
			
				 a = socket_bf.read();
				lab3part4_client.setAckNum(a);
			
		}
	
	}catch (Exception e) {
		e.printStackTrace();
	}
}
}