import java.io.*;
import java.net.*;
import java.util.*;

public final class UDPWebServer {
    public static void main(String[] argv) throws Exception {
        // Mission 1. Fill in #1 Create DatagramSocket
        DatagramSocket socket = new DatagramSocket(6777); // 使用端口6789，可以根据需要更改

        // Process HTTP service requests in an infinite loop
        while (true) {
            // Mission 1, Fill in #2 Init receiveData
            byte[] receiveData = new byte[1024]; // 初始化接收数据的字节数组
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length); // 创建接收数据报的包

            // Fill in #3 Listen for a UDP packet
            socket.receive(receivePacket); // 接收来自客户端的UDP数据包

            // Fill in #3 Construct an object to process the HTTP request message
            UDPHttpRequest request = new UDPHttpRequest(socket, receivePacket); // 创建HTTP请求处理对象

            // Create a new thread to process the request
            Thread thread = new Thread(request);

            // Start the thread
            thread.start();
        }
    }
}