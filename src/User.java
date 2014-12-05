import java.util.ArrayList;
import java.util.LinkedList;


public class User {
	private Bank bank;
	
	private int gu,hu;
	private int U;
	
	private LinkedList<Coin> coins = new LinkedList<Coin>();
	
	public User(int U, Bank bank){
		this.bank = bank;
		this.U = U;
		
		// User registration 
		gu = Util.multG(Util.modPow(Parameters.g1,U), Parameters.g2);
		hu = bank.register(gu);
	}
	
	// called by test class
	public User withdraw(){
		int s = Util.getRandomExp();
		int v1 = Util.getRandomExp();
		int v2 = Util.getRandomExp();
		int zp = Util.getRandomExp();
		int ep = Util.getRandomExp();

		// Create coin and save it
		int x = Util.modPow(gu, s);
		int a = Util.multG(Util.modPow(Parameters.g1, v1), Util.modPow(Parameters.g2,v2));
		OTvk vk = new OTvk(x,a);
		OTsk sk = new OTsk(Util.multE(U, s),s,v1,v2);
		
		// Randomize user id
		int gus = x;
		int hus = Util.modPow(hu, s);
		
		// Get commitment from Bank, and randomize it
		Pair pairHbarhbar = bank.withdrawCommit(gu);
		int gzp = Util.modPow(bank.getG(), zp);
		int hep = Util.modPow(bank.getH(), ep);
		int hnegep = Util.modInverse(hep);
		int Hbarp = Util.multG(gzp,hnegep);
		int hbarp = Util.multG(Util.modPow(gus, zp),Util.modInverse(Util.modPow(hus, ep)));
		int HbarHbarp = Util.multG(pairHbarhbar.x1, Hbarp);
		int hbarshbarp = Util.multG(Util.modPow(pairHbarhbar.x2,s), hbarp);
		
		// Compute challenge e from hash
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(bank.getG());
		list.add(bank.getH());
		list.add(gus);
		list.add(hus);
		list.add(HbarHbarp);
		list.add(hbarshbarp);
		list.add(a);
		
		int hash = Util.hash(list);
		int e = Util.addE(hash,-ep);
		
		// Get response from Bank by sending the challenge
		int z = bank.withdrawResponse(gu,e);
		
		// Compute signature
		BKSig sigmaB = new BKSig(bank.getG(), bank.getH(), gus, hus, HbarHbarp, hbarshbarp, Util.addE(z, zp));
		
		Coin c = new Coin(vk,sk,sigmaB);
		coins.add(c);

		return this;
	}
	
	public User spendCoin(Shop shop) throws InvalidCoinException, InvalidPidException, NoCoinException, DoubleDepositException, DoubleSpendingException {
		if(coins.size() == 0) throw new NoCoinException();
		int pid = shop.getpid();
		Coin c = coins.removeFirst();
		Pair sigma = Util.OTSign(c.sk, pid);
		shop.buy(c.vk, c.sigmaB, sigma, pid);

		return this;
	}	
	
	
	private class Coin{
		public final OTvk vk;
		public final OTsk sk;
		public final BKSig sigmaB;
		
		public Coin(OTvk vk, OTsk sk, BKSig sigmaB){
			this.vk = vk;
			this.sk = sk;
			this.sigmaB = sigmaB;
		}
		
	}

}
