package group4.innopolis.com.emotionalmap.Database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import group4.innopolis.com.emotionalmap.Database.EmotionMapContract.EmotionMapEntry;

public class EmotionMapContentProvider extends ContentProvider {

    private EmotionMapDbHelper dbHelper;

    public static final String AUTHORITY = "group4.innopolis.com.emotionalmap";
    //public static final Uri CONTENT_URI_EMOTIONAL_MAP = Uri.parse("content://ru.innopolis.sakhankov/projects/");

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
        final long id = dbHelper.getWritableDatabase().insert(getType(uri), null, values);
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
