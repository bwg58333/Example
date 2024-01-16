import java.util.Date;
import java.text.SimpleDateFormat;

public class Receipt {
    private Basket basket;
    private Date date;
    private double total;
    private static final double TAX_RATE = 0.07;

    public Receipt(Basket basket) {
        this.basket = basket;
        this.date = new Date(); // Sets the current date and time for the receipt
        this.total = calculateTotalWithTax();
    }

    private double calculateTotalWithTax() {
        double subtotal = basket.calculateTotal();
        return subtotal + (subtotal * TAX_RATE);
    }

    public String generateReceipt() {
        StringBuilder receipt = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        receipt.append("Receipt - ").append(dateFormat.format(date)).append("\n");
        for (LineItem item : basket.getItems()) {
            receipt.append(item.getItemName())
                    .append(" - Qty: ").append(item.getQuantity())
                    .append(" @ $").append(item.getPrice())
                    .append(" - Total: $").append(item.getTotalPrice())
                    .append("\n");
        }
        receipt.append("Subtotal: $").append(basket.calculateTotal()).append("\n");
        receipt.append("Tax: $").append(basket.calculateTotal() * TAX_RATE).append("\n");
        receipt.append("Total Amount: $").append(total);

        return receipt.toString();
    }
}
