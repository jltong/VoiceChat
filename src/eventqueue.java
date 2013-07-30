import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;



public class eventqueue {

	Queue<EventOp> eventq = new LinkedList<EventOp>() ;  //new Queue<EventOp>() ;
	private Lock lock = new ReentrantLock() ;
	private Condition wait = lock.newCondition() ;
	
	public eventqueue(){
		;
	}
	
	public int add( EventOp op )
	{
		lock.lock() ;
		eventq.offer( op );
		wait.signalAll() ;
		lock.unlock() ;
		System.out.println("length +" + eventq.size() ) ;
		return 0 ;
	}
	public EventOp get() {
//		eventq.size();
		EventOp op = null ;
		lock.lock();
		try{
			if( eventq.size() < 1 )
				wait.await() ;
			op = eventq.poll() ;
		}
		catch ( InterruptedException ex )
		{
			;
		}
		lock.unlock() ;
		return op ;
	}
	public int GetEventSize(){
		lock.lock();
		int size = eventq.size() ;
		lock.unlock();
		return size ;
	}
}
