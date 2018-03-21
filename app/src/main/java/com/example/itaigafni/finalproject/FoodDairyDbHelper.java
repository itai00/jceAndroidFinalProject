package com.example.itaigafni.finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by itaigafni on 06/03/2018.
 */

public class FoodDairyDbHelper extends SQLiteOpenHelper {
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE "+ Constant.FoodDairy.TABLE_NAME + " ("+
                    Constant.FoodDairy._ID+    " INTEGER PRIMARY KEY,"+
                    Constant.FoodDairy.CALORIES+ " INTEGER NOT NULL,"+
                    Constant.FoodDairy.NAME+ " TEXT NOT NULL,"+
                    Constant.FoodDairy.TIME+" TEXT NOT NULL,"+
                    Constant.FoodDairy.DATE+" TEXT NOT NULL,"+
                    Constant.FoodDairy.AMOUNT+" FLOAT NOT NULL,"+
                    Constant.FoodDairy.TYPE+ " TEXT NOT NULL"+
                    ");";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS "+ Constant.FoodDairy.TABLE_NAME;
    public static final int DATABASE_VERSION =5;
    public static final String DATABASE_NAME = "dairy.db";

    public FoodDairyDbHelper(Context context) {
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

    public void insertToDairy(String name,int cal,String time,String date,float amount,String type){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constant.FoodDairy.NAME,name);
        values.put(Constant.FoodDairy.CALORIES,cal);
        values.put(Constant.FoodDairy.TIME,time);
        values.put(Constant.FoodDairy.DATE,date);
        values.put(Constant.FoodDairy.AMOUNT,amount);
        values.put(Constant.FoodDairy.TYPE,type);
        db.insert(Constant.FoodDairy.TABLE_NAME, null, values);
        db.close();
    }
}
