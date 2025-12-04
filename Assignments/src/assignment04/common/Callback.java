package assignment04.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Callback extends Remote {
    void notifyMe(String message) throws RemoteException;
}