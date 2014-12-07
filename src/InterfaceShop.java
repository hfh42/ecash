/**
 * Created by Randi on 07-12-2014.
 */
public class InterfaceShop {
    private Shop shop;
    private String displayName;
    private int sales = 0;

    public InterfaceShop(String d, Shop s) {
        shop = s;
        displayName = d;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void increaseSales() {
        sales++;
    }

    public int getSales() {
        return sales;
    }
}
