package ecash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ecash.signature.bank.BKSig;
import ecash.signature.ot.OTvk;
import ecash.exception.DoubleDepositException;
import ecash.exception.DoubleSpendingException;
import ecash.exception.InvalidCoinException;
import ecash.exception.InvalidPidException;


public class Bank {
	private int G, H;
	private int w;
	
	private ArrayList<Integer> users = new ArrayList<Integer>();
	
	private Map<Integer,Integer> withdrawSession = new HashMap<Integer,Integer>();
	
	private Map<OTvk,Integer> usedCoins = new HashMap<OTvk,Integer>();
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
	
	public void deposit(OTvk c, BKSig sigmaB, Pair sigma, int pid, int shopID) throws InvalidCoinException, DoubleDepositException, InvalidPidException, DoubleSpendingException{
		checkShopId(pid, shopID);
		
		if(!Util.BKVer(G, H, c, sigmaB) && !Util.OTVer(c, pid, sigma)) throw new InvalidCoinException();
		
		if(usedCoins.keySet().contains(c)){
			int otherpid = usedCoins.get(c);
			// TODO: Find cheating user
			throw new DoubleSpendingException();
		}
		
		usedCoins.put(c,pid); // TODO: save c, sigma, pid
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
	
	
			
	private boolean isRegisteredUser(int gu){
		return users.contains(gu);
	}
	
}
