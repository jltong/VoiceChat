import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;



public class InData {

	Queue<SoftData> data = new LinkedList<SoftData>() ;  //new Queue<EventOp>() ;
	private Lock lock = new ReentrantLock() ;
	private Condition wait = lock.newCondition() ;
	
	public InData(){
		;
	}
	
	public int add( SoftData op )
	{
		lock.lock() ;
		data.offer( op );
		wait.signalAll() ;
		lock.unlock() ;
		return 0 ;
	}
	public SoftData get() {
//		eventq.size();
		System.out.println("]]]]]]]]]]]]]]]");
		SoftData op = null ;
		lock.lock();
		try{
			System.out.println("[[[[[[[[[[");
			if( data.size() < 1 )
				wait.await() ;
			op = data.poll() ;
		}
		catch ( InterruptedException ex )
		{
			;
		}
		lock.unlock() ;
		
		System.out.println("have get data") ;
		return op ;
	}
	public int GetEventSize(){
		lock.lock();
		int size = data.size() ;
		lock.unlock();
		return size ;
	}
}



