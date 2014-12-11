package ecash;

import java.util.ArrayList;
import javax.swing.*;

import ecash.exception.DoubleDepositException;
import ecash.exception.DoubleSpendingException;
import ecash.exception.InvalidCoinException;
import ecash.exception.InvalidPidException;
import ecash.exception.NoCoinException;
import ecash.ui.*;

public class Main {

	public static void main(String[] args) {
		final Bank b = new Bank();

		// Create some users
        User u1 = new User(225, b);
        User u2 = new User(337, b);
        User u3 = new User(789, b);
        CheatingUser cu1 = new CheatingUser(5035,b);
        final ArrayList<InterfaceUser> users = new ArrayList<InterfaceUser>();
        users.add(new InterfaceUser("User 1", u1));
        users.add(new InterfaceUser("User 2", u2));
        users.add(new InterfaceUser("User 3", u3));
        users.add(new InterfaceCheatingUser("Cheater 1", cu1));

		// Create a few shops
		Shop s1 = new Shop(22, b);
		Shop s2 = new Shop(37, b);
		CheatingShop s3 = new CheatingShop(42, b);
        final ArrayList<InterfaceShop> shops = new ArrayList<InterfaceShop>();
        shops.add(new InterfaceShop("Shop 1", s1));
        shops.add(new InterfaceShop("Shop 2", s2));
        shops.add(new InterfaceShop("Shop 3", s3));

		// Withdraw coins for different users
		u1.withdraw().withdraw();
		u3.withdraw();
		u2.withdraw().withdraw().withdraw();
        cu1.withdraw();
		// Spend the coins we just withdrawn
		trySpendCoin(u1, s1, "Spent coin for u1 in s1");
		trySpendCoin(u1, s2, "Spent coin for u1 in s2");
		
		trySpendCoin(u2, s3, "Spent coin for u2 in s3");
		trySpendCoin(u2, s3, "Spent coin for u2 in s3");
		trySpendCoin(u2, s3, "Spent coin for u2 in s1");

		trySpendCoin(u3, s2, "Spent coin for u3 in s2");
        trySpendCoin(cu1, s2, "Spent coin for cu1 in s2");

		// Try to spend a coin without withdrawing first
		trySpendCoin(u3, s1, "Spent coin for u3 in s1");
        //Try to spend a coin twice
        trySpendCoinTwice(cu1,s2,"spend spend coin for cu1 in s2");
        //Try to deposit a coin twict
        tryDepositCoinTwice(s3);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GUIInterface(new InterfaceBank("The Bank", b), users, shops);
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

    private static void trySpendCoinTwice(CheatingUser u, Shop s, String m) {
        try {
            u.spendUsedCoin(s);
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

    private static void tryDepositCoinTwice(CheatingShop s){
        try {
            s.depositCoinAgain();
        } catch (InvalidCoinException e) {
            System.out.println("Tried to deposit an invalid coin");
        } catch (InvalidPidException e) {
            System.out.println("Invalid pid of shop");
        } catch (DoubleDepositException e) {
            System.out.println("Tried to deposit a coin twice");
        } catch (DoubleSpendingException e) {
            System.out.println("Tried to spend an coin twice");
        } catch (NoCoinException e) {
            System.out.println("No coin to dobble spend");
        }
    }

}
