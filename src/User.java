import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;


public class User {
	private Bank bank;
	
	private BigInteger gu,hu;
	private BigInteger U;
	
	private LinkedList<Coin> coins = new LinkedList<Coin>();
	
	public User(BigInteger U, Bank bank){
		this.bank = bank;
		this.U = U;
		
		// User registration 
		gu = Util.multG(Util.modPow(Parameters.g1, U), Parameters.g2);
		hu = bank.register(gu);
	}
	
	// called by test class
	public User withdraw(){
		BigInteger s = Util.getRandomExp();
		BigInteger v1 = Util.getRandomExp();
		BigInteger v2 = Util.getRandomExp();
		BigInteger zp = Util.getRandomExp();
		BigInteger ep = Util.getRandomExp();

		// Create coin and save it
		BigInteger x = Util.modPow(gu, s);
		BigInteger a = Util.multG(Util.modPow(Parameters.g1, v1), Util.modPow(Parameters.g2, v2));
		OTvk vk = new OTvk(x,a);
		OTsk sk = new OTsk(Util.multE(U, s),s,v1,v2);
		
		// Randomize user id
		BigInteger gus = x;
		BigInteger hus = Util.modPow(hu, s);
		
		// Get commitment from Bank, and randomize it
		Pair pairHbarhbar = bank.withdrawCommit(gu);
		BigInteger gzp = Util.modPow(bank.getG(), zp);
		BigInteger hep = Util.modPow(bank.getH(), ep);
		BigInteger hnegep = Util.modInverse(hep);
		BigInteger Hbarp = Util.multG(gzp, hnegep);
		BigInteger hbarp = Util.multG(Util.modPow(gus, zp), Util.modInverse(Util.modPow(hus, ep)));
		BigInteger HbarHbarp = Util.multG(pairHbarhbar.x1, Hbarp);
		BigInteger hbarshbarp = Util.multG(Util.modPow(pairHbarhbar.x2, s), hbarp);
		
		// Compute challenge e from hash
		ArrayList<BigInteger> list = new ArrayList<BigInteger>();
		list.add(bank.getG());
		list.add(bank.getH());
		list.add(gus);
		list.add(hus);
		list.add(HbarHbarp);
		list.add(hbarshbarp);
		list.add(a);

		BigInteger hash = Util.hash(list);
		BigInteger e = Util.addE(hash, ep.negate());
		
		// Get response from Bank by sending the challenge
		BigInteger z = bank.withdrawResponse(gu,e);
		
		// Compute signature
		BKSig sigmaB = new BKSig(bank.getG(), bank.getH(), gus, hus, HbarHbarp, hbarshbarp, Util.addE(z, zp));
		
		Coin c = new Coin(vk,sk,sigmaB);
		coins.add(c);

		return this;
	}
	
	public User spendCoin(Shop shop) throws InvalidCoinException, InvalidPidException, NoCoinException, DoubleDepositException, DoubleSpendingException {
		if(coins.size() == 0) throw new NoCoinException();
		BigInteger pid = shop.getpid();
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
