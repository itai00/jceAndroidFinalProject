package com.example.itaigafni.finalproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RunActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener, View.OnClickListener {
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location lastLocation;
    private TextView km,songName;
    private Chronometer chronometer;
    private boolean isStart,isPlay;
    private Button start,play,prev,next,reset,records;
    private final int SAMPLE = 10 , GPS_REQUEST=1,READ_STORAGE_REQUEST=2,DISTANCE=1000;
    private SongPlayer player;
    private long timeWhenStopped;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
        km = findViewById(R.id.txtKm);
        chronometer = findViewById(R.id.timer);
        start = findViewById(R.id.btnStart);
        play = findViewById(R.id.btnPlay);
        prev = findViewById(R.id.btnPrev);
        next = findViewById(R.id.btnNext);
        reset = findViewById(R.id.btnReset);
        records = findViewById(R.id.btnRecords);
        play.setOnClickListener(this);
        prev.setOnClickListener(this);
        next.setOnClickListener(this);
        records.setOnClickListener(this);
        songName = findViewById(R.id.txtSongName);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        createLocationRequest();
        start.setOnClickListener(this);
        isStart=false;
        lastLocation = null;
        km.setText("Km: 0");
        reset.setOnClickListener(this);
        //is player started working
        isPlay=false;
        player = new SongPlayer(this);
        //use to on Chronometer
        timeWhenStopped=0;

    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, GPS_REQUEST);
            return;

        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(SAMPLE);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onLocationChanged(Location location) {
        if(lastLocation==null) {
            lastLocation = location;
            return;
        }
        //calculate the distance the user has passed
        float km = Float.parseFloat(this.km.getText().toString().substring(3));
        float distance = location.distanceTo(lastLocation)/DISTANCE;
        km+=distance;
        this.km.setText("Km: "+new DecimalFormat("##.##").format(km));
        lastLocation=location;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == play.getId()){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_REQUEST);
                return;
            }
            if(!isPlay) {
                player.init();
                player.play();
                String title = player.getTitle();
                if(title!=null) {
                    songName.setText(player.getTitle());
                    play.setBackgroundResource(android.R.drawable.ic_media_pause);
                    isPlay = true;
                }
            }
            else{
                play.setBackgroundResource(android.R.drawable.ic_media_play);
                player.stop();
                isPlay=false;
            }
        }
        else if(v.getId()==next.getId()) {
            if(!isPlay)
                return;
            player.next();
            String title = player.getTitle();
            if(title!=null)
                songName.setText(player.getTitle());
        }
        else if(v.getId()==prev.getId()){
            if(!isPlay)
                return;
            player.prev();
            String title = player.getTitle();
            if(title!=null)
                songName.setText(player.getTitle());
        }
        //save and reset run
        else if(v.getId() == reset.getId()){
            if(start.getText().toString().equals("stop")){
                Toast.makeText(this,"Please Stop Runing Before Ending Run",Toast.LENGTH_LONG).show();
                return;
            }
            //save the run to database
            String time = chronometer.getText().toString();
            float km = Float.parseFloat(this.km.getText().toString().substring(3));
            String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            RecordsDbHelper db = new RecordsDbHelper(this);
            db.insertToTable(km,date,time);
            chronometer.setBase(SystemClock.elapsedRealtime());
            timeWhenStopped = 0;
            chronometer.stop();
            this.km.setText("Km: 0");
        }
        //start the run
        else if(v.getId()==start.getId()&&!isStart){
            start.setText("stop");
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION}, GPS_REQUEST);
            }
            else{
                mGoogleApiClient.connect();
            }
            isStart=true;
            chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
            chronometer.start();
        }
        //stop the run
        else if(v.getId()==start.getId()&&isStart){
            start.setText("start");
            if(mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                mGoogleApiClient.disconnect();
            }
            timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
            chronometer.stop();
            isStart=false;

        }
        else if(v.getId()==this.records.getId()){
            Intent i = new Intent(this,RecordsActivity.class);
            startActivity(i);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty. 
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   mGoogleApiClient.connect();
                   chronometer.start();
                } else {
                    Toast.makeText(this,"GPS permission denied",Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    public void onPause() {
        super.onPause();
        if(mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());
        player.stop();

    }


}
