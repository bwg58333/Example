import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Formatter;

public class TransactionModel {
    private JFrame frame;
    private JTextField barcodeInput;
    private JTextArea virtualJournal;
    private JLabel poleDisplay;
    private JButton nextDollarButton;
    private JButton payForTicketButton;
    private JButton voidTransactionButton;
    private JButton voidLastItemButton;
    private Formatter formatter = new Formatter();

    private PriceBook priceBook;
    private Register register;
    private Scanner scanner;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TransactionModel::new);
    }

    public TransactionModel() {

        priceBook = new PriceBook("Tsv.txt");
        register = new Register(priceBook);
        scanner = new Scanner(register);
        setupGlobalKeyListener();
        initializeUI();

        register.printLiveReceipt();

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
        String barcode = barcodeInput.getText().trim();
        scanner.scan(barcode);
        updateVirtualJournal();
        barcodeInput.setText("");
    }

    private void initializeUI() {
        JFrame frame = new JFrame("Gas Station Transaction Model");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setPreferredSize(new Dimension(1000, 600));
        frame.setMinimumSize(new Dimension(1000, 600));

        barcodeInput = createBarcodeScannerPanel();
        frame.add(barcodeInput, BorderLayout.NORTH);

        virtualJournal = createVirtualJournalPanel();
        frame.add(new JScrollPane(virtualJournal), BorderLayout.CENTER);

        poleDisplay = createPoleDisplayPanel();
        frame.add(poleDisplay, BorderLayout.SOUTH);

        frame.add(createActionButtonsPanel(), BorderLayout.EAST);
        frame.add(createQuickKeysPanel(), BorderLayout.WEST);

        frame.pack();
        frame.setVisible(true);

    }

    private JTextField createBarcodeScannerPanel() {
        JTextField barcodeInput = new JTextField();
        barcodeInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processBarcodeInput();
            }
        });
        return barcodeInput;
    }

    private JTextArea createVirtualJournalPanel() {
        return new JTextArea();
    }

    private JLabel createPoleDisplayPanel() {
        return new JLabel("Subtotal and Quantity will Display Here as Items are Scanned");
    }

    private JPanel createQuickKeysPanel() {
        JPanel quickKeysPanel = new JPanel(new GridLayout(3, 3));
        JButton quickKey1 = new JButton("Red Bull SF 12Z");
        JButton quickKey2 = new JButton("Gatorade Fruit Punch");
        JButton quickKey3 = new JButton("Donut");

        quickKey1.addActionListener(e -> quickKey1Select());
        quickKey2.addActionListener(e -> quickKey2Select());
        quickKey3.addActionListener(e -> quickKey3Select());

        quickKeysPanel.add(quickKey1);
        quickKeysPanel.add(quickKey2);
        quickKeysPanel.add(quickKey3);

        return quickKeysPanel;
    }

    private JPanel createActionButtonsPanel() {
        JPanel actionButtonsPanel = new JPanel(new GridLayout(1, 4));

        nextDollarButton = new JButton("Next Dollar");
        voidLastItemButton = new JButton("Void Last Item");
        voidTransactionButton = new JButton("Void Transaction");
        payForTicketButton = new JButton("Pay");

        payForTicketButton.addActionListener(e -> payForTicket());
        nextDollarButton.addActionListener(e -> nextDollar());
        voidLastItemButton.addActionListener(e -> voidLastItem());
        voidTransactionButton.addActionListener(e -> voidTransaction());

        actionButtonsPanel.add(payForTicketButton);
        actionButtonsPanel.add(nextDollarButton);
        actionButtonsPanel.add(voidLastItemButton);
        actionButtonsPanel.add(voidTransactionButton);

        return actionButtonsPanel;
    }

    private void updateVirtualJournal() {
        virtualJournal.setText("");
        for (String[] item : register.getScannedItems()) {
            virtualJournal.append(String.join(" ", item) + "\n");
            if (voidTransactionButton.getModel().isPressed()) {
                virtualJournal.append("Transaction Voided");
            }
        }
        poleDisplay.setText("Subtotal: " + String.format("%.2f", register.getSubtotal()) +
                "\n" + "Basket Qty: " + register.getQuantity());

        if (register.getTotal() > 0) {
            poleDisplay.setText("Total: " + String.format("%.2f", register.getTotal()) +
                    "\n" + "Basket Qty: " + register.getQuantity());
        }

    }

    private void voidLastItem() {
        register.voidLastItem();
        virtualJournal.append("Item Voided");
        updateVirtualJournal();
    }

    private void voidTransaction() {
        register.voidTransaction();
        updateVirtualJournal();
        virtualJournal.append("Transaction Voided\n");
        System.out.println("Transaction Voided");
    }

    private void nextDollar() {
        /**
         * register.nextDollar(register.getTotal());
         * updateVirtualJournal();
         * System.out.println(register.getNextDollar());
         * virtualJournal.append("Change Generated: $" +
         * (Math.ceil(register.getNextDollar()) - register.getTotal()));
         */

        /**
         * double change = register.calculateChange();
         * updateVirtualJournal();
         * virtualJournal.append("Change Generated: $" + String.format("%.2f", change) +
         * "\n");
         */

        double roundedTotal = Math.ceil(register.getTotal()); // This rounds up to the nearest dollar.
        double change = roundedTotal - register.getTotal(); // This calculates the change to give back.

        // Update the total in the register to the rounded total.
        register.setTotal(roundedTotal);

        // Now, update the virtual journal with the new rounded total and the change.
        updateVirtualJournal();

        // Append the change information to the virtual journal.
        virtualJournal.append("Rounded Total: $" + String.format("%.2f", roundedTotal) + "\n");
        virtualJournal.append("Change Generated: $" + String.format("%.2f", change) + "\n");
    }

    private void payForTicket() {
        // register.updateTotal(register.getSubtotal());
        updateVirtualJournal();

        formatter.format("%.2f", register.getTotal());
        virtualJournal.append("Amount paid: $" + formatter);
        System.out.printf("Amount paid: $" + formatter);
    }

    private void quickKey1Select() {
        String barcode = "80";
        scanner.scan(barcode);
        updateVirtualJournal();
        barcodeInput.setText("");
    }

    private void quickKey2Select() {
        String barcode = "052000135138";
        scanner.scan(barcode);
        updateVirtualJournal();
        barcodeInput.setText("");
    }

    private void quickKey3Select() {
        String barcode = "049000000443";
        scanner.scan(barcode);
        updateVirtualJournal();
        barcodeInput.setText("");
    }

}
