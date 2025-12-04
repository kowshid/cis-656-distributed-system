package assignment04.server;

import assignment04.common.MethodRemote;

import java.rmi.Naming;

public class MyServer {

    public static void main(String[] args) {

        try {
            MethodRemote stub = new MethodRemote();
            Naming.rebind("rmi://localhost:5000/lab4", stub);
            System.out.println("The server is running.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}