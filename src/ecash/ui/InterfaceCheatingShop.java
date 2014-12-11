package ecash.ui;

import ecash.CheatingShop;
import ecash.exception.*;

/**
 * Created by Silwing on 11-12-2014.
 */
public class InterfaceCheatingShop extends InterfaceShop {
    private CheatingShop shop;

    public InterfaceCheatingShop(String d, CheatingShop s) {
        super(d, s);
        shop = s;
    }

    public void depositCoinAgain() throws InvalidCoinException, InvalidPidException, DoubleDepositException, DoubleSpendingException, NoCoinException {
        shop.depositCoinAgain();
    }
}
