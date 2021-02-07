package NSD.Client;

import NSD.Tools.Json_Encode_Decode;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;

public class Server_Handler implements Runnable {

    private static Socket server;

    public Server_Handler(Socket serverConnection) {

        server = serverConnection;

    }

    @Override
    public void run() {

        BufferedReader receive = null;

        try {

            Json_Encode_Decode json = new Json_Encode_Decode(); //TODO: Might need moving
            receive = new BufferedReader(new InputStreamReader(server.getInputStream()));

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

                System.out.println("[Server Message] Message: " + request);

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
