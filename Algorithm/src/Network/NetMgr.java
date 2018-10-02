package Network;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

//Class to Manage Connection
public class NetMgr {

	private String ipAddr;
	private int port;
	private Socket socket = null;

	private BufferedWriter out;
	private BufferedReader in;
	
	
	
	public NetMgr(String ipAddr, int port) {
		this.ipAddr = ipAddr;
		this.port = port;
	}

	// Getter and Setters for ipAddr and port
	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	// Initiate Connection with RPI T-for successful connection. F for failed
	// connection
	public boolean startConn() {
		// Init Connection
		try {
			System.out.println("Initiating Connection with RPI...");
			socket = new Socket(ipAddr, port);
			// Init in and out
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		} catch (UnknownHostException e) {
			
			System.out.println("Connection Failed (UnknownHostException)!");
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			
			System.out.println("Connection Failed (IOException)!");
			e.printStackTrace();
			return false;
		}

		return true;

	}

	// Close connection with RPI
	public boolean closeConn() {
		try {
			System.out.println("Closing Connection...");
			socket.close();
			out.close();
			in.close();

		} catch (IOException e) {
			
			System.out.println("Unable to Close Connection (IOException)!");
			e.printStackTrace();
			return false;
		}
		return true;

	}

	// Send Message
	public boolean send(String msg) {
		try {
			// KIV determine format for message traversal
			System.out.println("Sending Message...");
			out.write(msg);
			out.flush();
			System.out.println("Message: "+msg+" sent!");
			
		} catch (IOException e) {
			
			System.out.println("Sending Message Failed (IOException)!");
			e.printStackTrace();
			return false;
		}
		return true;

	}

	// Send Message
	public String recieve(String msg) {
		try {
			System.out.println("Recieving Message...");
			// KIV determine format for message traversal
			String recievedMsg = in.readLine();
			
			if(recievedMsg!=null&&recievedMsg.length()>0) {
				System.out.println("Recieved Message: "+recievedMsg);
				return recievedMsg;
			}
			
		} catch (Exception e) {
			System.out.println("Recieving Message Failed (IOException)!");
			e.printStackTrace();
		}
		
		return null;
	}
	
	// String Tokenizer
	
	

}
