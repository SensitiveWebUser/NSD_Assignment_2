package NSD.Client;

import NSD.Tools.Json_Encode_Decode;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.time.LocalTime;

public class Client {

    private static Socket server;
    private static Json_Encode_Decode json;
    private static BufferedOutputStream sender;
    private static String userName;
    BufferedReader keyboard;

    Client(final String ip, final int socket) {

        keyboard = new BufferedReader(new InputStreamReader(System.in));
        userName = userLogin();
        Startup(ip, socket);

    }


    private String userLogin() {

        String result = "";

        while (result == "") {

            result = "";
            System.out.println("Please input login name");

            try {

                result = keyboard.readLine();

            } catch (IOException err) {
                System.out.println("Input invalid");
                result = "";
            }
        }
        return result;

    }

    private void Startup(final String ip, final int socket) {

        try {

            json = new Json_Encode_Decode();

            server = new Socket(ip, socket);
            sender = new BufferedOutputStream(server.getOutputStream());

            new Thread(new Server_Handler(server)).start();

            sender.write(Json_Encode_Decode.encodeJsonOpen(userName));
            sender.flush();

            Run();

        } catch (IOException err) {
            System.out.println("Client Error | Error time: " + LocalTime.now() + " Error message: " + err.getMessage());
        } finally {
            System.out.println("Closing client");
            closeConnection();
            System.exit(0);
        }

        //TODO:Make your client waits to get server again

    }

    void Run() {

        while (true) {

            int input = 0;

            System.out.println("    1) Send message in channel");
            System.out.println("    2) Subscribe to channel");
            System.out.println("    3) Unsubscribe to channel");
            System.out.println("    4) View Subscribed channels");
            System.out.println("    5) Exit");

            try {
                String strInput = keyboard.readLine();
                input = Integer.parseInt(strInput);
            } catch (IOException err) {
                input = 0;
            } catch (NumberFormatException err) {
                input = 0;
            }

            switch (input) {
                case 1:
                    requestPublish();
                    break;
                case 2:
                    requestSubscribe();
                    break;
                case 3:
                    requestUnsubscribe();
                    break;
                case 4:
                    subscribedChannels();
                    break;
                case 5:
                    closeConnection();
                    System.exit(0);
                    break;
                default:
                    System.out.println("known input");
                    break;
            }

        }

    }

    private void requestPublish() {

        try {
            while (true){
                String channel = "";
                String message = "";


                try {
                    System.out.println("Please input channel to message (don't add channel if messaging your channel)");
                    channel = keyboard.readLine();
                    System.out.println("Please input the message");
                    message = keyboard.readLine();
                } catch (IOException err) {
                    channel = "";
                    message = "";
                }

                if(message != ""){

                    if(channel == "") channel = userName;

                    sender.write(json.encodeJsonPublish(channel, userName, message));
                    sender.flush();
                    return;

                }else {
                    System.out.println("Bad input");
                }
            }
        }catch (IOException err){

        }

    }

    private void requestSubscribe() {
    }

    private void requestUnsubscribe() {
    }

    private void subscribedChannels() {
    }

    private static void closeConnection() {
        try {
            sender.close();
            server.close();
        } catch (IOException err) {
            System.out.println("Critical fail!");
        }
    }

}
