package Network;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import Robot.RobotConstants;

//Class to Manage Connection
public class NetMgr {

	private String ipAddr;
	private int port = 8080;
	private static Socket socket = null;

	private BufferedWriter out;
	private BufferedReader in;
	private int msgCounter = 0;
	
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
			
		} catch (IOException e) {
			System.out.println("Connection Failed (IOException)! "+e.toString());
		}
		catch (Exception e) {
			System.out.println("Connection Failed (IOException)! "+e.toString());
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
			msgCounter++;
			System.out.println(msgCounter+" Message: "+msg+" sent!");
			
		}
		catch (IOException e) {
			System.out.println("Sending Message Failed (IOException)!");
			if(socket.isConnected())
				System.out.println("Connection still Established!");
			else {
				while(true)
				{
					System.out.println("Connection disrupted! Trying to Reconnect!");
					if(netMgr.startConn())
						break;
				}
			}
			return netMgr.send(msg);
		}
		catch (Exception e) {
			System.out.println("Sending Message Failed (IOException)!");
			e.printStackTrace();
			return false;
		}
		return true;

	}

	// Receive Message Waiting
	public String receive() {
		try {
			System.out.println("Receiving Message...");
			// KIV determine format for message traversal
			String receivedMsg = in.readLine();
			
			if(receivedMsg!=null&&receivedMsg.length()>0) {
				System.out.println("Received Message: "+receivedMsg);
				return receivedMsg;
			}
			
		} catch (IOException e) {
			System.out.println("Recieving Message Failed (IOException)!");
			if(socket.isConnected())
				System.out.println("Connection still Established!");
			else {
				while(true)
				{
					System.out.println("Connection disrupted! Trying to Reconnect!");
					if(netMgr.startConn())
						break;
				}
			}
			return receive();
		} catch (Exception e) {
			System.out.println("Receiving Message Failed!");
			e.printStackTrace();
		}
		
		return null;
	}
	
	// Receive Message Repeat non waiting receive
	public String receive(String msg) {
		try {
			
			//Set timer to resend specified msg if nothing received
			Timer wait = new Timer();
			TimerTask retransmit = new TimerTask() {
				public void run() {
					netMgr.send(msg);
				}
			};
			wait.schedule(retransmit, RobotConstants.WAIT_TIME, RobotConstants.WAIT_TIME);
			System.out.println("Receiving Message");
			// KIV determine format for message traversal
			String recievedMsg = in.readLine();
			wait.cancel();
			wait.purge();
			if (recievedMsg != null && recievedMsg.length() > 0) {
				System.out.println("Received Message: " + recievedMsg);
				return recievedMsg;
			}

		} catch (IOException e) {
			System.out.println("Recieving Message Failed (IOException)!");
			if(socket.isConnected())
				System.out.println("Connection still Established!");
			else {
				while(true)
				{
					System.out.println("Connection disrupted! Trying to Reconnect!");
					if(netMgr.startConn())
						break;
				}
			}
			return receive(msg);
		} catch (Exception e) {
			System.out.println("Receiving Message Failed!");
			e.printStackTrace();
		}

		return null;
	}
}
