import java.util.Scanner;

import Network.*;
import Robot.RobotConstants.Command;

public class TestBed {
	private static final NetMgr net = NetMgr.getInstance();

	public static void main(String[] args) {
		net.startConn();
		while(true) {
		Scanner sc = new Scanner(System.in);
		sc.nextLine();
		// TODO Auto-generated method stub
		net.send("Alg|Ard|W|3|");
		net.receive();
		net.send("Alg|Ard|A|1|");
		net.receive();
		net.send("Alg|Ard|W|7|");
		net.receive();
		net.send("Alg|Ard|X|1|");
		net.receive();
		net.send("Alg|Ard|W|5|");
		net.receive();
		sc.nextLine();
		// TODO Auto-generated method stub
		net.send("Alg|Ard|X|7|");
		net.receive();
		}
		
//		net.send("Alg|Ard|A|1|");
//		net.receive();
//		net.send("Alg|Ard|W|3|");
//		net.receive();
//		net.send("Alg|Ard|D|1|");
//		net.receive();
//		net.send("Alg|Ard|W|4|");
//		net.receive();
//		net.send("Alg|Ard|D|1|");
//		net.receive();
//		net.send("Alg|Ard|W|1|");
//		net.receive();
//		sc.nextLine();
//		net.send("Alg|Ard|X|1|");
//		net.receive();
//		net.send("Alg|Ard|A|1|");
//		net.receive();
//		net.send("Alg|Ard|W|3|");
//		net.receive();
//		net.send("Alg|Ard|A|1|");
//		net.receive();
//		net.send("Alg|Ard|W|6|");
//		net.receive();
//		net.send("Alg|Ard|D|1|");
//		net.receive();
//		net.send("Alg|Ard|W|1|");
//		net.receive();
//		sc.nextLine();
//		net.send("Alg|Ard|X|1|");
//		net.receive();
//		net.send("Alg|Ard|A|1|");
//		net.receive();
//		net.send("Alg|Ard|W|5|");
//		net.receive();
//		net.send("Alg|Ard|A|1|");
//		net.receive();
//		net.send("Alg|Ard|W|3|");
//		net.receive();
//		net.send("Alg|Ard|D|1|");
//		net.receive();
//		net.send("Alg|Ard|W|1|");
//		net.receive();
//		sc.nextLine();
//		net.send("Alg|Ard|X|1|");
//		net.receive();
//		net.send("Alg|Ard|A|1|");
//		net.receive();
//		net.send("Alg|Ard|W|3|");
//		net.receive();
//		net.send("Alg|Ard|A|1|");
//		net.receive();
//		net.send("Alg|Ard|W|5|");
//		net.receive();
//		net.send("Alg|Ard|D|1|");
//		net.receive();
//		net.send("Alg|Ard|W|1|");
//		net.receive();
//		sc.nextLine();
//		net.send("Alg|Ard|X|1|");
//		net.receive();
//		net.send("Alg|Ard|A|1|");
//		net.receive();
//		net.send("Alg|Ard|W|7|");
//		
//			
////		net.send("Alg|Ard|0|1\n");
//		//Keep trying to connect if fail to connec
//		
	}
}