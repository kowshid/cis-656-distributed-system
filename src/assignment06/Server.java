package assignment06;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class Server {

    public static final String MEMBERS = "members";
    public static final String QUIT = "quit";
    private static final int SERVER_PORT = 9090;
    private static final ConcurrentSkipListSet<String> MEMBER_IPS = new ConcurrentSkipListSet<>();
    private static final ConcurrentHashMap<String, String> MEMBER_DETAILS = new ConcurrentHashMap<>();
    private static String serverIp;

    public static void main(String[] args) {
        try {
            serverIp = InetAddress.getLocalHost().getHostAddress();
            initiateServer();

            Scanner scanner = new Scanner(System.in);
            while (true) {
                String command = scanner.nextLine().trim();
                if (command.equalsIgnoreCase(MEMBERS)) {
                    printMembers();
                } else if (command.equalsIgnoreCase(QUIT)) {
                    System.out.println("Terminating Central Server...");
                    System.exit(0);
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Could not determine local IP address. Exiting.");
            System.exit(1);
        }
    }

    private static void initiateServer() {
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
                System.out.println("The server is on at " + serverIp);
                while (true) {
                    Socket clientSocket = serverSocket.accept();

                    RegistrationHandler registrationHandler = new RegistrationHandler(
                            clientSocket,
                            serverIp,
                            MEMBER_DETAILS,
                            MEMBER_IPS
                    );

                    new Thread(registrationHandler).start();
                }
            } catch (IOException e) {
                System.out.println("Server listener stopped.");
            }
        }).start();
    }

    private static void printMembers() {
        if (MEMBER_IPS.isEmpty()) {
            System.out.println("No active members.");
        } else {
            System.out.println("Current Members:");
            for (String ip : MEMBER_IPS) {
                System.out.println(" - " + ip);
            }
        }
    }
}