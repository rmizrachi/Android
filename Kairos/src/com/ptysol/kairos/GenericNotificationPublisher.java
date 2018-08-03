package com.ptysolutions.kairos;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * Created by Ramon on 4/6/2016.
 */
public class GenericNotificationPublisher extends BroadcastReceiver {
    public static String NotificationId = "notification-id";
    public static String Notification = "notification";
    android.app.Notification WeatherNotification;

    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

         Notification notification = intent.getParcelableExtra(Notification);
         int id = intent.getIntExtra(NotificationId, 0);

        notificationManager.notify(id, notification);

    }
}