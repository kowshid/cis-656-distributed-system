package assignment03.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final StringProcessor stringProcessor;
    private final int clientId;

    public ClientHandler(Socket clientSocket, StringProcessor stringProcessor, int clientId) {
        this.clientSocket = clientSocket;
        this.stringProcessor = stringProcessor;
        this.clientId = clientId;
    }

    @Override
    public void run() {

        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());

            while (true) {
                Object object = objectInputStream.readObject();

                if (object instanceof RpcRequest) {

                    String result = stringProcessor.process(((RpcRequest) object).getMessage());
                    if(RpcRequest.EMPTY_STRING.equals(result)) {
                        break;
                    }

                    objectOutputStream.writeObject(result);
                    objectOutputStream.flush();
                }
            }
        } catch (Exception e) {
            System.out.println("Client #" + clientId + " disconnected unexpectedly: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (Exception e) {
                System.out.println("Client #" + clientId + " disconnected unexpectedly: " + e.getMessage());
            }

            System.out.println("Client #" + clientId + " disconnected.");
        }
    }
}
