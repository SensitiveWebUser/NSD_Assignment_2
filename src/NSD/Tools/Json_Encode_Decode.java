package NSD.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Json_Encode_Decode {

    public static byte[] encodeJsonOpen(String identity) {

        try {

            JSONObject result = new JSONObject();

            result.put("_class", "OpenRequest");
            result.put("identity", identity);

            return result.toString().getBytes(StandardCharsets.UTF_8);

        } catch (JSONException err) {
            return null;
        }

    }

    public static byte[] encodeJsonPublish(String identity, String author, String message) {

        try {

            JSONObject result = new JSONObject();

            result.put("_class", "PublishRequest");
            result.put("identity", identity);

            result.put("message", decodeJson(encodeJsonMessages(author, message)));

            return result.toString().getBytes(StandardCharsets.UTF_8);

        } catch (JSONException err) {
            return null;
        }

    }

    public static byte[] encodeJsonPublish(String identity, String author, String message, String encodedPic) {

        try {

            JSONObject result = new JSONObject();

            result.put("_class", "PublishRequest");
            result.put("identity", identity);

            result.put("message", decodeJson(encodeJsonMessages(author, message, encodedPic)));

            return result.toString().getBytes(StandardCharsets.UTF_8);

        } catch (JSONException err) {
            return null;
        }

    }

    public static byte[] encodeJsonSubscribe(String identity, String channel) {

        try {

            JSONObject result = new JSONObject();

            result.put("_class", "SubscribeRequest");
            result.put("identity", identity);
            result.put("channel", channel);

            return result.toString().getBytes(StandardCharsets.UTF_8);

        } catch (JSONException err) {
            return null;
        }

    }

    public static byte[] encodeJsonUnsubscribe(String identity, String channel) {

        try {

            JSONObject result = new JSONObject();

            result.put("_class", "UnsubscribeRequest");
            result.put("identity", identity);
            result.put("channel", channel);

            return result.toString().getBytes(StandardCharsets.UTF_8);

        } catch (JSONException err) {
            return null;
        }

    }

    public static byte[] encodeJsonGet(String identity, String after) {

        try {

            JSONObject result = new JSONObject();

            result.put("_class", "GetRequest");
            result.put("identity", identity);
            result.put("after", after);

            return result.toString().getBytes(StandardCharsets.UTF_8);

        } catch (JSONException err) {
            return null;
        }

    }

    public static byte[] encodeMessageList(JSONArray messages) {

        try {

            JSONObject result = new JSONObject();

            result.put("_class", "MessageListResponse");
            result.put("messages", messages);

            return result.toString().getBytes(StandardCharsets.UTF_8);

        } catch (JSONException err) {
            return null;
        }

    }

    public static byte[] encodeJsonMessages(String author, String message) {

        try {

            JSONObject result = new JSONObject();

            result.put("_class", "Message");
            result.put("from", author);
            result.put("when", LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss")));
            result.put("body", message);

            return result.toString().getBytes(StandardCharsets.UTF_8);

        } catch (JSONException err) {
            return null;
        }

    }

    public static byte[] encodeJsonMessages(String author, String message, String encodedPic) {

        try {

            JSONObject result = new JSONObject();

            result.put("_class", "Message");
            result.put("from", author);
            result.put("when", LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss")));
            result.put("body", message);
            result.put("pic", encodedPic);

            return result.toString().getBytes(StandardCharsets.UTF_8);

        } catch (JSONException err) {
            return null;
        }

    }

    public static JSONObject decodeJson(byte[] json) {

        try {

            String stringJson = new String(json, StandardCharsets.UTF_8);
            JSONObject message = new JSONObject(stringJson);

            return message;

        } catch (JSONException err) {
            return null;
        }

    }

}
