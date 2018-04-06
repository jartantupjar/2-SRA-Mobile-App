package com.example.micha.srafarmer.CropValidationSurvey;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class InitialCVS extends AppCompatActivity {

    private TextView tvFieldID, tvLocation;
    private Field field;
    private Spinner spnVariety, spnCropClass, spnTexture, spnTopography, spnFarmingSystem;
    private EditText etFurrowDistance, etPlantingDensity;
    private Button btnSave;
    private DBHelper dbHelper;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_cvs);
        sharedPreferences = getSharedPreferences(Login.MyPREFERENCES, MODE_PRIVATE);
        if (!sharedPreferences.getBoolean(Login.IS_LOGGED_IN, false)) {
            Intent i = new Intent();
            i.setClass(getBaseContext(), Login.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }

        field = (Field) getIntent().getExtras().get("field");


        tvFieldID = (TextView) findViewById(R.id.initial_cvs_fieldID);
        tvLocation = (TextView) findViewById(R.id.initial_cvs_location);

        tvFieldID.setText("Field ID: " + field.getId());
        tvLocation.setText(field.getBarangay() +", "+ field.getMunicipality());

        spnVariety = (Spinner) findViewById(R.id.intial_cvs_variety);
        spnCropClass = (Spinner) findViewById(R.id.intial_cvs_cropClass);
        spnTexture = (Spinner) findViewById(R.id.intial_cvs_texture);
        spnTopography = (Spinner) findViewById(R.id.intial_cvs_topography);
        spnFarmingSystem = (Spinner) findViewById(R.id.intial_cvs_farmingSystem);
        etFurrowDistance = (EditText) findViewById(R.id.intial_cvs_furrowDistance);
        etPlantingDensity = (EditText) findViewById(R.id.intial_cvs_plantingDensity);


        Gson gson = new Gson();
        String[] varieties = gson.fromJson(sharedPreferences.getString(Login.VARIETIES, null), String[].class);
        String[] cropClass = gson.fromJson(sharedPreferences.getString(Login.CROP_CLASS, null), String[].class);
        String[] texture = gson.fromJson(sharedPreferences.getString(Login.TEXTURES, null), String[].class);
        String[] topography = gson.fromJson(sharedPreferences.getString(Login.TOPOGRAPHY, null), String[].class);
        String[] farmingSystem = gson.fromJson(sharedPreferences.getString(Login.FARMING_SYSTEMS, null), String[].class);

        ArrayAdapter<CharSequence> varietiesAdapter = new ArrayAdapter<CharSequence>(getBaseContext(),
                android.R.layout.simple_spinner_item, varieties);
        ArrayAdapter<CharSequence> cropClassAdapter = new ArrayAdapter<CharSequence>(getBaseContext(),
                android.R.layout.simple_spinner_item, cropClass);
        ArrayAdapter<CharSequence> textureAdapter = new ArrayAdapter<CharSequence>(getBaseContext(),
                android.R.layout.simple_spinner_item, texture);
        ArrayAdapter<CharSequence> topographyAdapter = new ArrayAdapter<CharSequence>(getBaseContext(),
                android.R.layout.simple_spinner_item, topography);
        ArrayAdapter<CharSequence> farmingSystemAdapter = new ArrayAdapter<CharSequence>(getBaseContext(),
                android.R.layout.simple_spinner_item, farmingSystem);


        varietiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cropClassAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        textureAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        topographyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        farmingSystemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnVariety.setAdapter(varietiesAdapter);
        spnCropClass.setAdapter(cropClassAdapter);
        spnTexture.setAdapter(textureAdapter);
        spnTopography.setAdapter(topographyAdapter);
        spnFarmingSystem.setAdapter(farmingSystemAdapter);

         dbHelper = new DBHelper(getBaseContext(), "", null, -1);
        CVS cvs = dbHelper.getCVS(sharedPreferences.getInt(Login.YEAR, -1), field.getId());
        if (cvs ==null){
        spnVariety.setSelection(0);
        spnCropClass.setSelection(0);
        spnTexture.setSelection(0);
        spnTopography.setSelection(0);
        spnFarmingSystem.setSelection(0);}
        else{
            spnVariety.setSelection(varietiesAdapter.getPosition(cvs.getVariety()));
            spnCropClass.setSelection(cropClassAdapter.getPosition(cvs.getCropClass()));
            spnTexture.setSelection(textureAdapter.getPosition(cvs.getTexture()));
            spnTopography.setSelection(topographyAdapter.getPosition(cvs.getTopography()));
            spnFarmingSystem.setSelection(farmingSystemAdapter.getPosition(cvs.getFarmingSystem()));
            etFurrowDistance.setText(cvs.getFurrowDistance()+"");
            etPlantingDensity.setText(cvs.getPlantingDensity()+"");
        }



        btnSave = (Button) findViewById(R.id.intial_cvs_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CVS cvs = new CVS();
                cvs.setYear(sharedPreferences.getInt(Login.YEAR, -1));
                cvs.setField(field.getId());
                cvs.setVariety(spnVariety.getSelectedItem().toString());
                cvs.setCropClass(spnCropClass.getSelectedItem().toString());
                cvs.setTexture(spnTexture.getSelectedItem().toString());
                cvs.setTopography(spnTopography.getSelectedItem().toString());
                cvs.setFarmingSystem(spnFarmingSystem.getSelectedItem().toString());
                if (!TextUtils.isEmpty(etFurrowDistance.getText()))
                cvs.setFurrowDistance(Double.parseDouble(etFurrowDistance.getText().toString()));
                if (!TextUtils.isEmpty(etPlantingDensity.getText()))
                cvs.setPlantingDensity(Double.parseDouble(etPlantingDensity.getText().toString()));

                dbHelper.saveCVS(cvs);
                new UploadCVS().execute();
            }
        });

    }
    private class UploadCVS  extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            Response response;
            String result = null;
            OkHttpClient client = new OkHttpClient();
            CVS cvs = dbHelper.getCVS(sharedPreferences.getInt(Login.YEAR, -1), field.getId());
            RequestBody postRequestBody = new FormBody.Builder()
                    .add("cvs", new Gson().toJson(cvs))
                    .build();
            Request request = new Request.Builder()
                    .url(sharedPreferences.getString(Login.IPADDRESS, null) + "UploadCVS")
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
