package com.example.micha.srafarmer.Recommendations;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.micha.srafarmer.DBHelper;
import com.example.micha.srafarmer.Entity.Comment;
import com.example.micha.srafarmer.Entity.Field;
import com.example.micha.srafarmer.Entity.Post;
import com.example.micha.srafarmer.Entity.Recommendation;
import com.example.micha.srafarmer.Forum.ForumPost;
import com.example.micha.srafarmer.Login;
import com.example.micha.srafarmer.MainActivity;
import com.example.micha.srafarmer.R;
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

public class RecommendationActivity extends AppCompatActivity {
    private Field field;
    private TextView tvFieldId, tvLocation, tvName, tvType, tvDescription, tvPhase, tvDuration, tvStatus,tvDate;
    private Recommendation recommendation;
    private LinearLayout layoutButtons;
    private Button btnAccept, btnReject, btnFinish, btnGoToPost;

    private int accepted;
    private SharedPreferences sharedPreferences;

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
        setContentView(R.layout.activity_recommendation);

        field = (Field) getIntent().getExtras().get("field");
        tvFieldId = (TextView) findViewById(R.id.recommendation_fieldID);
        tvFieldId.setText("Field ID: "+ field.getId());
        tvLocation = (TextView) findViewById(R.id.recommmendation_location);
        tvLocation.setText(field.getBarangay() +", "+ field.getMunicipality());

        recommendation = (Recommendation) getIntent().getExtras().get("recommendation");
        tvName = (TextView) findViewById(R.id.recommendation_name);
        tvType = (TextView) findViewById(R.id.recommendation_type);
        tvDescription = (TextView) findViewById(R.id.recommendation_description);
        tvPhase = (TextView) findViewById(R.id.recommendation_phase);
        tvDuration = (TextView) findViewById(R.id.recommendation_duration);
        tvStatus = (TextView) findViewById(R.id.recommendation_status);
        tvDate = (TextView) findViewById(R.id.recommendation_date);
        layoutButtons = (LinearLayout) findViewById(R.id.recommendation_buttons);
        btnAccept = (Button) findViewById(R.id.recommendation_accept);
        btnReject = (Button) findViewById(R.id.recommendation_reject);
        btnFinish = (Button) findViewById(R.id.recommendation_finish);
        btnGoToPost = (Button) findViewById(R.id.recommendation_gotopost);

        tvName.setText("Recommendation: "+ recommendation.getRecommendation());
        tvType.setText("Type: "+ recommendation.getType());
        if(recommendation.getDescription()!=null)
            tvDescription.setText("Description: "+ recommendation.getDescription()); else tvDescription.setVisibility(View.GONE);
        if (recommendation.getPhase() != null)
            tvPhase.setText("Phase: " + recommendation.getPhase()); else tvPhase.setVisibility(View.GONE);
        if (recommendation.getDurationDays()!=0) tvDuration.setText("Duration: "+recommendation.getDurationDays()); else tvDuration.setVisibility(View.GONE);
        tvStatus.setText("Status: "+ recommendation.getStatus());
        tvDate.setText("Date: "+ recommendation.getDate());

        btnGoToPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetPostByRecommendation().execute();
            }
        });

        if (recommendation.getStatus().equalsIgnoreCase("Verifying")){
            layoutButtons.setVisibility(View.VISIBLE);
            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    accepted = 1;
                    new AcceptRejectRecommendation().execute();
                }
            });
            btnReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    accepted = 0;
                    new AcceptRejectRecommendation().execute();
                }
            });
        }
        else if (recommendation.getStatus().equalsIgnoreCase("Active")){
            btnFinish.setVisibility(View.VISIBLE);
            btnFinish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    accepted = 0;
                    new AcceptRejectRecommendation().execute();
                }
            });
        }
    }

    private class AcceptRejectRecommendation extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            Response response;
            String result = null;
            OkHttpClient client = new OkHttpClient();
            RequestBody postRequestBody = new FormBody.Builder()
                    .add("fieldID", field.getId()+"")
                    .add("decision", accepted+"")
                    .add("recommendationID", recommendation.getId()+"")
                    .add("date", sharedPreferences.getLong(Login.DATE, -1)+"")
                    .build();
            Request request = new Request.Builder()
                    .url(sharedPreferences.getString(Login.IPADDRESS, null) + "AcceptRejectRecommendation")
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
            DBHelper dbHelper = new DBHelper(getBaseContext(), "", null, -1);
            if (accepted==1) dbHelper.acceptRecommendation(recommendation);
            else dbHelper.deleteRecommendation(recommendation);
            Intent i = new Intent(getBaseContext(), MainActivity.class);
            startActivity(i);
        }
    }

    private class GetPostByRecommendation extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            Response response;
            String result = null;
            OkHttpClient client = new OkHttpClient();
            RequestBody postRequestBody = new FormBody.Builder()
                    .add("recommendationID", recommendation.getId()+"")
                    .add("date", sharedPreferences.getLong(Login.DATE, -1)+"")
                    .build();
            Request request = new Request.Builder()
                    .url(sharedPreferences.getString(Login.IPADDRESS, null) + "GetPostByRecommendation")
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
                Intent intent = new Intent();
                Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy").create();
                System.out.println(s);
                Post post = gson.fromJson(s, Post.class);
                intent.putExtra("post", post);
                intent.putExtra("comments", post.getComments());
                intent.setClass(getBaseContext(), ForumPost.class);
                startActivity(intent);
            } else{
                Toast.makeText(getBaseContext(), "No Post Found", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
