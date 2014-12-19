package ecash;

import java.util.ArrayList;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.lang.RuntimeException;
import java.lang.IllegalArgumentException;
import java.nio.ByteBuffer;

import ecash.signature.bank.BKSig;
import ecash.signature.ot.OTsk;
import ecash.signature.ot.OTvk;

public class Util {

		
	/**
	 * Hash Function 
	 * Takes a list of size 7, encodes it as a ByteArray, and uses SHA-256 to hash it
	 * @param list: A list of size 7
	 * @return A "random" in the group 
	 */
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
		
	
	/**
	 * The Banks Verification Algorithm
	 * @param G, H: The Banks verification key
	 * @param c: The Coin
	 * @param sigmaB: The signature
	 * @return Whether sigmaB is a valid signature on c
	 */
	public static boolean BKVer(int G, int H, OTvk c, BKSig sigmaB){
		ArrayList<Integer> list = sigmaB.getList();
		list.add(c.a);
		int e = hash(list);
		
		int Gz = Group.pow(G,sigmaB.z);
		int HbarHe = Group.mult(sigmaB.Hbar,Group.pow(H,e));
		int gz = Group.pow(sigmaB.gu,sigmaB.z);
		int hbarhe = Group.mult(sigmaB.hbar,Group.pow(sigmaB.hu,e));
				
		return Gz == HbarHe && gz == hbarhe;
	}
	

	/**
	 * One-Time Signing Algorithm
	 * @param sk: Signing key
	 * @param m: Message to sign
	 * @return the signature
	 */
	public static Pair OTSign(OTsk sk, int m){
		int z1 = Group.expAdd(Group.expMult(m,sk.w1), sk.v1);
		assert Group.isCorrectExp(z1): "z1: " + z1;
		int z2 = Group.expAdd(Group.expMult(m,sk.w2), sk.v2);
		assert Group.isCorrectExp(z2): "z2: " + z2;
				
		return new Pair(z1,z2);
	}
	
	/**
	 * One-Time Verification Algorithm
	 * @param c: Verification key
	 * @param m: Message 
	 * @param z: Signature
	 * @return whether z is a valid signature on m
	 */
	public static boolean OTVer(OTvk c, int m, Pair z){
		int left = Group.mult(Group.pow(Group.g1,z.x1),Group.pow(Group.g2,z.x2));
		int right = Group.mult(c.a, Group.pow(c.x,m));
		return left == right;
	}

    private static int shopIdMultiplier = 100000;
    
    /**
     * Payment identifier encoding algorithm
     * @param shopId
     * @param transactionId
     * @return Payment identifier
     */
    public static int encodePid(int shopId, int transactionId) {
        return shopId*shopIdMultiplier+transactionId;
    }

    /**
     * Payment identifier decoding algorithm
     * @param pid
     * @return The Shop ID
     */
    public static int decodePid(int pid) {
        return pid/shopIdMultiplier;
    }

}
