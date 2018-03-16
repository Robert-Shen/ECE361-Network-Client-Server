/* Group Number: y4y6
 * Student Name: Tony YuTong Fang, 1000699434
 * 				 Yong An Lai, 1000776706
 */


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.PriorityQueue;
import java.util.Collections;
import java.util.Random;
import java.nio.ByteBuffer;

import javax.xml.ws.handler.MessageContext.Scope;

public class Final {
	
	public static int lastAck = 0;
	static int sent = 1;
	
	public static void update(String ackNum)
	{
		//update lastAck here. note that last ack is accumulative, 
		//i.e., if ack for packet 10 is previously received and now ack for packet 7 is received, lastAck will remain 10
		//...
		Final.lastAck = Integer.parseInt(ackNum);
		
	}

	public static void adjacenyToEdges(double[][] matrix, List<Node> v)
	{
		for(int i = 0; i < matrix.length; i++)
		{
			v.get(i).neighbors = new Edge[matrix.length];
			for(int j = 0; j < matrix.length; j++)
			{
				v.get(i).neighbors[j] =  new Edge(v.get(j), matrix[i][j]);	
			}
		}
	}
	
	public static void computePaths(Node source)
	{
		// Complete the body of this function
		source.minDistance=0;
		
		PriorityQueue<Node> NodeQueue = new PriorityQueue<Node>();
		
		NodeQueue.add(source);
		
		while(!NodeQueue.isEmpty())
		{
			Node cur = NodeQueue.poll();
			
			for(int i=0;i<cur.neighbors.length;i++)
			{
				
				double distanceThroughSource = cur.minDistance + cur.neighbors[i].weight;
				
				if (distanceThroughSource < cur.neighbors[i].target.minDistance)
				{
					NodeQueue.remove(cur.neighbors[i].target);
					cur.neighbors[i].target.minDistance = distanceThroughSource;
					cur.neighbors[i].target.previous = cur;
					NodeQueue.add(cur.neighbors[i].target);
				}
			}	
		}	
	}
	
	public static List<Integer> getShortestPathTo(Node target)
	{
		// Complete the body of this function
		List<Integer> path = new ArrayList<Integer>();
		
		path.add(target.name);
		
		Node prev = target.previous;
		
		while(prev!=null)
		{
			path.add(prev.name);
			prev = prev.previous;
		}
		
		return path;
	}
	
	public static void main(String[] args){
		
		try{
			Socket socket=new Socket("localhost",9876);
			socket.setTcpNoDelay(true); 
			System.out.println("Connected to :localhost" + ":"+socket.getPort());
			
			BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream())); //for reading lines
			DataOutputStream writer=new DataOutputStream(socket.getOutputStream());	//for writing lines.
			Scanner scr = new Scanner(System.in);
			BufferedReader reader2=new BufferedReader(new InputStreamReader(System.in));
			
			//define thread and start it
			Thread thread=new Thread(new Listener(socket));
			
			/*********find shortest path***********/
			
			System.out.println("get number of nodes from server");
			
			String num_nodes = reader.readLine();
			
			int noNodes = Integer.parseInt(num_nodes);
			
			System.out.println("get adjacency matrix from server");
			
			String values = reader.readLine();					//read adjacency matrix from server
			
			double[][] matrix = new double[noNodes][noNodes];
			
			// Use StringToenizer to store the values read from the server in matrix
			StringTokenizer weight = new StringTokenizer(values);

			int col=0;
			int row=0;
			
			System.out.println("Adjacency Matrix");
			
			while(weight.hasMoreTokens())
			{
				matrix[row][col]=Double.parseDouble(weight.nextToken());
				
				
				System.out.print(matrix[row][col] + " ");
				col++;
				if(col == noNodes)
				{
					System.out.println();
					col=0;
					row++;
					
				}
			}
			
			//The nodes are stored in a list, nodeList
			List<Node> nodeList = new ArrayList<Node>();
			for(int i = 0; i < noNodes; i++){
				nodeList.add(new Node(i));
			}
			
			// Create edges from adjacency matrix
			adjacenyToEdges(matrix, nodeList);
			
			// Finding shortest path for all nodes
			computePaths(nodeList.get(0));
				
			System.out.println("Node 0");
			
			List<Integer> path = getShortestPathTo(nodeList.get(nodeList.size()-1));
					
			System.out.print("Total time to reach node" + (nodeList.size()-1));								
			System.out.print(": " + nodeList.get(nodeList.size()-1).minDistance + " ms, path: ");
			
			
			char[] srt_path = new char[path.size()+2+(path.size()-1)*2];
			
			int j =0;
			srt_path[j]='[';
			j++;
			for(int i = path.size()-1; i>=0; i--){
		
				srt_path[j] = (char) ('0' + path.get(i));
				j++;
				if(i==0){
					break;
				}
				srt_path[j] = ',';
				j++;
				srt_path[j] = ' ';
				j++;
			}
			srt_path[j] = ']';
			System.out.print(srt_path);
			System.out.println();
			
			writer.writeBytes(String.valueOf(srt_path) + "\r\n");			//send path to server

			/*******************transfer file*************************/
			
			System.out.print("User input filename: ");
			String filename = reader2.readLine();
			
			writer.writeBytes(filename + "\r\n");						//send file name to server
			
			File file=new File(filename);									//create file
			
			if (file.exists()){
				System.out.println("file exists");
				
				long num_packets =  (long)Math.ceil(file.length()/1000.0);

			    String nopackets = String.valueOf(num_packets);		    
				
				writer.writeBytes(nopackets + "\r\n");							//send packet number to server
				
				int timeout = ((int)nodeList.get(nodeList.size()-1).minDistance)*2 + 200;		//set timeout
				lastAck=0;
				sent=1;
				int cwnd=1;
				int ssthresh=1000000000;
				int len=0;
				
				FileInputStream fis= new FileInputStream(file);						//allocate file input stream
				byte[] buffer = new byte[1000];
				
				thread.start();													//start thread
				
				while (len!=-1){				
					int lost=1;
	            	int n = 0;
	            	long start=System.currentTimeMillis();
	            	
	            	while(n<cwnd){
	            		
	            		len=fis.read(buffer);							//read 1000 bytes of content from file
		    			
						if(len==-1)
							break;
						
						byte[] packet_number = ByteBuffer.allocate(4).putInt(sent).array();			
						
						byte[] packet = new byte[packet_number.length + buffer.length];			//concatenate the two arrays
						System.arraycopy(packet_number, 0, packet, 0, packet_number.length);
						System.arraycopy(buffer, 0, packet, packet_number.length, buffer.length);
	            		
						writer.write(packet,0,packet_number.length + len);	
						
	            		System.out.print("Packet number sent:");
	                	System.out.print(sent);
	                	System.out.println();
	                	n++;
	                	sent++;
	      
	                	if(sent>num_packets){
	                		break;
	                	}
	            	}
	                	
	                	while(System.currentTimeMillis()-start<timeout){
		                	
		                	if(lastAck==sent-1){
		                		if (cwnd<ssthresh){
		                			cwnd = cwnd*2;
		                		}
		                		else{
		                			cwnd++;
		                		}
		                		lost=0;
		                		break;
		                	}
		                }
	                	
	                	if(lost==1){
	                		
	                		fis = new FileInputStream(file);	
	                		fis.skip(lastAck*1000);
		                	if (sent==2 && cwnd<ssthresh){
		                		System.out.println();
		                		cwnd = cwnd*2;
		                	}
		                	else{
		                	System.out.println("timeout" + (lastAck+1));
		                	ssthresh =cwnd/2;
		                	cwnd = 1;
		                	sent = lastAck+1;
		                	}
		                }
	            	
				}
				
				fis.close();
			}
			else{
				System.out.println("file does not exist");
			}
			
			
			scr.close();
			socket.close();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}