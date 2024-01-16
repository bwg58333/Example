public class LineItem {
    private String itemName;
    private double price;
    private int quantity;

    public LineItem(String itemName, double price, int quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }

    // Getters
    public String getItemName() {
        return itemName;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    // Method to get total price for the line item
    public double getTotalPrice() {
        return price * quantity;
    }

    // Override toString for easy printing, if needed
    @Override
    public String toString() {
        return "LineItem{" +
                "itemName='" + itemName + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
