import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Register {
    private List<String[]> scannedItems;
    private int quantity;
    private double subtotal;
    private double total;
    private double taxAmount;
    private double nextDollar;
    private final PriceBook priceBook;
    private static final double TAX_RATE = 1.07;

    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    Date date = new Date();

    public Register(PriceBook priceBook) {
        this.priceBook = priceBook;
        scannedItems = new ArrayList<>();
        subtotal = 0.0;
        total = 0.0;
        quantity = 0;
    }

    public void setTotal(double newTotal) {
        this.total = newTotal;
    }

    public void scanItem(String barcode) {
        String[] productInfo = priceBook.getProductInfo(barcode);
        if (productInfo != null) {
            scannedItems.add(new String[] { barcode, productInfo[0], productInfo[1] });
            quantity++;
            updateSubtotal();
        }

        // quantity++;

        System.out.println("Item" + " " + productInfo[0] + " " + "scanned => " + "Basket Qty: " + quantity);
        System.out.println("Subtotal: $" + subtotal);

    }

    public void printLiveReceipt() {
        System.out.println("Welcome to POS");
        System.out.println("Cashier: Bobby");
        System.out.println(formatter.format(date));

    }

    /**
     * private void updateSubtotal() {
     * double sum = 0.0;
     * for (String[] item : scannedItems) {
     * double v = Double.parseDouble(item[2]);
     * sum += v;
     * }
     * subtotal = sum;
     * }
     * /
     */

    private void updateSubtotal() {
        double sum = 0.0;
        for (String[] item : scannedItems) {
            double v = Double.parseDouble(item[2]);
            sum += v;
        }
        subtotal = sum;
        updateTotal(); // Update the total whenever the subtotal changes
    }

    /**
     * public void updateTotal(double subtotal) {
     * // double taxAmount = subtotal * 0.07;
     * taxAmount = subtotal * 0.07;
     * total = taxAmount + subtotal;
     * }
     */

    public void updateTotal() {
        // Assuming tax is calculated as 7% of the subtotal
        taxAmount = subtotal * 0.07;
        total = subtotal + taxAmount;
    }

    public void voidLastItem() {
        if (!scannedItems.isEmpty()) {
            scannedItems.remove(scannedItems.size() - 1);
            updateSubtotal();
        }
        if (quantity > 0) {
            quantity--;
        }
    }

    public void voidTransaction() {
        // scannedItems.clear();

        // updateSubtotal();
        subtotal = 0;

        quantity = 0;

    }

    /**
     * public void nextDollar(double total) {
     * if (this.total > 0) {
     * this.total = Math.ceil(total);
     * }
     * nextDollar = this.total;
     * }
     */

    public double calculateChange() {
        double amountPaid = Math.ceil(total);
        return amountPaid - total;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getTotal() {
        return total;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getNextDollar() {
        return nextDollar;
    }

    public List<String[]> getScannedItems() {
        return scannedItems;
    }
}
