package com.example.micha.srafarmer.Inbox;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.micha.srafarmer.DBHelper;
import com.example.micha.srafarmer.DisasterSurvey;
import com.example.micha.srafarmer.Entity.Comment;
import com.example.micha.srafarmer.Entity.NotificationsLocal;
import com.example.micha.srafarmer.Entity.Post;
import com.example.micha.srafarmer.Entity.Problem;
import com.example.micha.srafarmer.Entity.Recommendation;
import com.example.micha.srafarmer.Forum.ForumPost;
import com.example.micha.srafarmer.Forum.MyPosts;
import com.example.micha.srafarmer.Login;
import com.example.micha.srafarmer.Problem.ProblemActivity;
import com.example.micha.srafarmer.R;
import com.example.micha.srafarmer.Recommendations.RecommendationActivity;
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

    public class Inbox extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<NotificationsLocal> notificationsLocals;

    private SharedPreferences sharedPreferences;
    private DBHelper dbHelper;
    private Problem selectedProblem;

    private int problemID;
    private int fieldID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(Login.MyPREFERENCES, MODE_PRIVATE);
        if (!sharedPreferences.getBoolean(Login.IS_LOGGED_IN, false)) {
            Intent i = new Intent();
            i.setClass(getBaseContext(), Login.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
        setContentView(R.layout.activity_inbox);
        dbHelper = new DBHelper(getBaseContext(), "", null, -1);

        notificationsLocals = (ArrayList<NotificationsLocal>) getIntent().getExtras().get("notifications");

        recyclerView = (RecyclerView) findViewById(R.id.inbox_rv);
        NotificationsListAdapter notificationsListAdapter = new NotificationsListAdapter(notificationsLocals);
        notificationsListAdapter.setmOnItemClickListener(new NotificationsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(NotificationsLocal notificationsLocal) {
                if (notificationsLocal.getType().equalsIgnoreCase("Forum")){
                    new GetPostsByFarmer().execute();
                }
                if (notificationsLocal.getType().equalsIgnoreCase("Problem")){
                    selectedProblem = dbHelper.getProblemByID(notificationsLocal.getProblemID());
                    fieldID = selectedProblem.getFieldsID();
                    new GetRecommendationsByProblem().execute();
                }
                if (notificationsLocal.getType().equalsIgnoreCase("Disaster")){
                    problemID = notificationsLocal.getProblemID();
                    fieldID = notificationsLocal.getFieldID();
                    new GetDisasterSurvey().execute();
                }
                if (notificationsLocal.getType().equalsIgnoreCase("recommendation")
                        || notificationsLocal.getType().equalsIgnoreCase("reminder")){
                    Intent intent = new Intent();
                    intent.putExtra("recommendation", dbHelper.getRecommendation(notificationsLocal.getFieldID(), notificationsLocal.getRecommendationID()));
                    intent.putExtra("field", dbHelper.getFieldByID(notificationsLocal.getFieldID()));
                    intent.setClass(getBaseContext(), RecommendationActivity.class);
                    startActivity(intent);
                }

            }
        });
        notificationsListAdapter.setmOnLongClickListener(new NotificationsListAdapter.OnLongClickListener() {
            @Override
            public void onLongClickListener(NotificationsLocal notificationsLocal) {

            }
        });
        recyclerView.setAdapter(notificationsListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false
        ));
    }

    private class GetPostsByFarmer extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            Response response;
            String result = null;
            OkHttpClient client = new OkHttpClient();
            RequestBody postRequestBody = new FormBody.Builder()
                    .add("page_num", 1+"")
                    .add("farmer_name", sharedPreferences.getString(Login.NAME, null))
                    .add("date", sharedPreferences.getLong(Login.DATE, -1) +"")
                    .build();
            Request request = new Request.Builder()
                    .url(sharedPreferences.getString(Login.IPADDRESS, null) + "GetPostsByFarmer")
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
            if (!s.equalsIgnoreCase("null")) {
                Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy").create();
                Intent intent = new Intent();
                intent.putExtra("posts", (Serializable) gson.fromJson(s, new TypeToken<List<Post>>(){}.getType()));
                intent.setClass(getBaseContext(), MyPosts.class);
                startActivity(intent);
            } else{
                Toast.makeText(getBaseContext(), "You have no posts.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetRecommendationsByProblem extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            Response response;
            String result = null;
            OkHttpClient client = new OkHttpClient();
            RequestBody postRequestBody = new FormBody.Builder()
                    .add("problemID", selectedProblem.getProblemsID()+"")
                    .build();
            Request request = new Request.Builder()
                    .url(sharedPreferences.getString(Login.IPADDRESS, null) + "GetRecommendationsByProblem")
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
            Intent intent = new Intent();
            intent.putExtra("field", dbHelper.getFieldByID(fieldID));
            Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy").create();
            if (!s.equalsIgnoreCase("null")){
                intent.putExtra("recommendations", (Serializable) gson.fromJson(s,  new TypeToken<List<Recommendation>>(){}.getType()));
            }
            intent.putExtra("problem", selectedProblem);
            intent.setClass(getBaseContext(), ProblemActivity.class);
            startActivity(intent);
        }
    }

    private class GetDisasterSurvey extends AsyncTask<String, Void,String>{
        @Override
        protected String doInBackground(String... strings) {
            Response response;
            String result = null;
            OkHttpClient client = new OkHttpClient();
            RequestBody postRequestBody = new FormBody.Builder()
                    .add("problemID", problemID+"")
                    .build();
            Request request = new Request.Builder()
                    .url(sharedPreferences.getString(Login.IPADDRESS, null) + "GetDisasterSurvey")
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
                Intent intent = new Intent();
                intent.putExtra("field", dbHelper.getFieldByID(fieldID));
                intent.putExtra("disaster", gson.fromJson(s, Problem.class));
                intent.setClass(getBaseContext(), DisasterSurvey.class);
                startActivity(intent);
            }
        }
    }

}
