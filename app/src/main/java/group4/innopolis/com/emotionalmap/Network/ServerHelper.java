package group4.innopolis.com.emotionalmap.Network;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.ProtocolException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import group4.innopolis.com.emotionalmap.EmotionMapRecord;


public class ServerHelper implements Runnable{

    private String[] jsonprojectfields = {"user", "emotion", "x", "y", "objectId"};
    String X_Parse_REST_API_Key = "l3q12AcFfsSGzgZXdEiqsUOLBe5LYTpgvhVCS3aT";
    String X_Parse_Application_Id = "SrHthPGmNtr3Jukwsm3stqAc9CMqAyW5Z7vwXljd";
    String request = "https://api.parse.com/1/classes/Map";

    String request_type;
    EmotionMapRecord request_param;

    public ServerHelper(String type, EmotionMapRecord param)
    {
        request_type = type;
        request_param = param;
    }

    public void run() {
        try {
            switch(request_type)
            {
                case "DELETE":
                    deleteEmotionMapRecord(request_param);
                case "GET":
                    getProjects();
                case "POST":
                    addEmotionMapRecord(new EmotionMapRecord(100, 101, 1, "Nikitos"));
            }
        }
        catch (Exception e) {
            int y = 0;
            //runOnUiThread(toast);
        }
    }

    public ArrayList<EmotionMapRecord> getProjects() throws Exception {
        HttpURLConnection connection = request(request, "GET", null);

        switch (connection.getResponseCode())
        {
            case HttpURLConnection.HTTP_OK:
                return parseProjects(connection);
            default:
                break;
        }
        return null;
    }

    public boolean addEmotionMapRecord(EmotionMapRecord r) throws Exception {
        JSONObject jsonParam = new JSONObject();
        JSONArray array = null;
        jsonParam.put("UserName", r.UserName);
        jsonParam.put("Type", r.Type);
        jsonParam.put("Lat", r.Lat);
        jsonParam.put("Lng", r.Lng);
        HttpURLConnection connection = request(request, "POST", jsonParam);

        final int statusCode = connection.getResponseCode();
        switch (statusCode)
        {
            case HttpURLConnection.HTTP_OK:
                parseProjects(connection);
            default:
                break;
        }
        return true;
    }

    public boolean deleteEmotionMapRecord(EmotionMapRecord r) throws Exception {
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("objectId", "ONJcNQkTOf");
//        jsonParam.put("Type", r.Type);
//        jsonParam.put("Lat", r.Lat);
//        jsonParam.put("Lng", r.Lng);
        HttpURLConnection connection = request(request, "DELETE", jsonParam);
        final int statusCode = connection.getResponseCode();
        return true;
    }

    public HttpURLConnection request(String request, String method, JSONObject params) throws Exception
    {
        URL requestURL	= new URL(request);
        HttpURLConnection connection = (HttpURLConnection)requestURL.openConnection();
        connection.setReadTimeout(10000);
        connection.setConnectTimeout(15000);
        connection.setRequestMethod(method);
        connection.setRequestProperty("X-Parse-REST-API-Key", X_Parse_REST_API_Key);
        connection.setRequestProperty("X-Parse-Application-Id", X_Parse_Application_Id);
        if (method.equals("DELETE") | method.equals("POST") | method.equals("PUT"))
        {
            if (!method.equals("DELETE"))
            {
                connection.setDoInput(true);
            }
            OutputStream f =  connection.getOutputStream();
            DataOutputStream printout = new DataOutputStream(f);
            printout.write(params.toString().getBytes());
            printout.close();
        }
        connection.connect();
        return connection;
    }

    private String readBuffer(HttpURLConnection connection) throws IOException {
        BufferedReader reader =	new	BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line	= reader.readLine();
        while (line	!=	null)	{
            sb.append(line + "\n");
            line = reader.readLine();
        }
        return sb.toString();
    }

    private JSONArray parseBuffer(HttpURLConnection connection) throws IOException {
        JSONObject json	= null;
        JSONArray array = null;
        String response = readBuffer(connection);

        try {
            json = new JSONObject(response);
            array = json.getJSONArray("results");
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return array;
    }

    private ArrayList<EmotionMapRecord> parseProjects(HttpURLConnection connection) throws IOException {
        JSONObject json	= null;
        ArrayList<EmotionMapRecord> data = new ArrayList<EmotionMapRecord>();
        String response = readBuffer(connection);

        try {
            json = new JSONObject(response);
            JSONArray array	= json.getJSONArray("results");
            for (int i = 0; i < array.length(); i++)
            {
                JSONObject obj = array.getJSONObject(i);
                ArrayList<String> jsonparsed = new ArrayList<String>();
                for (String field : jsonprojectfields)
                {
                    if (!obj.isNull(field))
                    {
                        jsonparsed.add(obj.getString(field));
                    }
                    else
                    {
                        jsonparsed.add("");
                    }
                }
                if (!jsonparsed.get(2).equals(""))
                    data.add(new EmotionMapRecord(0, 0, 0, jsonparsed.get(0)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }
}
