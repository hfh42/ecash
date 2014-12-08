package ecash.exception;
public class NoCoinException extends Exception {
	
	public NoCoinException(){
		
	}
	
	public NoCoinException(String msg){
		super(msg);
	}

}