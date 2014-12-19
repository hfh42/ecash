package ecash.ui;

import ecash.Shop;

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

    public Shop getShop() {
        return shop;
    }

    public int getSales() {
        return sales;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
