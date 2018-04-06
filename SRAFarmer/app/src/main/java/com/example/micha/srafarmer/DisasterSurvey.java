package com.example.micha.srafarmer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.micha.srafarmer.Entity.Field;
import com.example.micha.srafarmer.Entity.Problem;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DisasterSurvey extends AppCompatActivity {
    private Field field;
    private TextView tvFieldId, tvLocation, tvName, tvDescription, tvMaxArea, tvFieldArea;
    private EditText etDamage;

    private double damage, maxArea;
    private DBHelper dbHelper;
    private SharedPreferences sharedPreferences;

    private Button btnSubmit;

    private Problem disaster;


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
        setContentView(R.layout.activity_disaster_survey);
        dbHelper = new DBHelper(getBaseContext(), "", null, -1);

        field = (Field) getIntent().getExtras().get("field");
        disaster = (Problem) getIntent().getExtras().get("disaster");

        tvFieldId = (TextView) findViewById(R.id.problem_fieldID);
        tvFieldId.setText("Field ID: "+ field.getId());
        tvLocation = (TextView) findViewById(R.id.problem_location);
        tvLocation.setText(field.getBarangay() +", "+ field.getMunicipality());

        tvName = (TextView) findViewById(R.id.disaster_survey_name);
        tvDescription = (TextView) findViewById(R.id.disaster_survey_description);
        tvMaxArea = (TextView) findViewById(R.id.disaster_survey_standing_area);
        tvFieldArea = (TextView) findViewById(R.id.disaster_survey_area);
        etDamage = (EditText) findViewById(R.id.disaster_survey_damage);

        tvName.setText("Name: "+ disaster.getName());
        tvDescription.setText("Description: "+ disaster.getDescription());

        maxArea = field.getArea() - (dbHelper.getTotalAreaHarvested(sharedPreferences.getInt(Login.YEAR, -1), field.getId())+ field.getDamage());
        tvMaxArea.setText(maxArea+"");
        tvFieldArea.setText(field.getArea()+"");

        btnSubmit = (Button) findViewById(R.id.disaster_survey_btnSubmitSurvey);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(etDamage.getText().toString()+"************");
                damage = Double.parseDouble(etDamage.getText().toString());
                new SubmitDisasterSurvey().execute();
            }
        });

    }

    private class SubmitDisasterSurvey extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            Response response;
            String result = null;
            OkHttpClient client = new OkHttpClient();
            RequestBody postRequestBody = new FormBody.Builder()
                    .add("fieldID", field.getId()+"")
                    .add("problemID", disaster.getProblemsID()+"")
                    .add("damage", damage+"")
                    .add("date", sharedPreferences.getLong(Login.DATE, -1)+"")
                    .build();
            Request request = new Request.Builder()
                    .url(sharedPreferences.getString(Login.IPADDRESS, null)+ "SubmitDisasterSurvey")
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
            field.setDamage(field.getDamage()+damage);
            dbHelper.updateField(field);
            Intent intent = new Intent();
            intent.setClass(getBaseContext(), MainActivity.class);
            startActivity(intent);
        }
    }
}
