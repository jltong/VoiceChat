import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;


class multirev{
	public static void main(String argv[])
	{
		InetAddress group = null ;
		try {
			group = InetAddress.getByName("224.0.0.1");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int port = 9998 ;
		MulticastSocket msr = null ;
		
		try {
			msr = new MulticastSocket(port) ;
			msr.joinGroup(group) ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] buffer = new byte[8192] ;
		System.out.println("receive data");
		while(true)
		{
			DatagramPacket dp = new DatagramPacket(buffer,buffer.length);
			try {
				msr.receive(dp) ;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String s = new String(dp.getData() , 0 , dp.getLength() ) ;
			System.out.println(s ) ;
			
		}
		
		
		
	}
	
	
	
}
