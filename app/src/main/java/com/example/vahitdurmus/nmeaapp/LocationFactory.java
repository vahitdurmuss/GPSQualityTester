package com.example.vahitdurmus.nmeaapp;

/**
 * Created by vahit.durmus on 28.03.2019.
 */

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;


import com.example.vahitdurmus.nmeaapp.NmeaMessages.GGA;
import com.example.vahitdurmus.nmeaapp.NmeaMessages.GST;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import java.text.DecimalFormat;


public class LocationFactory {

    private volatile Location currentLocation;
    private volatile Location previousLocation;
    private volatile Location location;
    private volatile Location fixedLastLocation;
    private Context context;
    private LocationRequest locationRequest;
    private GoogleApiClient mGoogleApiClient;
    private LocationManager locationManager;
    private volatile int numberOfSatellites = 0;
    private volatile int numberOfConnectedSatellites = 0;
    private volatile GGA $GGA;
    private volatile GST $GST;


    public LocationFactory(Context context) {
        setContext(context);
        setZeroNumberOfSatellites();
    }

    public void  set$GGA(GGA gga){
        this.$GGA=gga;
    }

    public GGA get$GGA(){
        return this.$GGA;
    }

    public  void set$GST(GST gst){
        this.$GST=gst;
    }

    public GST get$GST(){
        return this.$GST;
    }

    public void setNmeaObject(String nmeaMessage){
        String $nmeamessagetype= nmeaMessage.split(",")[0];
        if ($nmeamessagetype.equals("$GPGGA") || $nmeamessagetype.equals("$GLGGA") || $nmeamessagetype.equals("$GNGGA")){
            GGA gga=new GGA(nmeaMessage);
            set$GGA(gga);
        }
        if ($nmeamessagetype.equals("$GPGST") || $nmeamessagetype.equals("$GLGST") || $nmeamessagetype.equals("$GNGST") ){
            GST gst=new GST(nmeaMessage);
            set$GST(gst);
        }
    }
    /**
     * is used to set context of activity. it takes context as parameter.
     * @param context
     */
    private void setContext(Context context) {
        this.context = context;
    }

    public void setLocationSettingsAndStart(long interval, long fastestInterval) {

        try {
            buildGoogleApiClient();
            createLocationRequest(interval, fastestInterval);
            connectGoogleAPI();
            locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
            addGpsStatusListener();
            addNmeaStatusListener();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeGpsStatusListener() {
        try {
            locationManager.removeGpsStatusListener((GpsStatus.Listener)context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addGpsStatusListener() {
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.addGpsStatusListener((GpsStatus.Listener) context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addNmeaStatusListener() {
        try{
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.addNmeaListener((GpsStatus.NmeaListener) context);

        }
        catch (Exception e){

        }
    }

    public void removeNmeaStatusListener() {
        try{
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.removeNmeaListener((GpsStatus.NmeaListener) context);

        }
        catch (Exception e){

        }
    }

    public void stopLocationTrack() throws Exception {
        stopLocationRequest();
        disConnectGoogleAPI();
        removeGpsStatusListener();
        removeNmeaStatusListener();
    }
    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder((Activity) context)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) context)
                .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) context)
                .addApi(LocationServices.API)
                .build();
    }
    private void createLocationRequest(long interval,long fastestInterval) {
        locationRequest = LocationRequest.create()
                .setInterval(interval)
                .setFastestInterval(fastestInterval)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }
    public void startLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, (LocationListener) context);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    public void stopLocationRequest() {
        try{
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (LocationListener) context);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    public void findNumberOfSatellites() {
        try{
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            int satellites = 0;
            int satellitesInFix = 0;
            int timetofix = locationManager.getGpsStatus(null).getTimeToFirstFix();

            for (GpsSatellite sat : locationManager.getGpsStatus(null).getSatellites()) {
                if (sat.usedInFix()) {
                    satellitesInFix++;
                }
                satellites++;
            }
            numberOfConnectedSatellites = satellitesInFix;
            numberOfSatellites = satellites;

        }
        catch (Exception e)
        {

        }


    }
    public void disConnectGoogleAPI() {
        try{
            if (mGoogleApiClient.isConnected() == true)
                mGoogleApiClient.disconnect();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    public void connectGoogleAPI() {
        try {
            if (mGoogleApiClient.isConnected() == false || mGoogleApiClient == null)
                mGoogleApiClient.connect();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    public int getNumberOfSatellites() {
        return numberOfSatellites;
    }
    public int getNumberOfSatellitesFromGGA() {
        return $GGA.getNumberOfSatellitesInUse();
    }
    public double getAccuracyFromGST() throws NullPointerException{
        return  this.$GST.getHRMS();
    }
    public int getFixQualityFromGGA(){
        return this.$GGA.getFixQuality();
    }
    public double getAccuracyFromLocation(){
        return this.getCurrentLocation().getAccuracy();
    }
    public  void setZeroNumberOfSatellites(){
        this.numberOfSatellites=0;
        this.numberOfConnectedSatellites=0;
    }
    public int getNumberOfConnectedSatellites() {
        return numberOfConnectedSatellites;
    }
    public  void  setCurrentLocation(Location currentLocation) {
        setPreviousLocation(getCurrentLocation());
        this.currentLocation=currentLocation;
    }
    public  Location getCurrentLocation(){
        return  this.currentLocation;
    }
    public void setLocation(Location location){
        this.location=location;
    }
    public Location getLocation(){
        return this.location;
    }
    public  void   setPreviousLocation(Location previousLocation)
    {
        if (previousLocation!=null)
            this.previousLocation=previousLocation;
    }

    public void setFixedLastLocation(Location location){
        this.fixedLastLocation=location;
    }
    public Location getFixedLastLocation(){
        return this.fixedLastLocation;
    }
    public  Location getPreviousLocation(){
        return  this.previousLocation;
    }
    public String getCurrentLongLatText(){
        return String.valueOf(getCurrentLocation().getLongitude()) + "," + String.valueOf(getCurrentLocation().getLatitude());
    }

    public String getLongLatText(){
        return String.valueOf(getLocation().getLongitude()) + "," + String.valueOf(getLocation().getLatitude());
    }
    public String getNmeaLongLatText(){

        try {
            return $GGA.getLongLatText();
        }
        catch (NullPointerException e){
            return null;
        }
    }

    public String getLongLatText(Location location){
        return String.valueOf(location.getLongitude()) + "," + String.valueOf(location.getLatitude());
    }

    public String getPreviousLongLatText(){
        return String.valueOf(getPreviousLocation().getLongitude()) + "," + String.valueOf(getPreviousLocation().getLatitude());
    }


    public String getCurrentLatLongText(){
        return String.valueOf(getPreviousLocation().getLongitude()) + "," + String.valueOf(getPreviousLocation().getLatitude());
    }

    public float getSpeedMS(){

        float speed;
        try {
            if (getCurrentLocation().hasSpeed()){
                speed=getCurrentLocation().getSpeed();
            }
            else
                speed=0.f;
        }
        catch (NullPointerException e){
            speed=0.f;
        }
        return speed;
    }
    public double getSpeedKS(){

        double speed;
        try {
            if (getCurrentLocation().hasSpeed()){
                speed=(getCurrentLocation().getSpeed()*18)/5;
                DecimalFormat df = new DecimalFormat("#.##");
                String dx=df.format(speed);
                dx= dx.replace(",",".");
                speed=Double.valueOf(dx);
            }
            else
                speed=0;
        }
        catch (NullPointerException e){
            speed=0;
        }
        catch (Exception e){
            speed=0;
        }
        return speed;
    }

    public double getSpeedKS(Location location){

        double speed;
        try {
            if (location.hasSpeed()){
                speed=(location.getSpeed()*18)/5;
                DecimalFormat df = new DecimalFormat("#.##");
                String dx=df.format(speed);
                dx= dx.replace(",",".");
                speed=Double.valueOf(dx);
            }
            else
                speed=0;
        }
        catch (NullPointerException e){
            speed=0;
        }
        catch (Exception e){
            speed=0;
        }
        return speed;
    }

}

