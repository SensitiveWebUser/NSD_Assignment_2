package NSD.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Server_Handler implements Runnable {

    private static Socket server;

    public Server_Handler(Socket serverConnection) {

        server = serverConnection;

    }

    @Override
    public void run() {

        BufferedReader receive = null;

        try {

            receive = new BufferedReader(new InputStreamReader(server.getInputStream()));

            while (true) {

                String request = receive.readLine();
                System.out.println("[Server Message] Message: " + request);

            }

        } catch (IOException err) {

        } finally {

            try {
                receive.close();
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
