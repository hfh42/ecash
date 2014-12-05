import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Bank {
	private int G, H;
	private int w;
	
	private ArrayList<Integer> users = new ArrayList<Integer>();
	
	private Map<Integer,Integer> withdrawSession = new HashMap<Integer,Integer>();
	
	private Map<OTvk,Integer> usedCoins = new HashMap<OTvk,Integer>();
	private ArrayList<Integer> usedPids	= new ArrayList<Integer>();
	
	public Bank(){
		G = Util.getRandom();
		w = Util.getRandom();
		H = Util.modPow(G, w);
	}

	public int getG() {return G;}
	public int getH() {return H;}

	
	/*
	 * User Registration
	 */

	public int register(int gu){
		if(isRegisteredUser(gu)) throw new IllegalArgumentException("User allready registered");
		
		users.add(gu);
		return calh(gu);
	}
	

	/*
	 * Withdraw methods
	 */
	public Pair withdrawCommit(int gu){
		if(!isRegisteredUser(gu)) throw new IllegalArgumentException("Not a registered user"); 
		
		int v = Util.getRandom();
		int Hbar = Util.modPow(G, v);
		int hbar = Util.modPow(gu, v);
		
		withdrawSession.put(gu,v);
		
		return new Pair(Hbar,hbar);
	}
	
	public int withdrawResponse(int gu, int e){
		if(!isRegisteredUser(gu)) throw new IllegalArgumentException("Not a registered user");
		
		int v = withdrawSession.remove(gu);		
		int z = Util.modAdd(Util.modMult(e, w), v);
		return z;
	}
	
	/*
	 * Deposit
	 */
	
	public void deposit(OTvk c, BKSig sigmaB, Pair sigma, int pid, Shop shop) throws InvalidCoinException, DoubleDepositException, InvalidPidException, DoubleSpendingException{
		checkShopId(pid, shop);
		
		if(!Util.BKVer(G, H, c, sigmaB) && !Util.OTVer(c, pid, sigma)) throw new InvalidCoinException();
		
		if(usedCoins.keySet().contains(c)){
			int otherpid = usedCoins.get(c);
			// TODO: Find cheating user
			throw new DoubleSpendingException();
		}
		
		usedCoins.put(c,pid); // TODO: save c, sigma, pid
		usedPids.add(pid);
	}
	
	private void checkShopId(int pid, Shop shop) throws DoubleDepositException, InvalidPidException{
		if(usedPids.contains(pid)) throw new DoubleDepositException();
		
		int id = pid - shop.getShopId();
		if(0 > id || id > 100000) throw new InvalidPidException();
	}
	
	/*
	 *	Helpers 
	 */
	
	private int calh(int g){
		return Util.modPow(g,w);
	}
			
	private boolean isRegisteredUser(int gu){
		return users.contains(gu);
	}
	
}
