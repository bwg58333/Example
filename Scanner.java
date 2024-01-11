public class Scanner {
    private final Register register;

    public Scanner(Register register) {
        this.register = register;
    }

    public void scan(String barcode) {
        register.scanItem(barcode);
    }
}
