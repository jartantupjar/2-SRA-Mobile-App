package com.example.micha.srafarmer.Recommendations;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.micha.srafarmer.DBHelper;
import com.example.micha.srafarmer.Entity.Field;
import com.example.micha.srafarmer.Entity.Recommendation;
import com.example.micha.srafarmer.Login;
import com.example.micha.srafarmer.R;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RecommendationsList extends AppCompatActivity {
    private Field field;
    private TextView tvFieldId, tvLocation;
    private RecyclerView rvRecommendations;
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

        setContentView(R.layout.activity_recommendations_list);
        field = (Field) getIntent().getExtras().get("field");
        tvFieldId = (TextView) findViewById(R.id.recommendations_fieldID);
        tvFieldId.setText("Field ID: "+ field.getId());
        tvLocation = (TextView) findViewById(R.id.recommmendations_location);
        tvLocation.setText(field.getBarangay() +", "+ field.getMunicipality());

        rvRecommendations = (RecyclerView) findViewById(R.id.recommendations_rv);
        RecommendationsListAdapter recommendationsListAdapter =
                new RecommendationsListAdapter((ArrayList<Recommendation>) getIntent().getExtras().get("recommendations"));
        recommendationsListAdapter.setmOnItemClickListener(new RecommendationsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Recommendation recommendation) {
                Intent intent = new Intent();
                intent.putExtra("recommendation", recommendation);
                intent.putExtra("field", field);
                intent.setClass(getBaseContext(), RecommendationActivity.class);
                startActivity(intent);
            }
        });
        rvRecommendations.setAdapter(recommendationsListAdapter);
        rvRecommendations.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false
        ));
    }
}
