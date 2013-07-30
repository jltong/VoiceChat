import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class ConnectionPool{
	final int maxconnection=100;//exist at most 100 link at the same time
	TreeMap<String,Socket> connection= new TreeMap<String,Socket>();

	public int getnum(){
		return connection.size();
	}
	
	public String[] getip(){
		Iterator<Entry<String , Socket>> it = connection.entrySet().iterator() ;
		String ip = null ;
		String[] ipmemory=new String[connection.size()+1];
		
		int count=0;
		try {
			String s=InetAddress.getLocalHost().toString();
			ipmemory[count]=s.substring(s.indexOf('/')+1);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		count++;
		
		while( it.hasNext() )
		{
			Map.Entry<String , Socket> entry = (Map.Entry<String, Socket>)it.next() ;
			ip = entry.getKey() ;
			ipmemory[count]=new String(ip);
			count++;
		}
		
		for (int i=0;i<count;i++)
		{
			System.out.println(ipmemory[i]);
		}
		
		return ipmemory;
	}
	
	public Socket getsocket(String ipaddr)
	{
		return connection.get(ipaddr);
	}

	public void addsocket(Socket sock,String ipaddr)
	{
		if (this.getsocket(ipaddr)==null)
		{
			connection.put(ipaddr,sock);
			System.out.println("Add successfully. The connection number is "+this.getnum());
		}
		else
		{
			//to-do
		}
	}
	
	public void destroysocket(String ipaddr){
	
		if( connection.get(ipaddr) == null )
		{
			System.out.println(" error ; socket not exist in function destroysocket class connectionpool") ;
			return ;
		}
			
		try {
			connection.get(ipaddr).close() ;
			connection.remove(ipaddr) ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		System.out.println("Destroy successfully. The connection number is "+this.getnum());
	}

	public void  sendheart(){		
		Iterator<Entry<String , Socket>> it = connection.entrySet().iterator() ;
		String ip = null ;
		
		while( it.hasNext() )
		{
			Map.Entry<String , Socket> entry = (Map.Entry<String, Socket>)it.next() ;
			ip = entry.getKey() ;
			this.senddata(ip, new String("")) ;
		}
	}
	
	public void senddata(String ipaddr,Object context){
		Socket sock=getsocket(ipaddr);
    	ObjectOutputStream output;
		try {
			output = new ObjectOutputStream(sock.getOutputStream());
			System.out.println("send byte "+context.toString());
			output.writeObject(context);
			output.flush();
		}
		catch( SocketException e )
		{
			;
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Send successfully.");
	}
}