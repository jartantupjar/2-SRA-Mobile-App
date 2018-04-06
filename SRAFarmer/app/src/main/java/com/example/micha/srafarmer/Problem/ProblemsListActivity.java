package com.example.micha.srafarmer.Problem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.micha.srafarmer.DBHelper;
import com.example.micha.srafarmer.Entity.Field;
import com.example.micha.srafarmer.Entity.Problem;
import com.example.micha.srafarmer.Entity.Recommendation;
import com.example.micha.srafarmer.Login;
import com.example.micha.srafarmer.ProductionActivity;
import com.example.micha.srafarmer.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProblemsListActivity extends AppCompatActivity {
    private Field field;
    private TextView tvFieldId, tvLocation;
    private RecyclerView rvProblems;
    private DBHelper dbHelper;
    private SharedPreferences sharedPreferences;

    private Problem selectedProblem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(Login.MyPREFERENCES, MODE_PRIVATE);if (!sharedPreferences.getBoolean(Login.IS_LOGGED_IN, false)) {
            Intent i = new Intent();
            i.setClass(getBaseContext(), Login.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
        setContentView(R.layout.activity_problems);
        dbHelper = new DBHelper(getBaseContext(), "", null, -1);

        field = (Field) getIntent().getExtras().get("field");
        tvFieldId = (TextView) findViewById(R.id.problems_fieldID);
        tvFieldId.setText("Field ID: "+ field.getId());
        tvLocation = (TextView) findViewById(R.id.problems_location);
        tvLocation.setText(field.getBarangay() +", "+ field.getMunicipality());

        rvProblems = (RecyclerView) findViewById(R.id.problems_rv_problemsList);
        ProblemsListAdapter problemsListAdapter = new ProblemsListAdapter(dbHelper.getProblemsByFieldID(field.getId()));
        problemsListAdapter.setmOnItemClickListener(new ProblemsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Problem problem) {
                selectedProblem = problem;
                new GetRecommendationsByProblem().execute();
            }
        });
        rvProblems.setAdapter(problemsListAdapter);
        rvProblems.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false
        ));
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
            intent.putExtra("field", field);
            Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy").create();
            if (!s.equalsIgnoreCase("null")){
                intent.putExtra("recommendations", (Serializable) gson.fromJson(s,  new TypeToken<List<Recommendation>>(){}.getType()));
            }
            intent.putExtra("problem", selectedProblem);
            intent.setClass(getBaseContext(), ProblemActivity.class);
            startActivity(intent);
        }
    }

}
