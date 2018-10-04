package Network;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

//Class to Manage Connection
public class NetMgr {

	private String ipAddr;
	private int port = 8080;
	private static Socket socket = null;

	private BufferedWriter out;
	private BufferedReader in;
	
	private static NetMgr netMgr = null;
	
	public NetMgr() {
		this.ipAddr = "192.168.18.18";
		this.port = 8080;
	}
	
	public static NetMgr getInstance() {
		if(netMgr == null)
			netMgr = new NetMgr();
		return netMgr;
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
			System.out.println("Connection Established!");
		} catch (UnknownHostException e) {
			System.out.println("Connection Failed (UnknownHostException)! "+e.toString());
			return false;
		} catch (IOException e) {
			System.out.println("Connection Failed (IOException)! "+e.toString());
			return false;
		}
		catch (Exception e) {
			System.out.println("Connection Failed (IOException)! "+e.toString());
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
			
		} catch (Exception e) {
			
			System.out.println("Sending Message Failed (IOException)!");
			e.printStackTrace();
			return false;
		}
		return true;

	}

	// Send Message
	public String recieve() {
		try {
			System.out.println("Recieving Message...");
			// KIV determine format for message traversal
			String recievedMsg = in.readLine();
			
			if(recievedMsg!=null&&recievedMsg.length()>0) {
				System.out.println("Recieved Message: "+recievedMsg);
				return recievedMsg;
			}
			
		} catch (Exception e) {
			System.out.println("Recieving Message Failed!");
			e.printStackTrace();
		}
		
		return null;
	}
}
