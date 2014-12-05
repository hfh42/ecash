import java.math.BigInteger;

public class Shop {
	
	private final BigInteger shopid;
	private BigInteger transactionCounter = BigInteger.ZERO;
	
	private Bank bank;

	public Shop(BigInteger id, Bank bank){
		this.shopid = id.multiply(new BigInteger("1000000"));
		this.bank = bank;
	}
	
	public BigInteger getShopId(){
		return shopid;
	}
	
	public BigInteger getpid(){
		transactionCounter.add(BigInteger.ONE);
		return shopid.add(transactionCounter);
	}
	
	public void buy(OTvk c, BKSig sigmaB, Pair sigma, BigInteger pid) throws InvalidCoinException, InvalidPidException, DoubleDepositException, DoubleSpendingException{
		if(!isValidPid(pid)) throw new InvalidPidException();
		
		boolean b1 = !Util.BKVer(bank.getG(), bank.getH(), c, sigmaB);
		boolean b2 = !Util.OTVer(c, pid, sigma);
		if(b1 || b2) {
			System.out.println("b1: " + b1 + ", b2: " + b2);
			throw new InvalidCoinException();
		}
		
		bank.deposit(c,sigmaB,sigma,pid, this);
	}
	
	private boolean isValidPid(BigInteger pid){
		BigInteger id = pid.subtract(shopid);
		return !(0 > id.compareTo(BigInteger.ZERO) || id.compareTo(new BigInteger("100000")) > 0);
	}
	
}
