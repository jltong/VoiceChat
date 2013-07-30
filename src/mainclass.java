import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.awt.Button;
import java.awt.Frame;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class mainclass {

	eventqueue eventq = new eventqueue() ;
	InData indata = new InData() ;
//	InData outdata = new InData();
	
	server myserver ;
	
	public String[] getip(){
		return myserver.pool.getip();
	}
	
	public mainclass()
	{
		myserver = new server() ;
		
		new Thread(new HttpServer(this)).start();
		
		new Thread(new Handle()).start() ;
	
		Timer timer = new Timer() ;
		timer.schedule( new Mytimer() , 1000 , 2000 ) ;  // every 2 seconds send one 

		Timer timer1 = new Timer() ;
		timer1.schedule( new alivecheck() , 2000 , 2000) ;
		
	}
	
	public static void main( String argv[] )
	{
		
		mainclass downlayer = new mainclass() ;
		new ChatConsole( downlayer ) ;
	}
	
	
	class Handle implements Runnable{

		public Handle()
		{
			System.out.println("main handle initial");
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true)
			{
				EventOp op = eventq.get() ;
				if( op.op == operationtype.heart)
				{
					myserver.pool.sendheart() ;
				}	
				else if( op.op == operationtype.heartcome )
				{
					myserver.heartb.reflash(op.ip) ;
					
				}
				else if( op.op == operationtype.datacome )
				{					
					System.out.println( op.ip + " : data has come in handle Thread" ) ; 
					SoftData data = ( SoftData ) op.data ; //.new SoftData() ;
					data.destip = new String(op.ip) ;
					
					SoftData dat = (SoftData) op.data ;
					System.out.println("type "+ dat.op + "  " + dat.Msg ) ;
					
					mainclass.this.indata.add(data) ;

				}
				else if( op.op == operationtype.checkalive )
				{
					myserver.heartb.check() ;
					System.out.println("checkalive");
				}else if( op.op == operationtype.senddata )
				{
					System.out.println("send data") ;

					myserver.pool.senddata(new String(op.ip),op.data ) ;
				}
					
			}
			
		}
	}
	
	public void softsenddata( SoftData data )
	{
		EventOp op = new EventOp() ;
		op.op = operationtype.senddata ;
		op.ip = new String(data.destip);
		op.data = data ;
		this.eventq.add(op) ;
	}
	
	class Mytimer extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			EventOp op = new EventOp() ;
			op.op = operationtype.heart ;
			
			eventq.add(op) ;
		}
		
	}
	
	class alivecheck extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			EventOp op = new EventOp();
			op.op = operationtype.checkalive ;
			
			eventq.add( op ) ;
		}
	}
	

	
	class heartbeat{
		
		TreeMap<String,TimeRecord> timerecord = null ;
		
		private Lock lock = new ReentrantLock() ;
		private Condition wait = lock.newCondition() ;
		
		public heartbeat(){
			timerecord = new TreeMap<String , TimeRecord>() ;
		}
		
		public boolean add( String ip )
		{
			lock.lock() ;
			
			if( timerecord.get(ip) != null )
			{
				return false ;
			}
			
			TimeRecord time = new TimeRecord() ;
			time.average = 2000 ;
			time.timestart = System.currentTimeMillis() ;
			timerecord.put(ip, time) ;
			
			lock.unlock() ;
			return true;
		}
		
		public boolean check()
		{
			Iterator<Entry<String , TimeRecord>> it = timerecord.entrySet().iterator() ;
			String ip = null ;
			Queue<String> ipdel = new LinkedList<String>() ;
			TimeRecord myrecord = null ;
			long cur = System.currentTimeMillis() ;

			long sub = 0 ;
			
			lock.lock() ;
			while( it.hasNext() )
			{
				Map.Entry<String , TimeRecord> entry = (Map.Entry<String, TimeRecord>)it.next() ;
				myrecord = entry.getValue() ;
				sub = cur - myrecord.timestart ;
				System.out.println( "sub : " + sub ) ;
				if( sub == 0 )
					;
				else
				{
					if( sub / myrecord.average > 5 )
					{
						ip = entry.getKey() ;
						timerecord.remove(ip) ;
						ipdel.offer(ip ) ;
						System.out.println(ip + " : time out ") ;
					}
				}
			}
			
			lock.unlock() ;
			
			while( ipdel.size() > 0 )
			{
				ip = ipdel.poll() ;
				mainclass.this.myserver.pool.destroysocket( ip ) ;
			}	
			return true;
		}
		
		public boolean reflash( String ip)
		{
			long cur = System.currentTimeMillis() ;
			TimeRecord myrecord = null ;
			lock.lock() ;
			
			myrecord = timerecord.get(ip) ;
			myrecord.average = (int) (( myrecord.average + cur - myrecord.timestart ) / 2) ;
			myrecord.timestart = cur ;
			lock.unlock() ;
			return true ;
		}
		
		public boolean delelt( String ip )
		{
			lock.lock() ;
			if( timerecord.get(ip) != null )
				timerecord.remove(ip) ;
			lock.unlock() ;
			return true; 
		}
		
		class TimeRecord{
			int average ;   // average time 
			long timestart ;  // this heart start time
		}
	}
	
	
	
	
	public class server extends JFrame{

		ConnectionPool pool=new ConnectionPool();
		
		private int yymode=1;	
		
		heartbeat heartb=new heartbeat();
		
		void dealheart(){
			System.out.println("heartbag end  "+pool.getnum());
		}
				
		void judge(Object context , String ipaddr )
		{
			if (context.equals(""))//´ý²âÊÔ
			{
				EventOp op = new EventOp() ;
				op.op = operationtype.heartcome ;
				op.ip = new String( ipaddr ) ;
				mainclass.this.eventq.add( op ) ;
				dealheart();//to-do
			}
			else
			{
				System.out.println("------------------");
				EventOp op = new EventOp() ;
				op.op = operationtype.datacome ;
				op.ip = new String(ipaddr) ;
				op.data = context ;
				mainclass.this.eventq.add( op ) ;
			}
		}
		
		void search() 
		{
			
			
			int port = 9998 ;
			InetAddress group = null ;
			MulticastSocket socket = null ;
			try{
				group = InetAddress.getByName("224.0.0.1") ;
				socket = new MulticastSocket(port) ;
				socket.setTimeToLive( 5 ) ;
				socket.joinGroup(group) ;
			}
			catch(Exception e)
			{
				System.out.println("Error: " + e ) ;
			}

			DatagramPacket packet = null ;
			
			String s = null;
			try {
				s = InetAddress.getLocalHost().toString();
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			byte data[] = s.substring(s.indexOf('/')+1).getBytes() ;
			packet = new DatagramPacket(data,data.length , group , port ) ;
			try {
				socket.send(packet);
				System.out.println("\n\n----------------") ;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			
		}
		
		void init(){
			search();
		}
		
		public server(){		
			StartServer startserver = new StartServer();
			new Thread(startserver).start();

			broadcastlistener broadcast = new broadcastlistener();
			new Thread(broadcast).start() ;
			
			init();
			
			// send audio packet to ..
	//		this.audio.capture("172.26.38.83");
		}
		
		
		class broadcastlistener implements Runnable{		
			public broadcastlistener(){
			}

			@Override
			public void run() {
				
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
				while(true)
				{
					DatagramPacket dp = new DatagramPacket(buffer,buffer.length);
					try {
						msr.receive(dp) ;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String ips = new String(dp.getData() , 0 , dp.getLength() ) ;
					
//					System.out.println(s ) ;
					
					if (pool.getsocket(ips)==null){
						Socket newsock = null;
						try {
							String s = InetAddress.getLocalHost().toString();
						
							if( s.substring(s.indexOf('/')+1).equals( ips) )
								continue ;
							
							newsock = new Socket(ips , 8889 );
							pool.addsocket(newsock,ips);
							
							heartb.add(ips) ;
							
							SocketListener socketlistener = new SocketListener(ips);
							new Thread(socketlistener).start();
							
						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}					
				}
			}
			
		}
		
		class StartServer implements Runnable{		
			public StartServer(){
			}

			@Override
			public void run() {
				try {
					ServerSocket serversocket=new ServerSocket(8889);
					while (true)
					{
						Socket socket=serversocket.accept();
						InetAddress ipaddr=socket.getInetAddress();
						String ips=ipaddr.toString().substring(1);
						
						if (pool.getsocket(ips)==null){
							pool.addsocket(socket,ips);
							
							heartb.add(ips) ;
							
							SocketListener socketlistener = new SocketListener(ips);
							new Thread(socketlistener).start();
						}
					}				
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		class SocketListener implements Runnable{
			private String ips;
			
			public SocketListener(String ips){
				this.ips=new String(ips);
			}
			
			@Override
			public void run() {
				Socket sock=pool.getsocket(ips);
				try {
					while (true)
					{
						try{
							ObjectInputStream input=new ObjectInputStream(sock.getInputStream());
							System.out.println("recving--------");
							Object context=input.readObject();
							judge(context, ips );
						} catch( SocketException e ){
							System.out.println( "---out of line");
							if(pool.getsocket(ips) == null)
							{
								heartb.delelt(ips) ;
								return ;
							}
							mainclass.this.myserver.pool.destroysocket(ips) ;
							heartb.delelt(ips);
							yymode=0;
							break ;
						}	catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.out.println( ips + " : this thread dead" ) ;	
			}
		}
	}
}
