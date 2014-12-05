import java.util.ArrayList;
import java.util.Random;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.lang.RuntimeException;
import java.lang.IllegalArgumentException;
import java.nio.ByteBuffer;

public class Util {

	private static Random rnd = new Random(System.currentTimeMillis()); //new Random(System.currentTimeMillis());
	
	public static BigInteger getRandomGroup(){
		return new BigInteger(256,rnd).mod(Parameters.p);
	}
	
	public static BigInteger getRandomExp(){
		return new BigInteger(256,rnd).mod(Parameters.q);
	}

	public static BigInteger hash(ArrayList<BigInteger> list){
		if(list.size() != 7) throw new IllegalArgumentException("The input list must be of length 7 to be hashed.");

		String s = "";
		for(BigInteger i : list)
			s += i.toString();

		ByteBuffer bb = ByteBuffer.allocate(s.length());
		bb.put(s.getBytes());
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			BigInteger result = new BigInteger(1,digest.digest(bb.array()));
			return result.mod(Parameters.q);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Could not complete hashing due to unexisting algorithm.", e);
		}
	}
	
	
	/*
	 * Math, group operations
	 */
	
	public static BigInteger modPow(BigInteger base, BigInteger exp){
		BigInteger x = BigInteger.ONE, y = base;
		
		while(exp.compareTo(BigInteger.ZERO) > 0){
			if ( mod(exp,new BigInteger("2")).compareTo(BigInteger.ONE) == 0){
				x = mod(x.multiply(y),Parameters.p);
			}
			y = mod(y.multiply(y),Parameters.p);
			exp = exp.divide(new BigInteger("2"));
		}

		BigInteger result = mod(x,Parameters.p);
		return result;
		//BigInteger b = BigInteger.valueOf(base);
		//BigInteger e = BigInteger.valueOf(exp);

		//return b.modPow(e, BigInteger.valueOf(Parameters.q)).intValue();
	}
	
	public static BigInteger modInverse(BigInteger base){
		//return modPow(base, -1);
		return modPow(base, Parameters.p.subtract(new BigInteger("2")));
	}
		
	public static BigInteger multG(BigInteger x, BigInteger y){
		return mult(x,y,Parameters.p);
	}
	
	public static BigInteger multE(BigInteger x, BigInteger y){
		return mult(x,y,Parameters.q);
	}
	
	private static BigInteger mult(BigInteger x, BigInteger y, BigInteger m){
		BigInteger a = x, b = y;
		return (mod(a.multiply(b),m));
	}
	
	public static BigInteger addE(BigInteger x, BigInteger y){
		BigInteger a = x, b = y;
		return (mod(a.add(b),Parameters.q));
	}
	

	private static BigInteger mod(BigInteger base, BigInteger mod) {
		BigInteger r = base.mod(mod);
		if(r.compareTo(BigInteger.ZERO) < 0)
			r = r.add(mod.abs());
		return r;
	}
	
	
	/*
	 * Bank Signature
	 */
	
	public static boolean BKVer(BigInteger G, BigInteger H, OTvk c, BKSig sigmaB){
		ArrayList<BigInteger> list = sigmaB.getList();
		list.add(c.a);
		BigInteger e = hash(list);

		BigInteger Gz = modPow(G,sigmaB.z);
		BigInteger HbarHe = multG(sigmaB.Hbar,modPow(H,e));
		BigInteger gz = modPow(sigmaB.gu,sigmaB.z);
		BigInteger hbarhe = multG(sigmaB.hbar,modPow(sigmaB.hu,e));
		
		return Gz.compareTo(HbarHe)==0 && gz.compareTo(hbarhe)==0;
	}
	
	/*
	 * One-Time Signature
	 */
	
	public static Pair OTSign(OTsk sk, BigInteger m){
		BigInteger z1 = addE(multE(m,sk.w1), sk.v1);
		BigInteger z2 = addE(multE(m,sk.w2), sk.v2);
				
		return new Pair(z1,z2);
	}
	
	public static boolean OTVer(OTvk c, BigInteger m, Pair z){
		BigInteger left = multG(modPow(Parameters.g1,z.x1),modPow(Parameters.g2,z.x2));
		BigInteger right = multG(c.a, modPow(c.x,m));
		return left.compareTo(right)==0;
	}

}
