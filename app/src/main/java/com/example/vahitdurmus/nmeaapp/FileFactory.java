package com.example.vahitdurmus.nmeaapp;

import android.os.Environment;
import android.widget.Toast;

import com.example.vahitdurmus.nmeaapp.NmeaMessages.GGA;
import com.example.vahitdurmus.nmeaapp.NmeaMessages.GST;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by vahit.durmus on 23.05.2019.
 */

public class FileFactory {

    private static File filegstgga;



    public static boolean isFileCreated(){

        if (filegstgga!=null){
            return true;
        }
        else
            return false;
    }

    public static String createFile(String fileName) {


        if (isFileCreated())
            return "Dosya daha önce oluşturulmuştur.";

        if (fileName!=null){

            if (fileName.toString().isEmpty()|| fileName.toString()==null)
            {
                return "Bir Dosya Adı giriniz";
            }
        }

        String state= Environment.getExternalStorageState();
        Boolean writable=false;
        Boolean readableonly=false;

        if (Environment.MEDIA_MOUNTED.equals(state))
            writable=true;
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
            readableonly=true;

        File root = new File(Environment.getExternalStorageDirectory(), "NmeaAppData");
        if (!root.exists()) {
            root.mkdirs();
        }

        JSONObject GeoJSON=new JSONObject();

        try {

            filegstgga = new File(root, fileName+".json");

            GeoJSON.put("type","FeatureCollection");
            JSONArray features=new JSONArray();
            GeoJSON.put("features",features);

            FileWriter writer = new FileWriter(filegstgga);
            writer.append(GeoJSON.toString());//boş bir JSON nesnesi
            writer.flush();
            writer.close();
           return "ok";
        }
        catch (IOException e){
            return "Hata:"+e.getMessage();
        }
        catch (JSONException e){
            return "Hata:"+e.getMessage();
        }

    }

    public  static String writeToFileGeoJSON(GGA gga) {

        try {

            if (!isFileCreated())
                return "Dosya oluşturulmamıştır.";



            JSONObject writenBeforeGeoJSON =getWritenBeforeGeoJSON();

            JSONObject feature=new JSONObject();
            feature.put("type","Feature");
            feature.put("properties",getFeaturesJSON(gga));

            JSONObject geometry=new JSONObject();
            geometry.put("type","Point");

            JSONArray coordinates=new JSONArray();

            coordinates.put(gga.getLongitude());
            coordinates.put(gga.getLatitude());

            geometry.put("coordinates",coordinates);

            feature.put("geometry",geometry);

            writenBeforeGeoJSON.getJSONArray("features").put(feature);

            FileWriter writer = new FileWriter(filegstgga);
            writer.append(writenBeforeGeoJSON.toString());
            writer.flush();
            writer.close();
            return "ok";


        }
        catch (JSONException e){
            return "Hata:"+e.getMessage();
        }
        catch (IOException e){
            return "Hata:"+e.getMessage();
        }
        catch (Exception e){
            return "Hata:"+e.getMessage();
        }
    }

    private static JSONObject getFeaturesJSON(GGA gga){

        JSONObject features=new JSONObject();

        try {
            features.put("latitude",gga.getLatitude());
            features.put("longitude",gga.getLatitude());
            features.put("fixuality",gga.getFixQuality());
            features.put("satellitesnumber",gga.getNumberOfSatellitesInUse());

        }
        catch (Exception e){
            return features;
        }
        return  features;
    }
    private static  JSONObject getWritenBeforeGeoJSON() {
        JSONObject json = new JSONObject();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filegstgga));
            StringBuilder stringBuilder = new StringBuilder();
            String readRow;

            while ((readRow = reader.readLine()) != null) {

                stringBuilder.append(readRow);
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




}
