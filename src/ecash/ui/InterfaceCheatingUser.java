package ecash.ui;

import ecash.CheatingUser;
import ecash.User;
import ecash.exception.*;

/**
 * Created by Silwing on 11-12-2014.
 */
public class InterfaceCheatingUser extends InterfaceUser {
    private CheatingUser user;

    public InterfaceCheatingUser(String d, CheatingUser u) {
        super(d, u);
        user = u;
    }

    public void spendUsedCoin(InterfaceShop shop, InterfaceBank bank) throws InvalidCoinException, InvalidPidException, NoCoinException, DoubleDepositException, DoubleSpendingException {
        user.spendUsedCoin(shop.getShop());
        decreaseCurrentCoins();
        shop.increaseSales();
        bank.increaseDeposits();
    }
}
