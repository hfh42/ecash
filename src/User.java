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
		int g1U = Group.modPow(Parameters.g1,U);
		assert Group.isInGroup(g1U): "g1U: " + g1U; 
		gu = Group.multG(g1U, Parameters.g2);
		assert Group.isInGroup(gu): "gu: " + gu + ", g1U: " + g1U;
		hu = bank.register(gu);
		assert Group.isInGroup(hu): "hu: " + hu;
	}
	
	// called by test class
	public User withdraw(){
		int s = Group.getRandomExponent();
		int v1 = Group.getRandomExponent();
		int v2 = Group.getRandomExponent();
		int zp = Group.getRandomExponent();
		int ep = Group.getRandomExponent();

		// Create coin and save it
		int x = Group.modPow(gu, s);
		assert Group.isInGroup(x);
		int a = Group.multG(Group.modPow(Parameters.g1, v1), Group.modPow(Parameters.g2,v2));
		assert Group.isInGroup(a);
		OTvk vk = new OTvk(x,a);
		OTsk sk = new OTsk(Group.multE(U, s),s,v1,v2);
		
		// Randomize user id
		int gus = x;
		int hus = Group.modPow(hu, s);
		assert Group.isInGroup(hus);
		
		// Get commitment from Bank, and randomize it
		Pair pairHbarhbar = bank.withdrawCommit(gu);
		int Gzp = Group.modPow(bank.getG(), zp);
		assert Group.isInGroup(Gzp);
		int Hep = Group.modPow(bank.getH(), ep);
		assert Group.isInGroup(Hep);
		int Hnegep = Group.modInverse(Hep);
		assert Group.isInGroup(Hnegep);
		int Hbarp = Group.multG(Gzp,Hnegep);
		assert Group.isInGroup(Hbarp);
		int hbarp = Group.multG(Group.modPow(gus, zp),Group.modInverse(Group.modPow(hus, ep)));
		assert Group.isInGroup(hbarp);
		int HbarHbarp = Group.multG(pairHbarhbar.x1, Hbarp);
		assert Group.isInGroup(HbarHbarp);
		int hbarshbarp = Group.multG(Group.modPow(pairHbarhbar.x2,s), hbarp);
		assert Group.isInGroup(hbarshbarp);
		
		
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
		assert hash >= 0;
		int e = Group.addE(hash,-ep);
		assert Group.isCorrectExp(e);
		
		// Get response from Bank by sending the challenge
		int z = bank.withdrawResponse(gu,e);
		int sumz = Group.addE(z, zp);
		assert Group.isCorrectExp(sumz);
		
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
