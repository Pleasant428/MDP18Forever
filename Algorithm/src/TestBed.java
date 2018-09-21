import Network.*;
import javafx.concurrent.*;

public class TestBed {
	private static NetMgr net;

	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
		net = new NetMgr("192.168.18.18", 8080);
		net.startConn();
		System.out.println("Starting");
		String msg = "";
		while(true) {
			
			net.recieve(msg);
			System.out.println(msg);
		}
		
	}
}