import java.util.Scanner;

import Network.*;
import Robot.RobotConstants.Command;

public class TestBed {
	private static NetMgr net;

	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
		System.out.println(Command.FORWARD.ordinal());
		net = NetMgr.getInstance();
		//Keep trying to connect if fail to connect
		
			
		System.out.println("Starting");
		Scanner sc = new Scanner(System.in);
		String msg = "";
		while(true) {
			System.out.print("Sending: Alg|Ard|S|0");
			net.send(sc.nextLine());
			System.out.println(net.receive());
		}
		
	}
}