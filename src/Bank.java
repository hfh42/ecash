import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Bank {
	private BigInteger G, H;
	private BigInteger w;
	
	private ArrayList<BigInteger> users = new ArrayList<BigInteger>();
	
	private Map<BigInteger,BigInteger> withdrawSession = new HashMap<BigInteger,BigInteger>();
	
	private Map<OTvk,BigInteger> usedCoins = new HashMap<OTvk,BigInteger>();
	private ArrayList<BigInteger> usedPids	= new ArrayList<BigInteger>();
	
	public Bank(){
		G = Util.getRandomGroup();
		w = Util.getRandomExp();
		H = Util.modPow(G, w);
	}

	public BigInteger getG() {return G;}
	public BigInteger getH() {return H;}

	
	/*
	 * User Registration
	 */

	public BigInteger register(BigInteger gu){
		if(isRegisteredUser(gu)) throw new IllegalArgumentException("User allready registered");
		
		users.add(gu);
		return Util.modPow(gu, w);
	}
	

	/*
	 * Withdraw methods
	 */
	public Pair withdrawCommit(BigInteger gu){
		if(!isRegisteredUser(gu)) throw new IllegalArgumentException("Not a registered user");

		BigInteger v = Util.getRandomExp();
		BigInteger Hbar = Util.modPow(G, v);
		BigInteger hbar = Util.modPow(gu, v);
		
		withdrawSession.put(gu,v);
		
		return new Pair(Hbar,hbar);
	}
	
	public BigInteger withdrawResponse(BigInteger gu, BigInteger e){
		if(!isRegisteredUser(gu)) throw new IllegalArgumentException("Not a registered user");

		BigInteger v = withdrawSession.remove(gu);
		BigInteger z = Util.addE(Util.multE(e, w), v);
		return z;
	}
	
	/*
	 * Deposit
	 */
	
	public void deposit(OTvk c, BKSig sigmaB, Pair sigma, BigInteger pid, Shop shop) throws InvalidCoinException, DoubleDepositException, InvalidPidException, DoubleSpendingException{
		checkShopId(pid, shop);
		
		if(!Util.BKVer(G, H, c, sigmaB) && !Util.OTVer(c, pid, sigma)) throw new InvalidCoinException();
		
		if(usedCoins.keySet().contains(c)){
			BigInteger otherpid = usedCoins.get(c);
			// TODO: Find cheating user
			throw new DoubleSpendingException();
		}
		
		usedCoins.put(c,pid); // TODO: save c, sigma, pid
		usedPids.add(pid);
	}
	
	private void checkShopId(BigInteger pid, Shop shop) throws DoubleDepositException, InvalidPidException{
		if(usedPids.contains(pid)) throw new DoubleDepositException();

		BigInteger id = pid.subtract(shop.getShopId());
		if(0 > id.compareTo(BigInteger.ZERO) || id.compareTo(new BigInteger("100000")) > 0) throw new InvalidPidException();
	}
	
	/*
	 *	Helpers 
	 */
	
	
			
	private boolean isRegisteredUser(BigInteger gu){
		return users.contains(gu);
	}
	
}
