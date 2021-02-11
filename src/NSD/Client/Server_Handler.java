package NSD.Client;

import NSD.Tools.Json_Encode_Decode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;

public class Server_Handler implements Runnable {

    private static Socket server;
    private static BufferedInputStream receive;
    private static Json_Encode_Decode json;

    public Server_Handler(Socket serverConnection) {
        server = serverConnection;
    }

    @Override
    public void run() {

        try {

            json = new Json_Encode_Decode(); //TODO: Might need moving
            receive = new BufferedInputStream(server.getInputStream());

            while (true) {

                byte[] receiveBytes = new byte[1024 * 2];

                receive.read(receiveBytes);
                JSONObject request = Json_Encode_Decode.decodeJson(receiveBytes);
                commands(request);

            }

        } catch (IOException err) {

        } finally {
            closeConnection();
        }

    }

    private static void commands(final JSONObject request) throws IOException {

        switch (request.getString("_class")) {

            case "SuccessResponse":
                System.out.println("[Server] Success Request");
                break;

            case "MessageListResponse":

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

}
