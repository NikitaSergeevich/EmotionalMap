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

    private static final UriMatcher uriMatcher;

    static
    {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, EmotionMapContract.EmotionMapEntry.TABLE_NAME, 1);
    }

    public EmotionMapContentProvider() { }

    @Override
    public boolean onCreate() {
        //final EmotionMapDbHelper dbHelper = new EmotionMapDbHelper(getContext());
        //db = dbHelper.getWritableDatabase();
        //return !db.isReadOnly();
        dbHelper =  new EmotionMapDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case 1:
                return EmotionMapContract.EmotionMapEntry.TABLE_NAME;
        }
        return "";
    }

    public static Uri getUri(int id) {
        Uri uri = ContentUris.withAppendedId(CONTENT_URI_EMOTION_MAP, id);
        return uri;
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
        long insert = dbHelper.getWritableDatabase().insert(EmotionMapEntry.TABLE_NAME, null, values);
        Uri _uri = ContentUris.withAppendedId(uri, insert);
        //addToProjects(values, insert);

        String UserName = (String)values.get(EmotionMapEntry.COLUMN_NAME_USER);
        double lng = (double)values.get(EmotionMapEntry.COLUMN_NAME_LNG);
        double lat = (double)values.get(EmotionMapEntry.COLUMN_NAME_LAT);
        int type = (int)values.get(EmotionMapEntry.COLUMN_NAME_EMOTION);

        new ServerHelper("POST", new EmotionMapRecord(UserName, type, lng, lat, null)) {
            @Override
            protected void onPostExecute(final ArrayList<EmotionMapRecord> list) {

            }
        }.execute();

        getContext().getContentResolver().notifyChange(_uri, null);
        return _uri;
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
