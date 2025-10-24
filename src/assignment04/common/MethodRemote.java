package assignment04.common;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;

public class MethodRemote extends UnicastRemoteObject implements Method {

    public MethodRemote() throws RemoteException {
        super();
    }

    public String action(String input, String clientHost, Callback callbackObj) {

        String result;

        if (input.equalsIgnoreCase("time")) {
            result = new Date().toString();
        } else {
            result = input.toUpperCase();
        }

        try {
            callbackObj.notifyMe(result);
        } catch (Exception e) {
            System.out.println("RMI error: " + e.getMessage());
        }

        return result;
    }
}