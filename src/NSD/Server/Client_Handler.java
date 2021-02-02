package NSD.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalTime;

public class Client_Handler implements Runnable {

    private static Socket client;

    public Client_Handler(Socket client) {

        this.client = client;
    }

    @Override
    public void run() {

        BufferedReader receive = null;
        PrintWriter sender = null;

        try {

            receive = new BufferedReader(new InputStreamReader(client.getInputStream()));
            sender = new PrintWriter(client.getOutputStream(), true);

            sender.println("Welcome client. Time of connection: " + LocalTime.now());

            while (true) {

                String command = receive.readLine();

                if (command.equals("Ping")) {
                    sender.println("Pong");
                } else {
                    System.out.println("[CLIENT MESSAGE] Message: " + command);
                    sender.println("Message Received");
                }

            }
        } catch (IOException err) {

            System.out.println("Server Error | Point 1 | Error time: " + LocalTime.now() + " Error message: " + err.getMessage());

        } finally {
            sender.close();

            try {
                receive.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
