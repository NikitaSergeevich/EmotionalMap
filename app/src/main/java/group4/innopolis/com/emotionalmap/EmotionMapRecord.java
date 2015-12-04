package group4.innopolis.com.emotionalmap;

/**
 * Created by n.avrelin on 12/4/2015.
 */
public class EmotionMapRecord {

    public double Lat;
    public double Lng;
    public int Type;
    public String UserName;

    public EmotionMapRecord(double lat, double lng, int type, String userName) {
        Lat = lat;
        Lng = lng;
        Type = type;
        UserName = userName;
    }
}
