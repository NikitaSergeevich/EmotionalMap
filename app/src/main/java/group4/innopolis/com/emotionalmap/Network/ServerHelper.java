package group4.innopolis.com.emotionalmap.Network;
import android.os.AsyncTask;

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


public abstract class ServerHelper extends AsyncTask<Void, Void, ArrayList<EmotionMapRecord>> {

    private String[] jsonprojectfields = {"UserName", "Type", "Lat", "Lng", "objectId"};
    String X_Parse_REST_API_Key = "l3q12AcFfsSGzgZXdEiqsUOLBe5LYTpgvhVCS3aT";
    String X_Parse_Application_Id = "SrHthPGmNtr3Jukwsm3stqAc9CMqAyW5Z7vwXljd";
    String request = "https://api.parse.com/1/classes/Map";

    String request_method;
    EmotionMapRecord request_param;

    public ServerHelper(String method, EmotionMapRecord param)
    {
        request_method = method;
        request_param = param;
    }

    @Override
    protected ArrayList<EmotionMapRecord> doInBackground(Void... params) {
        try {
            switch(request_method)
            {
                case "GET":
                    return getEmotionMapRecord();
                case "POST":
                    addEmotionMapRecord(request_param);
                case "DELETE":
                    deleteEmotionMapRecord(request_param.objectId);
                case "PUT":
                    deleteEmotionMapRecord(request_param.objectId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected abstract void onPostExecute(final ArrayList<EmotionMapRecord> result);


    public ArrayList<EmotionMapRecord> getEmotionMapRecord() throws Exception {
        HttpURLConnection connection = request(request, "GET", null);

        switch (connection.getResponseCode())
        {
            case HttpURLConnection.HTTP_OK:
                return parseProjects(connection);
            default:
                return null;
        }
    }

    public boolean addEmotionMapRecord(EmotionMapRecord r) throws Exception {
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("UserName", r.UserName);
        jsonParam.put("Type", r.Type);
        jsonParam.put("Lat", r.Lat);
        jsonParam.put("Lng", r.Lng);
        HttpURLConnection connection = request(request, "POST", jsonParam);
        final int statusCode = connection.getResponseCode();

        switch (statusCode)
        {
            case HttpURLConnection.HTTP_OK:
                //parseProjects(connection);
                return true;
            default:
                return false;
        }
    }

    public boolean deleteEmotionMapRecord(String objectId) throws Exception {
        HttpURLConnection connection = request(request + "/" + objectId, "DELETE", null);
        final int statusCode = connection.getResponseCode();

        switch (statusCode)
        {
            case HttpURLConnection.HTTP_OK:
                return true;
            default:
                return false;
        }
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
        connection.setDoInput(true);

        switch (method)
        {
            case "GET":
                connection.connect();
                break;
            case "POST":
                DataOutputStream printout = new DataOutputStream(connection.getOutputStream());
                printout.write(params.toString().getBytes());
                printout.close();
                break;
            case "DELETE":
                connection.getResponseCode();
                break;
        }
        return connection;
    }

    private ArrayList<EmotionMapRecord> parseProjects(HttpURLConnection connection) throws IOException {
        JSONObject json;
        ArrayList<EmotionMapRecord> data = new ArrayList<>();
        String response = readBuffer(connection);

        try {
            json = new JSONObject(response);
            JSONArray array	= json.getJSONArray("results");
            for (int i = 0; i < array.length(); i++)
            {
                JSONObject obj = array.getJSONObject(i);
                ArrayList<String> jsonparsed = new ArrayList<>();
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
                    data.add(new EmotionMapRecord(jsonparsed.get(0),
                            Integer.parseInt(jsonparsed.get(1)),
                            Double.parseDouble(jsonparsed.get(2)),
                            Double.parseDouble(jsonparsed.get(3)),
                            jsonparsed.get(4)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
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
}
