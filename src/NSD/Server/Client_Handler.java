package NSD.Server;

import NSD.Utils.Database;
import NSD.Utils.Json_Encode_Decode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

public class Client_Handler implements Runnable {

    private final Json_Encode_Decode json = new Json_Encode_Decode();
    private static Database db;
    private static Socket client;

    private BufferedReader receive;
    private PrintWriter sender;

    private static ArrayList<String> channels;
    private HashMap<String, Integer> activeChannels = new HashMap<>();

    public Client_Handler(final Socket client, final ArrayList<String> channels, final Database db) {
        try {
            setup(client, channels, db);
        } catch (IOException err) {
            System.out.println("Server Error | Point 1 | Error time: " + LocalTime.now() + " Error message: " + err.getMessage());
            closeConnection();
        }
    }

    private void commands(final JSONObject request) throws IOException {

        try {
            switch (request.getString("_class")) {

                case "PublishRequest":
                    if ((activeChannels.containsKey(request.getString("identity")))) {
                        JSONObject message = request.getJSONObject("message");
                        message.put("when", db.getChannelMessageNumber(request.getString("identity")) + 1);
                        if (Database.addMessage(message.toString().getBytes(StandardCharsets.UTF_8), request.getString("identity"))) {
                            sender.println(Json_Encode_Decode.encodeSuccess());
                        } else {
                            if(!(activeChannels.containsKey(request.getString("identity")))) sender.println(Json_Encode_Decode.encodeError(3, "Can't send message. Not in assigned channel"));
                            else if((activeChannels.containsKey(request.getString("identity")))) sender.println(Json_Encode_Decode.encodeError(3, "Unable to update db with message"));
                        }
                    }else if(!(activeChannels.containsKey(request.getString("identity")))) {
                        sender.println(Json_Encode_Decode.encodeError(3, "Can't send message. Not in assigned channel"));
                    }
                    break;

                case "OpenRequest":
                    if (!(channels.contains(request.getString("identity")))) {
                        Database.addChannel(request.getString("identity"));
                        channels.add(request.getString("identity"));
                        activeChannels.put(request.getString("identity"), 0);
                        sender.println(Json_Encode_Decode.encodeSuccess());
                    } else if (channels.contains(request.getString("identity"))) {
                        activeChannels.put(request.getString("identity"), db.getChannelMessageNumber(request.getString("identity")));
                        sender.println(Json_Encode_Decode.encodeSuccess());
                    }else {
                        sender.println(Json_Encode_Decode.encodeError(3, "Unknown error"));
                    }
                    break;

                case "GetRequest":
                    sender.println(Json_Encode_Decode.encodeMessageList(request.getString("identity"), request.getInt("after"), db));
                    break;

                case "SubscribeRequest":
                    if (!(activeChannels.containsKey(request.getString("identity"))) && channels.contains(request.getString("identity"))) {
                        activeChannels.put(request.getString("identity"), db.getChannelMessageNumber(request.getString("identity")));
                        sender.println(Json_Encode_Decode.encodeSuccess());
                    }else if (activeChannels.containsKey(request.getString("identity"))) {
                        sender.println(Json_Encode_Decode.encodeError(3, "Already in channel"));
                    }else if (!(channels.contains(request.getString("identity")))) {
                        sender.println(Json_Encode_Decode.encodeError(3, "Channel not found"));
                    } else {
                        sender.println(Json_Encode_Decode.encodeError(3, "Unknown error"));
                    }
                    break;

                case "UnsubscribeRequest":
                    if ((activeChannels.containsKey(request.getString("identity")))) {
                        activeChannels.remove(request.getString("identity"));
                        sender.println(Json_Encode_Decode.encodeSuccess());
                    } else if (!(activeChannels.containsKey(request.getString("identity")))) {
                        sender.println(Json_Encode_Decode.encodeError(1, ""));
                    }else {
                        sender.println(Json_Encode_Decode.encodeError(3, "Unknown error"));
                    }
                    break;

                default:
                    sender.println(json.encodeError(3, "Unknown request"));
                    System.out.println("Unknown request from client!");
                    break;

            }

            System.out.println("Received Json: " + request);

        } catch (JSONException err) {
            sender.println(Json_Encode_Decode.encodeError(3, "Bad Request!"));
            sender.flush();
        }
    }

    private void closeConnection() {
        try {
            receive.close();
            sender.close();
            client.close();
        } catch (IOException err) {
            System.out.println("Critical fail!");
        }
    }

    private void setup(Socket client, final ArrayList<String> channels, final Database db) throws IOException {

        Client_Handler.db = db;

        Client_Handler.client = client;
        Client_Handler.channels = channels;

        receive = new BufferedReader(new InputStreamReader(client.getInputStream()));
        sender = new PrintWriter(client.getOutputStream(), true);

    }

    @Override
    public void run() {
        try {
            while (true) {
                String input = receive.readLine();
                commands(Json_Encode_Decode.decodeJson(input));
            }
        } catch (IOException e) {
            closeConnection();
        }
    }

}
