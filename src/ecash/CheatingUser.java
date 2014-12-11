package ecash;

import ecash.Bank;
import ecash.Shop;
import ecash.User;
import ecash.exception.DoubleDepositException;
import ecash.exception.DoubleSpendingException;
import ecash.exception.InvalidPidException;
import ecash.exception.NoCoinException;
import ecash.exception.InvalidCoinException;

import java.util.LinkedList;

/**
 * Created by Nils Henning on 12/8/2014.
 */
public class CheatingUser extends User {
    private LinkedList<Coin> spendCoins = new LinkedList<Coin>();

    public CheatingUser(int U, Bank bank) {
        super(U, bank);
    }

    public User spendCoin(Shop shop) throws InvalidCoinException, InvalidPidException, NoCoinException, DoubleDepositException, DoubleSpendingException {
        spendCoins.add(super.coins.getFirst());
        super.spendCoin(shop);
        return this;
    }

    public User spendUsedCoin(Shop shop) throws InvalidCoinException, InvalidPidException, NoCoinException, DoubleDepositException, DoubleSpendingException {
        if(spendCoins.size() == 0) throw new NoCoinException();
        int pid = shop.getPid();
        Coin c = spendCoins.removeFirst();
        Pair sigma = Util.OTSign(c.sk, pid);
        shop.buy(c.vk, c.sigmaB, sigma, pid);
        return this;
    }
}