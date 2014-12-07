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

/**
 * Created by Randi on 05-12-2014.
 */
public class GUIInterface {
    private ArrayList<InterfaceUser> users;
    private ArrayList<InterfaceShop> shops;
    private InterfaceBank bank;

    private JFrame window;
    private JTextArea messages;
    private JPanel userPanel, shopPanel;
    private JLabel bankLabel;

    public GUIInterface(InterfaceBank b, ArrayList<InterfaceUser> u, ArrayList<InterfaceShop> s) {
        users = u;
        shops = s;
        bank = b;

        window = new JFrame("eCash GUI");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout());
        JLabel label = new JLabel("eCash System");
        label.setBorder(new EmptyBorder(10, 10, 10, 10));
        label.setHorizontalAlignment(JLabel.CENTER);
        contentPane.add(label, BorderLayout.PAGE_START);

        userPanel = new JPanel(new GridBagLayout());
        contentPane.add(userPanel, BorderLayout.LINE_START);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.anchor = GridBagConstraints.NORTH;
        c.weighty = 1;

        JButton userCreate = new JButton("Create User");
        userCreate.addActionListener(new CreateUserAction());
        c.gridwidth = 3;
        userPanel.add(userCreate, c);

        for(InterfaceUser user : users) {
            addUser(user);
        }

        shopPanel = new JPanel(new GridBagLayout());
        contentPane.add(shopPanel, BorderLayout.LINE_END);

        JButton shopCreate = new JButton("Create Shop");
        shopCreate.addActionListener(new CreateShopAction());
        c.gridx = GridBagConstraints.RELATIVE;
        shopPanel.add(shopCreate, c);

        for(InterfaceShop shop : shops) {
            addShop(shop);
        }

        messages = new JTextArea();
        messages.setEditable(false);
        messages.setColumns(50);
        messages.setRows(10);
        addStatusMessage("Welcome to the eCash System");
        addStatusMessage("Use the buttons in the interface to navigate the system.");
        messages.setAutoscrolls(true);
        contentPane.add(messages, BorderLayout.CENTER);

        bankLabel = new JLabel(bank.getDisplayName() + " (users: " + bank.getRegisteredUsers() + ", shops: " + bank.getShops() + ", deposits: " + bank.getDeposits() + ")");
        bankLabel.setHorizontalAlignment(JLabel.CENTER);
        bankLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.add(bankLabel, BorderLayout.PAGE_END);

        window.getContentPane().add(contentPane);
        window.pack();
        window.setVisible(true);
    }

    private void updateBank() {
        bankLabel.setText(bank.getDisplayName() + " (users: " + bank.getRegisteredUsers() + ", shops: " + bank.getShops() + ", deposits: " + bank.getDeposits() + ")");
        bankLabel.repaint();
    }

    private void addUser(InterfaceUser user) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.anchor = GridBagConstraints.NORTH;
        c.weighty = 1;
        bank.increaseRegisteredUsers();
        c.gridx = 0;
        JLabel userLabel = new JLabel(user.getDisplayName() + " (coins: " + user.getCurrentCoins() + ")");
        userPanel.add(userLabel, c);
        JButton userWithdraw = new JButton("Withdraw");
        JButton userSpend = new JButton("Spend");
        // TODO: add click handlers
        c.gridx = 1;
        userPanel.add(userWithdraw, c);
        c.gridx = 2;
        userPanel.add(userSpend, c);
    }

    private void addShop(InterfaceShop shop) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.anchor = GridBagConstraints.NORTH;
        c.weighty = 1;
        c.gridx = 0;
        JLabel shopLabel = new JLabel(shop.getDisplayName() + " (sales: " + shop.getSales() + ")");
        shopPanel.add(shopLabel, c);
    }

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

    private class CreateUserAction extends CreateAction {
        public CreateUserAction() { super("Create New User", "Create a new user by entering a name to display and an integer ID."); }
        @Override
        public void actionPerformed(ActionEvent e) {
            super.actionPerformed(e);
            if(!JOptionPane.UNINITIALIZED_VALUE.equals(dialog.getInputValue())) {
                InterfaceUser newUser = new InterfaceUser(dialog.getDisplayName(), new User(dialog.getId(), bank.getBank()));
                users.add(newUser);
                addUser(newUser);
                userPanel.repaint();
                updateBank();
                addStatusMessage("New user created with name '" + dialog.getDisplayName() + "'.");
            }
        }
    }

    private class CreateShopAction extends CreateAction {
        public CreateShopAction() { super("Create New Shop", "Create a new shop by entering a name to display and an integer ID."); }
        @Override
        public void actionPerformed(ActionEvent e) {
            super.actionPerformed(e);
            if(!JOptionPane.UNINITIALIZED_VALUE.equals(dialog.getInputValue())) {
                InterfaceShop newShop = new InterfaceShop(dialog.getDisplayName(), new Shop(dialog.getId(), bank.getBank()));
                shops.add(newShop);
                addShop(newShop);
                shopPanel.repaint();
                updateBank();
                addStatusMessage("New shop created with name '" + dialog.getDisplayName() + "'.");
            }
        }
    }

    private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
    private void addStatusMessage(String msg) {
        messages.append("[" + format.format(new Date()) + "] " + msg + "\n");
    }

    private class CreateDialog extends JDialog implements ActionListener, PropertyChangeListener {
        private JTextField idField, displayNameField;
        private int id = 0;
        private String displayName;
        private JOptionPane optionPane;

        private String createButton = "Create";
        private String cancelButton = "Cancel";

        public CreateDialog(Frame f, String title, String message) {
            super(f, true);

            setTitle(title);

            idField = new JTextField(10);
            displayNameField = new JTextField(10);
            JLabel idLabel = new JLabel("ID: "), nameLabel = new JLabel("Name: ");
            Object[] content = {message, idLabel, idField, nameLabel, displayNameField};
            Object[] options = {createButton, cancelButton};

            optionPane = new JOptionPane(content, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION, null, options, options[0]);

            setContentPane(optionPane);

            setDefaultCloseOperation(DISPOSE_ON_CLOSE);

            idField.addActionListener(this);
            displayNameField.addActionListener(this);

            optionPane.addPropertyChangeListener(this);
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getId() {
            return id;
        }

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
                    }
                }
            }
        }
    }
}
