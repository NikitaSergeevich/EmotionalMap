package group4.innopolis.com.emotionalmap;

/**
 * Created by n.avrelin on 12/3/2015.
 */
public class MapRecord {
    public static double Lat;
    public static double Lng;
    public static int Type;
    public static String UserName;

    public MapRecord(double lat, double lng, int type, String userName) {
        Lat = lat;
        Lng = lng;
        Type = type;
        UserName = userName;
    }
}
