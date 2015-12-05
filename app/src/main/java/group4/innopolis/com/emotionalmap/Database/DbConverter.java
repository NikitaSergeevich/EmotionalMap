package group4.innopolis.com.emotionalmap.Database;
import android.content.ContentValues;
import android.database.Cursor;
import group4.innopolis.com.emotionalmap.EmotionMapRecord;

public class DbConverter {

    public static ContentValues convertToContentValues(EmotionMapRecord record) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(EmotionMapContract.EmotionMapEntry.COLUMN_NAME_USER, record.UserName);
        contentValues.put(EmotionMapContract.EmotionMapEntry.COLUMN_NAME_LAT, record.Lat);
        contentValues.put(EmotionMapContract.EmotionMapEntry.COLUMN_NAME_LNG, record.Lng);
        contentValues.put(EmotionMapContract.EmotionMapEntry.COLUMN_NAME_EMOTION, record.Type);
        //ontentValues.put(EmotionMapContract.EmotionMapEntry.COLUMN_NAME_EMOTION, record.objectId);
        return contentValues;
    }

    public static EmotionMapRecord convertToEmotionMapRecord(Cursor cursor) {
//        int id = cursor.getInt(cursor.getColumnIndex(ProjectContract.ProjectEntry._ID));
//        String serviceId = cursor.getString(cursor.getColumnIndex(ProjectContract.ProjectEntry.COLUMN_NAME_SERVICE_ID));
//        String title = cursor.getString(cursor.getColumnIndex(ProjectContract.ProjectEntry.COLUMN_NAME_TITLE));
//        String description = cursor.getString(cursor.getColumnIndex(ProjectContract.ProjectEntry.COLUMN_NAME_DESCRIPTION));
//        String author = cursor.getString(cursor.getColumnIndex(ProjectContract.ProjectEntry.COLUMN_NAME_AUTHOR));
//        Integer rating = cursor.getInt(cursor.getColumnIndex(ProjectContract.ProjectEntry.COLUMN_NAME_RATING));
//        String linkToSite = cursor.getString(cursor.getColumnIndex(ProjectContract.ProjectEntry.COLUMN_NAME_LINK_TO_SITE));
//        String mainProgrammingLanguage = cursor.getString(cursor.getColumnIndex(ProjectContract.ProjectEntry.COLUMN_NAME_MAIN_PROGRAMMING_LANGUAGE));
//        String compactComment = cursor.getString(cursor.getColumnIndex(ProjectContract.ProjectEntry.COLUMN_NAME_COMMENTS));
//        byte[] image = cursor.getBlob(cursor.getColumnIndex(ProjectContract.ProjectEntry.COLUMN_NAME_LOGO));
//        Project project = new Project(title, description, author, getLogo(image), Utils.convertToUrl(linkToSite), mainProgrammingLanguage, rating);
//        project.addComments(Utils.convertToList(compactComment));
//        project.setId(id);
//        project.setServiceId(serviceId);
//        return project;
        return null;
    }

//    private static Bitmap getLogo(byte[] image) {
//        if (image != null) {
//            return BitmapFactory.decodeByteArray(image, 0, image.length);
//        }
//        return null;
//    }
//
//    private static byte[] convertBitmapToByteArray(Bitmap logo) {
//        if (logo != null) {
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            logo.compress(Bitmap.CompressFormat.PNG, 100, bos);
//            byte[] bArray = bos.toByteArray();
//            return bArray;
//        }
//        return null;
//    }
}
