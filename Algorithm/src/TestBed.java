import Network.*;
import javafx.concurrent.*;

public class TestBed {
	private static NetMgr net;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		net = new NetMgr("",8080);
		net.startConn();
		
		Thread reading = new Thread();
	}
}