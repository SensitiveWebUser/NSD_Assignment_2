package NSD.Client;

import NSD.Tools.Json_Encode_Decode;

import java.io.*;
import java.net.Socket;
import java.time.LocalTime;

public class Client {

    private static OutputStream sender;

    Client(String ip, int socket) {

        try {
            Run(ip, socket);
        } catch (IOException err) {
            System.out.println("Client Error | Error time: " + LocalTime.now() + " Error message: " + err.getMessage());
        } finally {
            //sender.close(); TODO: check
        }

    }

    void Run(String ip, int socket) throws IOException {

        Json_Encode_Decode json = new Json_Encode_Decode(); //TODO: Might need moving

        Socket server = new Socket(ip, socket);
        System.out.println("Client Starting, and trying to connect to port: " + socket);

        Server_Handler server_handler = new Server_Handler(server);
        new Thread(server_handler).start();

        sender = server.getOutputStream();
        sender.write(json.Encode_Message("Client", ("Hello server. Time of connection: " + LocalTime.now())));

        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            String input = keyboard.readLine();
            if (input.equals("Exit")) {
                break;
            } else {

                byte[] jsonFile = json.Encode_Message("Alex", input);

                if(jsonFile.length > 0)
                    sender.write(jsonFile);

            }
        }

    }
}
