
public class Shop {
	
	private final int id;
	private int transactionCounter = 0;
	
	private Bank bank;

	public Shop(int id, Bank bank){
		this.id = id*1000000;
		this.bank = bank;
	}
	
	public int getpid(){
		transactionCounter++;
		return id+transactionCounter;
	}
	
	public void buy(OTvk c, BKSig sigmaB, Pair sigma, int pid) throws InvalidCoinException, InvalidPidException{
		if(!isValidPid()) throw new InvalidPidException();
		
		if(!Util.BKVer(bank.getG(), bank.getH(), c, sigmaB) && !Util.OTVer(c, pid, sigma)) throw new InvalidCoinException();
		
		bank.deposit(c,sigmaB,sigma,pid);
	}
	
	private boolean isValidPid(){
		return false; // TODO: fix
	}
	
}
