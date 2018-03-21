package com.example.itaigafni.finalproject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddFoodActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, SearchView.OnSuggestionListener, View.OnClickListener {
    SearchView searchView;
    FoodDbHelper dbHelper;
    SimpleCursorAdapter adapter;
    TextView txtCal,txtFat,txtPro,txtPlus,txtMinus,txtAmount,txtAdd,txtType;
    EditText editCal,editFat,editPro;
    Button btnSub;
    LinearLayout linearLayout;
    Spinner spinner;
    boolean isAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);
        searchView = findViewById(R.id.sv);
        txtCal = findViewById(R.id.txtCal);
        txtFat = findViewById(R.id.txtFat);
        txtPro = findViewById(R.id.txtProtein);
        txtAmount = findViewById(R.id.txtAmount);
        txtPlus = findViewById(R.id.txtPlus);
        txtMinus = findViewById(R.id.txtMinus);
        txtAmount = findViewById(R.id.txtAmount);
        txtAdd = findViewById(R.id.txtAdd);
        btnSub = findViewById(R.id.btnSubmit);
        editCal = findViewById(R.id.editCal);
        editFat = findViewById(R.id.editFat);
        editPro = findViewById(R.id.editProtein);
        editPro.setImeOptions(EditorInfo.IME_ACTION_DONE);
        linearLayout = findViewById(R.id.linearLayout);
        spinner = findViewById(R.id.typeSpinner);
        txtType = findViewById(R.id.txtType);
        //to display if add to foo dairy database
        linearLayout.setVisibility(View.INVISIBLE);
        txtAmount.setVisibility(View.INVISIBLE);
        txtMinus.setVisibility(View.INVISIBLE);
        txtPlus.setVisibility(View.INVISIBLE);
        btnSub.setVisibility(View.INVISIBLE);
        // if add button was pressed
        isAdd=false;

        txtPlus.setOnClickListener(this);
        txtMinus.setOnClickListener(this);
        txtAdd.setOnClickListener(this);
        btnSub.setOnClickListener(this);

        dbHelper = new FoodDbHelper(this);
        //type of food
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.type,android.R.layout.simple_spinner_item);
        spinner.setAdapter(arrayAdapter);

        final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        final int[] to = new int[]{android.R.id.text1};
        final String[] from = new String[]{Constant.Food.NAME};
        //to be used for the autocomplete list
        adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_dropdown_item_1line,
                null,
                from,
                to,
                0);
        searchAutoComplete.setAdapter(adapter);
        searchView.setOnQueryTextListener(this);
        searchView.setOnSuggestionListener(this);
        searchView.setOnClickListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        //only search view will appear when typing
        linearLayout.setVisibility(View.INVISIBLE);
        txtAmount.setVisibility(View.INVISIBLE);
        txtMinus.setVisibility(View.INVISIBLE);
        txtPlus.setVisibility(View.INVISIBLE);
        btnSub.setVisibility(View.INVISIBLE);
        adapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                if (constraint == null  ||  constraint.length () == 0){
                    return db.query(Constant.Food.TABLE_NAME,
                            new String[]{"*"},
                            null,
                            null,
                            null,
                            null,
                            Constant.Food.NAME);
                }
                else{
                    //check if a food start with or end with constraint
                    return db.query(Constant.Food.TABLE_NAME,
                            new String[]{"*"},
                            Constant.Food.NAME+" like ? ",
                            new String[]{"%"+constraint.toString()+"%"},
                            null,
                            null,
                            Constant.Food.NAME);
                }
            }
        });
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return false;
    }
    //an option was selected
    @Override
    public boolean onSuggestionClick(int position) {
        Cursor c = adapter.getCursor();
        c.moveToPosition(position);
        searchView.clearFocus();
        searchView.setQuery(c.getString(c.getColumnIndex(Constant.Food.NAME)),false);
        txtCal.setText(c.getInt(c.getColumnIndex(Constant.Food.CALORIES))+"");
        txtFat.setText(c.getInt(c.getColumnIndex(Constant.Food.FAT))+"");
        txtPro.setText(c.getInt(c.getColumnIndex(Constant.Food.PROTEIN))+"");
        txtType.setText(c.getString(c.getColumnIndex(Constant.Food.TYPE)));
        //if add button was pressed disappear the add related stuff
        if(isAdd==true){
            isAdd=false;
            editPro.setVisibility(View.GONE);
            editFat.setVisibility(View.GONE);
            editCal.setVisibility(View.GONE);
            txtCal.setVisibility(View.VISIBLE);
            txtFat.setVisibility(View.VISIBLE);
            txtPro.setVisibility(View.VISIBLE);
            txtType.setVisibility(View.VISIBLE);
        }
        linearLayout.setVisibility(View.VISIBLE);
        txtPlus.setVisibility(View.VISIBLE);
        txtMinus.setVisibility(View.VISIBLE);
        btnSub.setVisibility(View.VISIBLE);
        txtAmount.setVisibility(View.VISIBLE);
        txtAmount.setText("1");
        btnSub.setOnClickListener(this);
        return true;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==searchView.getId()){
            //can press anywhere on the search view
            searchView.setIconified(false);
        }
        if(v.getId()==txtPlus.getId()){
            //check if the amount is float or integer
            try{
                int amount = Integer.parseInt(txtAmount.getText().toString());
                amount++;
                txtAmount.setText(amount+"");
            }
            catch (NumberFormatException e){
                float amount = Float.parseFloat(txtAmount.getText().toString());
                if(amount==0.5)
                    txtAmount.setText("1");
                else {
                    amount = amount * 2;
                    txtAmount.setText(amount + "");
                }
            }
        }
        else if(v.getId()==txtMinus.getId()){
            try {
                int amount = Integer.parseInt(txtAmount.getText().toString());
                if(amount==1)
                    txtAmount.setText("0.5");
                else{
                    amount--;
                    txtAmount.setText(amount+"");
                }
            }
            catch (NumberFormatException e){
                float amount = Float.parseFloat(txtAmount.getText().toString());
                amount= amount/2;
                txtAmount.setText(amount+"");
            }
        }
        else if(v.getId()==txtAdd.getId()){
            searchView.clearFocus();
            if(searchView.getQuery().toString().equals(""))
                return;
            if(checkIfFoodExist(searchView.getQuery().toString())) {
                Toast.makeText(this,"Food Exist In DataBase",Toast.LENGTH_LONG).show();
                return;
            }
            isAdd=true;
            linearLayout.setVisibility(View.VISIBLE);
            txtCal.setVisibility(View.GONE);
            txtFat.setVisibility(View.GONE);
            txtPro.setVisibility(View.GONE);
            txtType.setVisibility(View.GONE);
            editPro.setVisibility(View.VISIBLE);
            editFat.setVisibility(View.VISIBLE);
            editCal.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);
            btnSub.setVisibility(View.VISIBLE);
            txtPlus.setVisibility(View.INVISIBLE);
            txtAmount.setVisibility(View.INVISIBLE);
            txtMinus.setVisibility(View.INVISIBLE);
            editCal.requestFocus();
        }
        else if(v.getId()==btnSub.getId()){
            if(isAdd){
                btnSub.clearFocus();
                //insert the food to the food database
                dbHelper.insertFood(searchView.getQuery().toString(),
                        Integer.parseInt(editCal.getText().toString()),
                        Integer.parseInt(editFat.getText().toString()),
                        Integer.parseInt(editPro.getText().toString()),
                        spinner.getSelectedItem().toString()
                );
                this.recreate();
                searchView.setQuery("",false);
                searchView.clearFocus();
                searchView.setIconified(true);

            }
            else {
                //insert the food to food dairy
                FoodDairyDbHelper dairyDbHelper = new FoodDairyDbHelper(this);
                Intent i = getIntent();
                String date = i.getStringExtra("Date");
                dairyDbHelper.insertToDairy(searchView.getQuery().toString(),
                        Integer.parseInt(txtCal.getText().toString()),
                        new SimpleDateFormat("HH:mm:ss").format(new Date()),
                        date,
                        Float.parseFloat(txtAmount.getText().toString()),
                        txtType.getText().toString());
                this.recreate();
                searchView.setQuery("",false);
                searchView.clearFocus();
                searchView.setIconified(true);
            }
        }
    }
    //check if the food is the food database
    boolean checkIfFoodExist(String foodName){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(Constant.Food.TABLE_NAME,
                new String[]{Constant.Food.NAME},
                Constant.Food.NAME+"=?",
                new String[]{foodName},
                null,
                null,
                null);
        if(cursor.getCount()==0)
            return false;
        return true;
    }
}
