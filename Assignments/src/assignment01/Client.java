package assignment01;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        int port = 9999;
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter the server IP: ");
            String serverIp = scanner.nextLine();
            Socket socket = new Socket(serverIp, port);

            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String dateTime = bufferedReader.readLine();
            System.out.println("Server response: " + dateTime);
        } catch (IOException e) {
            System.out.println("Client exception: " + e.getMessage());
        }
    }
}
