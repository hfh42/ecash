import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Bank {
	private int G, H;
	private int w;
	
	private ArrayList<Integer> users = new ArrayList<Integer>();
	
	private Map<Integer,Integer> withdrawSession = new HashMap<Integer,Integer>();
	
	private Map<OTvk,Integer> usedCoins = new HashMap<OTvk,Integer>();
	private ArrayList<Integer> usedPids	= new ArrayList<Integer>();
	
	private List<Integer> elms;
	
	public Bank(List<Integer> elements){
		elms = elements;
		G = Group.getRandomGroupElement(elms);
		assert Group.isInGroup(G, elms);
		w = Group.getRandomExponent();
		H = Group.modPow(G, w);
		assert Group.isInGroup(H, elms);
	}

	public int getG() {return G;}
	public int getH() {return H;}

	
	/*
	 * User Registration
	 */

	public int register(int gu){
		if(isRegisteredUser(gu)) throw new IllegalArgumentException("User allready registered");
		
		users.add(gu);
		int hu = Group.modPow(gu,w);
		assert Group.isInGroup(hu, elms): "gu " + gu + ", hu " + hu + ", w " + w;
		return hu;
	}
	

	/*
	 * Withdraw methods
	 */
	public Pair withdrawCommit(int gu){
		if(!isRegisteredUser(gu)) throw new IllegalArgumentException("Not a registered user"); 
		
		int v = Group.getRandomExponent();
		int Hbar = Group.modPow(G, v);
		assert Group.isInGroup(Hbar, elms);
		int hbar = Group.modPow(gu, v);
		assert Group.isInGroup(hbar, elms);
		
		withdrawSession.put(gu,v);
		
		return new Pair(Hbar,hbar);
	}
	
	public int withdrawResponse(int gu, int e){
		if(!isRegisteredUser(gu)) throw new IllegalArgumentException("Not a registered user");
		
		int v = withdrawSession.remove(gu);		
		int z = Group.addE(Group.multE(e, w), v);
		assert Group.isCorrectExp(z): "z " + z;
		
		return z;
	}
	
	/*
	 * Deposit
	 */
	
	public void deposit(OTvk c, BKSig sigmaB, Pair sigma, int pid, Shop shop) throws InvalidCoinException, DoubleDepositException, InvalidPidException, DoubleSpendingException{
		checkShopId(pid, shop);
		
		if(!Util.BKVer(G, H, c, sigmaB, elms) && !Util.OTVer(c, pid, sigma, elms)) throw new InvalidCoinException();
		
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
	
	
			
	private boolean isRegisteredUser(int gu){
		return users.contains(gu);
	}
	
}
