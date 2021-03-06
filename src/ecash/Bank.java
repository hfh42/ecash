package ecash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ecash.exception.*;
import ecash.signature.bank.BKSig;
import ecash.signature.ot.OTvk;


public class Bank {
	private int G, H; // The Banks public verification key
	private int w; // The Banks secret signing key
	
	// List containing all users g_U
	private ArrayList<Integer> users = new ArrayList<Integer>();
	
	// Handle session i withdraw
	private Map<Integer,Integer> withdrawSession = new HashMap<Integer,Integer>();
	
	// Save deposit coins
	private Map<OTvk,Transaction> usedCoins = new HashMap<OTvk,Transaction>();
	private ArrayList<Integer> usedPids	= new ArrayList<Integer>();

	// Shop ids
    private ArrayList<Integer> shops = new ArrayList<Integer>();
	
    /**
     * Constructor
     * Create a new Bank and setup its verification and signing key
     */
	public Bank(){
		G = Group.getRandomGroupElement();
		w = Group.getRandomExponent();
		H = Group.pow(G, w);
	}

	
	public int getG() {return G;}
	public int getH() {return H;}

	
	/**
	 * User Registration
	 * @param gu: The first part of the Users public id 
	 * @return The second part h_U of the Users public id
	 */
	public int register(int gu){
		if(isRegisteredUser(gu)) throw new IllegalArgumentException("User already registered");
		
		users.add(gu);
		int hu = Group.pow(gu, w);

		return hu;
	}

    /**
     * Shop Registration
     * @param shopId: The Shops unique id
     */
	public void registerShop(int shopId) {
        shops.add(shopId);
    }

	/**
	 * The Banks first message of the withdraw protocol
	 * @param gu: The Users public id (should also send h_U)
	 * @return the fist message from the bank - the commitment
	 */
	public Pair withdrawCommit(int gu){
		if(!isRegisteredUser(gu)) throw new IllegalArgumentException("Not a registered user"); 
		
		int v = Group.getRandomExponent();
		int Hbar = Group.pow(G, v);
		int hbar = Group.pow(gu, v);
		
		withdrawSession.put(gu,v);
		
		return new Pair(Hbar,hbar);
	}
	
	/**
	 * The Banks second message of the withdraw protocol
	 * @param gu: The Users public id (should also send h_U)
	 * @param e: The Users challenge
	 * @return the second message from the bank - the response
	 */
	public int withdrawResponse(int gu, int e){
		if(!isRegisteredUser(gu)) throw new IllegalArgumentException("Not a registered user");
		
		int v = withdrawSession.remove(gu);
		int z = Group.expAdd(Group.expMult(e, w), v);
		assert Group.isCorrectExp(z): "z " + z;
		
		return z;
	}
	
	/**
	 * Deposit
	 * @param c: The Coin
	 * @param sigmaB: The Banks signature on the Coin
	 * @param sigma: The OT signature on the payment identifier
	 * @param pid: The payment identifier
	 */
	public void deposit(OTvk c, BKSig sigmaB, Pair sigma, int pid) throws InvalidCoinException, DoubleDepositException, InvalidPidException, DoubleSpendingException{
		checkShopId(pid);
		
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
	

	/*
	 * Data class used to save the transactions
	 */
    private class Transaction{
        public final Pair sigma;
        public final int pid;
        
        public Transaction(Pair sigma, int pid){
            this.sigma=sigma;
            this.pid = pid;
        }
    }

    /*
     * Check that the User ID is not already used
     */
	private boolean isRegisteredUser(int gu){
		return users.contains(gu);
	}
	
	/*
	 * Check that the Shop send the right payment identifier
	 */
	private void checkShopId(int pid) throws DoubleDepositException, InvalidPidException{
		if(usedPids.contains(pid)) throw new DoubleDepositException();
		if(!shops.contains(Util.decodePid(pid))) throw new InvalidPidException();
	}	
}
