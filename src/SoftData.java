
/* data.op definition;
 * op = 1; talk request
 * op = 2; refresh the friend infomation
 * op = 3; voice talk; 
 * op = 4; text talk
 */
public class SoftData implements java.io.Serializable {
	int op ;//set type of operation
	boolean accept;// accept  = true; reject = false;
	boolean isReply;//true for request sender;false for the reply sender,that is request receiver.
	String srcip;
	String destip; //target IP
	
	String Msg; // text talk;
	
	friendInfo friendinfo; //personal infomation;
	Object sound ;// store the voice 
	
	public SoftData()
	{
		srcip = new String("");
		destip = new String("") ;
		sound = new String("") ;
		Msg =new String("");
	}
}
