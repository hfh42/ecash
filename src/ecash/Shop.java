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
	
	/**
	 * Constructor
	 * Create a new Shop and register it at the Bank
	 * @param id: The Shop ID
	 * @param bank: The Shops Bank
	 */
	public Shop(int id, Bank bank){
		this.shopId = id;
		this.bank = bank;
        bank.registerShop(id);
	}
	
	/**
	 * Get a new unique payment identifier
	 * @return the unique payment identifier
	 */
	public int getNextPid(){
		transactionCounter++;
		return Util.encodePid(shopId, transactionCounter);
	}
	
	/**
	 * Spending Protocol
	 * A User contacts the Shop to buy something
	 * @param c: The Coin
	 * @param sigmaB: The Banks signature on the Coin
	 * @param sigma: The OT signature on the payment identifier
	 * @param pid: the payment identifier
	 */
	public void buy(OTvk c, BKSig sigmaB, Pair sigma, int pid) throws InvalidCoinException, InvalidPidException, DoubleDepositException, DoubleSpendingException{
		if(!isValidPid(pid)) throw new InvalidPidException();
		
		// Verify the Banks signature on the coin
		boolean b1 = !Util.BKVer(bank.getG(), bank.getH(), c, sigmaB);
		
		// Verify the OT signature on the payment identifier
		boolean b2 = !Util.OTVer(c, pid, sigma);
		
		if(b1 || b2) throw new InvalidCoinException();
		
		bank.deposit(c, sigmaB, sigma, pid);
	}
	
	/*
	 * Check that the User sends a valid payment identifier 
	 */
	private boolean isValidPid(int pid){
		return shopId == Util.decodePid(pid);
	}
	
}
