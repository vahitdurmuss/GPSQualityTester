package com.example.vahitdurmus.nmeaapp;

import android.location.GpsStatus;
import android.location.Location;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vahitdurmus.nmeaapp.NmeaMessages.GGA;
import com.example.vahitdurmus.nmeaapp.NmeaMessages.GST;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks,GpsStatus.NmeaListener,View.OnClickListener,GpsStatus.Listener {

    LocationFactory locationFactory;

    Button goButton;
    Button pauseButton;
    Button saveButton;

    TextView timeTextView;
    TextView latitudeTextView;
    TextView longitudeTextView;
    TextView fixqualityTextView;
    TextView numberofsatellitesTextView;
    TextView hdopTextView;
    TextView altitudeTextView;
    TextView wgs84TextView;
    TextView hrmsTextView;
    TextView vrmsTextView;
    TextView hdopvdoppdopTextView;
    EditText dosyaAdiEditText;

    ArrayAdapter<String> nmeaItemsAdapter;
    ListView nmeaMessageLisView;
    ArrayList<String> itemArrayList;
    Timestamp timestamp;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //createGeoJSON();

        timestamp=new Timestamp(System.currentTimeMillis());

        itemArrayList=new ArrayList<>();

        timeTextView=(TextView)findViewById(R.id.textview_time);
        latitudeTextView=(TextView)findViewById(R.id.textview_latitude);
        longitudeTextView=(TextView)findViewById(R.id.textview_longitude);
        fixqualityTextView=(TextView)findViewById(R.id.textview_fix_quality);
        numberofsatellitesTextView=(TextView)findViewById(R.id.textview_numberofsatellites);
        hdopTextView=(TextView)findViewById(R.id.textview_hdop);
        altitudeTextView=(TextView)findViewById(R.id.textview_altitude);
        wgs84TextView=(TextView)findViewById(R.id.textview_wgs84);

        hrmsTextView=(TextView)findViewById(R.id.textview_HRMS);
        vrmsTextView=(TextView)findViewById(R.id.textview_VRSMS);
        hdopvdoppdopTextView=(TextView)findViewById(R.id.textview_3drms);
        nmeaMessageLisView=(ListView)findViewById(R.id.listviewNmea);

        goButton=(Button)findViewById(R.id.buttongo);
        pauseButton=(Button)findViewById(R.id.buttonpause);
        saveButton=(Button)findViewById(R.id.buttonsave);
        dosyaAdiEditText=(EditText)findViewById(R.id.edittext_filename);

        goButton.setOnClickListener(this);
        pauseButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);

        try {
            locationFactory=new LocationFactory(this);
            locationFactory.setLocationSettingsAndStart(1000,1000);
        }
        catch (Exception e){
            Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationFactory.stopLocationRequest();
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationFactory.startLocationUpdates();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            locationFactory.stopLocationTrack();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNmeaReceived(long timestamp, String nmea) {

        try {
            takeNmeaMessage(nmea);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        locationFactory.startLocationUpdates();
    }


    public void takeNmeaMessage(String nmeaMessage){

        String $nmeamessagetype= nmeaMessage.split(",")[0];

        if ($nmeamessagetype.equals("$GPGGA") || $nmeamessagetype.equals("$GLGGA") || $nmeamessagetype.equals("$GNGGA")){


            locationFactory.set$GGA(new GGA(nmeaMessage));

            String[] ggaMessageArray= nmeaMessage.split(",");
            timeTextView.setText(ggaMessageArray[1]);
            latitudeTextView.setText(ggaMessageArray[2]+" "+ggaMessageArray[3]);
            longitudeTextView.setText(ggaMessageArray[4]+" "+ggaMessageArray[5]);
            fixqualityTextView.setText(ggaMessageArray[6]+" (0=no fix, 1=GPS fix, 2=Dif. GPS fix) ");
            numberofsatellitesTextView.setText(ggaMessageArray[7]);
            hdopTextView.setText(ggaMessageArray[8]);
            altitudeTextView.setText(ggaMessageArray[9]+" "+ggaMessageArray[10]);
            wgs84TextView.setText(ggaMessageArray[11]+" "+ ggaMessageArray[12]);
        }

        if ($nmeamessagetype.equals("$GPGST") || $nmeamessagetype.equals("$GLGST") || $nmeamessagetype.equals("$GNGST") ){

            locationFactory.set$GST(new GST(nmeaMessage));
            String[] gstMessageArray= nmeaMessage.split(",");

            double latEr=Double.parseDouble(gstMessageArray[6]);
            double lonEr=Double.parseDouble(gstMessageArray[7]);

            char[] characters= gstMessageArray[8].toCharArray();

            StringBuilder builder=new StringBuilder();

            for (char c:characters){
                if (c=='*'){
                    break;
                }
                builder.append(c);
            }

            double AltEr=Double.parseDouble(builder.toString());

            double VRMS=AltEr;
            double HRMS=2* Math.sqrt(0.5*((Math.pow(latEr, 2) + Math.pow(lonEr, 2))/2));
            double D3RMS = 3* Math.sqrt(((Math.pow(latEr, 2) + Math.pow(lonEr, 2))/2));

            vrmsTextView.setText(String.valueOf(VRMS));
            hrmsTextView.setText(String.valueOf(HRMS));
        }
        if ($nmeamessagetype.equals("$GPGSA") || $nmeamessagetype.equals("$GLGSA") || $nmeamessagetype.equals("$GNGSA")){
            String[] gstMessageArray= nmeaMessage.split(",");
            double pDop=Double.parseDouble(gstMessageArray[15]);
            double hDop=Double.parseDouble(gstMessageArray[16]);
            double vDop=Double.parseDouble(gstMessageArray[17]);

            hdopvdoppdopTextView.setText("pdop:"+String.valueOf(pDop) +"hdop:"+String.valueOf(hDop) +"vdop"+String.valueOf(vDop));
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
        locationFactory.connectGoogleAPI();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        locationFactory.connectGoogleAPI();
    }
    @Override
    public void onLocationChanged(Location location) {
        locationFactory.setCurrentLocation(location);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.buttonpause:
                locationFactory.stopLocationRequest();
                break;
            case R.id.buttongo:
                locationFactory.startLocationUpdates();
                break;
            case R.id.buttonsave:

                if (dosyaAdiEditText.isEnabled())
                {
                   String result1= FileFactory.createFile(dosyaAdiEditText.getText().toString());

                   if (result1.equals("ok"))
                   {
                       Toast.makeText(this,result1,Toast.LENGTH_LONG).show();
                       dosyaAdiEditText.setEnabled(false);
                       return;
                   }
                   else{
                       Toast.makeText(this,result1,Toast.LENGTH_LONG).show();
                       return;
                   }
                }
                locationFactory.removeNmeaStatusListener();

                String result= FileFactory.writeToFileGeoJSON(locationFactory.get$GGA());

                Toast.makeText(this, result, Toast.LENGTH_SHORT).show();

                locationFactory.addNmeaStatusListener();
                break;
            default:
                break;
        }
    }

    @Override
    public void onGpsStatusChanged(int event) {

    }
}

