import java.util.ArrayList;
import java.util.Random;


public class Util {
	
	public static int getRandom(){
		Random rnd = new Random(System.currentTimeMillis());
		return rnd.nextInt(Parameters.q);
	}
	
	public static int hash(ArrayList<Integer> list){
		return 42; // TODO: make hash
	}
	
	
	/*
	 * Math, group operations
	 */
	
	public static int modPow(int base, int exp){
		int x = 1, y = base;
		
		while(exp > 0){
			if ( exp % 2 == 1){
				x = x*y;
				if(x > Parameters.q) x %= Parameters.q;
			}
			y = y*y;
			if(y > Parameters.q) y %= Parameters.q;
			exp /= 2;
		}
		
		return x;
	}
	
	public static int modInverse(int base){
		return modPow(base, Parameters.q-2);
	}
		
	public static int modMult(int x, int y){
		return (x*y) % Parameters.q;
	}
		
	public static int modAdd(int x, int y){
		return (x+y) % Parameters.q;
	}
	
	
	/*
	 * Bank Signature
	 */
	
	public static boolean bankVerify(int G, int H, int x, int a, BankSignature sigmaB){
		ArrayList<Integer> list = sigmaB.getList();
		list.add(a);
		int e = hash(list);
		
		int Gz = modPow(G,sigmaB.z);
		int HbarHe = modMult(sigmaB.Hbar,modPow(H,e));
		int gz = modPow(sigmaB.gu,sigmaB.z);
		int hbarhe = modMult(sigmaB.hbar,modPow(sigmaB.hu,e)); 
		
		return Gz == HbarHe && gz == hbarhe;
	}
	
	/*
	 * One-Time Signature
	 */
	
	public static Pair OTSign(OTsk sk, int m){
		int z1 = modAdd(modMult(m,sk.w1), sk.r.x1);
		int z2 = modAdd(modMult(m,sk.w2), sk.r.x2);
				
		return new Pair(z1,z2);
	}
	
	public static boolean OTVer(int x, int a, int m, Pair z){
		int left = modMult(modPow(Parameters.g1,z.x1),modPow(Parameters.g2,z.x2));
		int right = modMult(a, modPow(x,m));
		return left == right;
	}

}
