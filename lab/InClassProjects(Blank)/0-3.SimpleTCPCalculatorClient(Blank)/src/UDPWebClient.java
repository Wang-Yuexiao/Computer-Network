import java.io.*;
import java.net.*;

/**
 * WebClient class implements a simple web client.
 * Its primary responsibilities include:
 * 1. Initializing the UDP connection to web server
 * 2. Sending HTTP request and receiving HTTP response
 */
public class UDPWebClient {

    public static void main(String[] args) {
        String host = "localhost"; // 服务器主机名
        int port = 6777;           // 服务器端口
        String resource = "/index.html"; // 请求的资源路径

        try {
            // Mission 1: Initialize InetAddress with host IP and DatagramSocket
            InetAddress address = InetAddress.getByName(host); // 根据主机名获取地址
            DatagramSocket socket = new DatagramSocket();      // 创建UDP套接字

            // Mission 2: Create HTTP GET Request Message
            String requestMessage = "GET " + resource + " HTTP/1.0\r\n" +
                                    "Host: " + host + "\r\n" +
                                    "User-Agent: UDPWebClient\r\n" +
                                    "Accept: text/html\r\n\r\n";
            byte[] requestData = requestMessage.getBytes(); // 转换为字节数组
            DatagramPacket requestPacket = new DatagramPacket(
                requestData, requestData.length, address, port
            );

            // Send the request to the server
            socket.send(requestPacket); // 发送请求数据包

            // Mission 3: Initialize buffer and DatagramPacket to receive data
            byte[] responseData = new byte[1024]; // 用于接收响应的缓冲区
            DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length);

            // Receive the response from the server
            socket.receive(responsePacket); // 接收服务器的响应

            // Convert the response to a string and display it
            String responseMessage = new String(responsePacket.getData(), 0, responsePacket.getLength());
            System.out.println("Response from server:\n" + responseMessage);

            // Close the socket
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}