package assignment05;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;

public class HttpServer {

    private static final int PORT = 8088;
    private static final String HTML_FILE = "hello.html";

    public static void main(String[] args) {

        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on http://localhost:" + PORT);

            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    handleClient(clientSocket);
                } catch (IOException e) {
                    System.err.println("Error handling client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Could not start server: " + e.getMessage());
        }
    }

    private static void handleClient(Socket clientSocket) throws IOException {

        InputStreamReader inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
        BufferedReader in = new BufferedReader(inputStreamReader);
        OutputStream out = clientSocket.getOutputStream();

        String requestLine = in.readLine();
        if (requestLine == null) return;

        System.out.println("Request: " + requestLine);

        String[] parts = requestLine.split(" ");
        if (parts.length < 2) {
            sendResponse(out, 400, "text/plain", "Bad Request");
            return;
        }

        String method = parts[0];
        String path = parts[1];

        if (method.equals("GET") && path.equals("/hello.html")) {
            serveFile(out);
        } else {
            send404(out);
        }
    }

    private static void serveFile(OutputStream out) throws IOException {

        File file = new File(HTML_FILE);

        if (!file.exists()) {
            sendResponse(out, 500, "text/plain",
                    "Server Error: " + HTML_FILE + " not found on server");
            return;
        }

        byte[] content = Files.readAllBytes(file.toPath());

        String header = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + content.length + "\r\n" +
                "Connection: close\r\n" +
                "\r\n";

        out.write(header.getBytes());
        out.write(content);
        out.flush();

        System.out.println("Served " + HTML_FILE);
    }

    private static void send404(OutputStream out) {

        String body = "<html><body><h1>404 Not Found</h1></body></html>";
        sendResponse(out, 404, "text/html", body);
        System.out.println("Sent 404 Not Found");
    }

    private static void sendResponse(OutputStream out, int statusCode, String contentType, String body) {

        String statusText = (statusCode == 404) ? "Not Found" :
                (statusCode == 400) ? "Bad Request" : "Internal Server Error";

        String header = "HTTP/1.1 " + statusCode + " " + statusText + "\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Connection: close\r\n" +
                "\r\n";

        try {
            out.write(header.getBytes());
            out.write(body.getBytes());
            out.flush();
        } catch (IOException e) {
            System.err.println("Could not serve request: " + e.getMessage());
        }
    }
}