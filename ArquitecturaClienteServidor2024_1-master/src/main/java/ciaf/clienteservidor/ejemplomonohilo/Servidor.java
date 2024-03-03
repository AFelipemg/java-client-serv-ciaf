package ciaf.clienteservidor.ejemplomonohilo;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Servidor {
    public static void main(String[] args) {
        // Crea un pool de 10 hilos
        ExecutorService executor = Executors.newFixedThreadPool(10);

        try {
            ServerSocket serverSocket = new ServerSocket(5000); // Puerto del servidor

            while (true) {
                Socket socket = serverSocket.accept(); // Espera por la conexión del cliente
                System.out.println("Cliente conectado.");

                //monohilo
                //ClienteHandler clienteHandler = new ClienteHandler(socket);
                //Thread clienteThread = new Thread(clienteHandler); // Inicia un nuevo hilo para el cliente
                //clienteThread.start();


                // Usa el pool de hilos para manejar la conexión del cliente
                executor.submit(new ClienteHandler(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }
}