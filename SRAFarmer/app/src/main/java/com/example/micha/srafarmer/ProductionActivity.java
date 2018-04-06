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
import android.widget.Toast;

import com.example.micha.srafarmer.Entity.Field;
import com.example.micha.srafarmer.Entity.Production;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProductionActivity extends AppCompatActivity {

    private Field field;
    private TextView tvFieldId, tvLocation, tvTotalArea, tvMaxArea;
    private EditText etTonsCane, etLkg, etAreaHarvested;
    private Button btnAdd;

    private double maxArea;
    private Production production;

    private DBHelper dbHelper;
    private SharedPreferences sharedPreferences;

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
        setContentView(R.layout.activity_production);
        dbHelper = new DBHelper(getBaseContext(), "", null, -1);

        field = (Field) getIntent().getExtras().get("field");

        tvFieldId = (TextView) findViewById(R.id.production_fieldID);
        tvFieldId.setText("Field ID: "+ field.getId());
        tvLocation = (TextView) findViewById(R.id.production_location);
        tvLocation.setText(field.getBarangay() +", "+ field.getMunicipality());

        etTonsCane = (EditText) findViewById(R.id.production_tonsCane);
        etLkg = (EditText) findViewById(R.id.production_lkg);
        etAreaHarvested = (EditText) findViewById(R.id.production_area_harvested);

        tvTotalArea = (TextView) findViewById(R.id.production_area);
        tvMaxArea = (TextView) findViewById(R.id.production_standing_area);
        maxArea =field.getArea() - (dbHelper.getTotalAreaHarvested(sharedPreferences.getInt(Login.YEAR, -1), field.getId()) +
                field.getDamage());
        tvMaxArea.setText(maxArea+"");
        tvTotalArea.setText(""+field.getArea());


        btnAdd = (Button) findViewById(R.id.production_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                production = new Production();
                production.setYear(sharedPreferences.getInt(Login.YEAR, -1));
                production.setFieldsID(field.getId());
                production.setLkg(Double.parseDouble(etLkg.getText().toString()));
                production.setTonsCane(Double.parseDouble(etTonsCane.getText().toString()));
                production.setAreaHarvested(Double.parseDouble(etAreaHarvested.getText().toString()));
                production.setDate(new Date(sharedPreferences.getLong(Login.DATE, -1)));
                new SendProduction().execute();
            }
        });

    }

    private class SendProduction extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            Response response;
            String result = null;
            OkHttpClient client = new OkHttpClient();
            RequestBody postRequestBody = new FormBody.Builder()
                    .add("production", new Gson().toJson(production))
                    .build();
            Request request = new Request.Builder()
                    .url(sharedPreferences.getString(Login.IPADDRESS, null) + "SendProduction")
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
                ArrayList<Production> list = gson.fromJson(s, new TypeToken<List<Production>>(){}.getType());
                dbHelper.updateProduction(list);
                Toast.makeText(getBaseContext(),"Production has been submitted.", Toast.LENGTH_LONG).show();
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                startActivity(i);
            } else{
                Toast.makeText(getBaseContext(), "Failed to submit production.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
