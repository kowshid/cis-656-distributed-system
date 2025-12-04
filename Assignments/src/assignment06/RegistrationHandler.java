package assignment06;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class RegistrationHandler implements Runnable {

    public static final String REGISTER = "REGISTER";
    private final Socket socket;
    private final String serverIp;
    private final ConcurrentHashMap<String, String> peerDetails;
    private final ConcurrentSkipListSet<String> activePeerIps;

    public RegistrationHandler(Socket socket, String serverIp, ConcurrentHashMap<String, String> peerDetails,
                               ConcurrentSkipListSet<String> activePeerIps) {

        this.socket = socket;
        this.serverIp = serverIp;
        this.peerDetails = peerDetails;
        this.activePeerIps = activePeerIps;
    }

    @Override
    public void run() {
        try {
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String request = bufferedReader.readLine();

            if (request != null && request.startsWith(REGISTER)) {
                String[] parts = request.split(" ");
                int peerListeningPort = Integer.parseInt(parts[1]);
                String peerIP = socket.getInetAddress().getHostAddress();
                String peerKey = peerIP + ":" + peerListeningPort;

                String randomPeerKey = null;
                if (!peerDetails.isEmpty()) {
                    List<String> keys = new ArrayList<>(peerDetails.keySet());
                    Random rand = new Random();
                    randomPeerKey = keys.get(rand.nextInt(keys.size()));
                }

                peerDetails.put(peerKey, socket.getInetAddress().getHostName());
                activePeerIps.add(peerIP);

                System.out.println("Peer " + peerIP + " entered the network");

                if (randomPeerKey == null) {
                    printWriter.println("EMPTY_NETWORK " + serverIp);
                } else {
                    printWriter.println("PEER " + randomPeerKey);
                }
            } else if (request != null && request.startsWith("DEREGISTER")) {
                String[] parts = request.split(" ");
                int peerListeningPort = Integer.parseInt(parts[1]);
                String peerIP = socket.getInetAddress().getHostAddress();
                String peerKey = peerIP + ":" + peerListeningPort;

                peerDetails.remove(peerKey);

                boolean hasOtherPorts = peerDetails.keySet().stream()
                        .anyMatch(key -> key.startsWith(peerIP + ":"));

                if (!hasOtherPorts) {
                    activePeerIps.remove(peerIP);
                }

                System.out.println("Peer " + peerIP + " left the network");
            }

        } catch (IOException ignored) {
        }
    }
}