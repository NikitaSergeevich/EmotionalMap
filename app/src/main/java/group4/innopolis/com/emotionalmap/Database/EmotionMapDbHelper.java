package group4.innopolis.com.emotionalmap.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import group4.innopolis.com.emotionalmap.Database.EmotionMapContract.EmotionMapEntry;

public class EmotionMapDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "EmotionMap.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + EmotionMapEntry.TABLE_NAME + " (" +
                    EmotionMapEntry._ID + " INTEGER PRIMARY KEY," +
                    EmotionMapEntry.COLUMN_NAME_USER + " TEXT," +
                    EmotionMapEntry.COLUMN_NAME_EMOTION + " INTEGER," +
                    EmotionMapEntry.COLUMN_NAME_LAT + " INTEGER," +
                    EmotionMapEntry.COLUMN_NAME_LNG + " INTEGER," +
                    EmotionMapEntry.COLUMN_NAME_TEXT + " TEXT," +
                    EmotionMapEntry.COLUMN_NAME_OBJECT_ID + " TEXT" +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + EmotionMapEntry.TABLE_NAME;

    public EmotionMapDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onClean(SQLiteDatabase db) {
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}