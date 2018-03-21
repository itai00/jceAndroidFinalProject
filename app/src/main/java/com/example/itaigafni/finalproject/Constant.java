package com.example.itaigafni.finalproject;

import android.provider.BaseColumns;

/**
 * Created by itaigafni on 11/02/2018.
 */

public class Constant {
    private Constant(){
        throw new AssertionError("Can't create constants class");
    }
    public static abstract class Records implements BaseColumns {
        public static final String TABLE_NAME = "records";
        public static final String KM = "km";
        public static final String TIME = "time";
        public static final String DATE = "date";
    }

    public static abstract class Food implements BaseColumns{
        public static final String TABLE_NAME = "food";
        public static final String NAME= "name";
        public static final String CALORIES = "cal";
        public static final String FAT = "fat";
        public static final String PROTEIN = "protein";
        public static final String TYPE = "type";
    }

    public static abstract class FoodDairy implements BaseColumns{
        public static final String TABLE_NAME = "dairy";
        public static final String NAME= "name";
        public static final String CALORIES = "cal";
        public static final String TIME = "time";
        public static final String DATE = "date";
        public static final String AMOUNT = "amount";
        public static final String TYPE = "type";
    }

}
