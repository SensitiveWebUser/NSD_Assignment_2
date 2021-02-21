package NSD.Client;

import NSD.Utils.Json_Encode_Decode;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Server_Handler implements Runnable {

    private static final Json_Encode_Decode json = new Json_Encode_Decode();
    private static Socket server;
    private static BufferedReader receive;

    public Server_Handler(Socket serverConnection) {
        server = serverConnection;
    }

    private static void commands(final JSONObject request) throws IOException {

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

            case "Message":  //TODO: Need to remove this request!
                System.out.println(request.getString("from") + " sent: " + request.getString("body"));
                break;

            default:
                System.out.println("Unknown request from server!");
                break;

        }
    }

    private static void closeConnection() {
        try {
            receive.close();
            server.close();
        } catch (IOException err) {
            System.out.println("Critical fail!");
        }
    }

    @Override
    public void run() {

        try {

            receive = new BufferedReader(new InputStreamReader(server.getInputStream()));

            while (true) {
                commands(json.decodeJson(receive.readLine()));
            }

        } catch (IOException err) {

        } finally {
            closeConnection();
        }

    }

}
