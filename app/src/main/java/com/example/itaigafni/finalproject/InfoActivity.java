package com.example.itaigafni.finalproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class InfoActivity extends AppCompatActivity implements View.OnClickListener{
    private Button submit;
    private EditText age,weight,height;
    private Spinner gender,activity,goal;
    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        submit = findViewById(R.id.btnSubmit);
        age = findViewById(R.id.editAge);
        weight = findViewById(R.id.editWeight);
        height = findViewById(R.id.editHeight);
        gender = findViewById(R.id.spinGender);
        activity = findViewById(R.id.spinActivity);
        goal = findViewById(R.id.spinGoal);
        //set the adapters for the spinner menus
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array,android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(arrayAdapter);
        arrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.activity_level,android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activity.setAdapter(arrayAdapter);
        arrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.goal,android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        goal.setAdapter(arrayAdapter);
        submit.setOnClickListener(this);
        preferences = getSharedPreferences("myProfile",MODE_PRIVATE);
        //check if a profile has been set already
        if(preferences.contains("age")){
            fillAcivity();
        }

    }

    @Override
    public void onClick(View v) {
        //calculate the calories needed according to the info
        int age=0,height=0;
        float weight=0;
        boolean isOneFieldEmpty = false;
        String message="";
        if(this.age.getText().toString().equals("")){
            message+="Age is illegal\n";
            isOneFieldEmpty=true;
        }
        else{
            age= Integer.parseInt(this.age.getText().toString());
        }
        if(this.weight.getText().toString().equals("")){
            message+="Weight is illegal\n";
            isOneFieldEmpty=true;
        }
        else{
            weight= Float.parseFloat(this.weight.getText().toString());
        }
        if(this.height.getText().toString().equals("")){
            message+="Height is illegal\n";
            isOneFieldEmpty=true;
        }
        else{
            height= Integer.parseInt(this.height.getText().toString());
        }
        //if one of the info filed is empty
        if(isOneFieldEmpty){
            showMessageOKCancel(message);
            return;
        }
        double bmr=calculateBmr(age,height,weight);
        int calories = calculateCalories(bmr);
        int caloriesNeeded = calculateCaloriesNeeded(calories);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("age",age);
        editor.putInt("height",height);
        editor.putFloat("weight",weight);
        editor.putInt("gender",gender.getSelectedItemPosition());
        editor.putInt("activity",activity.getSelectedItemPosition());
        editor.putInt("goal",goal.getSelectedItemPosition());
        editor.apply();
        Intent output= getIntent();
        output.putExtra("calories",caloriesNeeded);
        setResult(Activity.RESULT_OK,output);
        finish();

    }
    //calculate bmr
    private double calculateBmr(int age, int height, float weight){
        if(gender.getSelectedItem().equals("Man"))
            return (66 + (13.7*weight) + (5*height) - (6.8*age));
        else
            return (655 + (9.6 *weight) + (1.8*height) - (4.7*age));
    }
    private int calculateCaloriesNeeded(int calories){
        int factor=0;
        if(goal.getSelectedItem().equals("Loss Weight"))
            factor = -500;
        else if(goal.getSelectedItem().equals("Gain Weight"))
            factor = 500;
        return calories+factor;
    }
    //calculate calories according to bmr and exercise level
    private int calculateCalories(double bmr){
        double factor=0;
        if(activity.getSelectedItem().equals("Rarely Exercise"))
            factor=1.2;
        else if(activity.getSelectedItem().equals("Exercise 1 To 3 Days Per Week"))
            factor=1.375;
        else if(activity.getSelectedItem().equals("Exercise 3 To 5 Days Per Week"))
            factor = 1.55;
        else if(activity.getSelectedItem().equals("Exercise 6 To 7 Days Per Week"))
            factor = 1.725;
        return (int)(bmr*factor);

    }

    private void showMessageOKCancel(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .create()
                .show();
    }
    //fill the profile that has been saved
    private void fillAcivity(){
        age.setText(preferences.getInt("age",0)+"");
        weight.setText(preferences.getFloat("weight",0)+"");
        height.setText(preferences.getInt("height",0)+"");
        gender.setSelection(preferences.getInt("gender",0));
        activity.setSelection(preferences.getInt("activity",0));
        goal.setSelection(preferences.getInt("goal",0));
    }

}
