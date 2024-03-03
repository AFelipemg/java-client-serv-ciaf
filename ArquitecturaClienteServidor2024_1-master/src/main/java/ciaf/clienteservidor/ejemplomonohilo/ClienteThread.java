package ciaf.clienteservidor.ejemplomonohilo;
import java.io.*;
import java.net.*;

public class ClienteThread extends Thread {
    private Socket socket;

    public ClienteThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

            String mensajeDelServidor;
            while ((mensajeDelServidor = in.readLine()) != null) {
                System.out.println("Servidor: " + mensajeDelServidor);

                // Simulamos la compra de una camisa cuando el servidor solicita una acción al cliente
                if (mensajeDelServidor.equals("¿Desea comprar una camisa? (S/N):")) {
                    System.out.print("Cliente: ");
                    String respuesta = consoleInput.readLine().toUpperCase();
                    out.println(respuesta);
                }
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}