package ciaf.clienteservidor.chat_borr;

import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connected to server. Start typing messages:");

            Thread inputThread = new Thread(() -> {
                String userInputMessage;
                try {
                    while ((userInputMessage = userInput.readLine()) != null) {
                        out.println(userInputMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            inputThread.start();

            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                System.out.println("Received from server: " + serverMessage);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


