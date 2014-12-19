package ecash;

import ecash.exception.DoubleDepositException;
import ecash.exception.DoubleSpendingException;
import ecash.exception.InvalidPidException;
import ecash.exception.NoCoinException;
import ecash.exception.InvalidCoinException;

import java.util.LinkedList;


public class CheatingUser extends User {
    private LinkedList<Coin> spendCoins = new LinkedList<Coin>();

    /**
     * Constructor
     * Create a new User
     * @param U: The Users secret id
     * @param bank: The Users Bank
     */
    public CheatingUser(int U, Bank bank) {
        super(U, bank);
    }

    /**
     * Normal Spending protocol
     * Saves the spent Coin for later double use
     * @param shop: The Shop in which the User should use a Coin
     * @return This Cheating User 
     */
    public User spendCoin(Shop shop) throws InvalidCoinException, InvalidPidException, NoCoinException, DoubleDepositException, DoubleSpendingException {
        spendCoins.add(super.coins.getFirst());
        super.spendCoin(shop);
        return this;
    }

    /**
     * Cheat spend coin
     * Try to spend a used Coin
     * @param shop: The Shop in which the User should use a Coin
     * @return This Cheating User 
     */
    public User spendUsedCoin(Shop shop) throws InvalidCoinException, InvalidPidException, NoCoinException, DoubleDepositException, DoubleSpendingException {
        if(spendCoins.size() == 0) throw new NoCoinException();
        int pid = shop.getNextPid();
        Coin c = spendCoins.removeFirst();
        Pair sigma = Util.OTSign(c.sk, pid);
        shop.buy(c.vk, c.sigmaB, sigma, pid);
        return this;
    }
}