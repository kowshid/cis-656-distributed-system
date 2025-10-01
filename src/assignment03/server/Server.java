package assignment03.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {

    private static final int PORT = 5000;

    private final StringProcessor stringProcessor = StringProcessorImpl.getInstance();
    private static final AtomicInteger CLIENT_COUNTER = new AtomicInteger(0);

    public static void main(String[] args) throws IOException {
        new Server().begin();
    }

    private void begin() throws IOException {
        ServerSocket listener = new ServerSocket(PORT);
        System.out.println("The server is running.");

        while (true) {
            Socket clientSocket = listener.accept();
            int clientId = CLIENT_COUNTER.incrementAndGet();
            System.out.println("Received connection from client #" + clientId + " (" + clientSocket.getInetAddress() + ")");
            new Thread(new ClientHandler(clientSocket, stringProcessor, clientId)).start();
        }
    }
}
