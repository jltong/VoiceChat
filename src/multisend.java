import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;


public class multisend extends Thread {
	String s = "hello multi\n" ;
	int port = 9998 ;
	InetAddress group = null ;
	MulticastSocket socket = null ;
	multisend()
	{
		try{
			group = InetAddress.getByName("224.0.0.1") ;
			socket = new MulticastSocket(port) ;
			socket.setTimeToLive( 1 ) ;
			socket.joinGroup(group) ;
		}
		catch(Exception e)
		{
			System.out.println("Error: " + e ) ;
		}
	}
	public void run()
	{
		while(true)
		{
			DatagramPacket packet = null ;
			byte data[] = s.getBytes() ;
			packet = new DatagramPacket(data,data.length , group , port ) ;
			System.out.println( new String(data) ) ;
			try {
				socket.send(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Thread.sleep(2000) ;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main( String args[] )
	{
		new multisend().start() ;
	}
	
}


