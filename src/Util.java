import java.util.ArrayList;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.lang.RuntimeException;
import java.lang.IllegalArgumentException;
import java.nio.ByteBuffer;

import signature.bank.BKSig;
import signature.ot.OTsk;
import signature.ot.OTvk;

public class Util {

		
	public static int hash(ArrayList<Integer> list){
		if(list.size() != 7) throw new IllegalArgumentException("The input list must be of length 7 to be hashed.");

		ByteBuffer bb = ByteBuffer.allocate(4*list.size());
		for(Integer i : list)
			bb.putInt(i);

		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			BigInteger result = new BigInteger(1,digest.digest(bb.array()));	
			int h = result.mod(BigInteger.valueOf(Group.q)).intValue();
			assert Group.isCorrectExp(h): "The hash value must be a correct exponent";
			return h;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Could not complete hashing due to unexisting algorithm.", e);
		}
	}
	
	
	/*
	 * Math, group operations
	 */
	
		
	
	/*
	 * Bank Signature
	 */
	
	public static boolean BKVer(int G, int H, OTvk c, BKSig sigmaB){
		ArrayList<Integer> list = sigmaB.getList();
		list.add(c.a);
		int e = hash(list);
		
		int Gz = Group.pow(G,sigmaB.z);
		int HbarHe = Group.mult(sigmaB.Hbar,Group.pow(H,e));
		int gz = Group.pow(sigmaB.gu,sigmaB.z);
		int hbarhe = Group.mult(sigmaB.hbar,Group.pow(sigmaB.hu,e));
		
		/*System.out.println("G " + G);		
		System.out.println("H " + H);		
		System.out.println("hash/e " + e);		
		System.out.println("gu " + sigmaB.gu);		
		System.out.println("hu " + sigmaB.hu);		
		System.out.println("Hbar " + sigmaB.Hbar);		
		System.out.println("hbar " + sigmaB.hbar);		
		System.out.println("z " + sigmaB.z);*/
		
		return Gz == HbarHe && gz == hbarhe;
	}
	
	/*
	 * One-Time Signature
	 */
	
	public static Pair OTSign(OTsk sk, int m){
		int z1 = Group.expAdd(Group.expMult(m,sk.w1), sk.v1);
		assert Group.isCorrectExp(z1): "z1: " + z1;
		int z2 = Group.expAdd(Group.expMult(m,sk.w2), sk.v2);
		assert Group.isCorrectExp(z2): "z2: " + z2;
				
		return new Pair(z1,z2);
	}
	
	public static boolean OTVer(OTvk c, int m, Pair z){
		int left = Group.mult(Group.pow(Group.g1,z.x1),Group.pow(Group.g2,z.x2));
		int right = Group.mult(c.a, Group.pow(c.x,m));
		return left == right;
	}

}
