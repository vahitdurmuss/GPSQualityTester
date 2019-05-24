package com.example.vahitdurmus.nmeaapp.NmeaMessages;

/**
 * Created by vahit.durmus on 23.05.2019.
 */

public class GST {

    private double latErr;
    private double longErr;
    private double AltErr;

    public GST(String nmeaMessage) {
        try{
            String[] gstMessageArray= nmeaMessage.split(",");

            latErr=Double.parseDouble(gstMessageArray[6]);
            longErr=Double.parseDouble(gstMessageArray[7]);

            char[] characters= gstMessageArray[8].toCharArray();

            StringBuilder builder=new StringBuilder();

            for (char c:characters){
                if (c=='*'){
                    break;
                }
                builder.append(c);
            }
            AltErr=Double.parseDouble(builder.toString());
        }
        catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void GST(){}

    public double getHRMS(){
        return  2* Math.sqrt(0.5*((Math.pow(latErr, 2) + Math.pow(longErr, 2))/2));
    }
    public double getVRMS(){
        return AltErr;
    }
}
