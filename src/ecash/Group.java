package ecash;

import java.util.Random;


public class Group {

	public static final int q = 536871311;
	public static final int p = 1073742623; // p = 2q + 1   
	public static final int g1 = 48395732; 	// g1^q mod p = 1
	public static final int g2 = 121017764;	// x = 3938, 	g2 = g1^x mod p,	g2^q mod p = 1

	private static Random rnd = new Random(System.currentTimeMillis());

	/**
	 * Get a random exponent and uses it to calculate random element in the group
	 * @return a random element in the group of order q
	 */
	public static int getRandomGroupElement(){
		int exp = getRandomExponent(); 
		int res = mult(pow(g1,exp),g2);
		assert isInGroup(res);
		return res; 
	}

	/**
	 * @return a random exponent between 0 and q-1
	 */
	public static int getRandomExponent(){
		return rnd.nextInt(q);
	}

	/**
	 * Checks that element is in the group: g^q mod p = 1
	 * @param g: possible group element
	 */
	public static boolean isInGroup(int g){
		return power(g, q,p) == 1;
	}

	/**
	 * Check that the exponent is correct: 0 <= exp < q
	 * @param exp: possible exponent
	 */
	public static boolean isCorrectExp(int exp){
		return exp < q && exp >= 0;
	}
	
	/**
	 * Power function modulo p
	 * @param base is a group element
	 * @param exp is a integer, 0 <= exp < q
	 * @return base^exp mod p
	 */
	public static int pow(int base, int exp){
		assert isInGroup(base): "ecash.Group.pow, base is not in the group";
		assert isCorrectExp(exp): "ecash.Group.pow, exp is not correct";
		
		int result = power(base,exp,p);
		
		assert isInGroup(result): "ecash.Group.pow gives elm outside group";
		
		return result;
	}
	
	/*
	 * Need private power function with out assertions to be used in the isInGroup check
	 */
	private static int power(int base, int exp,int mod){
		long x = 1, y = base;
		while(exp > 0){
			if ( mod(exp,2) == 1){
				x = mod((x*y),mod);
			}
			y = mod((y*y),mod);
			exp /= 2;
		}
		return mod(x,mod);
	}

	/**
	 * Invert a exponent
	 * @param elm: The element to invert
	 * @return the inverse
	 */
    public static int expInverse(int elm){
        assert isCorrectExp(elm): "ecash.Exponent.inverse, base is not in the exponent";
        int result = inverse(elm,q);
        assert isCorrectExp(result): "eachs.expInverse inverse result is not in exponent, result "+ result+" base " + elm;
        assert expMult(elm, result) == 1: "ecash.Exponent.inverse, result is not the inverse";
        assert isCorrectExp(result): "ecash.Exponent.inverse gives element outside the exponent";

        return result;
    }
    
    /**
     * Invert a group element
     * @param base: The element to invert
     * @return the inverse
     */
    public static int inverse(int base){
        assert isInGroup(base): "ecash.Group.inverse, base is not in the group";
        int result = inverse(base,p);
        assert mult(base, result) == 1: "ecash.Group.inverse, result is not the inverse";
        assert isInGroup(result): "ecash.Group.inverse gives element outside the group";

        return result;
    }

	private static int inverse(int base,int exp){
        return power(base, exp-2,exp);
	}

		
	/**
	 * Group multiplication
	 * @param x
	 * @param y
	 * @return x*y mod p
	 */
	public static int mult(int x, int y){
		assert isInGroup(x): "ecash.Group.mult, x is not in the group";
		assert isInGroup(y): "ecash.Group.mult, y is not in the group";
		int res = mult(x,y,p);
		assert isInGroup(res): "ecash.Group.mult gives element outside the group";
		return res;
	}
	
	/**
	 * Exponent multiplication
	 * @param x
	 * @param y
	 * @return x*y mod q
	 */
	public static int expMult(int x, int y){
		assert isCorrectExp(x): "ecash.Group.expMult, x is not a corrct exponent";
		assert isCorrectExp(y): "ecash.Group.expMult, y is not a corrct exponent";
		int res = mult(x,y,q);
		assert isCorrectExp(res): "ecash.Group.expMult gives wrong exponent";
		return res;
	}
	
	private static int mult(int x, int y, int m){
		long a = x, b = y;
		return (mod((a*b),m));		
	}

	/**
	 * Exponent division
	 * @param x
	 * @param y
	 * @return x/y mod q
	 */
    public static int expDiv(int x,int y){
        assert isCorrectExp(x): "ecash.Group.expDiv, x is not a corrct exponent";
        assert isCorrectExp(y): "ecash.Group.expDiv, y is not a corrct exponent";
        int invY = expInverse(y);
        int xInvY = expMult(x,invY);
        assert isCorrectExp(y): "ecash.Group.expDiv, x/y is not a corrct exponent";
        return xInvY;
    }

    /**
     * Exponent addition
     * @param x
     * @param y
     * @return x+y mod q
     */
	public static int expAdd(int x, int y){
		assert isCorrectExp(x): "ecash.Group.expAdd, x is not a corrct exponent";
		assert isCorrectExp(y) || isCorrectExp(-y): "ecash.Group.expAdd, y is not a corrct exponent";
		long a = x, b = y;
		long result = a + b;
		while(result < 0){
			result = result + q; // make sure that the result is positive
		}
		int res = (mod((result),q));
		assert isCorrectExp(res): "ecash.Group.expAdd gives wrong exponent";
		return res;
	}
	

	private static int mod(long base, int mod) {
		assert base >= 0: "ecash.Group.mod, base must be positive" ;
		int r = (int)(base % mod);
		if(r < 0)
			r += Math.abs(mod);
		return r;
	}



}
