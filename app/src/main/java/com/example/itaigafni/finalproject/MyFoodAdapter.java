package com.example.itaigafni.finalproject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by itaigafni on 07/03/2018.
 */

public class MyFoodAdapter extends RecyclerView.Adapter<MyFoodAdapter.MyViewHolder>  {
    Cursor mCursor;
    FoodDairyActivity mContext;

    MyFoodAdapter(Cursor mCursor,FoodDairyActivity mContext){
        this.mContext=mContext;
        this.mCursor=mCursor;
    }

    @Override
    public MyFoodAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View contactView = inflater.inflate(R.layout.food_list_item,parent,false);
        return new MyFoodAdapter.MyViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(MyFoodAdapter.MyViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.txtName.setText(mCursor.getString(mCursor.getColumnIndex(Constant.FoodDairy.NAME)));
        holder.txtCal.setText("Calories: "+mCursor.getInt(mCursor.getColumnIndex(Constant.FoodDairy.CALORIES)));
        holder.txtAmount.setText("Amount: "+mCursor.getString(mCursor.getColumnIndex(Constant.FoodDairy.AMOUNT)));
        holder.txtTime.setText(mCursor.getString(mCursor.getColumnIndex(Constant.FoodDairy.TIME)));
        String type = mCursor.getString(mCursor.getColumnIndex(Constant.FoodDairy.TYPE));
        if(type.equals("Beverage")){
            holder.img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_drink,null));
        }
        else{
            holder.img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.food,null));
        }


    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtName,txtCal,txtAmount,txtTime;
        RelativeLayout viewBackground, viewForeground;
        ImageView img;
        public MyViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.food_name);
            txtCal = itemView.findViewById(R.id.food_cal);
            txtAmount = itemView.findViewById(R.id.food_quantity);
            img = itemView.findViewById(R.id.food_photo);
            txtTime = itemView.findViewById(R.id.food_time);
            viewBackground = itemView.findViewById(R.id.view_background);
            viewForeground = itemView.findViewById(R.id.view_foreground);
        }
    }
    public void removeItem(int position){
        mCursor.moveToPosition(position);
        final int id = mCursor.getInt(mCursor.getColumnIndex(Constant.Records._ID));
        FoodDairyDbHelper dbHelper = new FoodDairyDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(Constant.FoodDairy.TABLE_NAME,
                Constant.Records._ID +"=?"
                ,new String[]{id+""});
    }
}
