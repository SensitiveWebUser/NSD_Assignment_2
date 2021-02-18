package NSD.Client.ServerTestClient;

import NSD.Utils.Json_Encode_Decode;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientTester {

    private final static boolean Debug = true;

    private static int amountOfMessagesSending;
    private static int timeBetweenMessage;

    private static String ip;
    private static int socket;

    public static void main(String[] args) {
        try {
            amountOfMessagesSending = Integer.parseInt(args[1]);
            timeBetweenMessage = Integer.parseInt(args[2]);

            ip = args[3];
            socket = Integer.parseInt(args[4]);

            debug("arg: " + args[0]);
            switch (args[0]) {

                case "H":
                    highThroughputRun();
                    break;
                case "L":
                    lowThroughputRun();
                    break;
                case "R":
                default:
                    routineRun();
                    break;

            }
        } catch (RuntimeException err) {
            debug("Please only input  TypeOFTest (char), amountOfMessagesSending (Integer), timeBetweenMessage (Integer), ip (String) and socket (Integer)");
        }
    }

    //This will just run one server session
    private static void routineRun() {

        debug("Regular Session");
        Client sender1 = new Client(amountOfMessagesSending, timeBetweenMessage, 1, ip, socket);
        sender1.start();

    }

    //This will run 3 sessions from 3 different clients in parallel. Situation is 2 or 3 clients publish and read 10 messages. The clients are rate-limited to at most 1 request per second
    private static void lowThroughputRun() {

        debug("Low Throughput Session");

        //create 3 threads
        Client sender1 = new Client(amountOfMessagesSending, timeBetweenMessage, 1, ip, socket);
        Client sender2 = new Client(amountOfMessagesSending, timeBetweenMessage, 2, ip, socket);
        Client sender3 = new Client(amountOfMessagesSending, timeBetweenMessage, 3, ip, socket);

        //run 3 threads
        sender1.start();
        sender2.start();
        sender3.start();

    }

    //This will run 10 sessions from 10 different clients in parallel. Situation is 10 clients publish and read at least 1000 messages at an unlimited rate (i.e. they issues request as fast as they can).
    private static void highThroughputRun() {

        debug("High Throughput Session");

        //create 10 threads
        Client sender1 = new Client(amountOfMessagesSending, timeBetweenMessage, 1, ip, socket);
        Client sender2 = new Client(amountOfMessagesSending, timeBetweenMessage, 2, ip, socket);
        Client sender3 = new Client(amountOfMessagesSending, timeBetweenMessage, 3, ip, socket);
        Client sender4 = new Client(amountOfMessagesSending, timeBetweenMessage, 4, ip, socket);
        Client sender5 = new Client(amountOfMessagesSending, timeBetweenMessage, 5, ip, socket);
        Client sender6 = new Client(amountOfMessagesSending, timeBetweenMessage, 6, ip, socket);
        Client sender7 = new Client(amountOfMessagesSending, timeBetweenMessage, 7, ip, socket);
        Client sender8 = new Client(amountOfMessagesSending, timeBetweenMessage, 8, ip, socket);
        Client sender9 = new Client(amountOfMessagesSending, timeBetweenMessage, 9, ip, socket);
        Client sender10 = new Client(amountOfMessagesSending, timeBetweenMessage, 10, ip, socket);

        //run 10 threads
        sender1.start();
        sender2.start();
        sender3.start();
        sender4.start();
        sender5.start();
        sender6.start();
        sender7.start();
        sender8.start();
        sender9.start();
        sender10.start();

    }

    private static void debug(final String message) {
        if (Debug) System.out.println(message);
    }
}

class Client extends Thread {

    private final static boolean Debug = true;

    private int numberOfMessage;
    private int delay;
    private int identifier;

    private static String ip;
    private static int socket;

    public Client(final int number, final int period, final int id, final String ip, final int socket) {
        numberOfMessage = number;
        delay = period;
        identifier = id;

        Client.ip = ip;
        Client.socket = socket;
    }

    private void debug(final String message) {
        //if (Debug) System.out.println(message);
    }

    public void run() {
        try {
            Json_Encode_Decode json = new Json_Encode_Decode();

            Socket server = new Socket(ip, socket);
            PrintWriter sender = new PrintWriter(server.getOutputStream(), true);
            BufferedReader receive = new BufferedReader(new InputStreamReader(server.getInputStream()));

            final String name = "NSD Client " + identifier;

            sender.println(json.encodeJsonOpen(name));
            serverResponse(receive);

            for (int i = 0; i < numberOfMessage; i++) {
                // wait if applicable
                if (delay > 0) {
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        debug("Not expecting any exception here");
                    }
                }

                debug("Sending " + Json_Encode_Decode.encodeJsonPublish(name, name, "Hello Message " + i));
                sender.println(Json_Encode_Decode.encodeJsonPublish(name, name, "Hello Message " + i));
                serverResponse(receive);

            }

            debug("Sending " + Json_Encode_Decode.encodeJsonGet(name, 0));
            sender.println(Json_Encode_Decode.encodeJsonGet(name, 0));
            serverResponse(receive);

            debug("Sending " + Json_Encode_Decode.encodeJsonUnsubscribe(name, name));
            sender.println(Json_Encode_Decode.encodeJsonUnsubscribe(name, name));
            serverResponse(receive);

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

    private void serverResponse(BufferedReader receive) throws IOException {

        String input = receive.readLine();
        JSONObject request = Json_Encode_Decode.decodeJson(input);

        System.out.println("Received Json: " + request);

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
    }

}