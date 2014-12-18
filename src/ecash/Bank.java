package ecash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ecash.exception.*;
import ecash.signature.bank.BKSig;
import ecash.signature.ot.OTvk;


public class Bank {
	private int G, H;
	private int w;
	
	private ArrayList<Integer> users = new ArrayList<Integer>();
	
	private Map<Integer,Integer> withdrawSession = new HashMap<Integer,Integer>();
	
	private Map<OTvk,Transaction> usedCoins = new HashMap<OTvk,Transaction>();
	private ArrayList<Integer> usedPids	= new ArrayList<Integer>();
	
	public Bank(){
		G = Group.getRandomGroupElement();
		w = Group.getRandomExponent();
		H = Group.pow(G, w);
	}

	public int getG() {return G;}
	public int getH() {return H;}

	
	/*
	 * User Registration
	 */

	public int register(int gu){
		if(isRegisteredUser(gu)) throw new IllegalArgumentException("User already registered");
		
		users.add(gu);
		int hu = Group.pow(gu, w);

		return hu;
	}
	

	/*
	 * Withdraw methods
	 */
	public Pair withdrawCommit(int gu){
		if(!isRegisteredUser(gu)) throw new IllegalArgumentException("Not a registered user"); 
		
		int v = Group.getRandomExponent();
		int Hbar = Group.pow(G, v);
		int hbar = Group.pow(gu, v);
		
		withdrawSession.put(gu,v);
		
		return new Pair(Hbar,hbar);
	}
	
	public int withdrawResponse(int gu, int e){
		if(!isRegisteredUser(gu)) throw new IllegalArgumentException("Not a registered user");
		
		int v = withdrawSession.remove(gu);
		int z = Group.expAdd(Group.expMult(e, w), v);
		assert Group.isCorrectExp(z): "z " + z;
		
		return z;
	}
	
	/*
	 * Deposit
	 */
	
	public void deposit(OTvk c, BKSig sigmaB, Pair sigma, int pid, int shopID) throws InvalidCoinException, DoubleDepositException, InvalidPidException, DoubleSpendingException {
		checkShopId(pid, shopID);
		
		if(!Util.BKVer(G, H, c, sigmaB) && !Util.OTVer(c, pid, sigma)) throw new InvalidCoinException();
		
		if(usedCoins.keySet().contains(c)){
			Transaction t = usedCoins.get(c);
			int w1p = Group.expDiv(Group.expAdd(t.sigma.x1, -sigma.x1), Group.expAdd(t.pid, -pid));
            int w2p = Group.expDiv(Group.expAdd(t.sigma.x2,-sigma.x2),Group.expAdd(t.pid,-pid));
            int U = Group.expDiv(w1p,w2p);
            int gu = Group.mult(Group.pow(Group.g1, U), Group.g2);
            if(users.contains(gu))
			    throw new DoubleSpendingException(gu,U);
            else
                throw new CalculationException();
		}
		
		usedCoins.put(c,new Transaction(sigma,pid));
		usedPids.add(pid);
	}
	
	private void checkShopId(int pid, int shopID) throws DoubleDepositException, InvalidPidException{
		if(usedPids.contains(pid)) throw new DoubleDepositException();
		
		int id = pid - shopID;
		if(0 > id || id > 100000) throw new InvalidPidException();
	}
	
	/*
	 *	Helpers 
	 */

    private class Transaction{
        public final Pair sigma;
        public final int pid;
        public Transaction(Pair sigma, int pid)
        {
            this.sigma=sigma;
            this.pid = pid;
        }
    }
	
			
	private boolean isRegisteredUser(int gu){
		return users.contains(gu);
	}
	
}
