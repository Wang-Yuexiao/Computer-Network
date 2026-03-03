import java.net.*;
import java.io.*;

public class P2PNode{
    public static void main(String[] args) throws Exception {

        new Thread(new P2PServer()).start();

        new Thread(new P2PClient()).start();
    }
}

class P2PServer implements Runnable {
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(8888);
            System.out.println("P2P node is waiting for connections...");
            while (true) {
                Socket connectionSocket = serverSocket.accept();
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                String clientMessage = inFromClient.readLine();
                System.out.println("Received: " + clientMessage);
                connectionSocket.close();
            }
        } catch (Exception e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }
}

class P2PClient implements Runnable {
    public void run() {
        try {
            Thread.sleep(1000);
            Socket clientSocket = new Socket("localhost", 8888);
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            outToServer.writeBytes("Hello from P2P client\n");
            clientSocket.close();
        } catch (Exception e) {
            System.out.println("Client error: " + e.getMessage());
        }
    }
