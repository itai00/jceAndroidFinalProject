package com.example.itaigafni.finalproject;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by itaigafni on 11/01/2018.
 */

public class SongPlayer implements MediaPlayer.OnPreparedListener,MediaPlayer.OnCompletionListener {
    private Cursor mCursor;
    private Context mContext;
    private MediaPlayer player;
    private boolean isInit;
    private int songPos;
    String title;
    public SongPlayer(Context context){
        mContext=context;
        mCursor=null;
        songPos=0;
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        isInit=false;
        title="";

    }
    public void init(){
        //check if the user pressed play for the first time
        if(!isInit) {
            querySongs();
            isInit=true;
        }
    }
    //gest all song from phone
    private void querySongs(){
        ContentResolver musicResolver = mContext.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        mCursor = musicResolver.query(musicUri,
                new String[]{MediaStore.Video.Media._ID, MediaStore.Video.Media.TITLE},
                null,
                null,
                MediaStore.Video.Media.TITLE);

    }

    public void play(){
        if(mCursor.getPosition()==songPos) {
            player.start();
            return;
        }
        if(mCursor==null)
            return;
        //no more songs in the phone
        if(!mCursor.moveToPosition(songPos)) {
            Toast.makeText(mContext,"No Song To Play",Toast.LENGTH_LONG).show();
            return;
        }
        if(player.isPlaying())
            player.reset();
        //start playing songs
        long id = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Video.Media._ID));
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                id);
        try{
            player.setDataSource(mContext, trackUri);
            player.prepareAsync();
        }
        catch(Exception e){
            Log.e("MUSIC", "Error setting data source", e);
        }

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    public String getTitle(){
        if(mCursor==null||mCursor.isAfterLast()||mCursor.isBeforeFirst())
            return null;
        return mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.TITLE));

    }

    public void stop(){
        if(player.isPlaying())
            player.pause();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(songPos+1==mCursor.getCount()){
            Toast.makeText(mContext,"No Song To Play",Toast.LENGTH_LONG).show();
            player.reset();
            return;
        }
        play();
    }

    public void next(){
        //ckeck if its the last song
        if(songPos+1==mCursor.getCount()){
            Toast.makeText(mContext,"No Song To Play",Toast.LENGTH_LONG).show();
            player.reset();
            return;
        }
        songPos++;
        play();
    }
    public void prev(){
        //check if its the last song
        if(songPos==0) {
            Toast.makeText(mContext,"No Song To Play",Toast.LENGTH_LONG).show();
            player.reset();
            return;
        }
        songPos--;
        play();
    }
    public void close(){
        player.reset();
    }
}
