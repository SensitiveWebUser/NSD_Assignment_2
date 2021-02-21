package NSD.Utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.sqlite.SQLiteConfig;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;

public class Database {

    private static final SQLiteConfig config = new SQLiteConfig();
    private static Connection conn;

    public Database() {

        try {

            config.setSynchronous(SQLiteConfig.SynchronousMode.OFF);
            config.setJournalMode(SQLiteConfig.JournalMode.WAL);

            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:..\\NSD_Assignment_2\\NSD_Assigment_2_DB.sqlite";

            conn = DriverManager.getConnection(url);

            if (conn != null) {
                config.apply(conn);
                System.out.println("[Server] Successfully connected to SQLite database");
            }

        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println("[Server] An error occurred while connecting SQLite database");
            ex.printStackTrace();
        }

    }

    private ResultSet selectQuery(final String query) {

        try {

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            return rs;

        } catch (SQLException err) {
            return null;
        }
    }

    public int channelIdWhereName(final String channel_name) {

        int id = 0;

        try {
            ResultSet rs = selectQuery("SELECT id from table_channels WHERE name= '" + channel_name + "' LIMIT 1;");
            id = rs.getInt("id");
        } catch (SQLException err) {

        } finally {
            return id;
        }
    }

    public ArrayList<String> channels() {

        ArrayList<String> channels = new ArrayList<>();

        try {

            ResultSet rs = selectQuery("SELECT name from table_channels;");
            while (rs.next()) {
                channels.add(rs.getString("name"));
            }
        } catch (SQLException err) {

        } finally {
            return channels;
        }

    }

    public JSONArray AllMessagesWhereChannelName(final String channel_name, final int after) {

        final JSONArray messages = new JSONArray();

        try {

            final int channel_id = channelIdWhereName(channel_name);
            if (channel_id != 0) {
                ResultSet rs = selectQuery("SELECT json from table_messages WHERE channel_id=" + channel_id + ";");

                int activeMessage = 1;
                while (rs.next()) {
                    if (activeMessage >= after || after == 0) {
                        String message = rs.getString("json");
                        message = message.substring(1, message.length() - 1);
                        messages.put(new JSONObject(message));
                    }

                    activeMessage++;
                }
            }
        } catch (SQLException err) {

        } finally {
            return messages;
        }

    }

    public boolean addMessage(final byte[] json, final String channel_name) {

        try {
            final int channel_id = channelIdWhereName(channel_name);
            if (channel_id != 0) {
                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO table_messages(channel_id,json) VALUES(?,?);");
                pstmt.setInt(1, channel_id);
                pstmt.setString(2, "'" + new String(json, StandardCharsets.UTF_8) + "'");
                pstmt.executeUpdate();
                return true;
            }
        } catch (SQLException err) {
            return false;
        }
        return false;
    }

    public boolean addChannel(final String channel_name) {
        try {
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO table_channels(name) VALUES(?);");
            pstmt.setString(1, channel_name);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException err) {

        } finally {
            return false;
        }
    }

    public Integer getChannelMessageNumber(final String channel_name) {
        int result = 0;
        try {

            final int channel_id = channelIdWhereName(channel_name);
            if (channel_id != 0) {

                ResultSet rs = selectQuery("SELECT COUNT(*) AS messages FROM table_messages WHERE channel_id = " + channel_id + ';');
                rs.next();
                result = rs.getInt("messages");

            }

        } catch (SQLException err) {
        } finally {
            return result;
        }
    }
}
