package NSD.Server;

import NSD.Tools.Json_Encode_Decode;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.time.LocalTime;

public class Client_Handler implements Runnable {

    private static Socket client;

    public Client_Handler(Socket client) {

        this.client = client;
    }

    @Override
    public void run() {

        BufferedReader receive = null;
        OutputStream sender = null;

        try {

            Json_Encode_Decode json = new Json_Encode_Decode();

            receive = new BufferedReader( new InputStreamReader(client.getInputStream()));
            sender = client.getOutputStream();

            sender.write(json.Encode_Message( "Server", "Welcome client. Time of connection: " + LocalTime.now()));

            while (true) {

                String command = "";
                byte[] receiveBytes = receive.readLine().getBytes();
                JSONObject request = json.Decode_Message(receiveBytes);

                 if(request.getInt("Type") == 1){
                     command = request.getString("message");
                 }else if (request.getInt("Type") == 2){

                 }else {
                     //TODO: what happens if type not found
                 }

                if (command.equals("Ping")) {
                    sender.write(json.Encode_Message("Server", "Pong"));
                } else {
                    System.out.println("[CLIENT MESSAGE] Message: " + command);
                    sender.write(json.Encode_Message("Server", "Message Received"));
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
