package ciaf.clienteservidor.ejemplobasico;

import java.io.*;
import java.net.*;

public class Cliente {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 12345);

            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter salida = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            salida.println("Hola desde el cliente");

            String respuestaServidor = entrada.readLine();
            System.out.println("Respuesta del servidor: " + respuestaServidor);

            entrada.close();
            salida.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

