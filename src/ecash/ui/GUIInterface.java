package ecash.ui;

import ecash.CheatingShop;
import ecash.CheatingUser;
import ecash.Shop;
import ecash.User;
import ecash.exception.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Randi on 05-12-2014.
 */
public class GUIInterface {
    // The data classes used in the interface
    private ArrayList<InterfaceUser> users;
    private ArrayList<InterfaceShop> shops;
    private InterfaceBank bank;

   // Top level window
    private JFrame window;
    // Status message area in the center of the application
    private JTextArea messages;

    // Panels for the users and shops in the right and left sides
    private JPanel userPanel, shopPanel;

    // Label for bank status in the bottom of the window
    private JLabel bankLabel;

    public GUIInterface(InterfaceBank b, ArrayList<InterfaceUser> u, ArrayList<InterfaceShop> s) {
        users = u;
        shops = s;
        bank = b;

        window = new JFrame("eCash GUI");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add a content pane with a title in the top part
        JPanel contentPane = new JPanel(new BorderLayout());
        JLabel label = new JLabel("eCash System");
        label.setBorder(new EmptyBorder(5, 5, 5, 5));
        label.setHorizontalAlignment(JLabel.CENTER);
        contentPane.add(label, BorderLayout.PAGE_START);

        // Add a user panel to store users and buttons for user actions
        userPanel = new JPanel(new GridBagLayout());
        contentPane.add(userPanel, BorderLayout.LINE_START);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.NORTH;
        c.weighty = 1;

        JButton userCreate = new JButton("Create User");
        userCreate.addActionListener(new CreateUserAction());
        c.gridwidth = 4;
        userPanel.add(userCreate, c);

        for(InterfaceUser user : users) {
            addUser(user);
        }

        // Add a shop panel to store shops and buttons for shop actions
        shopPanel = new JPanel(new GridBagLayout());
        contentPane.add(shopPanel, BorderLayout.LINE_END);

        JButton shopCreate = new JButton("Create Shop");
        shopCreate.addActionListener(new CreateShopAction());
        c.gridx = GridBagConstraints.RELATIVE;
        c.gridwidth = 2;
        shopPanel.add(shopCreate, c);

        for(InterfaceShop shop : shops) {
            addShop(shop);
        }

        // Add the message area in the middle
        messages = new JTextArea();
        messages.setEditable(false);
        messages.setColumns(50);
        messages.setRows(10);
        addStatusMessage("Welcome to the eCash System");
        addStatusMessage("Use the buttons in the interface to navigate the system.");
        messages.setAutoscrolls(true);
        JScrollPane scroll = new JScrollPane(messages, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        contentPane.add(scroll, BorderLayout.CENTER);

        // Add the bank label in the bottom of the window
        bankLabel = new JLabel(bank.getDisplayName() + " (users: " + bank.getRegisteredUsers() + ", shops: " + bank.getShops() + ", deposits: " + bank.getDeposits() + ")");
        bankLabel.setHorizontalAlignment(JLabel.CENTER);
        bankLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.add(bankLabel, BorderLayout.PAGE_END);

        window.getContentPane().add(contentPane);
        window.pack();
        window.setVisible(true);
    }

    // Update the text in the bank label
    private void updateBank() {
        bankLabel.setText(bank.getDisplayName() + " (users: " + bank.getRegisteredUsers() + ", shops: " + bank.getShops() + ", deposits: " + bank.getDeposits() + ")");
    }

    // Used to fetch a user label to update current coins
    private HashMap<InterfaceUser,JLabel> userLabels = new HashMap<InterfaceUser,JLabel>();

    // Add a user to the user panel
    private void addUser(InterfaceUser user) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weighty = 1;
        c.gridx = 0;

        bank.increaseRegisteredUsers();

        JLabel userLabel = new JLabel(user.getDisplayName() + " (coins: " + user.getCurrentCoins() + ")");
        userLabels.put(user, userLabel);
        userPanel.add(userLabel, c);

        JButton userWithdraw = new JButton("Withdraw");
        JButton userSpend = new JButton("Spend");
        userWithdraw.addActionListener(new WithdrawAction(user));
        userSpend.addActionListener(new SpendAction(user, new HonestSpend()));

        c.gridx = 1;
        userPanel.add(userWithdraw, c);
        c.gridx = 2;

        if(!(user instanceof InterfaceCheatingUser)) {
            c.gridwidth = 2;
            userPanel.add(userSpend, c);
        } else {
            userPanel.add(userSpend, c);
            JButton userCheat = new JButton("Cheat Spend");
            userCheat.addActionListener(new SpendAction(user, new CheatingSpend()));

            c.gridx = 3;
            userPanel.add(userCheat, c);
        }
    }

    // Update a user label
    private void updateUser(InterfaceUser user) {
        userLabels.get(user).setText(user.getDisplayName() + " (coins: " + user.getCurrentCoins() + ")");
    }

    // Used to fetch a shop label to update sales
    private HashMap<InterfaceShop,JLabel> shopLabels = new HashMap<InterfaceShop, JLabel>();

    // Update a shop label
    private void updateShop(InterfaceShop shop) {
        shopLabels.get(shop).setText(shop.getDisplayName() + " (sales: " + shop.getSales() + ")");
    }

    // Add a shop to the shop panel
    private void addShop(InterfaceShop shop) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.NORTHEAST;
        c.weighty = 1;
        c.gridx = 0;

        JLabel shopLabel = new JLabel(shop.getDisplayName() + " (sales: " + shop.getSales() + ")");
        shopLabels.put(shop, shopLabel);

        if(!(shop instanceof InterfaceCheatingShop)) {
            c.gridwidth = 2;
            shopPanel.add(shopLabel, c);
        } else {
            JButton depositCheat = new JButton("Cheat Deposit");
            depositCheat.addActionListener(new DepositAction((InterfaceCheatingShop)shop));
            shopPanel.add(depositCheat, c);

            c.gridx = 1;
            shopPanel.add(shopLabel, c);
        }

        bank.increaseShops();
    }

    // Instantiate a CreateDialog
    private abstract class CreateAction implements ActionListener {
        private String title, message;
        protected CreateDialog dialog;

        public CreateAction(String t, String m) {
            title = t;
            message = m;
        }

        public void actionPerformed(ActionEvent e) {
            dialog = new CreateDialog(window, title, message);
            dialog.pack();
            dialog.setVisible(true);
        }
    }

    // Add a user if the CreateDialog was successful
    private class CreateUserAction extends CreateAction {
        public CreateUserAction() { super("Create New User", "Create a new user by entering a name to display and an integer ID."); }
        @Override
        public void actionPerformed(ActionEvent e) {
            super.actionPerformed(e);
            if(!JOptionPane.UNINITIALIZED_VALUE.equals(dialog.getInputValue())) {
                InterfaceUser newUser;
                if(dialog.getCheat())
                    newUser = new InterfaceCheatingUser(dialog.getDisplayName(), new CheatingUser(dialog.getId(), bank.getBank()));
                else
                    newUser = new InterfaceUser(dialog.getDisplayName(), new User(dialog.getId(), bank.getBank()));
                users.add(newUser);
                addUser(newUser);
                userPanel.repaint();
                updateBank();
                addStatusMessage("New user created with name '" + dialog.getDisplayName() + "'.");
            }
        }
    }

    // Add a shop if the CreateDialog was successful
    private class CreateShopAction extends CreateAction {
        public CreateShopAction() { super("Create New Shop", "Create a new shop by entering a name to display and an integer ID."); }
        @Override
        public void actionPerformed(ActionEvent e) {
            super.actionPerformed(e);
            if(!JOptionPane.UNINITIALIZED_VALUE.equals(dialog.getInputValue())) {
                InterfaceShop newShop;
                if(dialog.getCheat())
                    newShop = new InterfaceCheatingShop(dialog.getDisplayName(), new CheatingShop(dialog.getId(), bank.getBank()));
                else
                    newShop = new InterfaceShop(dialog.getDisplayName(), new Shop(dialog.getId(), bank.getBank()));
                shops.add(newShop);
                addShop(newShop);
                shopPanel.repaint();
                updateBank();
                addStatusMessage("New shop created with name '" + dialog.getDisplayName() + "'.");
            }
        }
    }

    // Withdraw a coin
    private class WithdrawAction implements ActionListener {
        private InterfaceUser user;

        public WithdrawAction(InterfaceUser u) {
            user = u;
        }
        public void actionPerformed(ActionEvent e) {
            user.withdraw();
            updateUser(user);
        }
    }

    private interface SpendStrategy {
        public void spend(InterfaceUser user, InterfaceShop shop, InterfaceBank bank) throws NoCoinException, InvalidCoinException, InvalidPidException, DoubleDepositException, DoubleSpendingException;
    }

    private class HonestSpend implements SpendStrategy {
        public void spend(InterfaceUser user, InterfaceShop shop, InterfaceBank bank) throws NoCoinException, InvalidCoinException, InvalidPidException, DoubleDepositException, DoubleSpendingException {
            user.spendCoin(shop, bank);
        }
    }

    private class CheatingSpend implements SpendStrategy {
        public void spend(InterfaceUser user, InterfaceShop shop, InterfaceBank bank) throws NoCoinException, InvalidCoinException, InvalidPidException, DoubleDepositException, DoubleSpendingException {
            if(user instanceof InterfaceCheatingUser) {
                ((InterfaceCheatingUser)user).spendUsedCoin(shop, bank);
            } else
                throw new IllegalArgumentException("Cannot cheat spend without a cheating user");
        }
    }

    // Spend a coin
    private class SpendAction implements ActionListener {
        private InterfaceUser user;
        private SpendStrategy strategy;
        public SpendAction(InterfaceUser u, SpendStrategy s) {
            user = u;
            strategy = s;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            InterfaceShop shop = (InterfaceShop)JOptionPane.showInputDialog(window, "Choose the shop you want to buy from:", "Spend a Coin", JOptionPane.PLAIN_MESSAGE, null, shops.toArray(), null);
            if(shop == null) return;

            try {
                strategy.spend(user, shop, bank);
                updateUser(user);
                updateShop(shop);
                updateBank();
                addStatusMessage(user.getDisplayName() + " spent a coin in " + shop.getDisplayName());
            } catch(InvalidCoinException ex) {
                user.decreaseCurrentCoins();
                updateUser(user);
                addStatusMessage(user.getDisplayName() + " tried to use an invalid coin in " + shop.getDisplayName());
            } catch(InvalidPidException ex) {
                addStatusMessage(user.getDisplayName() + " got an invalid pid from " + shop.getDisplayName());
            } catch(NoCoinException ex) {
                addStatusMessage(user.getDisplayName() + " have no coins left. Withdraw another and try to buy again.");
            } catch(DoubleDepositException ex) {
                addStatusMessage(shop.getDisplayName() + " tried to deposit the same coin twice.");
            } catch(DoubleSpendingException ex) {
                addStatusMessage(user.getDisplayName() + " with private id "+ ex.U + " tried to spend the same coin twice.");

            }
        }
    }

    private class DepositAction implements ActionListener {
        private InterfaceCheatingShop shop;
        public DepositAction(InterfaceCheatingShop s) {
            shop = s;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                shop.depositCoinAgain();
            } catch(InvalidCoinException | InvalidPidException | DoubleSpendingException ex) {
                addStatusMessage("Unexpected exception in deposit...");
            } catch(NoCoinException ex) {
                addStatusMessage(shop.getDisplayName() + " tried to redeposit but have no coins yet.");
            } catch(DoubleDepositException ex) {
                addStatusMessage(shop.getDisplayName() + " tried to deposit the same coin twice.");
            }
        }
    }

    // Post a formatted message with timestamp to the status area
    private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
    private void addStatusMessage(String msg) {
        messages.append("[" + format.format(new Date()) + "] " + msg + "\n");
    }

    private class CreateDialog extends JDialog implements ActionListener, PropertyChangeListener {
        private JTextField idField, displayNameField;
        private JCheckBox cheatField;
        private int id = 0;
        private String displayName;
        private boolean cheat = false;
        private JOptionPane optionPane;

        private String createButton = "Create";
        private String cancelButton = "Cancel";

        public CreateDialog(Frame f, String title, String message) {
            super(f, true);

            setTitle(title);

            idField = new JTextField(10);
            displayNameField = new JTextField(10);
            cheatField = new JCheckBox();
            JLabel idLabel = new JLabel("ID: "), nameLabel = new JLabel("Name: ");
            JLabel cheatLabel = new JLabel("Cheat: ");
            Object[] content = {message, idLabel, idField, nameLabel, displayNameField, cheatLabel, cheatField};
            Object[] options = {createButton, cancelButton};

            optionPane = new JOptionPane(content, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION, null, options, options[0]);

            setContentPane(optionPane);

            setDefaultCloseOperation(DISPOSE_ON_CLOSE);

            idField.addActionListener(this);
            displayNameField.addActionListener(this);
            cheatField.addActionListener(this);

            optionPane.addPropertyChangeListener(this);
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getId() {
            return id;
        }

        public boolean getCheat() { return cheat; }

        public Object getInputValue() {
            return optionPane.getInputValue();
        }

        public void actionPerformed(ActionEvent e) {
            optionPane.setInputValue(e.getSource());
        }

        private boolean validateUserId() {
            String userIdStr = idField.getText();
            if (userIdStr.isEmpty()) {
                JOptionPane.showMessageDialog(CreateDialog.this, "Please enter an ID.", "Empty field", JOptionPane.ERROR_MESSAGE);
                optionPane.setInputValue(JOptionPane.UNINITIALIZED_VALUE);
                idField.requestFocusInWindow();
                return false;
            } else {
                try {
                    id = Integer.parseInt(userIdStr);
                    return true;
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(CreateDialog.this, "The ID must be a valid integer value.", "Integer ID", JOptionPane.ERROR_MESSAGE);
                    optionPane.setInputValue(JOptionPane.UNINITIALIZED_VALUE);
                    idField.selectAll();
                    idField.requestFocusInWindow();
                    return false;
                }
            }
        }

        private boolean validateDisplayName() {
            displayName = displayNameField.getText();
            if (displayName.isEmpty()) {
                JOptionPane.showMessageDialog(CreateDialog.this, "Please enter a name to display.", "Empty field", JOptionPane.ERROR_MESSAGE);
                optionPane.setInputValue(JOptionPane.UNINITIALIZED_VALUE);
                displayNameField.requestFocusInWindow();
                return false;
            }
            return true;
        }

        public void propertyChange(PropertyChangeEvent e) {
            String prop = e.getPropertyName();

            if(isVisible() && (e.getSource() == optionPane)) {

                // the VALUE_PROPERTY is not the same as the INPUT_VALUE_PROPERTY. we make use of the INPUT_VALUE_PROPERTY to carry our data
                // but the JOptionPane uses the VALUE_PROPERTY for other stuff
                if (JOptionPane.VALUE_PROPERTY.equals(prop)) {
                    // newValues delivered by VALUE_PROPERTY PropertyChangeEvent can be the actual labels of the button clicked,
                    // that's sooo counter-intuitive to me, but it's how we know which button got clicked
                    if (createButton.equals(e.getNewValue())) {
                        // "OK" functionality...
                        // ...this will fire the event that takes the user input to the validation code
                        if(validateUserId() && validateDisplayName()) {
                            optionPane.setInputValue(createButton);
                            dispose();
                        } else {
                            optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                        }
                    } else if (cancelButton.equals(e.getNewValue())) {
                        // "CANCEL" functionality
                        optionPane.setInputValue(JOptionPane.UNINITIALIZED_VALUE);
                        dispose();
                    }

                } else if(JOptionPane.INPUT_VALUE_PROPERTY.equals(prop)) {
                    Object value = optionPane.getInputValue();
                    if (value == JOptionPane.UNINITIALIZED_VALUE)
                        return;

                    // If one of our text fields where changed, validate them
                    if (idField == value) {
                        validateUserId();
                    } else if (displayNameField == value) {
                        validateDisplayName();
                    } else if (cheatField == value) {
                        cheat = cheatField.isSelected();
                        optionPane.setInputValue(JOptionPane.UNINITIALIZED_VALUE);
                    }
                }
            }
        }
    }
}
