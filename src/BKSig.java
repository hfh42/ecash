import java.math.BigInteger;
import java.util.ArrayList;


public class BKSig {
	public final BigInteger G,H,gu,hu,Hbar,hbar,z;

	public BKSig(BigInteger G, BigInteger H, BigInteger gu, BigInteger hu, BigInteger Hbar, BigInteger hbar, BigInteger z){
		this.G = G;
		this.H = H;
		this.gu = gu;
		this.hu = hu;
		this.Hbar = Hbar;
		this.hbar = hbar;
		this.z = z;
	}
	
	/**
	 * Gives a list of elements in the signature which should be hashed
	 * @return list of all fields except z
	 */
	public ArrayList<BigInteger> getList(){
		ArrayList<BigInteger> res = new ArrayList<BigInteger>();
		res.add(G);
		res.add(H);
		res.add(gu);
		res.add(hu);
		res.add(Hbar);
		res.add(hbar);
		return res;
	}
}
