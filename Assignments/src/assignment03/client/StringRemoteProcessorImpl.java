package assignment03.client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class StringRemoteProcessorImpl implements StringProcessor {

    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    public StringRemoteProcessorImpl(String host, int port) throws Exception {

        Socket socket = new Socket(host, port);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public String process(String input) {
        try {
            RpcRequest request = new RpcRequest(input);
            out.writeObject(request);
            out.flush();

            Object response = in.readObject();
            if (response instanceof String) {
                return (String) response;
            } else {
                return "Invalid response from server.";
            }
        } catch (Exception e) {
            return "Error communicating with server: " + e.getMessage();
        }
    }
}
