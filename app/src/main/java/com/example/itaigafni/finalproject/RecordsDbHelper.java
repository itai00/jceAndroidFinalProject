package com.example.itaigafni.finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by itaigafni on 11/02/2018.
 */

public class RecordsDbHelper extends SQLiteOpenHelper {
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE "+ Constant.Records.TABLE_NAME + " ("+
                    Constant.Records._ID+    " INTEGER PRIMARY KEY,"+
                    Constant.Records.DATE+  " TEXT NOT NULL,"+
                    Constant.Records.KM+ " REAL NOT NULL,"+
                    Constant.Records.TIME+ " TEXT NOT NULL"+
                    ");";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS "+ Constant.Records.TABLE_NAME;
    public static final int DATABASE_VERSION =2;
    public static final String DATABASE_NAME = "records.db";
    public RecordsDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void insertToTable(float km, String date, String time){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constant.Records.DATE,date);
        values.put(Constant.Records.KM,km);
        values.put(Constant.Records.TIME,time);
        db.insert(Constant.Records.TABLE_NAME, null, values);
        db.close();
    }
}
