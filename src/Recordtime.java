import java.util.TreeMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//  for record time 

public class Recordtime {
	TreeMap<String , Integer> average = new TreeMap<String , Integer>() ;
	TreeMap<String , Integer> lasttime = new TreeMap<String , Integer>() ;
	
	private Lock lock = new ReentrantLock() ;
	private Condition wait = lock.newCondition() ;
	
	public Recordtime(){
		;
	}
	
	public void add( String ip )
	{
		lock.lock() ;
		if( average.get(ip) != null )
			System.out.println( "in recordtime class , add function , already existing" ) ;
//		if(  )
		Integer ave = new Integer( 10 ) ;
		average.put(ip, ave) ;
		Integer last = new Integer( 1 ) ;
		lasttime.put(ip, last) ;
		lock.unlock() ;
	}
	
	public String[] testdeath()
	{
		String[] list  ; //= new String()[] ;
		lock.lock() ;
		
		
		
		lock.unlock() ;
		return null;
	}
	
}
