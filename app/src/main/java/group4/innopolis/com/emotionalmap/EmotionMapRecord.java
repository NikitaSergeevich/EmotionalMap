package group4.innopolis.com.emotionalmap;

/**
 * Created by n.avrelin on 12/4/2015.
 */
public class EmotionMapRecord {

    public double Lat;
    public double Lng;
    public int Type;
    public String UserName;
    public String objectId;

    public EmotionMapRecord(String userName, int type, double lat, double lng, String Id) {
        Lat = lat;
        Lng = lng;
        Type = type;
        UserName = userName;
        objectId = Id;
    }
}
