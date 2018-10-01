import java.util.Scanner;

import Network.*;
import Robot.RobotConstants;
import Robot.RobotConstants.Command;
import javafx.concurrent.*;

public class TestBed {
	private static NetMgr net;

	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
		System.out.println(Command.FORWARD.ordinal());
		net = new NetMgr("192.168.18.18", 8080);
		net.startConn();
		System.out.println("Starting");
		Scanner sc = new Scanner(System.in);
		String msg = "";
		while(true) {
			net.recieve(msg);
			System.out.println(msg);
			System.out.print("Sending: ");
			net.send(sc.nextLine());
		}
		
	}
}