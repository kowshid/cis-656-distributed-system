package assignment02;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the server IP: ");
        String serverIp = scanner.nextLine();

        try {
            Socket socket = new Socket(serverIp, 9999);
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            System.out.println(bufferedReader.readLine());

            while (true) {
                System.out.print("Enter a string (or press ENTER to quit): ");
                String userInput = scanner.nextLine();
                printWriter.println(userInput);

                if (userInput.isEmpty()) {
                    System.out.println("Connection closed.");
                    break;
                }

                String response = bufferedReader.readLine();
                if (Objects.isNull(response)) {
                    System.out.println("Server closed the connection.");
                    break;
                }
                System.out.println("Server response: " + response);
            }
        } catch (IOException e) {
            System.out.println("Client exception: " + e.getMessage());
        }
    }
}
