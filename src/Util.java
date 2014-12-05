import java.util.ArrayList;
import java.util.Random;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.lang.RuntimeException;
import java.lang.IllegalArgumentException;
import java.nio.ByteBuffer;

public class Util {

	private static Random rnd = new Random(System.currentTimeMillis());
	
	public static int getRandom(){
		return rnd.nextInt(Parameters.q);
	}
	
	public static int hash(ArrayList<Integer> list){
		if(list.size() != 7) throw new IllegalArgumentException("The input list must be of length 7 to be hashed.");

		ByteBuffer bb = ByteBuffer.allocate(4*list.size());
		for(Integer i : list)
			bb.putInt(i);

		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			BigInteger result = new BigInteger(1,digest.digest(bb.array()));
			return result.mod(BigInteger.valueOf(Parameters.q)).intValue();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Could not complete hashing due to unexisting algorithm.", e);
		}
	}
	
	
	/*
	 * Math, group operations
	 */
	
	public static int modPow(int base, int exp){
		long x = 1, y = base;
		
		while(exp > 0){
			if ( mod(exp,2) == 1){
				x = mod((x*y),Parameters.q);
			}
			y = mod((y*y),Parameters.q);
			exp /= 2;
		}

		int result = mod(x,Parameters.q);
		return result;
		//BigInteger b = BigInteger.valueOf(base);
		//BigInteger e = BigInteger.valueOf(exp);

		//return b.modPow(e, BigInteger.valueOf(Parameters.q)).intValue();
	}
	
	public static int modInverse(int base){
		//return modPow(base, -1);
		return modPow(base, Parameters.q-2);
	}
		
	public static int modMult(int x, int y){
		long a = x, b = y;
		return (mod((a*b),Parameters.q));
	}
		
	public static int modAdd(int x, int y){
		long a = x, b = y;
		return (mod((a+b),Parameters.q));
	}

	public static int mod(long base, int mod) {
		int r = (int)(base % mod);
		if(r < 0)
			r += Math.abs(mod);
		return r;
	}
	
	
	/*
	 * Bank Signature
	 */
	
	public static boolean BKVer(int G, int H, OTvk c, BKSig sigmaB){
		ArrayList<Integer> list = sigmaB.getList();
		list.add(c.a);
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
		int z1 = modAdd(modMult(m,sk.w1), sk.v1);
		int z2 = modAdd(modMult(m,sk.w2), sk.v2);
				
		return new Pair(z1,z2);
	}
	
	public static boolean OTVer(OTvk c, int m, Pair z){
		int left = modMult(modPow(Parameters.g1,z.x1),modPow(Parameters.g2,z.x2));
		int right = modMult(c.a, modPow(c.x,m));
		return left == right;
	}

}
