package com.ptysol.kairos;


import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.NotificationCompat;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Calendar;
import java.util.Date;


/**
 * Created by Ramon on 4/4/2016.
 */
public class WeatherNotificationPublisher extends BroadcastReceiver {
    public static String NotificationId = "notification-id";
    public static String Notification = "notification";
    public static String Latitude = "latitude";
    public static String Longitude = "longitude";
    public static String Address = "address";

    Notification WeatherNotification;

    SettingsFragment LocalSettingFragment = null;

    public void onReceive(Context context, Intent intent) {
        try {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            String lat, lon, addrs;

            SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.kairos_share_preferences), Context.MODE_PRIVATE);

            lat = sharedPref.getString("latitude", " ");
            lon = sharedPref.getString("longitude", " ");
            addrs = sharedPref.getString("address", " ");


            Calendar calendar = Calendar.getInstance();
            long date = calendar.getTimeInMillis();

            WeatherHelper weatherHelper = new WeatherHelper();
            WeatherResult[] weatherResults = weatherHelper.GetWeather(lat, lon);

            // Notification notification = intent.getParcelableExtra(Notification);
            int id = intent.getIntExtra(NotificationId, 0);

            NotificationCompat.BigTextStyle inboxStyle = new NotificationCompat.BigTextStyle();
            inboxStyle.setSummaryText("Morning: " + weatherResults[0].MorningTemp + " · Day: " + weatherResults[0].DayTemp + " · Night: " + weatherResults[0].NightTemp );
            inboxStyle.setBigContentTitle(weatherResults[0].MinTemp + "/" + weatherResults[0].MaxTemp + "  " + addrs);
            inboxStyle.bigText(WordUtils.capitalizeFully(weatherResults[0].WeatherSummary) + "\n" + weatherResults[0].Message);

            int firstForecastEntryToUse = GetFirstForecastEntry(weatherResults[0].NormalDate);

            WeatherNotification = new NotificationCompat.Builder(context)
                    .setTicker(weatherResults[firstForecastEntryToUse].WeatherSummary)
                    .setSmallIcon(weatherResults[firstForecastEntryToUse].WeatherNotificationIcon)
                    .setAutoCancel(true)
                    .setColor(weatherResults[firstForecastEntryToUse].BgColor)
                    .setWhen(date)
                    .setStyle(inboxStyle)
                    .setContentTitle(weatherResults[firstForecastEntryToUse].MinTemp + "/" + weatherResults[firstForecastEntryToUse].MaxTemp + "  " + addrs)
                    .setContentText(weatherResults[firstForecastEntryToUse].WeatherSummary)
                    .build();


          /*  WeatherNotification = new NotificationCompat.Builder(context)
                    .setTicker(weatherResults[0].WeatherSummary)
                    .setSmallIcon(weatherResults[0].WeatherNotificationIcon)
                    //.setContentTitle(weatherResults[0].MinTemp + "/" + weatherResults[0].MaxTemp + "  " + addrs)
                    .setContentTitle("mor: " + weatherResults[0].MorningTemp + " day:" + weatherResults[0].DayTemp + " night:" + weatherResults[0].NightTemp + " " + addrs)
                    .setContentText(weatherResults[0].WeatherSummary)
                    .setAutoCancel(true)
                    .setColor(weatherResults[0].BgColor)
                    .setWhen(date)
                    .setStyle(inboxStyle)
                    .build();*/

            notificationManager.notify(id, WeatherNotification);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

        private void SetSettingsFragmentHandler(SettingsFragment settingsFragnment){
           LocalSettingFragment = settingsFragnment;

    }

    private  int GetFirstForecastEntry(Date forecastDate)
    {
        Calendar c = Calendar.getInstance();
        int currentDOW = c.get(Calendar.DAY_OF_WEEK);

        c.setTime(forecastDate);
        int forecastDOW = c.get(Calendar.DAY_OF_WEEK);

        if(forecastDOW != currentDOW) return  1;
        return  0;

    }

}
