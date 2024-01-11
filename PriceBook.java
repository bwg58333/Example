import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;

public class PriceBook {
    private Map<String, String[]> productData;

    public PriceBook(String filePath) {
        productData = new HashMap<>();
        loadProductDataFromTSV(filePath);
    }

    private void loadProductDataFromTSV(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split("\t");
                productData.put(values[0], new String[] { values[1], values[2] });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] getProductInfo(String barcode) {
        return productData.get(barcode);
    }

}