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
    BufferedReader keyboard;

    private static String userName;

    Client(String ip, int socket) {

        keyboard = new BufferedReader(new InputStreamReader(System.in));
        userName = userLogin();
        Startup(ip, socket);

    }


    private String userLogin(){

        String result = "";

        while (result == ""){

            result = "";
            System.out.println("Please input login name");

            try{

                result = keyboard.readLine();

            }catch (IOException err){
                System.out.println("Input invalid");
                result = "";
            }
        }
        return result;

    }

    private void Startup(String ip, int socket) {

        try {

            json = new Json_Encode_Decode();

            server = new Socket(ip, socket);
            sender = new BufferedOutputStream(server.getOutputStream());

            new Thread(new Server_Handler(server)).start();

            sender.write(Json_Encode_Decode.encodeJsonMessages("Client", "Hello server."));
            sender.flush();

            Run();

        } catch (IOException err) {
            System.out.println("Client Error | Error time: " + LocalTime.now() + " Error message: " + err.getMessage());
        }finally {
            System.out.println("Closing client");
            try {
                sender.close();
                server.close();
                System.exit(0);
            }catch (IOException err){
                System.exit(0);
            }
        }

        //TODO:Make your client waits to get server again

    }

    void Run() {

        while (true) {

            int input = 0;

            System.out.println("    1) Start channel");
            System.out.println("    2) Subscribe to channel");
            System.out.println("    3) Unsubscribe to channel");
            System.out.println("    4) View Subscribed channels");
            System.out.println("    5) Exit");

            try{
                String strInput = keyboard.readLine();
                input = Integer.parseInt(strInput);
            }catch (IOException err){
                input = 0;
            }catch (NumberFormatException err){
                input = 0;
            }

            switch (input){
                case 1:
                    requestOpen();
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
                    return;
                default:
                    System.out.println("known input");
                    break;
            }

        }

    }

    private void requestOpen() {
    }

    private void requestSubscribe() {
    }

    private void requestUnsubscribe() {
    }

    private void subscribedChannels() {
    }
}
