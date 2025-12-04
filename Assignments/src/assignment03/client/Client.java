package assignment03.client;

import java.util.Scanner;

public class Client {

    private static final int PORT = 5000;

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the server IP: ");
        String serverIp = scanner.nextLine().trim();

        try {
            StringProcessor stringProcessor = new StringRemoteProcessorImpl(serverIp, PORT);

            while (true) {
                System.out.print("Enter a string: ");
                String input = scanner.nextLine();
                if (RpcRequest.EMPTY_STRING.equals(input)) {
                    break;
                }

                String response = stringProcessor.process(input);
                System.out.println("Server response: " + response);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
