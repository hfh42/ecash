import java.util.ArrayList;
import java.util.Random;

public class Main {

	public static void main(String[] args) {
		
	}

	private static void testHash() {
		// test hashing
		ArrayList<Integer> test = new ArrayList<Integer>();
		Random rnd = new Random();
		for(int i = 0; i < 7; i++)
			test.add(rnd.nextInt());
		System.out.println(Util.hash(test));
	}

}
