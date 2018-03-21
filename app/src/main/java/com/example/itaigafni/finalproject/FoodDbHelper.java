package com.example.itaigafni.finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by itaigafni on 01/03/2018.
 */

public class FoodDbHelper extends SQLiteOpenHelper {
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE "+ Constant.Food.TABLE_NAME + " ("+
                    Constant.Food._ID+    " INTEGER PRIMARY KEY,"+
                    Constant.Food.CALORIES+ " INTEGER NOT NULL,"+
                    Constant.Food.FAT+ " INTEGER NOT NULL,"+
                    Constant.Food.NAME+ " TEXT NOT NULL,"+
                    Constant.Food.PROTEIN+ " INTEGER NOT NULL,"+
                    Constant.Food.TYPE+" TEXT NOT NULL"+
                    ");";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS "+ Constant.Food.TABLE_NAME;
    public static final int DATABASE_VERSION =4;
    public static final String DATABASE_NAME = "food.db";

    public FoodDbHelper(Context context) {
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
    public void insertFood(String name, int calories, int fat, int protein, String type){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constant.Food.NAME,name);
        values.put(Constant.Food.CALORIES,calories);
        values.put(Constant.Food.PROTEIN,protein);
        values.put(Constant.Food.FAT,fat);
        values.put(Constant.Food.TYPE,type);
        db.insert(Constant.Food.TABLE_NAME, null, values);
        db.close();
    }

}
