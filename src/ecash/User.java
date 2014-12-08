package ecash;

import java.util.ArrayList;
import java.util.LinkedList;

import ecash.signature.bank.BKSig;
import ecash.signature.ot.OTsk;
import ecash.signature.ot.OTvk;
import ecash.exception.DoubleDepositException;
import ecash.exception.DoubleSpendingException;
import ecash.exception.InvalidCoinException;
import ecash.exception.InvalidPidException;
import ecash.exception.NoCoinException;


public class User {
	private Bank bank;
	
	private int gu,hu;
	private int U;
	
	private LinkedList<Coin> coins = new LinkedList<Coin>();
	
	
	public User(int U, Bank bank){
		this.bank = bank;
		this.U = U;
		
		// ecash.User registration
		int g1U = Group.pow(Group.g1, U);
		gu = Group.mult(g1U, Group.g2);
		hu = bank.register(gu);
	}
	
	// called by test class
	public User withdraw(){
		int s = Group.getRandomExponent();
		int v1 = Group.getRandomExponent();
		int v2 = Group.getRandomExponent();
		int zp = Group.getRandomExponent();
		int ep = Group.getRandomExponent();

		// Create coin and save it
		int x = Group.pow(gu, s);
		int a = Group.mult(Group.pow(Group.g1, v1), Group.pow(Group.g2,v2));
		OTvk vk = new OTvk(x,a);
		OTsk sk = new OTsk(Group.expMult(U, s),s,v1,v2);
		
		// Randomize user id
		int gus = x;
		int hus = Group.pow(hu, s);
		
		// Get commitment from ecash.Bank, and randomize it
		Pair pairHbarhbar = bank.withdrawCommit(gu);
		int Gzp = Group.pow(bank.getG(), zp);
		int Hep = Group.pow(bank.getH(), ep);
		int Hnegep = Group.inverse(Hep);
		int Hbarp = Group.mult(Gzp,Hnegep);
		int hbarp = Group.mult(Group.pow(gus, zp),Group.inverse(Group.pow(hus, ep)));
		int HbarHbarp = Group.mult(pairHbarhbar.x1, Hbarp);
		int hbarshbarp = Group.mult(Group.pow(pairHbarhbar.x2,s), hbarp);
		
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
		int e = Group.expAdd(hash,-ep);
		assert Group.isCorrectExp(e);
		
		// Get response from ecash.Bank by sending the challenge
		int z = bank.withdrawResponse(gu,e);
		int sumz = Group.expAdd(z, zp);
		assert Group.isCorrectExp(sumz);
		
		// Compute ecash.signature
		BKSig sigmaB = new BKSig(bank.getG(), bank.getH(), gus, hus, HbarHbarp, hbarshbarp, sumz);
		
		Coin c = new Coin(vk,sk,sigmaB);
		coins.add(c);
		
		return this;
	}
	
	public User spendCoin(Shop shop) throws InvalidCoinException, InvalidPidException, NoCoinException, DoubleDepositException, DoubleSpendingException {
		if(coins.size() == 0) throw new NoCoinException();
		int pid = shop.getPid();
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
