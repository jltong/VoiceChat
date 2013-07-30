import java.net.Socket;

public class connection{
	Socket sock;
	String ipaddr;
	
	connection(){
		sock=null;
		ipaddr="0.0.0.0";
	}
	
	connection(Socket sock,String ipaddr){
		this.sock=sock;
		this.ipaddr=ipaddr;
	}
}