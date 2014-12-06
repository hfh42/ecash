import java.util.Random;


public class Group {

	private static Random rnd = new Random(System.currentTimeMillis());

	public static int getRandomGroupElement(){
		return rnd.nextInt(Parameters.p-1)+1;
	}

	public static int getRandomExponent(){
		return rnd.nextInt(Parameters.q-1)+1;
	}

	public static boolean isInGroup(int g){
		return g < Parameters.p && g > 0;
	}

	public static boolean isCorrectExp(int exp){
		return exp < Parameters.q && exp >= 0;
	}
	
	public static int modPow(int base, int exp){
		long x = 1, y = base;
		
		while(exp > 0){
			if ( mod(exp,2) == 1){
				x = mod((x*y),Parameters.p);
			}
			y = mod((y*y),Parameters.p);
			exp /= 2;
		}

		int result = mod(x,Parameters.p);
		return result;
		//BigInteger b = BigInteger.valueOf(base);
		//BigInteger e = BigInteger.valueOf(exp);

		//return b.modPow(e, BigInteger.valueOf(Parameters.q)).intValue();
	}
	
	public static int modInverse(int base){
		//return modPow(base, -1);
		int result = modPow(base, Parameters.p-2);
		int t = multG(base, result);
		assert t == 1;
		return result;
	}
		
	public static int multG(int x, int y){
		return mult(x,y,Parameters.p);
	}
	
	public static int multE(int x, int y){
		return mult(x,y,Parameters.q);
	}
	
	private static int mult(int x, int y, int m){
		long a = x, b = y;
		return (mod((a*b),m));		
	}
	
	public static int addE(int x, int y){
		long a = x, b = y;
		long result = a + b;
		while(result < 0){
			result = result + Parameters.q;
		}
		return (mod((result),Parameters.q));
	}
	

	public static int mod(long base, int mod) {
		assert base >= 0 ;
		int r = (int)(base % mod);
		if(r < 0)
			r += Math.abs(mod);
		return r;
	}



}
