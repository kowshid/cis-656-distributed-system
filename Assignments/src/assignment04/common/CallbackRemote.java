package assignment04.common;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class CallbackRemote extends UnicastRemoteObject implements Callback {

    public CallbackRemote() throws RemoteException {
        super();
    }

    public void notifyMe(String message) {

        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            System.out.println("Using Callback " + hostname + ": " + message);
        } catch (Exception e) {
            System.out.println("Callback error: " + e.getMessage());
        }
    }
}