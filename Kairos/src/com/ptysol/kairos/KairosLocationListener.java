package com.ptysolutions.kairos;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Created by Ramon on 4/6/2016.
 */
public class KairosLocationListener implements LocationListener {

    private Activity LocalActivity;

    KairosLocationListener(Activity activity) {

         LocalActivity = activity;
    }

    public void onLocationChanged(Location location) {

        SharedPreferences sharedPref = LocalActivity.getSharedPreferences(LocalActivity.getString(R.string.kairos_share_preferences),Context.MODE_PRIVATE);;

        boolean followMe = sharedPref.getBoolean("followMe", false);

        if(followMe)
        {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("latitude", Double.toString(location.getLatitude()));
            editor.putString("longitude",Double.toString(location.getLongitude()));

            LocationHelper locHelper = new LocationHelper();
            LocationInfo locInfo = locHelper.GetLocationByLatLong(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));

            editor.putString("address", locInfo.Address);
            editor.commit();
        }


    }



    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    public void onProviderEnabled(String s) {

    }

    public void onProviderDisabled(String s) {

    }
}
