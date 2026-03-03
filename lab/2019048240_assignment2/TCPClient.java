import java.io.*;
import java.net.*;

class TCPClient {
    public static void main(String argv[]) throws Exception {
        String host = "localhost"; // Server address
        int port = 888; // Server port number

		// Mission 1: Open TCP Socket to connect with Server and send a message
		// Fill#1, Initializing client-side socket to connect to Server
        Socket clientSocket = new Socket(host, 8886);/*Fill in the Blank*/; // Mission 1

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        // Fill#2, Read an integer from the user to make a message to send
        System.out.print("Enter an integer between 1 and 100: ");
        int userNumber = Integer.parseInt(inFromUser.readLine());/*Fill in the Blank*/; // Mission 1

        // Fill#3, Send the client name and the entered integer to the server
        String clientMessage = "Client of John Q. Smith: " + userNumber;
        outToServer.writeBytes(clientMessage + "\n");
        /*Fill in the Blank*/;  // Mission 1

        // Mission 4: Extract the server's chosen integer from the response and Calculate the sum of the client’s and server’s numbers
        // Fill#8, Read and print the response from the server
        String serverMessage = inFromServer.readLine();/*Fill in the Blank*/;  //Mission 4
        System.out.println("FROM SERVER: " + serverMessage/*Fill in the Blank*/); //Mission 4

        // Extract the server's chosen integer from the response
        String[] parts = serverMessage.split(": ");
        String serverName = parts[0];
        int serverNumber = Integer.parseInt(parts[1]);

        // Fill#9, Calculate and print the sum of the client's and server's numbers
        int sum = userNumber + serverNumber;/*Fill in the Blank*/; //Mission 4
        System.out.println("Client number: " + userNumber/*Fill in the Blank*/); //Mission 4
        System.out.println("Server number: " + serverNumber/*Fill in the Blank*/); //Mission 4
        System.out.println("Sum: " + sum/*Fill in the Blank*/); //Mission 4
        // Fill#10, Close socket
        clientSocket.close();
        /*Fill in the Blank*/;
    }
}
