package ciaf.clienteservidor.ejemplomonohilo;
import java.io.*;
        import java.net.*;

public class ClienteHandler implements Runnable {
    private Socket socket;

    public ClienteHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Simulamos el proceso de compra de camisas
            out.println("¡Bienvenido a la tienda de camisas!");
            out.println("¿Desea comprar una camisa? (S/N):");
            String respuesta = in.readLine();

            if (respuesta != null && respuesta.equalsIgnoreCase("S")) {
                out.println("Compra realizada con éxito. ¡Gracias por su compra!");
            } else {
                out.println("Gracias por visitarnos. ¡Hasta luego!");
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
