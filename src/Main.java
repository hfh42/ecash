import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Main {

	public static void main(String[] args) {
		final Bank b = new Bank();

		// Create some users
		User u1 = new User(424242, b);
		User u2 = new User(1337, b);
		User u3 = new User(123456789, b);
        final ArrayList<User> users = new ArrayList<User>();
        users.add(u1); users.add(u2); users.add(u3);

		// Create a few shops
		Shop s1 = new Shop(22, b);
		Shop s2 = new Shop(37, b);
		Shop s3 = new Shop(42, b);
        final ArrayList<Shop> shops = new ArrayList<Shop>();
        shops.add(s1); shops.add(s2); shops.add(s3);

		// Withdraw coins for different users
		u1.withdraw().withdraw();
		u3.withdraw();
		u2.withdraw().withdraw().withdraw();

		// Spend the coins we just withdrawn
		trySpendCoin(u1, s1, "Spent coin for u1 in s1");
		trySpendCoin(u1, s2, "Spent coin for u1 in s2");
		
		trySpendCoin(u2, s3, "Spent coin for u2 in s3");
		trySpendCoin(u2, s3, "Spent coin for u2 in s3");
		trySpendCoin(u2, s3, "Spent coin for u2 in s1");

		trySpendCoin(u3, s2, "Spent coin for u3 in s2");

		// Try to spend a coin without withdrawing first
		trySpendCoin(u3, s1, "Spent coin for u3 in s1");

        testInverse(159885032);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GUIInterface(b, users, shops);
            }
        });
	}

	private static void trySpendCoin(User u, Shop s, String m) {
		try {
			u.spendCoin(s);
			System.out.println(m);
		} catch(InvalidCoinException e) {
			System.out.println("Tried to spend an invalid coin");
		} catch(InvalidPidException e) {
			System.out.println("Contacted shop with an invalid transaction ID");
		} catch(NoCoinException e) {
			System.out.println("Tried to spend a coin with no coins withdrawn");
		} catch(DoubleDepositException e) {
			System.out.println("Tried to deposit the same transaction twice");
		} catch(DoubleSpendingException e) {
			System.out.println("Tried to spend the same coin twice");
		}
	}

    private static void testInverse(int a) {
        int b =  Util.modInverse(a);
        System.out.println(a + "^-1 = " + b);
        System.out.println(a + "*" + b + " = " + Util.multG(a,b));
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
