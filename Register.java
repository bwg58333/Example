import java.util.ArrayList;
import java.util.List;

public class Register {
    private List<String[]> scannedItems;
    private double subtotal;
    private double total;
    private double taxAmount;
    private int quantity;
    private int transaction;
    private final PriceBook priceBook;
    private static final double TAX_RATE = 0.07;

    public Register(PriceBook priceBook) {
        this.priceBook = priceBook;
        scannedItems = new ArrayList<>();
        subtotal = 0.0;
        total = 0.0;
        transaction = 1;
    }

    public String scanItem(String barcode) {
        String[] productInfo = priceBook.getProductInfo(barcode);
        if (productInfo != null) {
            scannedItems.add(new String[] { barcode, productInfo[0], productInfo[1] });
            updateSubtotal();
            quantity++;
            return "Item scanned: " + productInfo[0] + " - Price: $" + productInfo[1];
        } else {
            return "Item with barcode " + barcode + " not found.";
        }
    }

    private void updateSubtotal() {
        subtotal = 0.0;
        for (String[] item : scannedItems) {
            subtotal += Double.parseDouble(item[2]);
        }
        updateTotal();
    }

    private void updateTotal() {
        taxAmount = subtotal * TAX_RATE;
        total = subtotal + taxAmount;
    }

    public String voidLastItem() {
        if (!scannedItems.isEmpty()) {
            String[] removedItem = scannedItems.remove(scannedItems.size() - 1);
            updateSubtotal();
            quantity--;
            return "Last item voided: " + removedItem[1];
        } else {
            return "No items to void.";
        }
    }

    public String voidTransaction() {
        if (!scannedItems.isEmpty()) {
            scannedItems.clear();
            updateSubtotal();
            quantity = 0;
            return "Transaction voided.";
        } else {
            return "No transaction to void.";
        }
    }

    public void resetTransaction() {
        scannedItems.clear();
        subtotal = 0.0;
        total = 0.0;
        taxAmount = 0.0;
        quantity = 0;
        transaction++;
        // Add any additional reset logic needed
    }

    public String nextDollar() {
        double originalTotal = total;
        total = Math.ceil(total);
        double changeDue = total - originalTotal;

        if (changeDue > 0) {
            return String.format("Total rounded to next dollar: $%.2f. Change due: $%.2f", total, changeDue);
        } else {
            return String.format("Total (already at whole dollar): $%.2f", total);
        }
    }

    // Getters for subtotal, total, tax amount, and scanned items
    public double getSubtotal() {
        return subtotal;
    }

    public double getTotal() {
        return total;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getTransaction() {
        return transaction;
    }

    public List<String[]> getScannedItems() {
        return new ArrayList<>(scannedItems);
    }

}