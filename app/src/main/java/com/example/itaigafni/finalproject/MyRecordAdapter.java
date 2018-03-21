package com.example.itaigafni.finalproject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * Created by itaigafni on 11/02/2018.
 */

public class MyRecordAdapter extends RecyclerView.Adapter<MyRecordAdapter.MyViewHolder> {
    private Cursor mCursor;
    private RecordsActivity mContext;

    MyRecordAdapter(RecordsActivity context, Cursor cursor){
        this.mContext = context;
        this.mCursor = cursor;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View contactView = inflater.inflate(R.layout.single_row,parent,false);
        return new MyViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        final int id = mCursor.getInt(mCursor.getColumnIndex(Constant.Records._ID));
        holder.txtKm.setText(new DecimalFormat("##.##")
                .format(mCursor.getDouble(mCursor.getColumnIndex(Constant.Records.KM))));
        holder.txtTime.setText(mCursor.getString(mCursor.getColumnIndex(Constant.Records.TIME)));
        holder.txtDate.setText(mCursor.getString(mCursor.getColumnIndex(Constant.Records.DATE)));
        holder.txtDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // the delete query
                RecordsDbHelper dbHelper = new RecordsDbHelper(mContext);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                int x=db.delete(Constant.Records.TABLE_NAME,
                        Constant.Records._ID +"=?"
                        ,new String[]{id+""});
                mContext.refreshTable();
            }
        });


    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void changeCursor(Cursor cursor){
        mCursor.close();
        mCursor=cursor;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtKm,txtDate,txtTime,txtDel;
        public MyViewHolder(View itemView) {
            super(itemView);
            txtKm = itemView.findViewById(R.id.txtKm);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtDel = itemView.findViewById(R.id.txtDel);
        }
    }
}
