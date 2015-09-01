import java.awt.List;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;

	
	
public class Peer extends Thread implements Serializable{
	static int count=0;

	Coordinate coordinate=new Coordinate(0,0,0,0);
	
	String identifier;
	String ip;
	ArrayList<Peer> neighbors= new ArrayList<Peer>();
	Hashtable<String, Integer>  hashTable=new Hashtable<String,Integer>();
	
	DataInputStream input;
	DataOutputStream output;
	
	public Peer(){
		// This connection listening should be done continuously , 
		// More than one connection at a time might be needed to establish
		// may be the following should be done using threads
		/*
		try{
			ServerSocket serverSocket =new ServerSocket(8000);
			Socket socket = new Socket();
			socket=serverSocket.accept();
			input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            
            int code= input.readInt();
            System.out.println("Request" + code + " has been recieved.");
            
			
		}catch(Exception e){
			e.printStackTrace();
		}
		*/
	}
	
	public void setIdentifier(String id){
		this.identifier=id;
	}
	public String getIdentifier(){
		return this.identifier;
		
	}
	public void setCoordinate(double xStart,double xEnd,double yStart, double yEnd){
		coordinate.xStart=xStart;
		coordinate.xEnd=xEnd;
		coordinate.yStart=yStart;
		coordinate.yEnd=yEnd;
		
	}
	public Coordinate getCoordinate(){
		return coordinate;
	}
	public static void main (String args[]){
		System.out.println("a".hashCode());
	}
	public void setIP(String ip){
		this.ip=ip;
	}
	public String getIP(){
		return ip;
	}

	@Override
	public void run() {
		try{
			System.out.println(this.identifier+" is listening for connection..");
			ServerSocket serverSocket =new ServerSocket(9876);
			Socket socket = new Socket();
			System.out.println(this.identifier+" is listening for connection part 2");

			socket=serverSocket.accept();
			System.out.println(this.identifier+" is listening for connection part 3");

			input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            
            int code= input.readInt();
            System.out.println("Request" + code + " has been recieved.");
            
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
