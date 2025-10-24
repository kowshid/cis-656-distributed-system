package assignment04.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Method extends Remote {
    String action(String input, String clientHost, Callback callbackObj) throws RemoteException;
}