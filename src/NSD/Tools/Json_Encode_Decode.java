package NSD.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Json_Encode_Decode {

    public static byte[] encodeJsonOpen( final String identity) {
        try {

            final JSONObject result = new JSONObject();

            result.put("_class", "OpenRequest");
            result.put("identity", identity);

            return result.toString().getBytes(StandardCharsets.UTF_8);

        } catch (JSONException err) {
            return null;
        }
    }

    public static byte[] encodeJsonPublish( final String identity, final String author, final String message) {

        try {

            final JSONObject result = new JSONObject();

            result.put("_class", "PublishRequest");
            result.put("identity", identity);

            result.put("message", decodeJson(encodeJsonMessage(author, message)));

            return result.toString().getBytes(StandardCharsets.UTF_8);

        } catch (JSONException err) {
            return null;
        }

    }

    public static byte[] encodeJsonPublish( final String identity, final String author, final String message, final String encodedPic) {

        try {

            final JSONObject result = new JSONObject();

            result.put("_class", "PublishRequest");
            result.put("identity", identity);

            result.put("message", decodeJson(encodeJsonMessage(author, message, encodedPic)));

            return result.toString().getBytes(StandardCharsets.UTF_8);

        } catch (JSONException err) {
            return null;
        }

    }

    public static byte[] encodeJsonSubscribe( final String identity, final String channel) {

        try {

            final JSONObject result = new JSONObject();

            result.put("_class", "SubscribeRequest");
            result.put("identity", identity);
            result.put("channel", channel);

            return result.toString().getBytes(StandardCharsets.UTF_8);

        } catch (JSONException err) {
            return null;
        }

    }

    public static byte[] encodeJsonUnsubscribe( final String identity, final String channel) {

        try {

            final JSONObject result = new JSONObject();

            result.put("_class", "UnsubscribeRequest");
            result.put("identity", identity);
            result.put("channel", channel);

            return result.toString().getBytes(StandardCharsets.UTF_8);

        } catch (JSONException err) {
            return null;
        }

    }

    public static byte[] encodeJsonGet( final String identity, final int after) {

        try {

            final JSONObject result = new JSONObject();

            result.put("_class", "GetRequest");
            result.put("identity", identity);
            result.put("after", after);

            return result.toString().getBytes(StandardCharsets.UTF_8);

        } catch (JSONException err) {
            return null;
        }

    }

    public static byte[] encodeMessageList( final String channel, final int after,final Database db) {

        try {

            final JSONObject result = new JSONObject();

            result.put("_class", "MessageListResponse");
            result.put("messages", db.AllMessagesWhereChannelName(channel));

            return result.toString().getBytes(StandardCharsets.UTF_8);

        } catch (JSONException err) {
            return null;
        }

    }

    public static byte[] encodeSuccess () {

        try {

            final JSONObject result = new JSONObject();

            result.put("_class", "SuccessResponse");

            return result.toString().getBytes(StandardCharsets.UTF_8);

        } catch (JSONException err) {
            return null;
        }

    }

    public static byte[] encodeError(final int errorMessage, final String specialMessage) {

        try {

            final String[] errorMessages = {"NO SUCH CHANNEL: ", "MESSAGE TOO BIG: ", "INVALID REQUEST: "};
            final String message;

            switch (errorMessage + 1) {
                case 1:
                    message = errorMessages[0];
                    break;
                case 2:
                    message = errorMessages[1] + ' ' + specialMessage + " characters";
                    break;
                default:
                    message = errorMessages[2] + specialMessage;
                    break;

            }

            JSONObject result = new JSONObject();

            result.put("_class", "ErrorResponse");
            result.put("error", message);

            return result.toString().getBytes(StandardCharsets.UTF_8);

        } catch (JSONException err) {
            return null;
        }

    }


    public static byte[] encodeJsonMessage(final String author, final String message) {

        try {

            final JSONObject result = new JSONObject();

            result.put("_class", "Message");
            result.put("from", author);
            result.put("when", LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss")));
            result.put("body", message);

            return result.toString().getBytes(StandardCharsets.UTF_8);

        } catch (JSONException err) {
            return null;
        }

    }

    public static byte[] encodeJsonMessage(final String author, final String message, final String encodedPic) {

        try {

            final JSONObject result = new JSONObject();

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

    public static JSONObject decodeJson( final byte[] json) {

        try {

            final String stringJson = new String(json, StandardCharsets.UTF_8);
            final JSONObject message = new JSONObject(stringJson);

            return message;

        } catch (JSONException err) {
            return null;
        }

    }

}
