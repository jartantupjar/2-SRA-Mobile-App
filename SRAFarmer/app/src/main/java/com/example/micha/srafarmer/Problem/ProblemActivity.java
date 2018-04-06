package com.example.micha.srafarmer.Problem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.micha.srafarmer.DBHelper;
import com.example.micha.srafarmer.Entity.Field;
import com.example.micha.srafarmer.Entity.Problem;
import com.example.micha.srafarmer.Entity.Recommendation;
import com.example.micha.srafarmer.FieldsMenu;
import com.example.micha.srafarmer.Login;
import com.example.micha.srafarmer.MainActivity;
import com.example.micha.srafarmer.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProblemActivity extends AppCompatActivity {
    private Field field;
    private TextView tvFieldId, tvLocation, tvName, tvType, tvDescription, tvStatus, tvRecommendations;
    private Problem problem;
    private RecyclerView rvRecommendations;
    private Button btnUseRecommendations, btnAccept, btnReject;
    private LinearLayout acceptIgnore;
    private DBHelper dbHelper;
    private SharedPreferences sharedPreferences;

    private int accept;

    private ArrayList<Recommendation> selectedRecommendations;

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
        setContentView(R.layout.activity_problem);

        field = (Field) getIntent().getExtras().get("field");
        tvFieldId = (TextView) findViewById(R.id.problem_fieldID);
        tvFieldId.setText("Field ID: "+ field.getId());
        tvLocation = (TextView) findViewById(R.id.problem_location);
        tvLocation.setText(field.getBarangay() +", "+ field.getMunicipality());

        dbHelper = new DBHelper(getBaseContext(), "", null, -1);

        problem = (Problem) getIntent().getExtras().get("problem");

        tvName = (TextView) findViewById(R.id.problem_name);
        tvType = (TextView) findViewById(R.id.problem_type);
        tvDescription = (TextView) findViewById(R.id.problem_description);
        tvStatus = (TextView) findViewById(R.id.problem_status);
        tvRecommendations = (TextView) findViewById(R.id.problem_tvrecommendations);
        acceptIgnore = (LinearLayout) findViewById(R.id.problem_accept_ignore);
        btnAccept = (Button) findViewById(R.id.problem_accept);
        btnReject = (Button) findViewById(R.id.problem_reject);

        tvName.setText("Problem Name: " + problem.getName());
        tvType.setText("Type: "+ problem.getType());
        tvDescription.setText("Description: "+ problem.getDescription());
        tvStatus.setText("Status: "+ problem.getStatus());

        selectedRecommendations = new ArrayList<>();

        rvRecommendations = (RecyclerView) findViewById(R.id.problem_recommendations);
        btnUseRecommendations = (Button) findViewById(R.id.problem_btnUseRecommendations);

        ArrayList<Recommendation> recommendations = (ArrayList<Recommendation>) getIntent().getExtras().get("recommendations");
        if (problem.getStatus().equalsIgnoreCase("active") && recommendations!=null) {
            ProblemRecommendationsAdapter problemRecommendationsAdapter = new
                    ProblemRecommendationsAdapter(recommendations);
            problemRecommendationsAdapter.setmOnItemClickListener(new ProblemRecommendationsAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Recommendation recommendation, Boolean checked) {
                    if (checked) {
                        recommendation.setDate(new Date(sharedPreferences.getLong(Login.DATE, -1)));
                        selectedRecommendations.add(recommendation);
                    } else {
                        selectedRecommendations.remove(recommendation);
                    }
                }
            });
            rvRecommendations.setAdapter(problemRecommendationsAdapter);
            rvRecommendations.setLayoutManager(new LinearLayoutManager(
                    this, LinearLayoutManager.VERTICAL, false
            ));
            btnUseRecommendations.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectedRecommendations.size()>0)
                    new AddRecommendations().execute();
                    else
                        Toast.makeText(ProblemActivity.this, "Please select recommendations.", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (problem.getStatus().equalsIgnoreCase("verifying")){
            tvRecommendations.setVisibility(View.GONE);
            rvRecommendations.setVisibility(View.GONE);
            btnUseRecommendations.setVisibility(View.GONE);
            acceptIgnore.setVisibility(View.VISIBLE);
            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    accept = 1;
                    new AcceptIgnoreProblem().execute();
                }
            });
            btnReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    accept = 0;
                    new AcceptIgnoreProblem().execute();
                }
            });
        }
        else {
            tvRecommendations.setVisibility(View.GONE);
            rvRecommendations.setVisibility(View.GONE);
            btnUseRecommendations.setText("Back");
            btnUseRecommendations.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.putExtra("field", field);
                    intent.setClass(getBaseContext(), FieldsMenu.class);
                    startActivity(intent);
                }
            });
        }
    }

    private class AddRecommendations extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            Response response;
            String result = null;
            OkHttpClient client = new OkHttpClient();
            RequestBody postRequestBody = new FormBody.Builder()
                    .add("problem", problem.getId()+"")
                    .add("recommendations",new Gson().toJson(selectedRecommendations))
                    .add("fieldID", field.getId()+"")
                    .build();
            Request request = new Request.Builder()
                    .url(sharedPreferences.getString(Login.IPADDRESS, null) + "AddRecommendations")
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
            if(!s.equalsIgnoreCase("null")){
                System.out.println(s);
                Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy").create();
                dbHelper.deleteProblem(problem.getId());
                dbHelper.addRecommendations((ArrayList<Recommendation>) gson.fromJson(s, new TypeToken<List<Recommendation>>(){}.getType()));
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                Toast.makeText(getBaseContext(), "Added recommendations successfully.", Toast.LENGTH_SHORT).show();
                startActivity(i);
            }
        }
    }

    private class AcceptIgnoreProblem extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            Response response;
            String result = null;
            OkHttpClient client = new OkHttpClient();
            RequestBody postRequestBody = new FormBody.Builder()
                    .add("problem", problem.getId()+"")
                    .add("accept", accept+"")
                    .build();
            Request request = new Request.Builder()
                    .url(sharedPreferences.getString(Login.IPADDRESS, null) + "AcceptIgnoreProblem")
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
            if (accept ==0) {
                dbHelper.deleteProblem(problem.getId());
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                Toast.makeText(getBaseContext(), "Rejected problem.", Toast.LENGTH_SHORT).show();
                startActivity(i);
            }
            else{
                dbHelper.acceptProblem(problem);
                Toast.makeText(getBaseContext(), "Accepted problem.", Toast.LENGTH_SHORT).show();
                new GetRecommendationsByProblem().execute();
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
                    .add("problemID", problem.getProblemsID()+"")
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
            if (!s.equalsIgnoreCase("null")){
                Intent intent = new Intent();
                Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy").create();
                intent.putExtra("field", field);
                intent.putExtra("recommendations", (Serializable) gson.fromJson(s,  new TypeToken<List<Recommendation>>(){}.getType()));
                intent.putExtra("problem", dbHelper.getProblemByID(problem.getId()));
                intent.setClass(getBaseContext(), ProblemActivity.class);
                startActivity(intent);
            }
        }
    }
}
