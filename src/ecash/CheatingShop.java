package ecash;

import ecash.exception.*;
import ecash.signature.bank.BKSig;
import ecash.signature.ot.OTvk;
import java.util.ArrayList;

public class CheatingShop extends Shop {
    ArrayList<OTvk> coinList = new ArrayList<OTvk>();
    ArrayList<BKSig> sigmaBList = new ArrayList<BKSig>();
    ArrayList<Pair> sigmaList = new ArrayList<Pair>();
    ArrayList<Integer> pidList = new ArrayList<Integer>();


    /**
     * Constructor
     * Create a new Cheating Shop
     * @param id: The Shop id
     * @param bank: The Shops Bank
     */
    public CheatingShop(int id, Bank bank) {
        super(id, bank);
    }

    /**
	 * Spending Protocol
	 * A User contacts the Shop to buy something.
	 * The Cheating Shop saves the Coin to deposit it again later
	 * @param c: The Coin
	 * @param sigmaB: The Banks signature on the Coin
	 * @param sigma: The OT signature on the payment identifier
	 * @param pid: The payment identifier
	 */
    public void buy(OTvk c, BKSig sigmaB, Pair sigma, int pid) throws InvalidCoinException, InvalidPidException, DoubleDepositException, DoubleSpendingException {
        super.buy(c, sigmaB, sigma, pid);

        coinList.add(c);
        sigmaBList.add(sigmaB);
        sigmaList.add(sigma);
        pidList.add(pid);
    }

    /**
     * Cheat deposit
     * Try to deposit a Coin again
     */
    public void depositCoinAgain() throws InvalidCoinException, InvalidPidException, DoubleDepositException, DoubleSpendingException, NoCoinException {
        if(coinList.size() == 0) throw new NoCoinException();
        bank.deposit(coinList.get(0),sigmaBList.get(0),sigmaList.get(0),pidList.get(0));
    }
}
