package assignment02;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiClientServer {

    private static final AtomicInteger CLIENT_COUNTER = new AtomicInteger(0);

    public static void main(String[] args) {

        try {
            ServerSocket serverSocket = new ServerSocket(9999);
            System.out.println("The server is running...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                int clientId = CLIENT_COUNTER.incrementAndGet();
                System.out.println("Received connection from client #" + clientId + " (" + clientSocket.getInetAddress() + ")");

                new Thread(new ClientHandler(clientSocket, clientId)).start();
            }
        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
        }
    }
}
