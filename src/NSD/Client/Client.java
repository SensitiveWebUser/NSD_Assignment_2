package NSD.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalTime;

public class Client {

    private static PrintWriter sender;

    Client(String ip, int socket) {

        try {
            Run(ip, socket);
        } catch (IOException err) {
            System.out.println("Client Error | Error time: " + LocalTime.now() + " Error message: " + err.getMessage());
        } finally {
            sender.close();
        }

    }

    void Run(String ip, int socket) throws IOException {

        Socket server = new Socket(ip, socket);
        System.out.println("Client Starting, and trying to connect to port: " + socket);

        Server_Handler server_handler = new Server_Handler(server);
        new Thread(server_handler).start();

        sender = new PrintWriter(server.getOutputStream(), true);
        sender.println("Hello server. Time of connection: " + LocalTime.now());

        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            String input = keyboard.readLine();
            if (input.equals("Exit")) {
                break;
            } else {
                sender.println(input);
            }
        }

    }
}
