package group4.innopolis.com.emotionalmap;

public class EmotionMapRecord {

    public double Lat;
    public double Lng;
    public int Type;
    public String UserName;
    public String Text;
    public String objectId;

    public EmotionMapRecord(String userName, int type, double lat, double lng, String text, String id) {
        Lat = lat;
        Lng = lng;
        Type = type;
        Text = text;
        UserName = userName;
        objectId = id;
    }
}
