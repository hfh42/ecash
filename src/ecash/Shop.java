package ecash;

import ecash.signature.bank.BKSig;
import ecash.signature.ot.OTvk;
import ecash.exception.DoubleDepositException;
import ecash.exception.DoubleSpendingException;
import ecash.exception.InvalidCoinException;
import ecash.exception.InvalidPidException;


public class Shop {
	
	protected final int shopId;
	private int transactionCounter = 0;
	
	protected Bank bank;
	
	public Shop(int id, Bank bank){
		this.shopId = id;
		this.bank = bank;
        bank.registerShop(id);
	}
	
	public int getNextPid(){
		transactionCounter++;
		return Util.encodePid(shopId, transactionCounter);
	}
	
	public void buy(OTvk c, BKSig sigmaB, Pair sigma, int pid) throws InvalidCoinException, InvalidPidException, DoubleDepositException, DoubleSpendingException{
		if(!isValidPid(pid)) throw new InvalidPidException();
		
		boolean b1 = !Util.BKVer(bank.getG(), bank.getH(), c, sigmaB);
		boolean b2 = !Util.OTVer(c, pid, sigma);
		if(b1 || b2) {
			System.out.println("b1: " + b1 + ", b2: " + b2);
			throw new InvalidCoinException();
		}
		
		bank.deposit(c, sigmaB, sigma, pid);
	}
	
	private boolean isValidPid(int pid){
		return shopId == Util.decodePid(pid);
	}
	
}
