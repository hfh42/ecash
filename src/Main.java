import java.util.ArrayList;
import java.util.Random;

public class Main {

	public static void main(String[] args) {
		Bank b = new Bank();

		// Create some users
		User u1 = new User(424242, b);
		User u2 = new User(1337, b);
		User u3 = new User(123456789, b);

		// Create a few shops
		Shop s1 = new Shop(242424, b);
		Shop s2 = new Shop(7331, b);
		Shop s3 = new Shop(987654321, b);

		// Withdraw coins for different users
		u1.withdraw().withdraw();
		u3.withdraw();
		u2.withdraw().withdraw().withdraw();

		// Spend the coins we just withdrawed
		u1.spendCoin(s1).spendCoin(s2);
		u2.spendCoin(s3).spendCoin(s3).spendCoin(s1);
		u3.spendCoin(s2);

		// Try to spend a coin without withdrawing first
		u3.spendCoin(s1);
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
