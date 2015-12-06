package group4.innopolis.com.emotionalmap.Database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.ArrayList;

import group4.innopolis.com.emotionalmap.Database.EmotionMapContract.EmotionMapEntry;
import group4.innopolis.com.emotionalmap.EmotionMapRecord;
import group4.innopolis.com.emotionalmap.Network.ServerHelper;

public class EmotionMapContentProvider extends ContentProvider {

    private EmotionMapDbHelper dbHelper;

    public static final String AUTHORITY = "group4.innopolis.com.emotionalmap";
    public static final Uri CONTENT_URI_EMOTION_MAP = Uri.parse("content://group4.innopolis.com.emotionalmap/emotionmap/");

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);;

    static
    {
        uriMatcher.addURI(AUTHORITY, "emotionmap", 0);
    }

    public EmotionMapContentProvider() { }

    @Override
    public boolean onCreate() {
        dbHelper = new EmotionMapDbHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        dbHelper.onClean(db);
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case 0:
                return EmotionMapEntry.TABLE_NAME;
        }
        return "";
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        selection = "_ID = " + uri.getLastPathSegment();
        deleteObject(uri.getLastPathSegment());
        int delete = dbHelper.getWritableDatabase().delete(EmotionMapEntry.TABLE_NAME, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(EmotionMapEntry.CONTENT_URI, null);
        return delete;
    }

    private void deleteObject(String lastPathSegment) {
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT serviceid FROM  projects where _id = " + lastPathSegment, null);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        dbHelper.getWritableDatabase().insert(EmotionMapEntry.TABLE_NAME, null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return uri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return dbHelper.getWritableDatabase().query(getType(uri), projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final int updatedEntries = dbHelper.getWritableDatabase().update(getType(uri), values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return updatedEntries;
    }

}
