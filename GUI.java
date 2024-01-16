import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GUI {
    private JFrame frame;
    private JTextField barcodeInput;
    private JTextArea virtualJournalArea;
    private JLabel poleDisplay;
    private JButton nextDollarButton, payForTicketButton, voidTransactionButton, voidLastItemButton;
    private VirtualJournal virtualJournal;
    private Register register;
    private JPanel quickKeysPanel;
    private Server server;

    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    Date date = new Date();

    public GUI() {
        register = new Register(new PriceBook("Tsv.txt"));
        virtualJournal = new VirtualJournal();
        virtualJournalArea = virtualJournal.getJournalArea();
        virtualJournal.appendText(
                formatter.format(date) + " " + "Cashier: Bobby " + "Begin Transacation#"
                        + register.getTransactionNumber());

        try {
            server = new Server(1234);
            new Thread(server::start).start();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        server.broadcastUpdate(virtualJournal.getJournalEntries());

        initializeUI();
        setupGlobalKeyListener();
    }

    private void initializeUI() {
        frame = new JFrame("POS System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        frame.setPreferredSize(new Dimension(1000, 800));

        barcodeInput = new JTextField();
        barcodeInput.addActionListener(this::barcodeInputAction);

        virtualJournalArea = virtualJournal.getJournalArea();
        virtualJournalArea.setEditable(false);

        poleDisplay = new JLabel("Pole Display");

        setupQuickKeysPanel();

        JPanel mainPanel = new JPanel(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(virtualJournalArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(poleDisplay, BorderLayout.SOUTH);

        frame.add(barcodeInput, BorderLayout.NORTH);
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(quickKeysPanel, BorderLayout.WEST);
        frame.add(createActionButtonsPanel(), BorderLayout.EAST);

        frame.pack();
        frame.setVisible(true);
    }

    private void setupQuickKeysPanel() {
        quickKeysPanel = new JPanel(new GridLayout(3, 3));

        // Example quick keys
        JButton quickKey1 = new JButton("Red Bull");
        JButton quickKey2 = new JButton("Gatorade Fruit Punch");
        JButton quickKey3 = new JButton("Donut");

        quickKey1.addActionListener(e -> quickKeyAction("80"));
        quickKey2.addActionListener(e -> quickKeyAction("052000135138"));
        quickKey3.addActionListener(e -> quickKeyAction("049000000443"));

        quickKeysPanel.add(quickKey1);
        quickKeysPanel.add(quickKey2);
        quickKeysPanel.add(quickKey3);

    }

    private void quickKeyAction(String barcode) {
        String log = register.scanItem(barcode);
        virtualJournal.appendText(log);
        updatePoleDisplay();
        server.broadcastUpdate(log);
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
        server.broadcastUpdate(log);
    }

    private JPanel createActionButtonsPanel() {
        JPanel actionButtonsPanel = new JPanel(new GridLayout(4, 1));

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
        server.broadcastUpdate(log);
    }

    private void payForTicketAction() {
        String log = "Payment processed. Subtotal: $" + register.getSubtotal() +
                " | Total: $" + register.getTotal() +
                " | Items: " + register.getQuantity() +
                " | Tax: " + register.getTaxAmount();
        virtualJournal.appendText(log);
        server.broadcastUpdate(log);

        virtualJournal.clear();
        register.resetTransaction();

        String newTransactionLog = "New Transaction Started\n" +
                formatter.format(date) + " " + "Cashier: Bobby " + "Begin Transacation#"
                + register.getTransactionNumber();

        virtualJournal.appendText(newTransactionLog);
        server.broadcastUpdate(newTransactionLog);

        updatePoleDisplay();
    }

    private void voidTransactionAction() {
        String log = register.voidTransaction();
        virtualJournal.appendText(log);
        updatePoleDisplay();
        server.broadcastUpdate(log);
    }

    private void voidLastItemAction() {
        String log = register.voidLastItem();
        virtualJournal.appendText(log);
        updatePoleDisplay();
        server.broadcastUpdate(log);
    }

    private void updatePoleDisplay() {
        poleDisplay.setText("Subtotal: $" + register.getSubtotal() +
                " | Total: $" + register.getTotal() +
                " | Items: " + register.getQuantity() +
                " | Tax: " + register.getTaxAmount());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUI::new);
    }

}
