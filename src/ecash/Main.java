package ecash;

import java.util.ArrayList;
import javax.swing.*;
import ecash.ui.*;

public class Main {

	public static void main(String[] args) {
		final Bank b = new Bank();

		// Create some users
        User u1 = new User(337, b);
        CheatingUser cu1 = new CheatingUser(5035,b);
        final ArrayList<InterfaceUser> users = new ArrayList<InterfaceUser>();
        users.add(new InterfaceUser("User 1", u1));
        users.add(new InterfaceCheatingUser("Cheater 1", cu1));

		// Create a few shops
		Shop s1 = new Shop(22, b);
		CheatingShop s3 = new CheatingShop(42, b);
        final ArrayList<InterfaceShop> shops = new ArrayList<InterfaceShop>();
        shops.add(new InterfaceShop("Shop 1", s1));
        shops.add(new InterfaceCheatingShop("Cheat Shop 1", s3));

        // Create UI
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GUIInterface(new InterfaceBank("The Bank", b), users, shops);
            }
        });
	}
}
