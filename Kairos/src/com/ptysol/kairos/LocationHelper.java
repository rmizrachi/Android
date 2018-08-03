package com.ptysol.kairos;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;


import org.json.JSONObject;

/**
 * Created by Ramon on 4/5/2016.
 */
public class LocationHelper {




    public  LocationHelper() {
    }

      public LocationInfo GetLocation(String searchAddress){
          try {
              String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + searchAddress + "&key=AIzaSyCA3PAwt_OfsSF5uJUeaNbYPdH8QzylNzA";
              JSONParser jsonParser = new JSONParser();
              JSONObject json = jsonParser.execute(url).get();
              return  FillLocationInfo(json);

          }catch (Exception ex)
          {
              ex.printStackTrace();
              return  null;
          }
      }

    public LocationInfo GetLocationByLatLong(String latitude, String longitude){
        try {
            String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&key=AIzaSyCA3PAwt_OfsSF5uJUeaNbYPdH8QzylNzA";
            JSONParser jsonParser = new JSONParser();
            JSONObject json = jsonParser.execute(url).get();
            return  FillLocationInfo(json);

        }catch (Exception ex)
        {
            ex.printStackTrace();
            return  null;
        }
    }



    private  LocationInfo FillLocationInfo(JSONObject json)
    {
        try {
            LocationInfo locInfo = new LocationInfo();
            //parse JSON object
            locInfo.Address = json.getJSONArray("results").getJSONObject(0).getJSONArray("address_components").getJSONObject(2).get("long_name").toString() + ", " +
                    json.getJSONArray("results").getJSONObject(0).getJSONArray("address_components").getJSONObject(5).get("short_name").toString();
            locInfo.Latitude = json.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lat").toString();
            locInfo.Longitude = json.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lng").toString();

            return locInfo;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return  null;
        }

    }

    public  LocationInfo GetLocationFromSavedPreferences(Activity activity, Context context) {
        try {
            LocationInfo locInfo = new LocationInfo();

            SharedPreferences sharedPref = activity.getSharedPreferences(context.getString(R.string.kairos_share_preferences),Context.MODE_PRIVATE);
            locInfo.Latitude = sharedPref.getString("latitude", "");
            locInfo.Longitude = sharedPref.getString("longitude", "");
            locInfo.Address = sharedPref.getString("address", "");

            if(locInfo.Latitude.equals("") || locInfo.Longitude.equals(("")))
            {
                ShowGoToSettingsDialog(context);
                return null;
            }


            return  locInfo;
        }catch (Exception ex){
            ex.printStackTrace();
            return  null;
        }

    }


    private  void ShowGoToSettingsDialog(Context context)
    {
        AlertDialog.Builder builderAlertMsg = new AlertDialog.Builder(context);
        builderAlertMsg.setMessage("Location is not set.  Please go to settings and set your location.");

        builderAlertMsg.setCancelable(true);
        builderAlertMsg.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });


        AlertDialog alert11 = builderAlertMsg.create();
        alert11.show();
    }




}
