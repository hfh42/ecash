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

    public User getUser() {
        return user;
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
