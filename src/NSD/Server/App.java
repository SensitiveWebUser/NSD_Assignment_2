package NSD.Server;

import NSD.Tools.Database;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App {

    static Database db;
    static ServerSocket app;
    static ExecutorService client_Pool;

    static int clientLimit = 0;

    App(final int socket, final int clientLimit) {
        this.clientLimit = clientLimit;
        startup(socket, clientLimit);
    }

    private static void startup(final int socket, final int clientLimit) {
        try {

            db = new Database();
            app = new ServerSocket(socket);
            System.out.println("[Server] Server Started, now listening on port: " + app.getLocalPort());

            client_Pool = Executors.newFixedThreadPool(clientLimit);
            Run();

        } catch (IOException err) {
            System.out.println("Server Error | Point 2 | Error time: " + LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss")) + " Error message: " + err.getMessage());
            System.exit(0);
        }
    }

    private static void Run() throws IOException {

        ArrayList<String> channels = db.channels();
        ArrayList<Client_Handler> clients = new ArrayList<>();

        int activeClients = 0;

        while (true) {

            //while (activeClients < clientLimit){
                Socket client = app.accept();
                activeClients++;

                Client_Handler clientThread = new Client_Handler(client, clients, channels, activeClients, db);
                clients.add(clientThread);

                client_Pool.execute(clientThread);
            //}

        }
    }

}
