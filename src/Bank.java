import java.util.ArrayList;


public class Bank {
	private int G, H;
	private int w;
	
	private ArrayList<Integer> users;
	
	public Bank(){
		G = Util.getRandom();
		w = Util.getRandom();
		H = Util.modPow(G, w);
		
		users = new ArrayList<Integer>();
	}

	public int getG() {return G;}
	public int getH() {return H;}

	private int calh(int g){
		return Util.modPow(g,w);
	}
	
	public int register(int gu){
		users.add(gu);
		return calh(gu);
	}
	
	
	/*
	 * Withdraw methods
	 */
	public Pair withdrawCommit(){
		
		return null;
	}
	
	public int withdrawResponse(int e){
		
		return 0;
	}
	
}
