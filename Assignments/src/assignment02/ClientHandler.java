package assignment02;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.Objects;

class ClientHandler implements Runnable {

    private static final String TIME_STRING = "time";
    private final Socket clientSocket;
    private final int clientId;

    public ClientHandler(Socket socket, int clientId) {
        this.clientSocket = socket;
        this.clientId = clientId;
    }

    @Override
    public void run() {

        try {
            PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            InputStreamReader inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            printWriter.println("Hello, you are client #" + clientId);

            String inputLine;
            while (Objects.nonNull(inputLine = bufferedReader.readLine())) {
                if (inputLine.isEmpty()) {
                    System.out.println("Client #" + clientId + " disconnected.");
                    break; // end connection
                }

                handleInput(inputLine, printWriter);
            }
        } catch (IOException e) {
            System.out.println("Error with client #" + clientId + ": " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Failed to close client socket for #" + clientId);
            }
        }
    }

    private void handleInput(String inputLine, PrintWriter printWriter) {

        if (inputLine.equalsIgnoreCase(TIME_STRING)) {
            printWriter.println(new Date());
        } else {
            printWriter.println(inputLine.toUpperCase());
        }
    }
}
