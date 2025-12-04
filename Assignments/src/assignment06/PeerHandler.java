package assignment06;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class PeerHandler implements Runnable {

    final Socket socket;
    final PrintWriter printWriter;
    final BufferedReader bufferedReader;
    final int remoteListeningPort;
    private final CopyOnWriteArrayList<PeerHandler> neighborList;

    public PeerHandler(Socket socket, PrintWriter printWriter, BufferedReader bufferedReader, int port,
                       CopyOnWriteArrayList<PeerHandler> neighborList) {

        this.socket = socket;
        this.printWriter = printWriter;
        this.bufferedReader = bufferedReader;
        this.remoteListeningPort = port;
        this.neighborList = neighborList;
    }

    public void sendDisconnect() {
        printWriter.println("DISCONNECT");
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }

    @Override
    public void run() {
        try {
            String msg;
            while ((msg = bufferedReader.readLine()) != null) {
                if (msg.equals("DISCONNECT")) {
                    System.out.println("\n: Peer " + socket.getInetAddress().getHostAddress() + " disconnected!");
                    break;
                }
            }
        } catch (IOException e) {
            // Connection lost unexpectedly
        } finally {
            neighborList.remove(this);
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }
}