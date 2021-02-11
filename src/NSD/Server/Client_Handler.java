package NSD.Server;

import NSD.Tools.Database;
import NSD.Tools.Json_Encode_Decode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.ArrayList;

public class Client_Handler implements Runnable {

    private static Database db;
    private static Socket client;
    private final static Json_Encode_Decode json = new Json_Encode_Decode();

    private static BufferedInputStream receive;
    private static BufferedOutputStream sender;

    private static ArrayList<Client_Handler> clients;
    private static ArrayList<String> channels;

    private static int activeClients = 0;

    public Client_Handler(final Socket client, final ArrayList<Client_Handler> clients, final ArrayList<String> channels, final int activeClients, final Database db) {
        try {
            setup(client, clients, channels, activeClients,db);
        } catch (IOException err) {
            System.out.println("Server Error | Point 1 | Error time: " + LocalTime.now() + " Error message: " + err.getMessage());
            closeConnection();
        }
    }

    private void setup(final Socket client, final ArrayList<Client_Handler> clients, final ArrayList<String> channels, final int activeClients, final Database db) throws IOException {

        this.db = db;
        this.activeClients = activeClients;

        Client_Handler.clients = clients;
        Client_Handler.channels = channels;

        Client_Handler.client = client;

        receive = new BufferedInputStream(client.getInputStream());
        sender = new BufferedOutputStream(client.getOutputStream());

        byte[] se = json.encodeJsonMessage("Server", "Welcome client. Time of connection: " + LocalTime.now());
        sender.write(se);
        sender.flush();

    }

    @Override
    public void run() {

        try {
            while (true) {

                byte[] byteReceived = new byte[1024 * 2];
                receive.read(byteReceived);

                commands(Json_Encode_Decode.decodeJson(byteReceived));

            }
        } catch (IOException e) {
            closeConnection();
        }
    }

    private static void commands(final JSONObject request) throws IOException {

        try{

                switch (request.getString("_class")) {

                case "PublishRequest":
                    if((channels.contains(request.getString("identity")))){
                        //TODO: Add message to channel/ push message to all users in channel
                        if(db.addMessage(request.getString("message").getBytes(StandardCharsets.UTF_8),request.getString("identity"))){
                            sender.write(json.encodeSuccess());
                            sender.write(json.encodeJsonMessage(request.getString("identity"), json.decodeJson(request.getString("message").getBytes(StandardCharsets.UTF_8)).getString("body")));
                        }else {
                            sender.write(json.encodeError(3,"Unable to update db with message"));
                        }
                        sender.flush();
                    }
                    break;

                case "OpenRequest":
                    if(!(channels.contains(request.getString("identity")))){
                        channels.add(request.getString("identity"));
                        sender.write(json.encodeSuccess());
                        sender.flush();
                    }else {
                        String message = "Unknown error";

                        if(channels.contains(request.getString("identity"))) message = "Channel open : " + request.getString("identity");

                        sender.write(json.encodeError(3, message));
                        sender.flush();
                    }
                    break;

                case "GetRequest":
                    sender.write(json.encodeMessageList(request.getString("identity"), 50, db));
                    sender.flush();
                    break;

                case "SubscribeRequest":
                    //TODO: Add Sub to channel;
                    break;

                case "UnsubscribeRequest":
                    //TODO: Add Unsub to channel;
                    break;

                case "Message": //TODO: Need to remove this request!
                    System.out.println("[CLIENT MESSAGE] Message: " + request.getString("body"));
                    break;

                default:
                    System.out.println("Unknown request from client!");
                    break;

            }

        }catch (JSONException err){
            sender.write(json.encodeError(3, "Bad Request!"));
            sender.flush();
        }
    }

    private static void closeConnection() {
        try {
            activeClients --;
            receive.close();
            sender.close();
            client.close();
        } catch (IOException err) {
            System.out.println("Critical fail!");
        }finally {
        }
    }

}
