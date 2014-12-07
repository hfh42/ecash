/**
 * Created by Randi on 07-12-2014.
 */
public class InterfaceBank {
    private Bank bank;
    private String displayName;
    private int registeredUsers = 0;
    private int deposits = 0;
    private int shops = 0;

    public InterfaceBank(String d, Bank b) {
        bank = b;
        displayName = d;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void increaseRegisteredUsers() {
        registeredUsers++;
    }

    public int getRegisteredUsers() {
        return  registeredUsers;
    }

    public void increaseShops() {
        shops++;
    }

    public int getShops() {
        return shops;
    }

    public void increaseDeposits() {
        deposits++;
    }

    public int getDeposits() {
        return deposits;
    }

    public Bank getBank() {
        return bank;
    }
}
