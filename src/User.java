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
		int g1U = Util.modPow(Parameters.g1,U);
		assert Util.isInGroup(g1U): "g1U: " + g1U; 
		gu = Util.multG(g1U, Parameters.g2);
		assert Util.isInGroup(gu): "gu: " + gu + ", g1U: " + g1U;
		hu = bank.register(gu);
		assert Util.isInGroup(hu): "hu: " + hu;
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
		assert Util.isInGroup(x);
		int a = Util.multG(Util.modPow(Parameters.g1, v1), Util.modPow(Parameters.g2,v2));
		assert Util.isInGroup(a);
		OTvk vk = new OTvk(x,a);
		OTsk sk = new OTsk(Util.multE(U, s),s,v1,v2);
		
		// Randomize user id
		int gus = x;
		int hus = Util.modPow(hu, s);
		assert Util.isInGroup(hus);
		
		// Get commitment from Bank, and randomize it
		Pair pairHbarhbar = bank.withdrawCommit(gu);
		int Gzp = Util.modPow(bank.getG(), zp);
		assert Util.isInGroup(Gzp);
		int Hep = Util.modPow(bank.getH(), ep);
		assert Util.isInGroup(Hep);
		int Hnegep = Util.modInverse(Hep);
		assert Util.isInGroup(Hnegep);
		int Hbarp = Util.multG(Gzp,Hnegep);
		assert Util.isInGroup(Hbarp);
		int hbarp = Util.multG(Util.modPow(gus, zp),Util.modInverse(Util.modPow(hus, ep)));
		assert Util.isInGroup(hbarp);
		int HbarHbarp = Util.multG(pairHbarhbar.x1, Hbarp);
		assert Util.isInGroup(HbarHbarp);
		int hbarshbarp = Util.multG(Util.modPow(pairHbarhbar.x2,s), hbarp);
		assert Util.isInGroup(hbarshbarp);
		
		
		/*System.out.println("G " + bank.getG());
		System.out.println("H " + bank.getH());
		System.out.println("zp " + zp);
		System.out.println("ep " + ep);
		System.out.println("Gzp " + Gzp);
		System.out.println("Hbar " + pairHbarhbar.x1);
		System.out.println("Hep " + Hep);
		System.out.println("Hnegep " + Hnegep);
		System.out.println("Hbarp " + Hbarp);
		System.out.println("HbarHbarp " + HbarHbarp);*/
		
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
		assert e < Parameters.q && e > 0;
		
		// Get response from Bank by sending the challenge
		int z = bank.withdrawResponse(gu,e);
		int sumz = Util.addE(z, zp);
		assert sumz < Parameters.q && sumz > 0;
		
		// Compute signature
		BKSig sigmaB = new BKSig(bank.getG(), bank.getH(), gus, hus, HbarHbarp, hbarshbarp, sumz);
		
		Coin c = new Coin(vk,sk,sigmaB);
		coins.add(c);
		
		/*System.out.println("G " + bank.getG());		
		System.out.println("H " + bank.getH());		
		System.out.println("hash/e " + hash);		
		System.out.println("gu " + gus);		
		System.out.println("hu " + hus);		
		System.out.println("Hbar " + HbarHbarp);		
		System.out.println("hbar " + hbarshbarp);		
		System.out.println("z " + sumz);*/

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
