import java.util.ArrayList;


public class User {
	private Bank bank;
	
	private int gu,hu;
	private int U;
	
	public User(int U, Bank bank){
		this.bank = bank;
		this.U = U;
		
		// User registration 
		gu = Util.modMult(Util.modPow(Parameters.g1,U), Parameters.g2);
		hu = bank.register(gu);
	}
	
	// called by test class
	public Pair withdraw(){
		int s = Util.getRandom();
		int v1 = Util.getRandom();
		int v2 = Util.getRandom();
		int zp = Util.getRandom();
		int ep = Util.getRandom();
		
		int x = Util.modPow(gu, s);
		int a = Util.modMult(Util.modPow(Parameters.g1, v1), Util.modPow(Parameters.g2,v2));
		
		Pair pairHbarhbar = bank.withdrawCommit();
		
		int gus = Util.modPow(gu, s);
		int hus = Util.modPow(hu, s);
		
		int Hbarp = Util.modMult(Util.modPow(bank.getG(), zp),Util.modInverse(Util.modPow(bank.getH(), ep)));
		int hbarp = Util.modMult(Util.modPow(gus, zp),Util.modInverse(Util.modPow(hus, ep)));
		
		int HbarHbarp = Util.modMult(pairHbarhbar.x1, Hbarp);
		int hbarhbarp = Util.modMult(Util.modPow(pairHbarhbar.x2,s), hbarp);
		
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(bank.getG());
		list.add(bank.getH());
		list.add(gus);
		list.add(hus);
		list.add(HbarHbarp);
		list.add(hbarhbarp);
		list.add(a);
		
		int e = Util.modAdd(Util.hash(list),-ep);
		
		return null;
	}
	
	

}
