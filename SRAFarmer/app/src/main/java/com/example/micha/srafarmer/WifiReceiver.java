package com.example.micha.srafarmer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

public class WifiReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Login.MyPREFERENCES, MODE_PRIVATE);
        if (sharedPreferences.getBoolean(Login.IS_LOGGED_IN, false)) {
            ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = conMan.getActiveNetworkInfo();
            if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                Log.d("WifiReceiver", "Have Wifi Connection");
                Intent alarmReceiverIntent = new Intent(context, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.
                        getBroadcast(context, 1, alarmReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Service.ALARM_SERVICE);
                alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        30000,
                        30000, pendingIntent);
            } else {
                Log.d("WifiReceiver", "Don't have Wifi Connection");
                Intent alarmReceiverIntent = new Intent(context, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.
                        getBroadcast(context, 1, alarmReceiverIntent, 0);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Service.ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
            }
        }
    }
}
