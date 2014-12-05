import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Randi on 05-12-2014.
 */
public class GUIInterface {
    private ArrayList<User> users;
    private ArrayList<Shop> shops;
    private Bank bank;

    private JFrame window;

    public GUIInterface(Bank b, ArrayList<User> u, ArrayList<Shop> s) {
        users = u;
        shops = s;
        bank = b;

        window = new JFrame("eCash GUI");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout());
        JLabel label = new JLabel("eCash System");
        label.setHorizontalAlignment(JLabel.CENTER);
        contentPane.add(label, BorderLayout.PAGE_START);

        JPanel userPanel = new JPanel(new BorderLayout());

        window.getContentPane().add(contentPane);
        window.pack();
        window.setVisible(true);
    }
}
