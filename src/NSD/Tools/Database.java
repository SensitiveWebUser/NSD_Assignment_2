package NSD.Tools;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;

public class Database {

    private static Connection conn;

    public Database() {

        try {

            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:..\\NSD_Assignment_2\\NSD_Assigment_2_DB.sqlite";

            conn = DriverManager.getConnection(url);

            if (conn != null) {
                System.out.println("[Server] Successfully connected to SQLite database");
            }

        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println("[Server] An error occurred while connecting SQLite database");
            ex.printStackTrace();
        }

    }

    private static ResultSet selectQuery(final String query) {

        try {

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            return rs;

        } catch (SQLException err) {
            return null;
        }
    }

    public static int channelIdWhereName(final String channel_name) {

        int id = 0;

        try {
            ResultSet rs = selectQuery("SELECT id from table_channels " + "WHERE name='" + channel_name + "' LIMIT 1;");
            id = rs.getInt("id");
        } catch (SQLException err) {

        }finally {
            return id;
        }
    }

    public static ArrayList<String> channels() {

        ArrayList<String> channels = new ArrayList<>();

        try {

            ResultSet rs = selectQuery("SELECT name from table_channels;");
            while (rs.next()){
                channels.add(rs.getString("name"));
            }
        } catch (SQLException err) {

        }finally {
            return channels;
        }

    }

    public static JSONArray AllMessagesWhereChannelName(final String channel_name) {

        final JSONArray messages = new JSONArray();

        try {

            final int channel_id = channelIdWhereName(channel_name);
            if(channel_id != 0){
                ResultSet rs = selectQuery("SELECT json from table_messages " + "WHERE id='" + channel_id + "';");

                while (rs.next()){
                    messages.put (Json_Encode_Decode.decodeJson(rs.getString("json").getBytes(StandardCharsets.UTF_8)));
                }
            }
        }catch (SQLException err){

        }finally {
            return messages;
        }
    }

    public static boolean addMessage(final byte[] json, final String channel_name) {

        try {
            final int channel_id = channelIdWhereName(channel_name);
            if(channel_id != 0){
                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO table_messages(channel_id,json) VALUES(?,?);");
                pstmt.setInt(1,channel_id);
                pstmt.setString(2,json.toString());
                pstmt.executeUpdate();
                return true;
            }
        }catch (SQLException err){

        }finally {
            return false;
        }
    }

    public static boolean addChannel(final byte[] json, final String channel_name) {

        final JSONArray messages = new JSONArray();

        try {
                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO table_messages(name) VALUES(?);");
                pstmt.setString(1,channel_name);
                pstmt.executeUpdate();
                return true;
        }catch (SQLException err){

        }finally {
            return false;
        }
    }



}
