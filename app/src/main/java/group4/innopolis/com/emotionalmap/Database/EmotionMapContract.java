package group4.innopolis.com.emotionalmap.Database;

import android.net.Uri;
import android.provider.BaseColumns;

public final class EmotionMapContract {

    public EmotionMapContract() {}

    public static abstract class EmotionMapEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://group4.innopolis.com.emotopnalmap/EmotionDb");
        public static final String TABLE_NAME = "EmotionDb";
        public static final String COLUMN_NAME_USER = "user";
        public static final String COLUMN_NAME_TEXT = "text";
        public static final String COLUMN_NAME_EMOTION = "emotion";
        public static final String COLUMN_NAME_LAT = "lat";
        public static final String COLUMN_NAME_LNG = "lng";
        public static final String COLUMN_NAME_OBJECT_ID = "object_id";
    }

}