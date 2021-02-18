package NSD.Server;

import NSD.Utils.Database;

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

    public static void main(String[] args) {
        startup(12345, 10);
    }

    App(final int socket, final int clientLimit) {
        App.clientLimit = clientLimit;
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

        ArrayList<String> channels = Database.channels();
        ArrayList<Client_Handler> clients = new ArrayList<>();

        while (true) {

            Socket client = app.accept();

            Client_Handler clientThread = new Client_Handler(client, channels, db);
            clients.add(clientThread);
            client_Pool.execute(clientThread);
        }
    }

}
