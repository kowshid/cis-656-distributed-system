package assignment04.client;

import assignment04.common.CallbackRemote;
import assignment04.common.Method;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.server.UnicastRemoteObject;

public class MyClient {

    public static void main(String[] args) {

        try {
            Method stub = (Method) Naming.lookup("rmi://localhost:5000/lab4");
            final CallbackRemote callbackObj = new CallbackRemote();

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String input;
            String hostname = InetAddress.getLocalHost().getHostName();

            while (true) {
                System.out.println("Enter a string to send to the server (empty to quit):");
                input = br.readLine();

                if (input == null || input.trim().isEmpty()) {
                    System.out.println("Client exiting...");
                    UnicastRemoteObject.unexportObject(callbackObj, true);
                    break;
                }

                String result = stub.action(input, hostname, callbackObj);
                System.out.println("Using RMI from " + hostname + ": " + result);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}