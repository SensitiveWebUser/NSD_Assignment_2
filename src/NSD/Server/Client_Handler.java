package NSD.Server;

import NSD.Tools.Json_Encode_Decode;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalTime;

public class Client_Handler implements Runnable {

    private static Socket client;
    private static Json_Encode_Decode json;

    public Client_Handler(Socket client) {

        Client_Handler.client = client;
    }

    @Override
    public void run() {

        BufferedInputStream receive = null;
        BufferedOutputStream sender = null;

        try {

            json = new Json_Encode_Decode();

            receive = new BufferedInputStream(client.getInputStream());
            sender = new BufferedOutputStream(client.getOutputStream());

            byte[] se = json.encodeJsonMessages("Server", "Welcome client. Time of connection: " + LocalTime.now());
            sender.write(se);
            sender.flush();

            while (true) {

                byte[] byteReceived = new byte[1024];
                receive.read(byteReceived);

                String command = "";

                JSONObject request = json.decodeJson(byteReceived);

                if (request.getString("_class").equals("Message")) {
                    command = request.getString("body");
                } else if (request.getString("_class").equals("")) {

                } else {
                    //TODO: what happens if type not found
                }

                if (command.equals("Ping")) {
                    sender.write(json.encodeJsonMessages("Server", "Hi"));
                    sender.flush();
                } else {

                    System.out.println("[CLIENT MESSAGE] Message: " + command);
                    //sender.write(Json_Encode_Decode.encodeJsonMessages("Server", "Message Received"));
                    sender.flush();
                }

            }
        } catch (IOException err) {

            System.out.println("Server Error | Point 1 | Error time: " + LocalTime.now() + " Error message: " + err.getMessage());

        } finally {
            // sender.close(); TODO: check

            try {
                receive.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
