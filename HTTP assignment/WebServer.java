package Interoperability;

import java.io.*;
import java.net.*;

/**
 * WebServer class implements a simple HTTP server that listens for incoming
 * requests and responds with a basic HTML page.
 */
public class WebServer {

    private static final int PORT = 8080; // Server port

    public static void main(String[] args) {
        System.out.println("Starting Web Server on port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                // Accept an incoming client connection
                Socket clientSocket = serverSocket.accept();

                // Handle the client request in a separate thread
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Error starting Web Server: " + e.getMessage());
        }
    }

    /**
     * ClientHandler class handles a single client connection.
     */
    static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (InputStream input = clientSocket.getInputStream();
                 OutputStream output = clientSocket.getOutputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                 PrintWriter writer = new PrintWriter(output, true)) {

                // Read the HTTP request line
                String requestLine = reader.readLine();
                if (requestLine == null || requestLine.isEmpty()) {
                    return;
                }
                System.out.println("Received request: " + requestLine);

                // Consume the rest of the HTTP headers
                while (reader.readLine() != null && !reader.readLine().isEmpty()) {
                    // Do nothing (headers are not processed in this simple server)
                }

                // Prepare a simple HTML response
                String httpResponse = "HTTP/1.1 200 OK\r\n" +
                                      "Content-Type: text/html\r\n" +
                                      "Content-Length: 48\r\n" +
                                      "\r\n" +
                                      "<html><body><h1>Welcome to Web Server</h1></body></html>";

                // Send the response to the client
                writer.println(httpResponse);

            } catch (IOException e) {
                System.err.println("Error handling client request: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Error closing client socket: " + e.getMessage());
                }
            }
        }
    }
}
