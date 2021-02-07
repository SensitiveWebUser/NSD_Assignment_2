package NSD.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Json_Encode_Decode
{

    public Json_Encode_Decode()
    {

    }

    public byte[] Encode_Message(String author, String message){

        try{

            JSONObject result = new JSONObject();

            result.put("Type", 1);
            result.put("Author", author);
            result.put("Time", LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss")));
            result.put("Message", message);

            return result.toString().getBytes(StandardCharsets.UTF_8);

        }catch (JSONException err){
            return null;
        }

    }

    public JSONObject Decode_Message(byte[] json){

        try{

            String stringJson = new String(json, StandardCharsets.UTF_8);
            JSONObject message = new JSONObject(stringJson);

            return message;

        }catch (JSONException err){
            return null;
        }

    }

    public byte[] Decode_All_Message(ResultSet messages){

        try{

            JSONArray result = new JSONArray();

            for(int i = 0; i < messages.getFetchSize() ; i++){

                String encoded_Message = messages.getObject(i).toString();
                result.put(encoded_Message);

            }

            return result.toString().getBytes(StandardCharsets.UTF_8);

        }catch (JSONException err){
            return null;
        }catch (SQLException err){
            return null;
        }

    }

    public Object Decode_To_Json(BufferedReader  encoded_JSON){

        try{

            return null;

        }catch (JSONException err){
            return null;
        }

    }

}
