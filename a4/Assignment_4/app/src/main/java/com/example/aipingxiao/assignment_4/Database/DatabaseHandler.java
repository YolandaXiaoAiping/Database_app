package com.example.aipingxiao.assignment_4.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.nfc.Tag;
import android.util.Log;

import com.example.aipingxiao.assignment_4.BuildConfig;
import com.example.aipingxiao.assignment_4.PersonInformation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * Created by Aiping Xiao on 2016-02-11.
 */
public class DatabaseHandler extends SQLiteOpenHelper{
    String Tag = "DatabaseHandler";

    private static final String TABLE_NAME = "person";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_INTRO = "intro";
    private static final String COLUMN_IMAGEURL = "imageurl";

    private static final String DATABASE_NAME = "person.db";
    private static final int DATABASE_VERSION = 1;
    private static String DATABASE_PATH = null;
    private static String DATABASE_BACKUP_PATH = null;

    private static DatabaseHandler databaseHandler = null;
    private static Context mContext = null;

    private static final String DATABASE_CREATE = "CREATE TABLE "
            +TABLE_NAME
            +" ("
            +COLUMN_ID +" integer primary key autoincrement,"
            +COLUMN_NAME+" text not null,"
            +COLUMN_INTRO+" text not null,"
            +COLUMN_IMAGEURL +" text not null"
            +");";


    private DatabaseHandler(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        mContext = context;
        databaseHandler = this;
    }

    //get the database
    public static DatabaseHandler getHandler(Context context){
        if (databaseHandler == null){
            return initialHandler(context);
        }
        return databaseHandler;
    }

    //initialization
    public static DatabaseHandler initialHandler(Context context){
        databaseHandler = new DatabaseHandler(context);

        DATABASE_BACKUP_PATH = "/data/data/"+mContext.getPackageName()+"/database/backup/";
        File backupDir = new File(DATABASE_BACKUP_PATH);
        if (!backupDir.exists()){
            backupDir.mkdir();
        }

        SQLiteDatabase db = databaseHandler.getReadableDatabase();
        DATABASE_PATH = db.getPath();
        db.close();
        return databaseHandler;
    }

    public static String getDBPath(){
        return DATABASE_PATH;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (BuildConfig.DEBUG) Log.w(Tag,
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    //Delete the whole database
    public void deleteDatabase(){
        mContext.deleteDatabase(DATABASE_NAME);
        databaseHandler = null;
        DatabaseHandler.initialHandler(mContext);
    }

    //export database ,the database will be deleted
    public void exportDatabase(String name)throws IOException{
        databaseHandler.close();
        String outFileName = DATABASE_BACKUP_PATH+name+".db";
        File fromFile = new File(DATABASE_PATH);
        File toFile = new File(outFileName);
        FileInputStream fis = new FileInputStream(fromFile);
        FileOutputStream fos = new FileOutputStream(toFile);
        FileChannel fromChannel = null;
        FileChannel toChannel = null;
        fromChannel = fis.getChannel();
        toChannel = fos.getChannel();
        fromChannel.transferTo(0,fromChannel.size(),toChannel);
        if (fromChannel!=null){
            fromChannel.close();
        }
        if (toChannel!=null){
            toChannel.close();
        }
        deleteDatabase();
    }

    public void importDatabase(String name)throws IOException{
        databaseHandler.close();
        String inFileName = "";
        if (name.contains(".db")){
            inFileName = DATABASE_BACKUP_PATH+name;
        }else {
            inFileName = DATABASE_BACKUP_PATH+name+".db";
        }

        File fromFile = new File(inFileName);
        File toFile = new File(DATABASE_PATH);
        FileInputStream fis = new FileInputStream(fromFile);
        FileOutputStream fos = new FileOutputStream(toFile);
        FileChannel fromChannel = null;
        FileChannel toChannel = null;
        fromChannel = fis.getChannel();
        toChannel = fos.getChannel();
        fromChannel.transferTo(0,fromChannel.size(),toChannel);
        if (fromChannel!=null){
            fromChannel.close();
        }
        if (toChannel!=null){
            toChannel.close();
        }
    }

    //input single item into database
    public void addValue(PersonInformation pin){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME,pin.getName());
        values.put(COLUMN_INTRO,pin.getIntro());
        values.put(COLUMN_IMAGEURL,pin.getImageUrl());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    //get single item from database
    public PersonInformation getValue(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{
                COLUMN_ID,
                COLUMN_NAME,
                COLUMN_INTRO,
                COLUMN_IMAGEURL
        }
                , COLUMN_ID + "=?", new String[]{
                String.valueOf(id)
        }
                , null, null, null, null);

        PersonInformation pin = null;
        if (cursor.moveToFirst()) {
            pin = new PersonInformation(
                    Long.parseLong(cursor.getString(0)),    // ID
                    cursor.getString(1),                    // name
                    cursor.getString(2),                    // intro
                    cursor.getString(3)                     // ImageURL
            );
        }
        db.close();
        return pin;
    }

    //check if the item already exists
    public boolean isInDatabase(String name){
        String CHECK_DB = "SELECT * FROM"+TABLE_NAME+"WHERE"+COLUMN_NAME+"="+name;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{
                COLUMN_ID,
                COLUMN_NAME,
                COLUMN_INTRO,
                COLUMN_IMAGEURL
        }
                , COLUMN_NAME + "=?", new String[]{
                name
        }
                , null, null, null, null);
        int returnCount = cursor.getCount();
        cursor.close();
        db.close();

        // return count
        if (returnCount > 0) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<PersonInformation> getAllValue(){
        ArrayList<PersonInformation> arrayList = new ArrayList<PersonInformation>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{
                COLUMN_ID,
                COLUMN_NAME,
                COLUMN_INTRO,
                COLUMN_IMAGEURL
        }
                , null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                PersonInformation tableEntry = new PersonInformation(
                        Long.parseLong(cursor.getString(0)),    // ID
                        cursor.getString(1),                    // name
                        cursor.getString(2),                    // intro
                        cursor.getString(3)                     // ImageURL
                );
                arrayList.add(tableEntry);
            } while (cursor.moveToNext());
        }

        db.close();
        return arrayList;

    }

    //get the number of items in the database
    public int getValuesCount(){
        String COUNT_DB = "SELECT * FROM "+TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(COUNT_DB, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    //update single item
    public int updateValue(PersonInformation pin){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME,pin.getName());
        values.put(COLUMN_INTRO,pin.getIntro());
        values.put(COLUMN_IMAGEURL, pin.getImageUrl());
        int result = db.update(TABLE_NAME, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(pin.getId())});

        db.close();
        return result;
    }

    //delete single item
    public void deleteValue(PersonInformation personInformation){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,COLUMN_ID+"=?",new
        String[]{
                String.valueOf(personInformation.getId())
        });
        db.close();
    }


}
