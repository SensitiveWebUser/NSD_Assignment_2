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

                byte[] receiveBytes = new byte[1024];

                receive.read(receiveBytes);
                JSONObject request = Json_Encode_Decode.decodeJson(receiveBytes);

                //String command = "";

                if (request.getString("_class").equals("Message")) {
                    System.out.println("Author: " + request.getString("from") + " Message: " + request.getString("body"));
                } else if (request.getString("_class").equals("GetRequest")) {

                    JSONArray messages = request.getJSONArray("messages");

                    if(messages.length() != 0){
                        for(int x = 0; x < messages.length(); x++){
                            JSONObject message =  messages.getJSONObject(x);
                            System.out.println("Author: " + message.getString("from") + " Message: " + message.getString("body"));
                        }
                    }

                } else {
                    //TODO: what happens if type not found
                }

            }

        } catch (IOException err) {

        } finally {

            try {
                receive.close();
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
