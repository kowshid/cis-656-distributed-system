package assignment06;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class Peer {

    public static final String NEIGHBOR = "neighbors";
    public static final String QUIT = "quit";
    public static final String DELIMITER = ":";
    public static final String ACCEPT = "ACCEPT";
    public static final int MAX_NEIGHBOR_COUNT = 3;
    private static final int SERVER_PORT = 9090;
    private static final CopyOnWriteArrayList<PeerHandler> NEIGHBOR_LIST = new CopyOnWriteArrayList<>();
    private static int myPort;
    private static String serverIp;
    private static ServerSocket myServerSocket;
    private static volatile boolean running = true;

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        try {
            myServerSocket = new ServerSocket(0);
            myPort = myServerSocket.getLocalPort();

            System.out.print("Please enter server's IP address: ");
            serverIp = scanner.nextLine().trim();

            startPeerServer();
            registerAndJoin();

            while (running) {
                String command = scanner.nextLine().trim();
                if (command.equalsIgnoreCase(NEIGHBOR)) {
                    printNeighbors();
                } else if (command.equalsIgnoreCase(QUIT)) {
                    performGracefulQuit();
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Could not determine local IP address. Exiting.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Failed to start server socket: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private static void registerAndJoin() {

        try {
            Socket socket = new Socket(serverIp, SERVER_PORT);
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            printWriter.println("REGISTER " + myPort);
            String response = bufferedReader.readLine();
            System.out.println("Registered to central server " + serverIp);

            if (response != null && response.startsWith("PEER")) {
                String target = response.split(" ")[1];
                connectToPeerRecursive(target);
            } else if (response != null && response.startsWith("EMPTY_NETWORK")) {
                System.out.println("You are the only peer bufferedReader the network");
            }
        } catch (IOException e) {
            System.out.println("Error connecting to Central Server.");
        }
    }

    private static void connectToPeerRecursive(String targetAddress) {
        String currentTarget = targetAddress;
        boolean connected = false;

        while (!connected && running) {
            String[] parts = currentTarget.split(DELIMITER);
            String ip = parts[0];
            int port = Integer.parseInt(parts[1]);

            try {
                Socket socket = new Socket(ip, port);
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                printWriter.println("HELLO " + myPort);

                String response = bufferedReader.readLine();

                if (response != null && response.startsWith(ACCEPT)) {
                    System.out.println("Connected to network via peer " + ip);

                    PeerHandler handler = new PeerHandler(socket, printWriter, bufferedReader, port, NEIGHBOR_LIST);
                    NEIGHBOR_LIST.add(handler);
                    new Thread(handler).start();
                    connected = true;

                    System.out.println("Peer " + ip + " is connected to me");

                } else if (response != null && response.startsWith("REDIRECT")) {
                    String newTarget = response.split(" ")[1];
                    socket.close();
                    currentTarget = newTarget;
                } else {
                    socket.close();
                    break;
                }
            } catch (IOException e) {
                System.out.println("Failed to connect to " + currentTarget + ". Aborting join attempt.");
                break;
            }
        }
    }

    private static void startPeerServer() {
        new Thread(() -> {
            try {
                while (running) {
                    Socket clientSocket = myServerSocket.accept();
                    handleIncoming(clientSocket);
                }
            } catch (IOException ignored) {
            }
        }).start();
    }

    private static void handleIncoming(Socket socket) {
        try {
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String req = bufferedReader.readLine();
            if (req != null && req.startsWith("HELLO")) {
                int remoteListenPort = Integer.parseInt(req.split(" ")[1]);
                String remoteIP = socket.getInetAddress().getHostAddress();

                if (NEIGHBOR_LIST.size() < MAX_NEIGHBOR_COUNT) {
                    printWriter.println("ACCEPT");

                    System.out.println("Peer " + remoteIP + " is connected to me");

                    PeerHandler handler = new PeerHandler(socket, printWriter, bufferedReader, remoteListenPort, NEIGHBOR_LIST);
                    NEIGHBOR_LIST.add(handler);
                    new Thread(handler).start();
                } else {
                    PeerHandler randomNeighbor = NEIGHBOR_LIST.get(new Random().nextInt(NEIGHBOR_LIST.size()));

                    String neighborIP = randomNeighbor.socket.getInetAddress().getHostAddress();
                    int neighborPort = randomNeighbor.remoteListeningPort;

                    printWriter.println("REDIRECT " + neighborIP + ":" + neighborPort);
                    socket.close();
                }
            }
        } catch (IOException ignored) {
        }
    }

    private static void printNeighbors() {
        if (NEIGHBOR_LIST.isEmpty()) System.out.println("No direct neighbors.");
        else {
            for (PeerHandler ph : NEIGHBOR_LIST) {
                System.out.println(" - " + ph.socket.getInetAddress().getHostAddress());
            }
        }
    }

    private static void performGracefulQuit() {

        running = false;

        for (PeerHandler ph : NEIGHBOR_LIST) {
            ph.sendDisconnect();
        }

        try (Socket socket = new Socket(serverIp, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println("DEREGISTER " + myPort);
        } catch (IOException e) {
            System.err.println("Could not deregister from server.");
        }

        try {
            if (myServerSocket != null) myServerSocket.close();
        } catch (IOException ignored) {
        }

        System.out.println("Peer stopped.");
        System.exit(0);
    }
}