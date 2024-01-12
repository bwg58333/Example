import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    private ServerSocket serverSocket;
    private final List<PrintWriter> clientWriters = new CopyOnWriteArrayList<>();

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server started on port: " + port);
    }

    public void start() {
        ExecutorService pool = Executors.newCachedThreadPool();
        System.out.println("Waiting for clients...");

        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                clientWriters.add(writer);

                pool.execute(() -> handleClient(clientSocket, writer));
            } catch (IOException e) {
                System.out.println("Server exception: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void handleClient(Socket clientSocket, PrintWriter writer) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            System.out.println("Client connected.");
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received from client: " + inputLine);
                // Example: Echoing received message back to all clients
                for (PrintWriter clientWriter : clientWriters) {
                    clientWriter.println("Echo: " + inputLine);
                }
            }
        } catch (IOException e) {
            System.out.println("Client connection error: " + e.getMessage());
        } finally {
            clientWriters.remove(writer);
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Client disconnected.");
        }
    }

    public void broadcastUpdate(String update) {
        for (PrintWriter writer : clientWriters) {
            writer.println(update);
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(1234);
        server.start();
    }
}
