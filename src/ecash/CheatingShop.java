package ecash;

import ecash.exception.*;
import ecash.signature.bank.BKSig;
import ecash.signature.ot.OTvk;
import java.util.ArrayList;

/**
 * Created by Nils Henning on 11-12-2014.
 */
public class CheatingShop extends Shop {
    ArrayList<OTvk> coinList = new ArrayList<OTvk>();
    ArrayList<BKSig> sigmaBList = new ArrayList<BKSig>();
    ArrayList<Pair> sigmaList = new ArrayList<Pair>();
    ArrayList<Integer> pidList = new ArrayList<Integer>();



    public CheatingShop(int id, Bank bank) {
        super(id, bank);
    }

    public void buy(OTvk c, BKSig sigmaB, Pair sigma, int pid) throws InvalidCoinException, InvalidPidException, DoubleDepositException, DoubleSpendingException {
        coinList.add(c);
        sigmaBList.add(sigmaB);
        sigmaList.add(sigma);
        pidList.add(pid);

        super.buy(c, sigmaB, sigma, pid);
    }

    public void depositCoinAgain() throws InvalidCoinException, InvalidPidException, DoubleDepositException, DoubleSpendingException, NoCoinException {
        if(coinList.size() == 0) throw new NoCoinException();
        bank.deposit(coinList.get(0),sigmaBList.get(0),sigmaList.get(0),pidList.get(0), shopid);
    }
}
