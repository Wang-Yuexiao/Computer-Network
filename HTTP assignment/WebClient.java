package Interoperability;

import java.io.*;
import java.net.*;

/**
 * WebClient class implements a simple web client that sends HTTP requests
 * through a proxy server and prints the response.
 */
public class WebClient {

    public static void main(String[] args) {
        // Define proxy server and target server details
        String proxyHost = "127.0.0.1"; // Proxy server IP (localhost for testing)
        int proxyPort = 8999;           // Proxy server port

        String targetUrl = "http://www.baidu.com"; // Target server URL

        try {
            // Parse the target URL
            URL url = new URL(targetUrl);
            String host = url.getHost();
            String path = url.getPath().isEmpty() ? "/" : url.getPath();

            // Establish a connection to the proxy server
            try (Socket proxySocket = new Socket(proxyHost, proxyPort);
                 PrintWriter out = new PrintWriter(proxySocket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(proxySocket.getInputStream()))) {

                // Send an HTTP GET request through the proxy
                out.println("GET " + targetUrl + " HTTP/1.1");
                out.println("Host: " + host);
                out.println("Connection: Close");
                out.println();

                // Read and print the response from the proxy server
                String responseLine;
                while ((responseLine = in.readLine()) != null) {
                    System.out.println(responseLine);
                }
            }

        } catch (MalformedURLException e) {
            System.err.println("Invalid URL: " + targetUrl);
        } catch (IOException e) {
            System.err.println("Error communicating with proxy server: " + e.getMessage());
        }
    }
}
