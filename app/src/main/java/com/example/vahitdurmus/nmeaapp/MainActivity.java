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

    File filegstgga;
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

    int counter=0;
    int order=0;


    volatile String ggaString;
    volatile String gstString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //createGeoJSON();

        timestamp=new Timestamp(System.currentTimeMillis());

        itemArrayList=new ArrayList<>();
        filegstgga=null;


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


    public void createGeoJSON(){
        String chcnavdif="{\"nmea\":{\"1\":{\"gga\":\"$GNGGA,131917.00,3954.45253,N,03248.57007,E,5,11,1.06,889.3,M,36.0,M,1.0,0731*6E\\r\\n\",\"gst\":\"$GNGST,131917.00,11,,,,0.18,0.24,0.54*7B\\r\\n\"},\"2\":{\"gga\":\"$GNGGA,131924.00,3954.45347,N,03248.57095,E,5,12,0.90,887.9,M,36.0,M,1.0,0731*68\\r\\n\",\"gst\":\"$GNGST,131924.00,22,,,,0.17,0.24,0.59*79\\r\\n\"},\"3\":{\"gga\":\"$GNGGA,131932.00,3954.45459,N,03248.56885,E,5,12,0.90,887.5,M,36.0,M,1.0,0731*63\\r\\n\",\"gst\":\"$GNGST,131932.00,19,,,,0.53,0.44,0.71*7A\\r\\n\"},\"4\":{\"gga\":\"$GNGGA,131940.00,3954.45353,N,03248.56674,E,5,12,1.09,888.2,M,36.0,M,1.0,0731*62\\r\\n\",\"gst\":\"$GNGST,131940.00,15,,,,0.50,0.43,0.71*77\\r\\n\"}}}";
        String chcnavnodif="{\"nmea\":{\"1\":{\"gga\":\"$GNGGA,132206.00,3954.45456,N,03248.56893,E,1,11,1.12,883.1,M,36.0,M,,*42\\r\\n\",\"gst\":\"$GNGST,132206.00,13,,,,1.6,1.2,3.0*48\\r\\n\"},\"2\":{\"gga\":\"$GNGGA,132212.00,3954.45441,N,03248.56934,E,1,12,1.12,880.5,M,36.0,M,,*49\\r\\n\",\"gst\":\"$GNGST,132212.00,17,,,,1.5,1.3,3.2*49\\r\\n\"},\"3\":{\"gga\":\"$GNGGA,132217.00,3954.45493,N,03248.56770,E,1,11,1.16,880.5,M,36.0,M,,*4A\\r\\n\",\"gst\":\"$GNGST,132217.00,27,,,,1.6,1.5,3.6*4E\\r\\n\"},\"4\":{\"gga\":\"$GNGGA,132223.00,3954.45397,N,03248.56695,E,1,12,1.02,880.6,M,36.0,M,,*41\\r\\n\",\"gst\":\"$GNGST,132223.00,26,,,,1.7,1.6,3.9*45\\r\\n\"}}}";
        String leicadiff="{\"nmea\":{\"1\":{\"gga\":\"$GPGGA,131219.00,3954.4527661,N,03248.5686165,E,4,15,1.0,886.404,M,36.90,M,01,0725*52\\r\\n\",\"gst\":\"$GNGST,131219.00,1.66,0.01,0.01,-55.1244,0.01,0.01,0.02*6D\\r\\n\"},\"2\":{\"gga\":\"$GPGGA,131231.00,3954.4536239,N,03248.5697979,E,4,15,1.0,885.748,M,36.90,M,01,0725*5C\\r\\n\",\"gst\":\"$GNGST,131231.00,1.82,0.02,0.01,63.3765,0.01,0.02,0.06*45\\r\\n\"},\"3\":{\"gga\":\"$GPGGA,131237.00,3954.4545373,N,03248.5685092,E,4,13,1.2,885.297,M,36.90,M,01,0725*5D\\r\\n\",\"gst\":\"$GNGST,131237.00,2.64,0.09,0.01,75.7083,0.02,0.09,0.31*43\\r\\n\"},\"4\":{\"gga\":\"$GPGGA,131245.00,3954.4536253,N,03248.5673882,E,4,15,1.0,886.146,M,36.90,M,01,0725*57\\r\\n\",\"gst\":\"$GNGST,131245.00,1.89,0.03,0.02,85.2094,0.02,0.03,0.14*4E\\r\\n\"}}}";
        String leicanodiff="{\"nmea\":{\"1\":{\"gga\":\"$GPGGA,131516.00,3954.4531592,N,03248.5675572,E,2,10,1.1,886.748,M,36.90,M,96,0725*5B\\r\\n\",\"gst\":\"$GNGST,131516.00,2.76,1.45,1.04,-37.0975,1.32,1.21,2.76*6D\\r\\n\"},\"2\":{\"gga\":\"$GPGGA,131525.00,3954.4521042,N,03248.5684770,E,2,11,1.1,887.820,M,36.90,M,99,0725*52\\r\\n\",\"gst\":\"$GNGST,131525.00,3.68,1.63,1.26,-46.0913,1.45,1.46,3.11*60\\r\\n\"},\"3\":{\"gga\":\"$GPGGA,131531.00,3954.4534856,N,03248.5701357,E,2,11,1.1,885.291,M,36.90,M,99,0725*51\\r\\n\",\"gst\":\"$GNGST,131531.00,3.36,1.70,1.24,-40.9591,1.52,1.46,3.33*61\\r\\n\"},\"4\":{\"gga\":\"$GPGGA,131538.00,3954.4543983,N,03248.5680865,E,2,11,1.1,884.976,M,36.90,M,99,0725*50\\r\\n\",\"gst\":\"$GNGST,131538.00,3.66,1.83,1.29,-40.2304,1.63,1.54,3.52*6B\\r\\n\"}}}";
        String samsung="{\"nmea\":{\"1\":{\"gga\":\"$GPGGA,132415.000,3954.4520,N,03248.5571,E,1,11,0.8,874.2,M,38.6,M,,*53\\r\\n\"},\"2\":{\"gga\":\"$GPGGA,132422.000,3954.4516,N,03248.5599,E,1,11,0.8,872.9,M,38.6,M,,*59\\r\\n\"},\"3\":{\"gga\":\"$GPGGA,132427.000,3954.4520,N,03248.5615,E,1,11,0.8,874.8,M,38.6,M,,*59\\r\\n\"},\"4\":{\"gga\":\"$GPGGA,132433.000,3954.4518,N,03248.5625,E,1,09,1.2,872.1,M,38.6,M,,*59\\r\\n\"}}}";
        String[] messageArray={chcnavdif,chcnavnodif,leicadiff,leicanodiff,samsung};
        String[] fileNameArray={"chcnavdif","chcnavnodif","leicadiff","leicanodiff","samsung"};



        String chcnav29dif="{\"nmea\":{\"1\":{\"gga\":\"$GNGGA,061336.00,3954.41017,N,03248.52886,E,5,08,1.08,882.5,M,36.0,M,2.0,0833*64\\r\\n\",\"gst\":\"$GNGST,061336.00,18,,,,0.43,0.35,0.55*70\\r\\n\"},\"2\":{\"gga\":\"$GNGGA,061341.00,3954.40927,N,03248.52937,E,5,09,1.07,882.9,M,36.0,M,1.0,0833*65\\r\\n\",\"gst\":\"$GNGST,061341.00,43,,,,0.43,0.34,0.58*72\\r\\n\"},\"3\":{\"gga\":\"$GNGGA,061347.00,3954.40836,N,03248.52975,E,5,09,1.07,883.0,M,36.0,M,1.0,0833*6C\\r\\n\",\"gst\":\"$GNGST,061347.00,37,,,,0.48,0.34,0.67*70\\r\\n\"},\"4\":{\"gga\":\"$GNGGA,061351.00,3954.40774,N,03248.53020,E,5,09,1.07,883.3,M,36.0,M,1.0,0833*69\\r\\n\",\"gst\":\"$GNGST,061351.00,9.7,,,,0.46,0.44,0.76*5A\\r\\n\"},\"5\":{\"gga\":\"$GNGGA,061356.00,3954.40674,N,03248.53086,E,5,09,1.07,883.4,M,36.0,M,2.0,0833*67\\r\\n\",\"gst\":\"$GNGST,061356.00,16,,,,0.45,0.35,0.76*7F\\r\\n\"},\"6\":{\"gga\":\"$GNGGA,061400.00,3954.40598,N,03248.53113,E,5,09,1.07,883.4,M,36.0,M,1.0,0833*6C\\r\\n\",\"gst\":\"$GNGST,061400.00,33,,,,0.48,0.51,0.65*71\\r\\n\"},\"7\":{\"gga\":\"$GNGGA,061403.00,3954.40535,N,03248.53163,E,5,09,1.07,883.5,M,36.0,M,1.0,0833*6E\\r\\n\",\"gst\":\"$GNGST,061403.00,9.3,,,,0.42,0.38,0.63*55\\r\\n\"},\"8\":{\"gga\":\"$GNGGA,061407.00,3954.40465,N,03248.53213,E,5,09,1.07,883.3,M,36.0,M,2.0,0833*6F\\r\\n\",\"gst\":\"$GNGST,061407.00,23,,,,0.59,0.42,0.87*79\\r\\n\"},\"9\":{\"gga\":\"$GNGGA,061411.00,3954.40390,N,03248.53287,E,5,09,1.07,883.5,M,36.0,M,1.0,0833*6D\\r\\n\",\"gst\":\"$GNGST,061411.00,20,,,,0.41,0.32,0.62*78\\r\\n\"}}}";
        String chcnav29nodiff="{\"nmea\":{\"1\":{\"gga\":\"$GNGGA,061452.00,3954.41005,N,03248.52878,E,1,06,1.36,880.8,M,36.0,M,,*4F\\r\\n\",\"gst\":\"$GNGST,061452.00,26,,,,2.5,1.8,4.1*42\\r\\n\"},\"2\":{\"gga\":\"$GNGGA,061455.00,3954.40917,N,03248.52916,E,1,06,1.36,881.2,M,36.0,M,,*41\\r\\n\",\"gst\":\"$GNGST,061455.00,9.4,,,,2.2,1.6,3.8*65\\r\\n\"},\"3\":{\"gga\":\"$GNGGA,061459.00,3954.40834,N,03248.52942,E,1,05,1.39,881.2,M,36.0,M,,*40\\r\\n\",\"gst\":\"$GNGST,061459.00,9.4,,,,1.9,1.4,3.5*6E\\r\\n\"},\"4\":{\"gga\":\"$GNGGA,061503.00,3954.40766,N,03248.52999,E,1,06,1.81,881.3,M,36.0,M,,*41\\r\\n\",\"gst\":\"$GNGST,061503.00,29,,,,2.0,1.4,3.6*41\\r\\n\"},\"5\":{\"gga\":\"$GNGGA,061507.00,3954.40687,N,03248.53063,E,1,07,1.33,880.8,M,36.0,M,,*44\\r\\n\",\"gst\":\"$GNGST,061507.00,34,,,,1.9,1.3,3.5*47\\r\\n\"},\"6\":{\"gga\":\"$GNGGA,061510.00,3954.40626,N,03248.53124,E,1,07,1.33,880.8,M,36.0,M,,*4B\\r\\n\",\"gst\":\"$GNGST,061510.00,24,,,,1.8,1.3,3.4*40\\r\\n\"},\"7\":{\"gga\":\"$GNGGA,061513.00,3954.40541,N,03248.53183,E,1,07,1.33,881.1,M,36.0,M,,*4F\\r\\n\",\"gst\":\"$GNGST,061513.00,21,,,,1.7,1.3,3.2*4F\\r\\n\"},\"8\":{\"gga\":\"$GNGGA,061516.00,3954.40456,N,03248.53240,E,1,07,1.33,881.3,M,36.0,M,,*43\\r\\n\",\"gst\":\"$GNGST,061516.00,20,,,,1.6,1.2,3.1*48\\r\\n\"},\"9\":{\"gga\":\"$GNGGA,061520.00,3954.40362,N,03248.53303,E,1,07,1.33,881.3,M,36.0,M,,*40\\r\\n\",\"gst\":\"$GNGST,061520.00,21,,,,1.7,1.2,3.2*4E\\r\\n\"}}}";
        String leica29diff="{\"nmea\":{\"1\":{\"gga\":\"$GPGGA,061817.00,3954.4095901,N,03248.5293852,E,4,11,1.7,882.665,M,36.90,M,01,0837*54\\r\\n\",\"gst\":\"$GNGST,061817.00,1.35,0.01,0.01,37.3657,0.01,0.01,0.03*47\\r\\n\"},\"2\":{\"gga\":\"$GPGGA,061822.00,3954.4089213,N,03248.5298073,E,4,10,2.9,882.582,M,36.90,M,01,0837*51\\r\\n\",\"gst\":\"$GNGST,061822.00,1.38,0.02,0.01,-15.6907,0.02,0.01,0.05*68\\r\\n\"},\"3\":{\"gga\":\"$GPGGA,061827.00,3954.4081703,N,03248.5304383,E,4,11,2.4,882.382,M,36.90,M,01,0837*5A\\r\\n\",\"gst\":\"$GNGST,061827.00,1.37,0.02,0.01,-16.8239,0.02,0.01,0.05*69\\r\\n\"},\"4\":{\"gga\":\"$GPGGA,061831.00,3954.4074617,N,03248.5309998,E,4,12,1.3,882.318,M,36.90,M,01,0837*5A\\r\\n\",\"gst\":\"$GNGST,061831.00,1.48,0.02,0.01,-18.9785,0.02,0.01,0.05*6B\\r\\n\"},\"5\":{\"gga\":\"$GPGGA,061836.00,3954.4067154,N,03248.5316091,E,4,10,1.3,882.374,M,36.90,M,02,0837*5A\\r\\n\",\"gst\":\"$GNGST,061836.00,1.43,0.02,0.01,-29.9527,0.02,0.02,0.04*6D\\r\\n\"},\"6\":{\"gga\":\"$GPGGA,061841.00,3954.4060065,N,03248.5321267,E,4,10,2.9,882.422,M,36.90,M,01,0837*5F\\r\\n\",\"gst\":\"$GNGST,061841.00,2.15,0.02,0.01,-19.9948,0.02,0.01,0.05*69\\r\\n\"},\"7\":{\"gga\":\"$GPGGA,061845.00,3954.4052496,N,03248.5326652,E,4,11,2.4,882.404,M,36.90,M,01,0837*5F\\r\\n\",\"gst\":\"$GNGST,061845.00,1.38,0.02,0.01,-25.2497,0.02,0.01,0.04*6B\\r\\n\"},\"8\":{\"gga\":\"$GPGGA,061848.00,3954.4045954,N,03248.5331668,E,4,12,2.4,882.386,M,36.90,M,01,0837*56\\r\\n\",\"gst\":\"$GNGST,061848.00,1.38,0.02,0.01,-26.2312,0.02,0.01,0.04*6F\\r\\n\"},\"9\":{\"gga\":\"$GPGGA,061852.00,3954.4038646,N,03248.5337509,E,4,12,2.4,882.443,M,36.90,M,03,0837*55\\r\\n\",\"gst\":\"$GNGST,061852.00,1.39,0.02,0.01,-28.8372,0.02,0.02,0.04*64\\r\\n\"}}}";
        String leica29nodiff="{\"nmea\":{\"1\":{\"gga\":\"$GPGGA,062006.00,3954.4097357,N,03248.5295155,E,2,08,1.3,882.470,M,36.90,M,66,0837*51\\r\\n\",\"gst\":\"$GNGST,062006.00,1.53,1.38,0.94,7.4077,1.37,0.94,2.63*77\\r\\n\"},\"2\":{\"gga\":\"$GPGGA,062006.00,3954.4097357,N,03248.5295155,E,2,08,1.3,882.470,M,36.90,M,66,0837*51\\r\\n\",\"gst\":\"$GNGST,062006.00,1.53,1.38,0.94,7.4077,1.37,0.94,2.63*77\\r\\n\"},\"3\":{\"gga\":\"$GPGGA,062011.00,3954.4081462,N,03248.5306159,E,2,07,2.3,886.132,M,36.90,M,71,0837*5B\\r\\n\",\"gst\":\"$GNGST,062011.00,1.56,3.01,1.12,-23.7249,2.79,1.59,5.83*64\\r\\n\"},\"4\":{\"gga\":\"$GPGGA,062015.00,3954.4078474,N,03248.5310766,E,2,07,2.3,883.771,M,36.90,M,75,0837*53\\r\\n\",\"gst\":\"$GNGST,062015.00,1.58,3.06,1.15,-24.1800,2.83,1.63,5.86*61\\r\\n\"},\"5\":{\"gga\":\"$GPGGA,062018.00,3954.4073298,N,03248.5314989,E,2,07,2.3,883.340,M,36.90,M,78,0837*51\\r\\n\",\"gst\":\"$GNGST,062018.00,1.61,3.12,1.18,-24.2678,2.89,1.68,5.98*62\\r\\n\"},\"6\":{\"gga\":\"$GPGGA,062022.00,3954.4064260,N,03248.5321298,E,2,07,2.3,883.732,M,36.90,M,82,0837*50\\r\\n\",\"gst\":\"$GNGST,062022.00,1.64,3.20,1.23,-24.5365,2.95,1.73,6.10*6D\\r\\n\"},\"7\":{\"gga\":\"$GPGGA,062026.00,3954.4063209,N,03248.5325429,E,2,06,2.7,880.215,M,36.90,M,86,0837*56\\r\\n\",\"gst\":\"$GNGST,062026.00,1.67,4.54,1.44,-15.4352,4.39,1.84,10.66*56\\r\\n\"},\"8\":{\"gga\":\"$GPGGA,062030.00,3954.4050206,N,03248.5332252,E,2,07,2.3,883.240,M,36.90,M,90,0837*53\\r\\n\",\"gst\":\"$GNGST,062030.00,1.83,3.60,1.37,-22.4597,3.37,1.87,7.29*63\\r\\n\"},\"9\":{\"gga\":\"$GPGGA,062033.00,3954.4041349,N,03248.5337383,E,2,07,2.3,884.085,M,36.90,M,93,0837*5D\\r\\n\",\"gst\":\"$GNGST,062033.00,1.79,3.53,1.38,-24.3380,3.27,1.92,6.84*68\\r\\n\"},\"10\":{\"gga\":\"$GPGGA,062037.00,3954.4042439,N,03248.5342098,E,2,06,2.7,880.156,M,36.90,M,97,0837*5B\\r\\n\",\"gst\":\"$GNGST,062037.00,1.80,4.95,1.63,-15.0075,4.80,2.03,11.59*56\\r\\n\"}}}";
        String samsung29="{\"nmea\":{\"1\":{\"gga\":\"$GPGGA,060944.000,3954.4044,N,03248.5253,E,1,15,0.6,876.7,M,38.6,M,,*51\\r\\n\"},\"2\":{\"gga\":\"$GPGGA,060948.000,3954.4047,N,03248.5262,E,1,15,0.6,877.2,M,38.6,M,,*58\\r\\n\"},\"3\":{\"gga\":\"$GPGGA,060948.000,3954.4047,N,03248.5262,E,1,15,0.6,877.2,M,38.6,M,,*58\\r\\n\"},\"4\":{\"gga\":\"$GPGGA,060948.000,3954.4047,N,03248.5262,E,1,15,0.6,877.2,M,38.6,M,,*58\\r\\n\"},\"5\":{\"gga\":\"$GPGGA,060948.000,3954.4047,N,03248.5262,E,1,15,0.6,877.2,M,38.6,M,,*58\\r\\n\"},\"6\":{\"gga\":\"$GPGGA,060948.000,3954.4047,N,03248.5262,E,1,15,0.6,877.2,M,38.6,M,,*58\\r\\n\"},\"7\":{\"gga\":\"$GPGGA,060948.000,3954.4047,N,03248.5262,E,1,15,0.6,877.2,M,38.6,M,,*58\\r\\n\"},\"8\":{\"gga\":\"$GPGGA,060948.000,3954.4047,N,03248.5262,E,1,15,0.6,877.2,M,38.6,M,,*58\\r\\n\"},\"9\":{\"gga\":\"$GPGGA,060948.000,3954.4047,N,03248.5262,E,1,15,0.6,877.2,M,38.6,M,,*58\\r\\n\"}}}";
        String[] messageArray29={chcnav29dif,chcnav29nodiff,leica29diff,leica29nodiff,samsung29};
        String[] fileNameArray29={"chcnavdif29","chcnavnodif29","leicadiff29","leicanodiff29","samsung29"};


        try {


            for (int i=0;i<messageArray.length;i++){

                JSONObject jsonObject=new JSONObject(messageArray[i]);
                JSONObject GeoJSON=new JSONObject();

                GeoJSON.put("type","FeatureCollection");
                JSONArray features=new JSONArray();
                GeoJSON.put("features",features);

                for (int j=1;j<=4;j++){

                    String nmeaGga= jsonObject.getJSONObject("nmea").getJSONObject(String.valueOf(j)).getString("gga");

                    String nmeaGst=null;

                    try{
                        nmeaGst= jsonObject.getJSONObject("nmea").getJSONObject(String.valueOf(j)).getString("gst");
                    }
                    catch (Exception e){

                    }

                    JSONObject nmeaGGAJSON=collectfromGGA(nmeaGga);
                    JSONObject nmeaGSTJSON=collectFromGST(nmeaGst);

                   try {
                       if (!fileNameArray29[i].equals("samsung") || !fileNameArray[i].equals("samsung29")){
                           nmeaGGAJSON.put("HRMS",nmeaGSTJSON.getDouble("HRMS"));
                           nmeaGGAJSON.put("VRMS",nmeaGSTJSON.getDouble("VRMS"));
                       }
                   }
                   catch (Exception e){

                   }

                    JSONObject feature=new JSONObject();
                    feature.put("type","Feature");
                    feature.put("properties",nmeaGGAJSON);

                    JSONObject geometry=new JSONObject();
                    geometry.put("type","Point");

                    JSONArray coordinates=new JSONArray();

                    coordinates.put(nmeaGGAJSON.getDouble("longitude"));
                    coordinates.put(nmeaGGAJSON.getDouble("latitude"));

                    geometry.put("coordinates",coordinates);

                    feature.put("geometry",geometry);

                    GeoJSON.getJSONArray("features").put(feature);

                }


                File root = new File(Environment.getExternalStorageDirectory(), "NmeaText");

                if (!root.exists()) {
                    root.mkdirs();
                }

                filegstgga = new File(root, fileNameArray[i]+".json");
                FileWriter writer = new FileWriter(filegstgga);
                writer.append(GeoJSON.toString());//boş bir JSON nesnesi
                writer.flush();
                writer.close();

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }


    public void dosyaIslemleri() {


        if (dosyaAdiEditText.isEnabled()){

            if (dosyaAdiEditText.getText().toString().isEmpty()|| dosyaAdiEditText.getText().toString()==null)
            {
                Toast.makeText(getApplicationContext(),"Bir Dosya Adı giriniz",Toast.LENGTH_SHORT).show();
                return;
            }

        }

        if (filegstgga!=null){
            return;
        }

        String state= Environment.getExternalStorageState();
        Boolean writable=false;
        Boolean readableonly=false;

        if (Environment.MEDIA_MOUNTED.equals(state))
            writable=true;
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
            readableonly=true;

        File root = new File(Environment.getExternalStorageDirectory(), "Nmea");
        if (!root.exists()) {
            root.mkdirs();
        }

        JSONObject GeoJSON=new JSONObject();

        try {
            String dosyaAdi=dosyaAdiEditText.getText().toString();
            dosyaAdiEditText.setEnabled(false);
            filegstgga = new File(root, dosyaAdi+".json");

            GeoJSON.put("type","FeatureCollection");
            JSONArray features=new JSONArray();
            GeoJSON.put("features",features);

            FileWriter writer = new FileWriter(filegstgga);
            writer.append(GeoJSON.toString());//boş bir JSON nesnesi
            writer.flush();
            writer.close();
            Toast.makeText(getApplicationContext(),"Dosya Oluşturulmuştur",Toast.LENGTH_SHORT).show();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    public static String zamanAl() {
        String zaman = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date());
        return zaman;
    }

    public synchronized void setGgaString(String ggaString){
        this.ggaString=ggaString;
    }
    public synchronized String getGgaString(){

        if (this.ggaString!=null)
            return this.ggaString;
        else
            return null;
    }
    public synchronized void setGstString(String gstString){
        this.gstString=gstString;
    }

    public synchronized String getGstString(){
        if (this.gstString!=null)
            return this.gstString;
        else
            return null;
    }

    public  JSONObject collectfromGGA(String nmea){

        JSONObject jsonObject=new JSONObject();
        String[] ggaMessageArray= nmea.split(",");

        try {



            double latdob=Double.valueOf(ggaMessageArray[2]);
            String latStringNmea=String.valueOf(latdob);

            String latdegree=latStringNmea.substring(0,2);
            String latminute=latStringNmea.substring(2,latStringNmea.length()-2);
            double latdegreeD=Double.parseDouble(latdegree);
            double latminuteD=Double.parseDouble(latminute);
            double latitude=latdegreeD+(latminuteD/60);


            double longdob=Double.valueOf(ggaMessageArray[4]);
            String longStringNmea=String.valueOf(longdob);

            String longdegree=longStringNmea.substring(0,2);
            String longminute=longStringNmea.substring(2,longStringNmea.length()-2);
            double longdegreeD=Double.parseDouble(longdegree);
            double longminuteD=Double.parseDouble(longminute);
            double longitude=longdegreeD+(longminuteD/60);


            jsonObject.put("latitude",latitude);
            jsonObject.put("longitude",longitude);
            jsonObject.put("fixuality",ggaMessageArray[6]);
            jsonObject.put("satellitesnumber",ggaMessageArray[7]);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();
        }
        return jsonObject;
    }
    public  JSONObject collectFromGST(String nmea){
        JSONObject jsonObject1;

        try {
            jsonObject1 =new JSONObject();

            String[] gstMessageArray= nmea.split(",");

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
            jsonObject1.put("VRMS",VRMS);
            jsonObject1.put("HRMS",HRMS);

        }
        catch (JSONException e){
            return null;
        }
        catch (Exception e){
            return  null;
        }
        return jsonObject1;
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

    public  void mesajEkle(JSONObject yeniBilgiler) {
        JSONObject kayitliVeriler = varOlanMesajlar();
        try {

            order++;
            kayitliVeriler.getJSONObject("nmea").put(String.valueOf(order),yeniBilgiler);
            String degisenVeri = kayitliVeriler.toString();
            FileWriter writer = new FileWriter(filegstgga);
            writer.append(degisenVeri);
            writer.flush();
            writer.close();
            Toast.makeText(getApplicationContext(),"Kayıt Başarılı: "+order,Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  void writeToFileGeoJSON(JSONObject nmeaJSON) {

        try {
            JSONObject writenBeforeGeoJSON =getWritenBeforeGeoJSON();

            JSONObject feature=new JSONObject(nmeaJSON.toString());
            feature.put("type","Feature");
            feature.put("properties",nmeaJSON);

            JSONObject geometry=new JSONObject();
            geometry.put("type","Point");

            JSONArray coordinates=new JSONArray();

            coordinates.put(nmeaJSON.getDouble("longitude"));
            coordinates.put(nmeaJSON.getDouble("latitude"));

            geometry.put("coordinates",coordinates);

            feature.put("geometry",geometry);

            writenBeforeGeoJSON.getJSONArray("features").put(feature);

                FileWriter writer = new FileWriter(filegstgga);
                writer.append(writenBeforeGeoJSON.toString());
                writer.flush();
                writer.close();
                Toast.makeText(getApplicationContext(),"Kayıt Başarılı: "+order,Toast.LENGTH_SHORT).show();

        }
        catch (JSONException e){

        }
        catch (IOException e){

        }
    }

    public  JSONObject varOlanMesajlar() {
        JSONObject json = new JSONObject();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filegstgga));
            StringBuilder stringBuilder = new StringBuilder();
            String okunanSatir;

            while ((okunanSatir = reader.readLine()) != null) {

                stringBuilder.append(okunanSatir);
            }

            String s = stringBuilder.toString();
            json = new JSONObject(s);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    public  JSONObject getWritenBeforeGeoJSON() {
        JSONObject json = new JSONObject();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filegstgga));
            StringBuilder stringBuilder = new StringBuilder();
            String okunanSatir;

            while ((okunanSatir = reader.readLine()) != null) {

                stringBuilder.append(okunanSatir);
            }

            String s = stringBuilder.toString();
            json = new JSONObject(s);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    public  JSONObject varOlanMesajlar(File file) {
        JSONObject json = new JSONObject();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder stringBuilder = new StringBuilder();
            String okunanSatir;

            while ((okunanSatir = reader.readLine()) != null) {
                stringBuilder.append(okunanSatir);
            }

            String s = stringBuilder.toString();
            json = new JSONObject(s);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
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

