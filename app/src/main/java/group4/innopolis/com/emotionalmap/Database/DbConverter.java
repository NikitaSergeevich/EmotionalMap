package group4.innopolis.com.emotionalmap.Database;
import android.content.ContentValues;
import android.database.Cursor;
import group4.innopolis.com.emotionalmap.EmotionMapRecord;
import group4.innopolis.com.emotionalmap.Database.EmotionMapContract.EmotionMapEntry;

public class DbConverter {

    public static ContentValues convertToContentValues(EmotionMapRecord record) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(EmotionMapEntry.COLUMN_NAME_USER, record.UserName);
        contentValues.put(EmotionMapEntry.COLUMN_NAME_LAT, record.Lat);
        contentValues.put(EmotionMapEntry.COLUMN_NAME_LNG, record.Lng);
        contentValues.put(EmotionMapEntry.COLUMN_NAME_TEXT, record.Text);
        contentValues.put(EmotionMapEntry.COLUMN_NAME_EMOTION, record.Type);
        contentValues.put(EmotionMapEntry.COLUMN_NAME_OBJECT_ID, record.objectId);
        return contentValues;
    }

    public static EmotionMapRecord convertToEmotionMapRecord(Cursor cursor) {
        String objectid = cursor.getString(cursor.getColumnIndex(EmotionMapEntry.COLUMN_NAME_OBJECT_ID));
        String username = cursor.getString(cursor.getColumnIndex(EmotionMapEntry.COLUMN_NAME_USER));
        double lat = cursor.getDouble(cursor.getColumnIndex(EmotionMapEntry.COLUMN_NAME_LAT));
        double lng = cursor.getDouble(cursor.getColumnIndex(EmotionMapEntry.COLUMN_NAME_LNG));
        int emotion = cursor.getInt(cursor.getColumnIndex(EmotionMapEntry.COLUMN_NAME_EMOTION));
        String text = cursor.getString(cursor.getColumnIndex(EmotionMapEntry.COLUMN_NAME_TEXT));
        EmotionMapRecord r = new EmotionMapRecord(username, emotion, lat, lng, text, objectid);
        return r;
    }
}
