package com.example.itaigafni.finalproject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private TextView calories,calConsumed;
    private ProgressBar progressBar;
    private final int INFO_ACTIVITY_REQUEST_CODE=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button info=findViewById(R.id.btnInfo);
        calories = findViewById(R.id.txtCalories);
        calConsumed = findViewById(R.id.txtConsumed);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(),InfoActivity.class);
                startActivityForResult(i,INFO_ACTIVITY_REQUEST_CODE);
            }
        });
        progressBar = findViewById(R.id.progressBar);
        Button run = findViewById(R.id.btnRun);
        run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(),RunActivity.class);
                startActivity(i);
            }
        });
        Button dairy = findViewById(R.id.btnDairy);
        dairy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(),FoodDairyActivity.class);
                startActivity(i);
            }
        });

    }

    //fill the calories consumed and needed from profile and dairy
    private void setInfo() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences("myProfile",MODE_PRIVATE);
        int calories = preferences.getInt("calories",0);
        if(calories == 0){
            this.calories.setText("Please Fill Your Profile");
        }
        else{
            this.calories.setText("Calories Needed: "+calories);
            this.progressBar.setMax(calories);
        }
        Calendar calendar = Calendar.getInstance();
        String date = calendar.get(Calendar.DAY_OF_MONTH)+"/"+(calendar.get(Calendar.MONTH)+1)+"/"+calendar.get(Calendar.YEAR);
        //get to current date consumed calories
        int consumedCal = totalCalories(date);
        calConsumed.setText("Consumed Calories: "+consumedCal);
        this.progressBar.setProgress(consumedCal);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setInfo();
    }
    //wait for response from info activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == INFO_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            this.calories.setText("Calories Needed: "+data.getIntExtra("calories",0));
            SharedPreferences.Editor editor = getSharedPreferences("myProfile",MODE_PRIVATE).edit();
            editor.putInt("calories",data.getIntExtra("calories",0));
            editor.apply();
        }
    }
    //sum the calories for a given date
    private int totalCalories(String date){
        FoodDairyDbHelper dbHelper = new FoodDairyDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(Constant.FoodDairy.TABLE_NAME,
                new String[]{"SUM("+Constant.FoodDairy.CALORIES+"*"+Constant.FoodDairy.AMOUNT+")"},
                Constant.FoodDairy.DATE+"=?",
                new String[]{date},
                null,
                null,
                null);
        if(cursor.moveToNext())
            return cursor.getInt(0);
        return 0;
    }
}
