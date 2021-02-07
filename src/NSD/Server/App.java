package NSD.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App {

    App(final int socket, final int clientLimit) {
        try {

            Run(socket, clientLimit);

        } catch (IOException err) {

            System.out.println("Server Error | Point 2 | Error time: " + LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss")) + " Error message: " + err.getMessage());

        }
    }

    private static void Run(final int socket, final int clientLimit) throws IOException {

         final ArrayList<Client_Handler> clients = new ArrayList<>();
         ExecutorService client_Pool = Executors.newFixedThreadPool(clientLimit);

        ServerSocket app = new ServerSocket(socket);
        System.out.println("[Server] Server Started, now listening on port: " + app.getLocalPort());

        while (true) {

            Socket client = app.accept();

            Client_Handler clientThread = new Client_Handler(client);
            clients.add(clientThread);

            client_Pool.execute(clientThread);

        }

    }

}
