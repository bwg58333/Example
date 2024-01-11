import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class TransactionModel {
    private JFrame frame;
    private JTextField barcodeInput;
    private JTextArea virtualJournalArea;
    private JLabel poleDisplay;
    private JButton nextDollarButton, payForTicketButton, voidTransactionButton, voidLastItemButton;
    private VirtualJournal virtualJournal;
    private Register register;
    private JPanel quickKeysPanel;

    public TransactionModel() {
        register = new Register(new PriceBook("Tsv.txt"));
        virtualJournal = new VirtualJournal();
        virtualJournalArea = virtualJournal.getJournalArea();
        initializeUI();
        setupGlobalKeyListener();
    }

    /**
     * private void initializeUI() {
     * frame = new JFrame("POS System");
     * frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     * frame.setLayout(new BorderLayout());
     * 
     * frame.setPreferredSize(new Dimension(1000, 800));
     * 
     * barcodeInput = new JTextField();
     * barcodeInput.addActionListener(this::barcodeInputAction);
     * 
     * virtualJournalArea = new JTextArea();
     * virtualJournalArea.setEditable(false);
     * 
     * poleDisplay = new JLabel("Pole Display");
     * 
     * setupButtons();
     * setupQuickKeysPanel();
     * 
     * JPanel mainPanel = new JPanel(new BorderLayout());
     * mainPanel.add(new JScrollPane(virtualJournalArea), BorderLayout.CENTER);
     * mainPanel.add(poleDisplay, BorderLayout.SOUTH);
     * 
     * frame.add(barcodeInput, BorderLayout.NORTH);
     * frame.add(mainPanel, BorderLayout.CENTER);
     * frame.add(quickKeysPanel, BorderLayout.WEST);
     * frame.add(createActionButtonsPanel(), BorderLayout.EAST); // Add the action
     * buttons panel here
     * 
     * frame.pack();
     * frame.setVisible(true);
     * }
     */

    private void initializeUI() {
        frame = new JFrame("POS System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        frame.setPreferredSize(new Dimension(1000, 800));

        barcodeInput = new JTextField();
        barcodeInput.addActionListener(this::barcodeInputAction);

        // Use the JTextArea from the VirtualJournal instance
        virtualJournalArea = virtualJournal.getJournalArea();
        virtualJournalArea.setEditable(false);

        poleDisplay = new JLabel("Pole Display");

        setupButtons();
        setupQuickKeysPanel();

        JPanel mainPanel = new JPanel(new BorderLayout());
        // Add the JScrollPane containing virtualJournalArea to the main panel
        JScrollPane scrollPane = new JScrollPane(virtualJournalArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(poleDisplay, BorderLayout.SOUTH);

        frame.add(barcodeInput, BorderLayout.NORTH);
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(quickKeysPanel, BorderLayout.WEST);
        frame.add(createActionButtonsPanel(), BorderLayout.EAST); // Add the action buttons panel

        frame.pack();
        frame.setVisible(true);
    }

    private void setupQuickKeysPanel() {
        quickKeysPanel = new JPanel(new GridLayout(3, 3)); // Adjust grid layout as needed

        // Example quick keys
        JButton quickKey1 = new JButton("Item 1");
        JButton quickKey2 = new JButton("Item 2");
        JButton quickKey3 = new JButton("Item 3");

        quickKey1.addActionListener(e -> quickKeyAction("80"));
        quickKey2.addActionListener(e -> quickKeyAction("barcode2"));
        quickKey3.addActionListener(e -> quickKeyAction("barcode3"));

        quickKeysPanel.add(quickKey1);
        quickKeysPanel.add(quickKey2);
        quickKeysPanel.add(quickKey3);

        // Add more quick keys as needed
    }

    private void quickKeyAction(String barcode) {
        String log = register.scanItem(barcode);
        virtualJournal.appendText(log);
        updatePoleDisplay();
    }

    private void setupGlobalKeyListener() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (!barcodeInput.isFocusOwner()) {
                    if (e.getID() == KeyEvent.KEY_TYPED && e.getKeyChar() != '\n') {
                        barcodeInput.setText(barcodeInput.getText() + e.getKeyChar());
                    } else if (e.getID() == KeyEvent.KEY_RELEASED && e.getKeyCode() == KeyEvent.VK_ENTER) {
                        processBarcodeInput();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void processBarcodeInput() {
        String barcode = barcodeInput.getText();
        String log = register.scanItem(barcode);
        virtualJournal.appendText(log);
        updatePoleDisplay();
        barcodeInput.setText("");
    }

    private JPanel createActionButtonsPanel() {
        JPanel actionButtonsPanel = new JPanel(new GridLayout(4, 1)); // 4 buttons, 1 column

        nextDollarButton = new JButton("Next Dollar");
        payForTicketButton = new JButton("Pay");
        voidTransactionButton = new JButton("Void Transaction");
        voidLastItemButton = new JButton("Void Last Item");

        nextDollarButton.addActionListener(e -> nextDollarAction());
        payForTicketButton.addActionListener(e -> payForTicketAction());
        voidTransactionButton.addActionListener(e -> voidTransactionAction());
        voidLastItemButton.addActionListener(e -> voidLastItemAction());

        actionButtonsPanel.add(payForTicketButton);
        actionButtonsPanel.add(nextDollarButton);
        actionButtonsPanel.add(voidLastItemButton);
        actionButtonsPanel.add(voidTransactionButton);

        return actionButtonsPanel;
    }

    private void setupButtons() {
        nextDollarButton = new JButton("Next Dollar");
        payForTicketButton = new JButton("Pay");
        voidTransactionButton = new JButton("Void Transaction");
        voidLastItemButton = new JButton("Void Last Item");

        nextDollarButton.addActionListener(e -> nextDollarAction());
        payForTicketButton.addActionListener(e -> payForTicketAction());
        voidTransactionButton.addActionListener(e -> voidTransactionAction());
        voidLastItemButton.addActionListener(e -> voidLastItemAction());

        // Add buttons to the frame or a panel (not shown in this snippet)
    }

    private void barcodeInputAction(ActionEvent e) {
        String barcode = barcodeInput.getText();

        System.out.println("Scanning barcode: " + barcode);

        String log = register.scanItem(barcode);

        System.out.println("Log from register: " + log);

        virtualJournal.appendText(log);
        updatePoleDisplay();
        barcodeInput.setText("");
    }

    private void nextDollarAction() {
        String log = register.nextDollar();
        virtualJournal.appendText(log);
        updatePoleDisplay();
    }

    private void payForTicketAction() {
        // Implement payment logic if necessary
        String log = "Payment processed. Total: $" + register.getTotal();
        virtualJournal.appendText(log);
        virtualJournal.clear();
        register.resetTransaction();
        virtualJournal.appendText("New Transaction started");
        updatePoleDisplay();
    }

    private void voidTransactionAction() {
        String log = register.voidTransaction();
        virtualJournal.appendText(log);
        updatePoleDisplay();
    }

    private void voidLastItemAction() {
        String log = register.voidLastItem();
        virtualJournal.appendText(log);
        updatePoleDisplay();
    }

    private void updatePoleDisplay() {
        poleDisplay.setText("Subtotal: $" + register.getSubtotal() +
                " | Total: $" + register.getTotal() +
                " | Items: " + register.getQuantity());
    }

    // Main method to run the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(TransactionModel::new);
    }

}
