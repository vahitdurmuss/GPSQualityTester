package com.example.vahitdurmus.nmeaapp.NmeaMessages;

/**
 * Created by vahit.durmus on 23.05.2019.
 */

public class GGA {


    private String time;
    private volatile String latitude;
    private String NS;
    private volatile String longitude;
    private String EW;
    private int fixQuality;
    private int numberOfSatellitesInUse;
    private double hdop;
    private double altitude;
    private String altitudeUnit;
    private double wgs84;
    private String unitWgs84;

    public GGA(String nmea) {
        try{
            String[] ggaMessageArray= nmea.split(",");
            time=   ggaMessageArray[1];
            latitude= ggaMessageArray[2];
            NS=ggaMessageArray[3];
            longitude=ggaMessageArray[4];
            EW=ggaMessageArray[5];
            fixQuality=Integer.parseInt(ggaMessageArray[6]);
            numberOfSatellitesInUse=Integer.parseInt(ggaMessageArray[7]);
            hdop=Double.parseDouble(ggaMessageArray[8]);
            altitude=Double.parseDouble(ggaMessageArray[9]);
            altitudeUnit=ggaMessageArray[10];
            wgs84=Double.parseDouble(ggaMessageArray[11]);
            unitWgs84=ggaMessageArray[12];
        }
        catch (IllegalArgumentException e){
        }
        catch (NullPointerException e){
        }
    }

    public void GGA(){}

    public int getFixQuality(){
        return this.fixQuality;
    }

    public int getNumberOfSatellitesInUse(){
        return this.numberOfSatellitesInUse;
    }

    public double getLatitude() throws Exception{

        double latdob=Double.valueOf(this.latitude);
        String latStringNmea=String.valueOf(latdob);

        String latdegree=latStringNmea.substring(0,2);
        String latminute=latStringNmea.substring(2,latStringNmea.length()-2);
        double latdegreeD=Double.parseDouble(latdegree);
        double latminuteD=Double.parseDouble(latminute);
        double latitudeReturn=latdegreeD+(latminuteD/60);
        return latitudeReturn;

    }
    public double getLongitude() throws Exception{

        double longdob=Double.valueOf(this.latitude);
        String longStringNmea=String.valueOf(longdob);

        String longdegree=longStringNmea.substring(0,2);
        String longminute=longStringNmea.substring(2,longStringNmea.length()-2);
        double longdegreeD=Double.parseDouble(longdegree);
        double longminuteD=Double.parseDouble(longminute);
        double longitudeReturn=longdegreeD+(longminuteD/60);
        return longitudeReturn;
    }
    public String getLongLatText(){
        String returnText;
        try {
            returnText= String.valueOf(getLongitude()) + "," + String.valueOf(getLatitude());
        }
        catch (Exception e){
            returnText=null;
        }
        return returnText;
    }
}
