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

        out.println("Hello World!");

        //New Thread to listen
        //new Thread(this::listenForServerMessages).start();
        listenForServerMessages();
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

    public static void main(String[] args) {
        Client client = new Client();
        try {
            //ME
            client.connect("192.168.168.7", 1234);

            //Mason
            //client.connect("192.168.168.8", 4206);

            //Michael
            //client.connect("192.168.168.24", 5000);

            //Dean
            //client.connect("192.168.168.141", 7777);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



