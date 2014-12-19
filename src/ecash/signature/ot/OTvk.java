package ecash.signature.ot;

/**
 * Data class that represents a Verification Key of the One-Time Signature Scheme
 */
public class OTvk {
	public final int x, a;
	
	public OTvk(int x, int a){
		this.x = x;
		this.a = a;
	}
}
