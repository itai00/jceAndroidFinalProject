package com.example.itaigafni.finalproject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

public class RecordsActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnSortKm;
    private RecordsDbHelper dbHelper;
    private MyRecordAdapter mAdapter;
    private boolean isSort;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);
        this.btnSortKm = findViewById(R.id.btnSortKm);
        this.btnSortKm.setOnClickListener(this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        dbHelper = new RecordsDbHelper(this);
        mAdapter = new MyRecordAdapter(this,queryTable());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        isSort=false;

    }

    public Cursor queryTable(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] cols = {"*"};
        Cursor cursor = db.query(Constant.Records.TABLE_NAME,
                cols,
                null,
                null,
                null,
                null,
                null
                );
        return cursor;
    }

    public Cursor sortByKm() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] cols = {"*"};
        Cursor cursor = db.query(Constant.Records.TABLE_NAME,
                cols,
                null,
                null,
               null,
                null,
                Constant.Records.KM+","+Constant.Records.TIME
        );
        return cursor;
    }

    public void refreshTable(){
        Cursor cursor=null;
        if(!isSort)
            cursor = queryTable();
        if(isSort)
            cursor = sortByKm();
        mAdapter.changeCursor(cursor);
    }

    @Override
    public void onClick(View v) {
        isSort=true;
        refreshTable();

    }
}
