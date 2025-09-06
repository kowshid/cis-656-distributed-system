package assignment01;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Server {

    public static void main(String[] args) {
        int port = 9999;

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("the server is up...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("received connection from client: " + clientSocket.getInetAddress());

                String currentTime = new Date().toString();
                PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
                printWriter.println(currentTime);
            }
        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
        }
    }
}
