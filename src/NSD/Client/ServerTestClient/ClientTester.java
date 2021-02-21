package NSD.Client.ServerTestClient;

import NSD.Utils.Json_Encode_Decode;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientTester {

    private final static boolean Debug = true;

    private static int amountOfMessagesSending;
    private static int timeBetweenMessage;

    private static String ip;
    private static int socket;

    private static ExecutorService clients;

    public static void main(String[] args) {
        try {
            amountOfMessagesSending = Integer.parseInt(args[1]);
            timeBetweenMessage = Integer.parseInt(args[2]);

            ip = args[3];
            socket = Integer.parseInt(args[4]);

            switch (args[0]) {

                case "H":
                    clients = Executors.newFixedThreadPool(10);
                    highThroughputRun();
                    break;
                case "L":
                    clients = Executors.newFixedThreadPool(3);
                    lowThroughputRun();
                    break;
                case "R":
                default:
                    clients = Executors.newFixedThreadPool(1);
                    routineRun();
                    break;

            }
        } catch (RuntimeException err) {
            System.err.println("Please only input  TypeOFTest (char), amountOfMessagesSending (Integer), timeBetweenMessage (Integer), ip (String) and socket (Integer)");
        }
    }

    //This will just run one server session
    private static void routineRun() {

        System.out.println("Regular Session");
            Client sender = new Client(amountOfMessagesSending, timeBetweenMessage, 1, ip, socket);
            clients.execute(sender);

    }

    //This will run 3 sessions from 3 different clients in parallel. Situation is 2 or 3 clients publish and read 10 messages. The clients are rate-limited to at most 1 request per second
    private static void lowThroughputRun() {

        System.out.println("Low Throughput Session");

        for(int i = 1; i <= 3; i++){
            Client sender = new Client(amountOfMessagesSending, timeBetweenMessage, i, ip, socket);
            clients.execute(sender);
        }
    }

    //This will run 10 sessions from 10 different clients in parallel. Situation is 10 clients publish and read at least 1000 messages at an unlimited rate (i.e. they issues request as fast as they can).
    private static void highThroughputRun() {

        System.out.println("High Throughput Session");

        for(int i = 1; i <= 10; i++){
            Client sender = new Client(amountOfMessagesSending, timeBetweenMessage, i, ip, socket);
            clients.execute(sender);
        }

    }
}

class Client implements Runnable {

    private static String ip;
    private static int socket;
    private final Json_Encode_Decode json = new Json_Encode_Decode();
    private final int numberOfMessage;
    private final int delay;
    private final int identifier;

    public Client(final int number, final int period, final int id, final String ip, final int socket) {
        numberOfMessage = number;
        delay = period;
        identifier = id;

        Client.ip = ip;
        Client.socket = socket;
    }

    @Override
    public void run() {
        try {

            Socket server = new Socket(ip, socket);
            PrintWriter sender = new PrintWriter(server.getOutputStream(), true);
            BufferedReader receive = new BufferedReader(new InputStreamReader(server.getInputStream()));

            final String name = "NSD Client " + identifier;

            Server_Handler server_handler = new Server_Handler(receive);
            server_handler.start();

            sender.println(json.encodeJsonOpen(name));

            for (int i = 0; i < numberOfMessage; i++) {
                // wait if applicable
                if (delay > 0) {
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {

                    }
                }

                sender.println(json.encodeJsonPublish(name, name, "Hello Message " + i));

            }

            sender.println(json.encodeJsonGet(name, 0));
            sender.println(json.encodeJsonUnsubscribe(name, name));

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + ip);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + ip);
            System.exit(1);
        } catch (Exception e) {
            System.err.println("improper behaviour from " + ip);
            System.exit(1);
        }
    }

}

class Server_Handler extends Thread {

    boolean runService = true;
    BufferedReader receive;
    private final Json_Encode_Decode json = new Json_Encode_Decode();

    public Server_Handler(BufferedReader receive) {
        this.receive = receive;
    }

    public void run() {

        while (runService){

            final String input;
            try {
                input = receive.readLine();

                JSONObject request = json.decodeJson(input);

                //System.out.println("Json Received: " + input);

                switch (request.getString("_class")) {

                    case "SuccessResponse":
                        System.out.println("[Server] Success Request");
                        break;

                    case "MessageListResponse":
                        System.out.println("Received message list " + request);
                        break;

                    case "ErrorResponse":
                        System.out.println(request.getString("error"));
                        break;

                    default:
                        System.out.println("Unknown request from server!");
                        break;

                }
            } catch (IOException e) {
                return;
            }
        }
    }

    public void StopService(){
        runService = false;
    }
}