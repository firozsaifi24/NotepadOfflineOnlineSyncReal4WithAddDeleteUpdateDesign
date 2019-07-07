package example.firoz.notepadofflineonlinesyncreal2.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    //Constants for Database name, table name, and column names
    public static final String DB_NAME = "Notepad";
    public static final String TABLE_NAME = "notes";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_UNIQUE_ID = "unique_id";
    public static final String COLUMN_NOTES = "notes";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_UPDATED_AT = "updated_at";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_DELETE_STATUS = "delete_status";
    public static final String COLUMN_UPDATE_STATUS = "update_status";

    public static final int NOTES_DELETED_FROM_SERVER = 2;
    public static final int NOTES_NOT_DELETED_FROM_SERVER = 1;

    public static final int NOTES_UPDATED_TO_SERVER = 2;
    public static final int NOTES_NOT_UPDATED_TO_SERVER = 1;

    //Constructor
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    //database version
    private static final int DB_VERSION = 1;

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE " + TABLE_NAME
                + "(" + COLUMN_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_UNIQUE_ID +
                " VARCHAR, " + COLUMN_NOTES +
                " VARCHAR, " + COLUMN_USER_ID +
                " INTEGER, " + COLUMN_CREATED_AT +
                " VARCHAR, " + COLUMN_UPDATED_AT +
                " VARCHAR, " + COLUMN_STATUS +
                " TINYINT, " + COLUMN_DELETE_STATUS +
                " INTEGER DEFAULT 0, " + COLUMN_UPDATE_STATUS +
                " INTEGER DEFAULT 0);";
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS "+TABLE_NAME;
        db.execSQL(sql);
        onCreate(db);
    }

    /* to add notes
     * This method is taking two arguments
     * first one is the name that is to be saved
     * second one is the status
     * 0 means the name is not synced with the server
     * 1 means the name is synced with the server
     * */
    public boolean addNotes(String unique_id, String notes, int userid, String created_at, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_UNIQUE_ID, unique_id);
        contentValues.put(COLUMN_NOTES, notes);
        contentValues.put(COLUMN_USER_ID, userid);
        contentValues.put(COLUMN_CREATED_AT, created_at);
        contentValues.put(COLUMN_STATUS, status);


        db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    //to delete the notes
    public boolean deleteNotes(String unique_id, int deleteStatus) {
        SQLiteDatabase db = this.getWritableDatabase();

        //db.delete(TABLE_NAME,"id=? and name=?",new String[]{"1","jack"});
        db.delete(TABLE_NAME,COLUMN_UNIQUE_ID+"=?",new String[]{unique_id});

        db.close();
        return true;

    }

    //to update the notes
    public boolean updateNotes(String unique_id, String note, String updated_at, int update_status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NOTES, note);
        contentValues.put(COLUMN_UPDATED_AT, updated_at);
        contentValues.put(COLUMN_UPDATE_STATUS, update_status);

        db.update(TABLE_NAME, contentValues, COLUMN_UNIQUE_ID + "=" + "'"+unique_id+"'", null);

        db.close();
        return true;

    }

    /* update sync status
     * This method taking two arguments
     * first one is the id of the name for which
     * we have to update the sync status
     * and the second one is the status that will be changed
     * */
    public boolean updateNotesStatus(String unique_id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_STATUS, status);
        db.update(TABLE_NAME, contentValues, COLUMN_UNIQUE_ID + "=" + "'"+unique_id+"'", null);
        db.close();
        return true;
    }

    /**
     * update notes delete status
     * @param unique_id
     * @param delete_status
     * @return
     */
    public boolean updateNotesDeleteStatus(String unique_id, int delete_status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DELETE_STATUS, delete_status);
        db.update(TABLE_NAME, contentValues, COLUMN_UNIQUE_ID + "=" + "'"+unique_id+"'", null);
        db.close();
        return true;
    }

    /**
     * update notes delete status
     * @param unique_id
     * @param delete_status
     * @return
     */
    public boolean updateNotesUpdateStatus(String unique_id, int update_status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_UPDATE_STATUS, update_status);
        db.update(TABLE_NAME, contentValues, COLUMN_UNIQUE_ID + "=" + "'"+unique_id+"'", null);
        db.close();
        return true;
    }

    /*
     * this method will give us all the notes stored in sqlite
     * */
    public Cursor getNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        //String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_DELETE_STATUS + " !=? " + " ORDER BY " + COLUMN_ID + " ASC;";
        //String sql = "SELECT * FROM " + TABLE_NAME +  " ORDER BY " + COLUMN_ID + " ASC;";
        //rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        //String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_DELETE_STATUS + " != " + NOTES_NOT_DELETED_FROM_SERVER +  " ORDER BY " + COLUMN_ID + " ASC;";
        //Cursor c = db.rawQuery(sql, null);
        //Cursor c = db.rawQuery(sql, new String[] {String.valueOf(NOTES_NOT_DELETED_FROM_SERVER)});
        //Cursor c= db.rawQuery("select * from notes", null);

        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_DELETE_STATUS + " != 1;";
        Cursor c = db.rawQuery(sql, null);

        return c;
    }

    /* get list of unsynced notes
     * this method is for getting all the unsynced name
     * so that we can sync it with database
     * */
    public Cursor getUnsyncedNotes() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_STATUS + " = 0;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    //get list of undeleted notes
    public Cursor getUnDeletedNotes() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_DELETE_STATUS + " = 1;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    //get list of unupdated notes
    public Cursor getUnUpdatedNotes() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_UPDATE_STATUS + " = 1;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }
}
