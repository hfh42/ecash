import java.util.List;


public class Shop {
	
	private final int shopid;
	private int transactionCounter = 0;
	
	private Bank bank;
	
	private List<Integer> elms;

	public Shop(int id, Bank bank, List<Integer> elms){
		this.shopid = id*100;
		this.bank = bank;
		this.elms = elms;
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
		
		boolean b1 = !Util.BKVer(bank.getG(), bank.getH(), c, sigmaB,elms);
		boolean b2 = !Util.OTVer(c, pid, sigma,elms);
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
