package com.example.itaigafni.finalproject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

public class FoodDairyActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    TextView date,txtTotal;
    DatePickerDialog datePickerDialog;
    RecyclerView rv;
    FoodDairyDbHelper dbHelper;
    MyFoodAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_dairy);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // initiate the date picker and a button
        date = findViewById(R.id.date);
        rv= findViewById(R.id.rv);
        txtTotal = findViewById(R.id.txtCal);
        final Calendar c = Calendar.getInstance();
        date.setText("Date: "+c.get(Calendar.DAY_OF_MONTH)+"/"+(c.get(Calendar.MONTH)+1)+"/"+c.get(Calendar.YEAR));
        // perform click event on edit text and show calender
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(FoodDairyActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                date.setText("Date: "+dayOfMonth + "/"
                                        + (monthOfYear + 1) + "/" + year);
                                reQueryFood();
                                totalCalories(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FoodDairyActivity.this,AddFoodActivity.class);
                i.putExtra("Date",date.getText().toString().substring(6));
                startActivity(i);
            }
        });
        dbHelper = new FoodDairyDbHelper(this);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        //related to swiping left to delete
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rv);
        totalCalories(date.getText().toString().substring(6));
    }
    //return the selected date food on database
    public Cursor getFoodByDate(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.query(Constant.FoodDairy.TABLE_NAME,
                new String[]{"*"},
                Constant.FoodDairy.DATE+"=?",
                new String[]{date.getText().toString().substring(6)},
                null,
                null,
                null);
    }
    //after the food dairy database was updated
    private void reQueryFood(){
        Calendar c = Calendar.getInstance();
        mAdapter = new MyFoodAdapter(getFoodByDate(),this);
        rv.setAdapter(mAdapter);
    }
    @Override
    protected void onResume() {
        super.onResume();
        reQueryFood();
        totalCalories(date.getText().toString().substring(6));
    }
    //calculate how many calories was consumed for a given date
    private void totalCalories(String date){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(Constant.FoodDairy.TABLE_NAME,
                new String[]{"SUM("+Constant.FoodDairy.CALORIES+"*"+Constant.FoodDairy.AMOUNT+")"},
                Constant.FoodDairy.DATE+"=?",
                new String[]{date},
                null,
                null,
                null);
        if(cursor.moveToNext())
            txtTotal.setText("Total Calories: "+cursor.getInt(0));
    }
    //remove the food from data base when swiping left
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof MyFoodAdapter.MyViewHolder) {
            mAdapter.removeItem(viewHolder.getAdapterPosition());
            reQueryFood();
            totalCalories(date.getText().toString().substring(6));
        }
    }
}
