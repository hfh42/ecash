
public class Shop {
	
	private final int shopid;
	private int transactionCounter = 0;
	
	private Bank bank;

	public Shop(int id, Bank bank){
		this.shopid = id*1000000;
		this.bank = bank;
	}
	
	public int getShopId(){
		return shopid;
	}
	
	public int getpid(){
		transactionCounter++;
		return shopid+transactionCounter;
	}
	
	public void buy(OTvk c, BKSig sigmaB, Pair sigma, int pid) throws InvalidCoinException, InvalidPidException, DoubleDepositException, DoubleSpendingException{
		if(!isValidPid(pid)) throw new InvalidPidException();
		
		boolean b1 = !Util.BKVer(bank.getG(), bank.getH(), c, sigmaB);
		boolean b2 = !Util.OTVer(c, pid, sigma);
		if(b1 || b2) {
			System.out.println("b1: " + b1 + ", b2: " + b2);
			throw new InvalidCoinException();
		}
		
		bank.deposit(c,sigmaB,sigma,pid, this);
	}
	
	private boolean isValidPid(int pid){
		int id = pid - shopid;
		return !(0 > id || id > 100000);
	}
	
}
