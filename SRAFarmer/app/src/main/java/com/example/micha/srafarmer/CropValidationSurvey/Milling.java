package com.example.micha.srafarmer.CropValidationSurvey;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.micha.srafarmer.DBHelper;
import com.example.micha.srafarmer.Entity.CVS;
import com.example.micha.srafarmer.Entity.Field;
import com.example.micha.srafarmer.Login;
import com.example.micha.srafarmer.MainActivity;
import com.example.micha.srafarmer.R;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Milling extends AppCompatActivity {

    private DBHelper dbHelper;
    private Field field;
    private SharedPreferences sharedPreferences;

    private TextView tvFieldID, tvLocation;
    private EditText etNumMillable, etAvg, etBrix, etStalkLength, etDiameter, etWeight;
    private Button btnSave;

    private CVS cvs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milling);
        sharedPreferences = getSharedPreferences(Login.MyPREFERENCES, MODE_PRIVATE);
        if (!sharedPreferences.getBoolean(Login.IS_LOGGED_IN, false)) {
            Intent i = new Intent();
            i.setClass(getBaseContext(), Login.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }

        field = (Field) getIntent().getExtras().get("field");
        dbHelper = new DBHelper(getBaseContext(), "", null, -1);

        tvFieldID = (TextView) findViewById(R.id.milling_fieldID);
        tvLocation = (TextView) findViewById(R.id.miling_location);

        tvFieldID.setText("Field ID: " + field.getId());
        tvLocation.setText(field.getBarangay() +", "+ field.getMunicipality());

        etNumMillable = (EditText) findViewById(R.id.milling_numMillable);
        etAvg = (EditText) findViewById(R.id.milling_avg);
        etBrix = (EditText) findViewById(R.id.milling_brix);
        etStalkLength = (EditText) findViewById(R.id.milling_stalkLength);
        etDiameter = (EditText) findViewById(R.id.milling_diameter);
        etWeight = (EditText) findViewById(R.id.milling_weight);

         cvs = dbHelper.getMillingCVS(sharedPreferences.getInt(Login.YEAR, -1), field.getId());
        if (cvs!=null){
            etNumMillable.setText(cvs.getNumMillable()+"");
            etAvg.setText(cvs.getAvgMillableStool()+"");
            etBrix.setText(cvs.getBrix()+"");
            etStalkLength.setText(cvs.getStalkLength()+"");
            etDiameter.setText(cvs.getDiameter()+"");
            etWeight.setText(cvs.getWeight()+"");
        }

        btnSave = (Button) findViewById(R.id.milling_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cvs = new CVS();
                cvs.setYear(sharedPreferences.getInt(Login.YEAR, -1));
                cvs.setField(field.getId());
                if (!TextUtils.isEmpty(etNumMillable.getText()))
                cvs.setNumMillable(Integer.parseInt(etNumMillable.getText().toString()));
                if (!TextUtils.isEmpty(etAvg.getText()))
                cvs.setAvgMillableStool(Double.parseDouble(etAvg.getText().toString()));
                if (!TextUtils.isEmpty(etBrix.getText()))
                cvs.setBrix(Double.parseDouble(etBrix.getText().toString()));
                if (!TextUtils.isEmpty(etStalkLength.getText()))
                cvs.setStalkLength(Double.parseDouble(etStalkLength.getText().toString()));
                if (!TextUtils.isEmpty(etDiameter.getText()))
                cvs.setDiameter(Double.parseDouble(etDiameter.getText().toString()));
                if (!TextUtils.isEmpty(etWeight.getText()))
                    cvs.setWeight(Double.parseDouble(etWeight.getText().toString()));
                dbHelper.saveMillingCVS(cvs);
                new UpdateMillingCVS().execute();
            }
        });

    }

    private class UpdateMillingCVS   extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            Response response;
            String result = null;
            OkHttpClient client = new OkHttpClient();
            RequestBody postRequestBody = new FormBody.Builder()
                    .add("cvs", new Gson().toJson(dbHelper.getMillingCVS(sharedPreferences.getInt(Login.YEAR, -1), field.getId())))
                    .build();
            Request request = new Request.Builder()
                    .url(sharedPreferences.getString(Login.IPADDRESS, null) + "UploadMillingSurvey")
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

            Intent i = new Intent();
            i.setClass(getBaseContext(), MainActivity.class);
            Toast.makeText(getBaseContext(), "Crop validation survey has been updated.", Toast.LENGTH_SHORT).show();
            startActivity(i);
        }
    }
}
