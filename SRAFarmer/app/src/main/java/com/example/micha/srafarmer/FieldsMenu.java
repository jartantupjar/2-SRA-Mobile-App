package com.example.micha.srafarmer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.micha.srafarmer.CropValidationSurvey.CVSMenu;
import com.example.micha.srafarmer.CropValidationSurvey.Milling;
import com.example.micha.srafarmer.Entity.Field;
import com.example.micha.srafarmer.Entity.Phase;
import com.example.micha.srafarmer.Entity.Problem;
import com.example.micha.srafarmer.Entity.Production;
import com.example.micha.srafarmer.Entity.Recommendation;
import com.example.micha.srafarmer.Problem.ProblemsListActivity;
import com.example.micha.srafarmer.Recommendations.RecommendationsList;
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

public class FieldsMenu extends AppCompatActivity {

    private Field field;
    private TextView tvFieldId, tvLocation;
    private Button btnCvs, btnRecommendations, btnProblems, btnMillProduction, btnDisasterReport;
    private Intent intent;
    private DBHelper dbHelper;
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
        setContentView(R.layout.activity_fields_menu);
        field = (Field) getIntent().getExtras().get("field");
        tvFieldId = (TextView) findViewById(R.id.fields_menu_fieldID);
        tvFieldId.setText("Field ID: "+ field.getId());
        tvLocation = (TextView) findViewById(R.id.fields_menu_location);
        tvLocation.setText(field.getBarangay() +", "+ field.getMunicipality());

        dbHelper = new DBHelper(getBaseContext(), "", null, -1);

        intent = new Intent();
        intent.putExtra("field", field);

        btnCvs = (Button) findViewById(R.id.fields_menu_cvs);
        btnCvs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setClass(getBaseContext(), CVSMenu.class);
                startActivity(intent);
            }
        });

        btnMillProduction = (Button) findViewById(R.id.fields_menu_millProduction);
        btnMillProduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPhase("Milling")) {
                    new GetProduction().execute();
                } else Toast.makeText(getBaseContext(), "Not in Milling phase.", Toast.LENGTH_SHORT).show();
            }
        });

        btnProblems = (Button) findViewById(R.id.fields_menu_problems);
        btnProblems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Problem> problems = dbHelper.getProblemsByFieldID(field.getId());
                if (problems!=null) {
                    intent.putExtra("problems", problems);
                    intent.setClass(getBaseContext(), ProblemsListActivity.class);
                    startActivity(intent);
                } else{
                    Toast.makeText(getBaseContext(), "Problems list is empty.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnRecommendations = (Button) findViewById(R.id.fields_menu_recommendations);
        btnRecommendations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Recommendation> recommendations = dbHelper.getRecommendationsByFieldID(field.getId());
                if (recommendations!=null) {
                    intent.putExtra("recommendations", recommendations);
                    intent.setClass(getBaseContext(), RecommendationsList.class);
                    startActivity(intent);

                } else{
                    Toast.makeText(getBaseContext(), "Recommendations list is empty.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnDisasterReport = (Button) findViewById(R.id.fields_menu_disaster);
        btnDisasterReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    new GetSubmittedDisasterReports().execute();
            }
        });
    }
    private boolean isPhase(String phase){
        Gson gson = new Gson();
        Phase[] phases = gson.fromJson(sharedPreferences.getString(Login.PHASES, null), Phase[].class);
        for (int i =0; i < phases.length; i++){
            if (phases[i].getPhase().equalsIgnoreCase(phase)) return true;
        }
        return false;
    }

    private class GetProduction extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            Response response;
            String result = null;
            OkHttpClient client = new OkHttpClient();
            RequestBody postRequestBody = new FormBody.Builder()
                    .add("fieldID", field.getId()+"")
                    .build();
            Request request = new Request.Builder()
                    .url(sharedPreferences.getString(Login.IPADDRESS, null)+ "GetProduction")
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
            intent.setClass(getBaseContext(), ProductionList.class);
            Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy").create();
            if (!s.equalsIgnoreCase("null"))
                intent.putExtra("production_list", (Serializable) gson.fromJson(s, new TypeToken<List<Production>>(){}.getType()));
            intent.putExtra("field", field);
            startActivity(intent);
        }
    }

    private class GetSubmittedDisasterReports extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            Response response;
            String result = null;
            OkHttpClient client = new OkHttpClient();
            RequestBody postRequestBody = new FormBody.Builder()
                    .add("fieldID", field.getId()+"")
                    .build();
            Request request = new Request.Builder()
                    .url(sharedPreferences.getString(Login.IPADDRESS, null)+ "GetSubmittedDisasterReports")
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
            intent.setClass(getBaseContext(), DisasterReportList.class);
            Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy").create();
            if (!s.equalsIgnoreCase("null"))
            intent.putExtra("disaster_reports", (Serializable) gson.fromJson(s, new TypeToken<List<Problem>>(){}.getType()));
            intent.putExtra("field", field);
            startActivity(intent);
        }
    }


}
