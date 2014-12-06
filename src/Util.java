import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.lang.RuntimeException;
import java.lang.IllegalArgumentException;
import java.nio.ByteBuffer;

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
			return result.abs().mod(BigInteger.valueOf(Parameters.p)).intValue();
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
	
	public static boolean BKVer(int G, int H, OTvk c, BKSig sigmaB, List<Integer> elms){
		ArrayList<Integer> list = sigmaB.getList();
		list.add(c.a);
		int e = hash(list);
		
		int Gz = Group.modPow(G,sigmaB.z);
		assert Group.isInGroup(Gz,elms);
		int HbarHe = Group.multG(sigmaB.Hbar,Group.modPow(H,e));
		assert Group.isInGroup(HbarHe,elms);
		int gz = Group.modPow(sigmaB.gu,sigmaB.z);
		assert Group.isInGroup(gz, elms);
		int hbarhe = Group.multG(sigmaB.hbar,Group.modPow(sigmaB.hu,e));
		assert Group.isInGroup(hbarhe, elms); 
		
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
		int z1 = Group.addE(Group.multE(m,sk.w1), sk.v1);
		assert Group.isCorrectExp(z1): "z1: " + z1;
		int z2 = Group.addE(Group.multE(m,sk.w2), sk.v2);
		assert Group.isCorrectExp(z2): "z2: " + z2;
				
		return new Pair(z1,z2);
	}
	
	public static boolean OTVer(OTvk c, int m, Pair z, List<Integer> elms){
		int left = Group.multG(Group.modPow(Parameters.g1,z.x1),Group.modPow(Parameters.g2,z.x2));
		assert Group.isInGroup(left, elms);
		int right = Group.multG(c.a, Group.modPow(c.x,m));
		assert Group.isInGroup(right, elms);
		return left == right;
	}

}
