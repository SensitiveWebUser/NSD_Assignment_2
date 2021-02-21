package NSD.Server;

import NSD.Utils.Database;
import NSD.Utils.Json_Encode_Decode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

public class Client_Handler implements Runnable {

    private Database db;
    private static Socket client;
    private static ArrayList<String> channels;
    private BufferedReader receive;
    private PrintWriter sender;
    private final HashMap<String, Integer> activeChannels = new HashMap<>();
    private final Json_Encode_Decode json = new Json_Encode_Decode();

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

            String identity = request.getString("identity");

            switch (request.getString("_class")) {

                case "PublishRequest":
                    if ((activeChannels.containsKey(identity))) {

                        JSONObject message = request.getJSONObject("message");
                        message.put("when", db.getChannelMessageNumber(identity) + 1);

                        if (db.addMessage(message.toString().getBytes(StandardCharsets.UTF_8), identity)) {
                            sender.println(json.encodeSuccess());
                        } else {
                            if (!(activeChannels.containsKey(identity)))
                                sender.println(json.encodeError(3, "Can't send message. Not in assigned channel"));
                            else if ((activeChannels.containsKey(identity)))
                                sender.println(json.encodeError(3, "Unable to update db with message"));
                        }

                    } else if (!(activeChannels.containsKey(identity))) {
                        sender.println(json.encodeError(3, "Can't send message. Not in assigned channel"));
                    }
                    break;

                case "OpenRequest":
                    if (!(channels.contains(identity))) {
                        db.addChannel(identity);
                        channels.add(identity);
                        activeChannels.put(identity, 0);
                        sender.println(json.encodeSuccess());
                    } else if (channels.contains(identity)) {
                        activeChannels.put(identity, db.getChannelMessageNumber(identity));
                        sender.println(json.encodeSuccess());
                    } else {
                        sender.println(json.encodeError(3, "Unknown error"));
                    }
                    break;

                case "GetRequest":
                    sender.println(json.encodeMessageList(identity, request.getInt("after"), db));
                    break;

                case "SubscribeRequest":
                    if (!(activeChannels.containsKey(identity)) && channels.contains(identity)) {
                        activeChannels.put(identity, db.getChannelMessageNumber(identity));
                        sender.println(json.encodeSuccess());
                    } else if (activeChannels.containsKey(identity)) {
                        sender.println(json.encodeError(3, "Already in channel"));
                    } else if (!(channels.contains(identity))) {
                        sender.println(json.encodeError(3, "Channel not found"));
                    } else {
                        sender.println(json.encodeError(3, "Unknown error"));
                    }
                    break;

                case "UnsubscribeRequest":
                    if ((activeChannels.containsKey(identity))) {
                        activeChannels.remove(identity);
                        sender.println(json.encodeSuccess());
                    } else if (!(activeChannels.containsKey(identity))) {
                        sender.println(json.encodeError(1, ""));
                    } else {
                        sender.println(json.encodeError(3, "Unknown error"));
                    }
                    break;

                default:
                    sender.println(json.encodeError(3, "Unknown request"));
                    System.err.println("Unknown request from client!");
                    break;

            }

        } catch (JSONException err) {
            sender.println(json.encodeError(3, "Bad Request!"));
        }
    }

    private void closeConnection() {
        try {
            receive.close();
            sender.close();
            client.close();
        } catch (IOException err) {
            System.err.println("Critical fail!");
        }
    }

    private void setup(Socket client, final ArrayList<String> channels, final Database db) throws IOException {

        this.db = db;

        Client_Handler.client = client;
        Client_Handler.channels = channels;

        receive = new BufferedReader(new InputStreamReader(client.getInputStream()));
        sender = new PrintWriter(client.getOutputStream(), true);

    }

    @Override
    public void run() {
        try {
            while (true) {
                final String input = receive.readLine();
                commands(json.decodeJson(input));
            }
        } catch (IOException e) {
            closeConnection();
        }
    }

}
