package ecash.ui;

import ecash.User;
import ecash.exception.*;
/**
 * Created by Randi on 07-12-2014.
 */
public class InterfaceUser {
    private String displayName;
    private User user;
    private int currentCoins = 0;

    public InterfaceUser(String d, User u) {
        displayName = d;
        user = u;
    }

    public void withdraw() {
        user.withdraw();
        increaseCurrentCoins();
    }

    public void spendCoin(InterfaceShop shop, InterfaceBank bank) throws InvalidCoinException, InvalidPidException, NoCoinException, DoubleDepositException, DoubleSpendingException  {
        user.spendCoin(shop.getShop());
        decreaseCurrentCoins();
        shop.increaseSales();
        bank.increaseDeposits();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void increaseCurrentCoins() {
        currentCoins++;
    }

    public void decreaseCurrentCoins() {
        currentCoins--;
    }

    public int getCurrentCoins() {
        return currentCoins;
    }
}
