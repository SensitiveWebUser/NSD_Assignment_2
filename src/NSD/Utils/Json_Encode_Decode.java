package NSD.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Json_Encode_Decode {

    public String encodeJsonOpen(final String identity) {
        try {

            final JSONObject result = new JSONObject();

            result.put("_class", "OpenRequest");
            result.put("identity", identity);

            return Base64.getEncoder().encodeToString(result.toString().getBytes(StandardCharsets.UTF_8));

        } catch (JSONException err) {
            return null;
        }
    }

    public String encodeJsonSubscribe(final String identity, final String channel) {

        try {

            final JSONObject result = new JSONObject();

            result.put("_class", "SubscribeRequest");
            result.put("identity", identity);
            result.put("channel", channel);

            return Base64.getEncoder().encodeToString(result.toString().getBytes(StandardCharsets.UTF_8));

        } catch (JSONException err) {
            return null;
        }

    }

    public String encodeJsonUnsubscribe(final String identity, final String channel) {

        final JSONObject result = new JSONObject();

        result.put("_class", "UnsubscribeRequest");
        result.put("identity", identity);
        result.put("channel", channel);

        return Base64.getEncoder().encodeToString(result.toString().getBytes(StandardCharsets.UTF_8));

    }

    public String encodeJsonGet(final String identity, final int after) {

        final JSONObject result = new JSONObject();

        result.put("_class", "GetRequest");
        result.put("identity", identity);
        result.put("after", after);

        return Base64.getEncoder().encodeToString(result.toString().getBytes(StandardCharsets.UTF_8));
    }

    public String encodeMessageList(final String channel, final int after, final Database db) {

        final JSONObject result = new JSONObject();

        result.put("_class", "MessageListResponse");
        result.put("messages", db.AllMessagesWhereChannelName(channel, after));

        return Base64.getEncoder().encodeToString(result.toString().getBytes(StandardCharsets.UTF_8));

    }

    public String encodeSuccess() {

        final JSONObject result = new JSONObject();

        result.put("_class", "SuccessResponse");

        return Base64.getEncoder().encodeToString(result.toString().getBytes(StandardCharsets.UTF_8));

    }

    public String encodeError(final int errorMessage, final String specialMessage) {

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

        return Base64.getEncoder().encodeToString(result.toString().getBytes(StandardCharsets.UTF_8));
    }

    public String encodeJsonMessage(final String author, final String message) {

        final JSONObject result = new JSONObject();

        result.put("_class", "Message");
        result.put("from", author);
        result.put("when", 0);
        result.put("body", message);

        return Base64.getEncoder().encodeToString(result.toString().getBytes(StandardCharsets.UTF_8));

    }

    public String encodeJsonMessage(final String author, final String message, final String encodedPic) {

        final JSONObject result = new JSONObject();

        result.put("_class", "Message");
        result.put("from", author);
        result.put("when", 0);
        result.put("body", message);
        result.put("pic", encodedPic);

        return Base64.getEncoder().encodeToString(result.toString().getBytes(StandardCharsets.UTF_8));

    }

    public String encodeJsonPublish(final String identity, final String author, final String message) {

        final JSONObject result = new JSONObject();

        result.put("_class", "PublishRequest");
        result.put("identity", identity);

        result.put("message", decodeJson(encodeJsonMessage(author, message)));

        return Base64.getEncoder().encodeToString(result.toString().getBytes(StandardCharsets.UTF_8));

    }

    public String encodeJsonPublish(final String identity, final String author, final String message, final String encodedPic) {

        final JSONObject result = new JSONObject();

        result.put("_class", "PublishRequest");
        result.put("identity", identity);

        result.put("message", decodeJson(encodeJsonMessage(author, message, encodedPic)));

        return Base64.getEncoder().encodeToString(result.toString().getBytes(StandardCharsets.UTF_8));

    }

    public JSONObject decodeJson(final String json) {

        JSONObject message = new JSONObject();

        try {
            final byte[] decoded = Base64.getDecoder().decode(json);
            message = new JSONObject(new String(decoded));
            return message;
        } catch (JSONException err) {
            return message;
        }

    }

}
