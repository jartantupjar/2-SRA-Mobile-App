package com.example.micha.srafarmer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.micha.srafarmer.Entity.NotificationsLocal;
import com.example.micha.srafarmer.Entity.Post;
import com.example.micha.srafarmer.Forum.ForumHome;
import com.example.micha.srafarmer.Inbox.Inbox;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private Button fields, forum, inbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences(Login.MyPREFERENCES, MODE_PRIVATE);
        if (!sharedPreferences.getBoolean(Login.IS_LOGGED_IN, false)) {
            Intent i = new Intent();
            i.setClass(getBaseContext(), Login.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
        else {

            Intent alarmReceiverIntent = new Intent(getBaseContext(), AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.
                    getBroadcast(getBaseContext(), 1, alarmReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    30000,
                    30000, pendingIntent);


        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Main Menu");
        setSupportActionBar(myToolbar);
        fields = (Button) findViewById(R.id.btnFields);
        fields.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra("class", "FieldsMenu");
                i.setClass(getBaseContext(), Fields.class);
                startActivity(i);
            }
        });

        forum = (Button) findViewById(R.id.btnForums);
        forum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GetForumPosts().execute();
            }
        });

        inbox = (Button) findViewById(R.id.btnInbox);
        inbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBHelper dbHelper = new DBHelper(getBaseContext(), "", null, -1);
                ArrayList<NotificationsLocal> notificationsLocals = dbHelper.getNotifications();
                if (notificationsLocals!=null){
                    Intent intent = new Intent();
                    intent.putExtra("notifications", notificationsLocals);
                    intent.setClass(getBaseContext(), Inbox.class);
                    startActivity(intent);
                } else
                    Toast.makeText(MainActivity.this, "You have no notifications.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                Intent i = new Intent(getBaseContext(), Login.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                new DBHelper(getBaseContext(),"",null,-1).deleteAll();
                Intent alarmReceiverIntent = new Intent(getBaseContext(), AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.
                        getBroadcast(getBaseContext(), 1, alarmReceiverIntent, 0);
                AlarmManager alarmManager = (AlarmManager) getBaseContext().getSystemService(Service.ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
                startActivity(i);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private class GetForumPosts extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            Response response;
            String result = null;
            OkHttpClient client = new OkHttpClient();
            RequestBody postRequestBody = new FormBody.Builder()
                    .add("pagenum", 1+"")
                    .add("date", sharedPreferences.getLong(Login.DATE, -1)+"")
                    .build();
            Request request = new Request.Builder()
                    .url(sharedPreferences.getString(Login.IPADDRESS, null) + "GetForumPosts")
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
            if (!s.equalsIgnoreCase("null")){
                Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy").create();
                Intent i = new Intent();
                i.putExtra("posts", (Serializable) gson.fromJson(s, new TypeToken<List<Post>>(){}.getType()));
                i.setClass(getBaseContext(), ForumHome.class);
                startActivity(i);
            }
        }
    }


}
