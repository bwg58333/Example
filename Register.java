
/** 
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Register {
    private List<String[]> scannedItems;
    private double subtotal;
    private double total;
    private double taxAmount;
    //private BigDecimal bg;
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
        //bg = BigDecimal.valueOf(subtotal);
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

    public double getSubtotal() {
        subtotal = (double) Math.round(subtotal * 100) / 100;
        return subtotal;
    }

    public double getTotal() {
        total = (double) Math.round(total * 100) / 100;
        return total;
    }

    public double getTaxAmount() {
        taxAmount = (double) Math.round(taxAmount * 100) / 100;
        return taxAmount;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getTransaction() {
        return transaction;
    }


}
*/

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class Register {
    private Basket basket;
    private final PriceBook priceBook;
    private static final double TAX_RATE = 0.07;
    private Scanner scanner;
    private int transactionNumber;
    private double total;

    public Register(PriceBook priceBook) {
        this.priceBook = priceBook;
        this.basket = new Basket();
        this.scanner = new Scanner(this);
        this.transactionNumber = 1;
        this.total = 0;
    }

    public String scanItem(String barcode) {
        String[] productInfo = priceBook.getProductInfo(barcode);
        if (productInfo != null) {
            LineItem item = new LineItem(productInfo[0], Double.parseDouble(productInfo[1]), 1);
            basket.addItem(item);

            updateTotal();

            return "Item scanned: " + item.getItemName() + " - Price: $" + item.getPrice();
        } else {
            return "Item with barcode " + barcode + " not found.";
        }
    }

    private void updateTotal() {
        double subtotal = basket.calculateTotal();
        BigDecimal totalWithTax = BigDecimal.valueOf(subtotal)
                .add(BigDecimal.valueOf(subtotal).multiply(BigDecimal.valueOf(TAX_RATE)));
        this.total = totalWithTax.doubleValue();
    }

    public String voidLastItem() {
        if (!basket.getItems().isEmpty()) {
            List<LineItem> items = basket.getItems();
            LineItem removedItem = items.get(items.size() - 1);
            basket.removeItem(removedItem);
            updateTotal();
            return "Last item voided: " + removedItem.getItemName() + " - Price: $" + removedItem.getPrice();
        } else {
            return "No items to void.";
        }
    }

    public double calculateTotal() {
        double subtotal = basket.calculateTotal();
        BigDecimal total = BigDecimal.valueOf(subtotal)
                .add(BigDecimal.valueOf(subtotal).multiply(BigDecimal.valueOf(TAX_RATE)));
        return total.doubleValue();
    }

    public String printReceipt() {
        Receipt receipt = new Receipt(basket);
        return receipt.generateReceipt();
    }

    public String processBarcode(String barcode) {
        String[] productInfo = priceBook.getProductInfo(barcode);
        if (productInfo != null) {
            LineItem item = new LineItem(productInfo[0], Double.parseDouble(productInfo[1]), 1);
            basket.addItem(item);

            // Log message for GUI
            return String.format("Item scanned: %s - Price: $%.2f", item.getItemName(), item.getPrice());
        } else {
            return "Item with barcode " + barcode + " not found.";
        }
    }

    public void resetTransaction() {
        basket.clear();
        transactionNumber++;
        total = 0.0;
    }

    public String completeTransaction() {
        // Logic to complete the transaction, generate receipt, and reset for new
        // transaction
        String receipt = printReceipt();
        resetTransaction();
        return receipt;
    }

    public String getPoleDisplayText() {
        // Generate text for the pole display, including subtotal, total, items, tax,
        // etc.
        return String.format("Subtotal: $%.2f | Total: $%.2f | Items: %d | Tax: $%.2f",
                getSubtotal(), getTotal(), getQuantity(), getTaxAmount());
    }

    public String nextDollar() {
        double originalTotal = total; // Store the original total
        total = Math.ceil(total); // Round the total up to the nearest whole number
        double changeDue = total - originalTotal; // Calculate the change due

        if (changeDue > 0) {
            return String.format("Total rounded to next dollar: $%.2f. Change due: $%.2f", total, changeDue);
        } else {
            return String.format("Total (already at whole dollar): $%.2f", total);
        }
    }

    public double getSubtotal() {

        BigDecimal subtotal = BigDecimal.valueOf(basket.calculateTotal());
        return subtotal.setScale(2, RoundingMode.HALF_UP).doubleValue();
        // return basket.calculateTotal();

    }

    public double getTotal() {
        // return calculateTotal();
        // return this.total;

        total = (double) Math.round(total * 100) / 100;
        return total;
    }

    public double getTaxAmount() {
        BigDecimal subtotal = BigDecimal.valueOf(getSubtotal());
        BigDecimal tax = subtotal.multiply(BigDecimal.valueOf(TAX_RATE));
        return tax.setScale(2, RoundingMode.HALF_UP).doubleValue();
        // return getSubtotal() * TAX_RATE;
    }

    public int getQuantity() {
        return basket.getItems().stream().mapToInt(LineItem::getQuantity).sum();
    }

    public int getTransactionNumber() {
        return transactionNumber;
    }

    public String voidTransaction() {
        basket.clear();
        updateTotal();
        return "Transaction voided.";
    }

    // Additional methods and logic as required...
}
