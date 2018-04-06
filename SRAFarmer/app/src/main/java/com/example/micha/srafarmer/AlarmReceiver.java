package com.example.micha.srafarmer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

import com.example.micha.srafarmer.Entity.Notifications;
import com.example.micha.srafarmer.Entity.NotificationsLocal;
import com.example.micha.srafarmer.Inbox.Inbox;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }
    private SharedPreferences sharedPreferences;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        sharedPreferences = context.getSharedPreferences(Login.MyPREFERENCES, MODE_PRIVATE);
        if (sharedPreferences.getBoolean(Login.IS_LOGGED_IN, false)) {
            this.context = context;
            sharedPreferences = context.getSharedPreferences(Login.MyPREFERENCES, Login.MODE_PRIVATE);
            new CheckForNotifs().execute();
        }
    }

    private class CheckForNotifs extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            Response response;
            String result = null;
            OkHttpClient client = new OkHttpClient();
            RequestBody postRequestBody = new FormBody.Builder()
                    .add("farmer",sharedPreferences.getString(Login.NAME, null))
                    .build();
            Request request = new Request.Builder()
                    .url(sharedPreferences.getString(Login.IPADDRESS, null) + "CheckForNotifs")
                    .post(postRequestBody)
                    .build();
            try {
                response = client.newCall(request).execute();
                result = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s!=null){
            if (!s.equalsIgnoreCase("null")) {
                int notifs = 0;
                DBHelper dbHelper = new DBHelper(context, "", null, -1);
                Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy").create();
                Notifications notifications = gson.fromJson(s, Notifications.class);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(Login.YEAR, notifications.getYear());
                editor.putString(Login.PHASES, gson.toJson(notifications.getPhases()));
                editor.putLong(Login.DATE, notifications.getDate().getTime());
                editor.commit();

                if (notifications != null) {
                    ArrayList<NotificationsLocal> notificationsLocals = new ArrayList<>();
                    if (notifications.getProblems() != null) {
                        dbHelper.addProblems(notifications.getProblems());
                        notifs += notifications.getProblems().size();
                        for (int i = 0; i < notifications.getProblems().size(); i++) {
                            NotificationsLocal notificationsLocal = new NotificationsLocal();
                            notificationsLocal.setType("Problem");
                            notificationsLocal.setFieldID(notifications.getProblems().get(i).getFieldsID());
                            notificationsLocal.setMessage(notifications.getProblems().get(i).getMessage());
                            notificationsLocal.setId(notifications.getProblems().get(i).getNotificationID());
                            notificationsLocal.setProblemID(notifications.getProblems().get(i).getId());
                            notificationsLocal.setDate(notifications.getProblems().get(i).getDate());
                            notificationsLocals.add(notificationsLocal);
                        }
                    }
                    if (notifications.getRecommendations() != null) {
                        dbHelper.addRecommendations(notifications.getRecommendations());
                        notifs += notifications.getRecommendations().size();
                        for (int i = 0; i < notifications.getRecommendations().size(); i++) {
                            NotificationsLocal notificationsLocal = new NotificationsLocal();
                            notificationsLocal.setType("Recommendation");
                            notificationsLocal.setFieldID(notifications.getRecommendations().get(i).getFieldID());
                            notificationsLocal.setMessage(notifications.getRecommendations().get(i).getMessage());
                            notificationsLocal.setId(notifications.getRecommendations().get(i).getNotificationID());
                            notificationsLocal.setRecommendationID(notifications.getRecommendations().get(i).getId());
                            notificationsLocal.setDate(notifications.getRecommendations().get(i).getDate());
                            notificationsLocals.add(notificationsLocal);
                        }
                    }
                    if (notifications.getDisasters() != null) {
                        notifs += notifications.getDisasters().size();
                        for (int i = 0; i < notifications.getDisasters().size(); i++) {
                            NotificationsLocal notificationsLocal = new NotificationsLocal();
                            notificationsLocal.setType("Disaster");
                            notificationsLocal.setFieldID(notifications.getDisasters().get(i).getFieldsID());
                            notificationsLocal.setMessage(notifications.getDisasters().get(i).getMessage());
                            notificationsLocal.setId(-1 * notifications.getDisasters().get(i).getDisasterAlertID());
                            notificationsLocal.setProblemID(notifications.getDisasters().get(i).getProblemsID());
                            notificationsLocal.setDate(notifications.getDisasters().get(i).getDate());
                            notificationsLocals.add(notificationsLocal);
                        }
                    }
                    if (notifications.getPosts() != null) {
                        notifs += notifications.getPosts().size();
                        for (int i = 0; i < notifications.getPosts().size(); i++) {
                            NotificationsLocal notificationsLocal = new NotificationsLocal();
                            notificationsLocal.setType("Forum");
                            notificationsLocal.setMessage(notifications.getPosts().get(i).getMessage());
                            notificationsLocal.setId(notifications.getPosts().get(i).getNotificationID());
                            notificationsLocal.setPostID(notifications.getPosts().get(i).getId());
                            notificationsLocal.setDate(notifications.getPosts().get(i).getDatePosted());
                            notificationsLocals.add(notificationsLocal);
                        }
                    }
                    if (notifications.getReminders()!=null){
                        notifs += notifications.getReminders().size();
                        for (int i = 0; i < notifications.getReminders().size(); i++) {
                            NotificationsLocal notificationsLocal = new NotificationsLocal();
                            notificationsLocal.setType("Reminder");
                            notificationsLocal.setMessage(notifications.getReminders().get(i).getMessage());
                            notificationsLocal.setRecommendationID(notifications.getReminders().get(i).getId());
                            notificationsLocal.setFieldID(notifications.getReminders().get(i).getFieldID());
                            notificationsLocal.setDate(notifications.getReminders().get(i).getDate());
                            notificationsLocals.add(notificationsLocal);
                        }
                    }
                    if (notificationsLocals.size()>0) {
                        dbHelper.addNotifications(notificationsLocals);
                        Intent intent = new Intent();
                        intent.putExtra("notifications", dbHelper.getNotifications());
                        intent.setClass(context, Inbox.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        NotificationCompat.Builder notificationBuilder
                                = new NotificationCompat.Builder(context)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("SRA")
                                .setContentText("You have " + notifs + " new notification/s")
                                .setAutoCancel(true)
                                .setDefaults(Notification.DEFAULT_SOUND)
                                .setContentIntent(pendingIntent)
                                .setDefaults(Notification.DEFAULT_VIBRATE);
                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
                        notificationManager.notify(1, notificationBuilder.build());
                    }
                }
            }
            }
        }
    }
}
