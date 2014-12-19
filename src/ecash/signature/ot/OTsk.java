package ecash.signature.ot;

/**
 * Data class that represents a Signing Key of the One-Time Signature Scheme
 */
public class OTsk {
	public final int w1,w2,v1,v2;
	
	public OTsk(int w1, int w2, int v1, int v2){
		this.w1 = w1; 
		this.w2 = w2; 
		this.v1 = v1; 
		this.v2 = v2;
	}
}
