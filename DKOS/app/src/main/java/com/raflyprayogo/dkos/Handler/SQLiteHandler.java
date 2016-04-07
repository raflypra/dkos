package com.raflyprayogo.dkos.Handler;

/**
 * Created by raflyprayogo on 2/23/2016.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "android_dkos";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        //create table user
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + ts.TABLE_USER + "("
                + ts.KEY_ID + " INTEGER PRIMARY KEY," + ts.KEY_NAME + " TEXT,"
                + ts.KEY_EMAIL + " TEXT UNIQUE," + ts.KEY_UID + " TEXT,"
                + ts.KEY_CREATED_AT + " TEXT," + ts.KEY_PICTURE + " TEXT,"+ ts.KEY_USERNAME + " TEXT,"
                + ts.KEY_GROUPNAME+" TEXT,"
                + ts.KEY_GROUPID+" INTEGER"
                + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        String CREATE_EXPENSE_TABLE = "CREATE TABLE "+ts.TABLE_EXPENSE+"("
                + ts.EXPENSE_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ts.EXPENSE_TITLE +" TEXT,"
                + ts.EXPENSE_DESC +" TEXT,"
                + ts.EXPENSE_COST +" TEXT,"
                + ts.EXPENSE_ISBIG +" INTEGER,"
                + ts.EXPENSE_STATUS +" INTEGER,"
                + ts.EXPENSE_CD +" TEXT"
                +")";
        db.execSQL(CREATE_EXPENSE_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + ts.TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + ts.TABLE_EXPENSE);

        // Create tables again
        onCreate(db);
    }

    public void addUser(String name, String email, String uid, String created_at, String picture, String username, String group_name, String group_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ts.KEY_NAME, name); // Name
        values.put(ts.KEY_EMAIL, email); // Email
        values.put(ts.KEY_UID, uid); // Email
        values.put(ts.KEY_CREATED_AT, created_at); // Created At
        values.put(ts.KEY_PICTURE, picture); // Created At
        values.put(ts.KEY_USERNAME, username); // Created At
        values.put(ts.KEY_GROUPNAME, group_name); // Created At
        values.put(ts.KEY_GROUPID, group_id); // Created At

        // Inserting Row
        long id = db.insert(ts.TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + ts.TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("uid", cursor.getString(3));
            user.put("created_at", cursor.getString(4));
            user.put("picture", cursor.getString(5));
            user.put("username", cursor.getString(6));
            user.put("group_name", cursor.getString(7));
            user.put("group_id", cursor.getString(8));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(ts.TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

    //    ======================================== EXPENSE
//    public void addExpense() {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//
//        values.put(ts.EXPENSE_TITLE, name);
//        values.put(ts.EXPENSE_DESC, name);
//        values.put(ts.EXPENSE_COST, name);
//        values.put(ts.EXPENSE_ISBIG, name);
//        values.put(ts.EXPENSE_STATUS, "1");
//        values.put(ts.EXPENSE_CD, name);
//
//        long id = db.insert(ts.TABLE_EXPENSE, null, values);
//        db.close();
//    }

}
