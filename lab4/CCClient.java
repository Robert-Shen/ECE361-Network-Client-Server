import java.awt.Color;
import java.awt.image.SampleModel;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;



public class CCClient {

	static String host;
	static int port;
	final static String CRLF="\r\n";
	public static int wstart;
	static long totalTime;
	static int timeOut;
	public static int lastAck = 0;
	static int sent = 1;
	static long[] send_timer;
	
	static long startTime;
	static long endTime;
	public static int EstimatedRTT;
	public static int DevRTT;
	public static int SampleRTT;
	public static final double alpha=0.125;
	public static final double beta=0.25;
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		host="localhost";
		port=9876;
		try 
		{
			Socket socket=new Socket(host,9876);
			socket.setTcpNoDelay(true); 
			System.out.println("Connected to : "+ host+ ":"+socket.getPort());

			//reader and writer:
			BufferedReader reader=new BufferedReader(new InputStreamReader (socket.getInputStream()));
			DataOutputStream writer= new DataOutputStream(socket.getOutputStream());
			Scanner scr = new Scanner(System.in);

			//define the thread and start it
			Thread thread=new Thread(new Listener(socket));
			 

			System.out.println("Enter number of packets to be sent to the server [0-127], 0 to Quit: ");
			int noPackets = scr.nextInt();
			writer.write(noPackets);
			//the noPackets to the server
			//...
			
			
			EstimatedRTT=1200;
			DevRTT=0;
			timeOut = EstimatedRTT+4*DevRTT; //in milliseconds
			lastAck=0;
			sent=1;
			int cwnd=1;
			int ssthresh=1000000000;
			int RTT_count=0;
			thread.start();
			startTime=System.currentTimeMillis();
			try {
				while(sent<noPackets)
				{
					//THE MAIN PART OF THE CODE!
					//send the packets with congestion control using the given instructions
					int lost=1;
	            	int n = 0;
	            	long start=System.currentTimeMillis();
	            	while(n<cwnd){
	            		writer.write(sent);
	            		System.out.print("Packet number sent:");
	                	System.out.print(sent);
	                	System.out.println();
	                	n++;
	                	sent++;
	                	if(sent>noPackets){
	                		break;
	                	}
	            	}
	                while(System.currentTimeMillis()-start<timeOut){
	                	//System.out.println("s");
	                	if(lastAck==sent-1){
	                		System.out.println();
	                		SampleRTT=(int)(System.currentTimeMillis()-start);
	                		DevRTT = (int)((1-beta)*DevRTT+beta*Math.abs(SampleRTT-EstimatedRTT));
	                		EstimatedRTT = (int)((1-alpha)*EstimatedRTT+alpha*SampleRTT);
	                		timeOut = EstimatedRTT+4*DevRTT;
	                		if (cwnd<ssthresh){
	                			cwnd = cwnd*2;
	                		}
	                		else{
	                			cwnd++;
	                		}
	                		RTT_count++;
	                		lost=0;
	                		break;
	                	}
	                }
	                
	                if(lost==1){
	                	if (sent==2 && cwnd<ssthresh){
	                		RTT_count++;
	                		System.out.println();
	                		cwnd = cwnd*2;
	                	}
	                	else{
	                	System.out.println("timeout" + (lastAck+1));
	                	ssthresh =cwnd/2;
	                	cwnd = 1;
	                	sent = lastAck+1;
	                	RTT_count++;
	                	}
	                }
	                }
				
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				endTime = System.currentTimeMillis();
				totalTime = endTime - startTime;
			}
			
			System.out.print("total taken time: ");
			System.out.print(totalTime);
			System.out.println();
			
			System.out.print("Number of succesfully sent packets: ");
			System.out.print(noPackets);
			System.out.println();
			
			System.out.print("Number of RTT: ");
			System.out.print(RTT_count);
			System.out.println();
			
			//print the total taken time, number of sucessfully sent packets, etc. 
			//...
			
			writer.flush();
			socket.close();
			System.out.println("Quitting...");
			socket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void update(int ackNum)
	{
		//update lastAck here. note that last ack is accumulative, 
		//i.e., if ack for packet 10 is previously received and now ack for packet 7 is received, lastAck will remain 10
		//...
		CCClient.lastAck = ackNum;
	}

}
