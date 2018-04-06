package com.example.micha.srafarmer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.micha.srafarmer.Entity.Field;
import com.example.micha.srafarmer.Entity.Problem;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DisasterReport extends AppCompatActivity {
    private ArrayList<Problem> disasters;

    private Field field;
    private TextView tvFieldId, tvLocation, tvDescription,tvArea, tvMaxArea;
    private EditText etDamage;
    private Spinner name;

    private double damage, maxArea;
    private DBHelper dbHelper;
    private SharedPreferences sharedPreferences;

    private int selectedDisasterID;

    private Button btnSubmit;

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disaster_report);

        dbHelper = new DBHelper(getBaseContext(), "", null, -1);

        field = (Field) getIntent().getExtras().get("field");
        disasters = (ArrayList<Problem>) getIntent().getExtras().get("disasters");

        String[] disasterNames = new String[disasters.size()];
        for (int i =0; i< disasterNames.length;i++){
            disasterNames[i] = disasters.get(i).getName();
        }

        tvFieldId = (TextView) findViewById(R.id.disaster_report_fieldID);
        tvFieldId.setText("Field ID: "+ field.getId());
        tvLocation = (TextView) findViewById(R.id.disaster_report_location);
        tvLocation.setText(field.getBarangay() +", "+ field.getMunicipality());

        tvDescription = (TextView) findViewById(R.id.disaster_report_description);
        tvMaxArea = (TextView) findViewById(R.id.disaster_report_standing_area);
        tvArea = (TextView) findViewById(R.id.disaster_report_area);
        etDamage = (EditText) findViewById(R.id.disaster_report_damage);
        btnSubmit = (Button) findViewById(R.id.disaster_report_submit);
        name = (Spinner) findViewById(R.id.disaster_report_name);

        maxArea = field.getArea() - (dbHelper.getTotalAreaHarvested(sharedPreferences.getInt(Login.YEAR, -1), field.getId())+ field.getDamage());
        tvMaxArea.setText(maxArea+"");

        tvArea.setText(field.getArea()+"");
        etDamage.setText("0");

        ArrayAdapter<CharSequence> disasterAdapter = new ArrayAdapter<CharSequence>(getBaseContext(),
                android.R.layout.simple_spinner_item, disasterNames);
        disasterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        name.setAdapter(disasterAdapter);
        name.setSelection(0);
        selectedDisasterID = disasters.get(0).getProblemsID();
        tvDescription.setText("Description: " + disasters.get(0).getDescription());
        name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tvDescription.setText("Description: " + disasters.get(i).getDescription());
                selectedDisasterID = disasters.get(i).getProblemsID();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                damage = Double.parseDouble(etDamage.getText().toString());
                new SubmitDisasterReport().execute();
            }
        });
    }

    private class SubmitDisasterReport extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {

            Response response;
            String result = null;
            OkHttpClient client = new OkHttpClient();
            RequestBody postRequestBody = new FormBody.Builder()
                    .add("fieldID", field.getId()+"")
                    .add("problemID", selectedDisasterID+"")
                    .add("date", sharedPreferences.getLong(Login.DATE, -1)+"")
                    .add("damage", damage +"")
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
