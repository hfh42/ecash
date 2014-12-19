package ecash.signature.bank;
import java.util.ArrayList;

/**
 * Data class that represents the Banks Signature on a Coin
 */
public class BKSig {
	public final int G,H,gu,hu,Hbar,hbar,z;

	public BKSig(int G, int H, int gu, int hu, int Hbar, int hbar, int z){
		this.G = G;
		this.H = H;
		this.gu = gu;
		this.hu = hu;
		this.Hbar = Hbar;
		this.hbar = hbar;
		this.z = z;
	}
	
	/**
	 * Gives a list of elements in the ecash.signature which should be hashed
	 * @return list of all fields except z
	 */
	public ArrayList<Integer> getList(){
		ArrayList<Integer> res = new ArrayList<Integer>();
		res.add(G);
		res.add(H);
		res.add(gu);
		res.add(hu);
		res.add(Hbar);
		res.add(hbar);
		return res;
	}
}
