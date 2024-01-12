import java.io.*;
import java.net.*;

public class Client {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public void connect(String serverAddress, int serverPort) throws IOException {
        socket = new Socket(serverAddress, serverPort);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Start a new thread to listen for messages from the server
        new Thread(this::listenForServerMessages).start();
    }

    private void listenForServerMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Message from server: " + message);
            }
        } catch (IOException e) {
            System.out.println("Error reading from server: " + e.getMessage());
        }
    }

    public void disconnect() throws IOException {
        if (socket != null) {
            socket.close();
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        try {
            client.connect("127.0.0.1", 1234);
            // The client will continue to listen for messages from the server
            // Add any additional client logic here
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
