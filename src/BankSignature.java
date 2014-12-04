import java.util.ArrayList;


public class BankSignature {
	public final int G,H,gu,hu,Hbar,hbar,z;

	public BankSignature(int G, int H, int gu, int hu, int Hbar, int hbar, int z){
		this.G = G;
		this.H = H;
		this.gu = gu;
		this.hu = hu;
		this.Hbar = Hbar;
		this.hbar = hbar;
		this.z = z;
	}
	
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
