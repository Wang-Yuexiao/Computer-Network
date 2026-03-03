package Interoperability;

import java.io.*;
import java.net.*;

/**
 * ProxyServer class implements a simple multi-threaded HTTP proxy server.
 */
public class ProxyServer {

    // Proxy server port
    private static final int PROXY_PORT = 8999;

    public static void main(String[] args) {
        System.out.println("Starting Proxy Server on port " + PROXY_PORT);

        try (ServerSocket serverSocket = new ServerSocket(PROXY_PORT)) {
            while (true) {
                // Accept a client connection
                Socket clientSocket = serverSocket.accept();

                // Handle the client request in a separate thread
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Error starting Proxy Server: " + e.getMessage());
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
            try (InputStream clientInput = clientSocket.getInputStream();
                 OutputStream clientOutput = clientSocket.getOutputStream();
                 BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientInput));
                 PrintWriter clientWriter = new PrintWriter(clientOutput, true)) {

                // Read the HTTP request line from the client
                String requestLine = clientReader.readLine();
                if (requestLine == null || requestLine.isEmpty()) {
                    return;
                }
                System.out.println("Received request: " + requestLine);

                // Parse the request line
                String[] requestParts = requestLine.split(" ");
                if (requestParts.length < 3) {
                    System.err.println("Invalid request format");
                    return;
                }
                String method = requestParts[0];
                String url = requestParts[1];
                
                // Extract the host and port from the URL
                URL targetURL = new URL(url);
                String targetHost = targetURL.getHost();
                int targetPort = targetURL.getPort() == -1 ? 80 : targetURL.getPort();

                // Connect to the target server
                try (Socket targetSocket = new Socket(targetHost, targetPort);
                     InputStream targetInput = targetSocket.getInputStream();
                     OutputStream targetOutput = targetSocket.getOutputStream()) {

                    // Forward the client's request to the target server
                    PrintWriter targetWriter = new PrintWriter(targetOutput, true);
                    targetWriter.println(requestLine);

                    // Forward all headers
                    String header;
                    while ((header = clientReader.readLine()) != null && !header.isEmpty()) {
                        targetWriter.println(header);
                    }
                    targetWriter.println();

                    // Forward the target server's response back to the client
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = targetInput.read(buffer)) != -1) {
                        clientOutput.write(buffer, 0, bytesRead);
                    }
                }

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
