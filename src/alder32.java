import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class alder32 {
	static public void main( String argv[] )
	{
		alder32 en = new alder32() ;
		en.encryptmd5("haha");
		en.encryptalder32() ;
		return ;
	}
	public byte[] encryptmd5( String message )
	{
		
		String password = "12345" ;
		MessageDigest encrypt = null;
		try {
			encrypt = MessageDigest.getInstance( "MD5" );
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		encrypt.update( password.getBytes() ) ;
		byte byteData[] = encrypt.digest();
		
		BigInteger bigInt = new BigInteger(1,byteData);
		String hashtext = bigInt.toString(16);
		// Now we need to zero pad it if you actually want the full 32 chars.
		while(hashtext.length() < 32 ){
		  hashtext = "0"+hashtext;
		}
		
		System.out.println( hashtext ) ;
		
		encrypt.reset() ;
		
		System.out.println( password ) ;
		
		encrypt.update( password.getBytes() );
		byteData = encrypt.digest();
		
		bigInt = new BigInteger(1,byteData);
		String hashtext1 = bigInt.toString(16);
		// Now we need to zero pad it if you actually want the full 32 chars.
		while(hashtext1.length() < 32 ){
		  hashtext1 = "0"+hashtext1;
		}
		
		System.out.println( hashtext1 ) ;
		
		System.out.println( hashtext.equals(hashtext1) ) ;
		
		//System.out.println( byteData.equals( encrypt.digest() ) ) ;

		
		return byteData ;
	}
	

	String getMd5String( String context , MessageDigest encrypt )
	{
		byte[] byteData = null ;
		BigInteger bigInt = null ;
		String hashtext = null ;

		
		encrypt.reset() ;
		encrypt.update( context.getBytes() ) ;
		byteData = encrypt.digest() ;
		bigInt = new BigInteger(1, byteData );
		hashtext = bigInt.toString(16);
		// Now we need to zero pad it if you actually want the full 32 chars.
		while(hashtext.length() < 32 ){
		  hashtext = "0"+hashtext;
		}
		
		return hashtext ;
	}
	
	
	public long encryptalder32( )
	{
		
		MessageDigest encrypt = null;
		try {
			encrypt = MessageDigest.getInstance( "MD5" );
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		String message = new String("12345") ;
		int aldermaxnum = message.length() ;
		
		byte[] byteData = null ;
		BigInteger bigInt = null ;
		
		long sum1 = 0 ;
		long sum2 = 0 ;
		long num = 0 ;
		
		sum1 = 0 ;
		sum2 = 0 ;
		for( int i = 0 ; i < message.length() ; i ++ )
		{
			sum1 = ( sum1 + ( message.charAt(i) - 0 ) ) % 65521 ; 
			sum2 = ( sum2 + (aldermaxnum - i ) * ( message.charAt(i) - 0 ) ) % 65521 ;
		}
		sum1 = ( sum1 + 1 ) % 65521 ;
		sum2 = ( sum2 + aldermaxnum ) % 65521 ;
		num = ( sum2 << 16 + sum1 );
	
		System.out.println("old num : " + num) ;
		
		String message2 = new String(message) ;
		message2 = "1" + message2 + "1" ;
		
		
		sum1 = 0 ;
		sum2 = 0 ;
		for( int i = 0 ; i < aldermaxnum ; i ++ )
		{
			sum1 = ( sum1 + ( message2.charAt(i) - 0 ) ) % 65521 ; 
			sum2 = ( sum2 + (aldermaxnum - i ) * ( message2.charAt(i) - 0 ) ) % 65521 ;
		}
		sum1 = ( sum1 + 1 ) % 65521 ;
		sum2 = ( sum2 + aldermaxnum ) % 65521 ;

		
		int i = aldermaxnum ;
		System.out.println("max num : " + aldermaxnum) ;

		encrypt.reset() ;
		
		encrypt.update( message.getBytes() ) ;
		byteData = encrypt.digest() ;
		bigInt = new BigInteger(1, byteData );
		String hashtext = bigInt.toString(16);
		// Now we need to zero pad it if you actually want the full 32 chars.
		while(hashtext.length() < 32 ){
		  hashtext = "0"+hashtext;
		}
		
//		System.out.println( " origin MD5 num : " + encrypt.digest() ) ;

		System.out.println("\n\n") ;
		while( true )
		{
//			System.out.print(i + "  ");
//			System.out.println( "this time index : " + (i - aldermaxnum) + "  new num :" + (sum2 << 16 + sum1 ) ) ;

			encrypt.reset() ;
			encrypt.update( (message2.substring(i-aldermaxnum, i)).getBytes() ) ;

			
			
//			System.out.println( " also MD5 num : " + encrypt.digest() ) ;

			byteData = encrypt.digest() ;
			bigInt = new BigInteger(1, byteData );
			String hashtext1 = bigInt.toString(16);
			// Now we need to zero pad it if you actually want the full 32 chars.
			while(hashtext1.length() < 32 ){
			  hashtext1 = "0"+hashtext1;
			}
			
			
			if( (sum2 << 16 + sum1 ) == num )
				System.out.println( "yes : alder index " + (i - aldermaxnum) ) ;
			if( hashtext.equals(hashtext1) )
				System.out.println( "yes : MD5 index " + (i - aldermaxnum) ) ;
			
			if( i >= message2.length() )
				break ;
			
			sum1 = ( sum1 + message2.charAt(i) - message2.charAt(i-aldermaxnum) ) % 65521 ;
			sum2 = ( sum2 + sum1 - 1 - aldermaxnum * message2.charAt(i-aldermaxnum) ) % 65521 ;
			
			
			i ++ ;
		}
		
		
		return num ;
	}
	
}
