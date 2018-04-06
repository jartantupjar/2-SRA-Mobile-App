package com.example.micha.srafarmer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.micha.srafarmer.Entity.Farmer;
import com.example.micha.srafarmer.Entity.LoginResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login extends AppCompatActivity {

    private EditText etUsername, etPassword, etIPAddress;
    private Button btnLogin;
    private SharedPreferences sharedPreferences;

    private String username, password, ip_address;

    public static final String MyPREFERENCES = "MyPrefs";
    public static final String NAME = "Name";
    public static final String DISTRICT = "District";
    public static final String IS_LOGGED_IN = "IsLoggedIn";
    public static final String VARIETIES = "Varieties";
    public static final String CROP_CLASS = "CropClass";
    public static final String TEXTURES = "Textures";
    public static final String TOPOGRAPHY = "Topography";
    public static final String FERTILIZERS = "Fertilizers";
    public static final String FARMING_SYSTEMS ="FarmingSystems";
    public static final String YEAR = "year";
    public static final String DATE = "date";
    public static final String PHASES = "phases";
    public static final String IPADDRESS = "ipAddress";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        etUsername = (EditText) findViewById(R.id.input_username);
        etPassword = (EditText) findViewById(R.id.input_password);
        etIPAddress = (EditText) findViewById(R.id.input_ipaddress);

        sharedPreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = etUsername.getText().toString();
                password = etPassword.getText().toString();
                ip_address = etIPAddress.getText().toString();
                new LoginURLHelper().execute();
            }
        });
    }
    private class LoginURLHelper extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            Response response;
            String result = null;
            OkHttpClient client = new OkHttpClient();
            RequestBody postRequestBody = new FormBody.Builder()
                    .add("username",username)
                    .add("password", password)
                    .build();
            Request request = new Request.Builder()
                    .url("http://" + ip_address + "/SRAMobile/Authenticate")
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
                LoginResult loginResult = gson.fromJson(s, LoginResult.class);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(NAME, loginResult.getFarmer().getName());
                editor.putString(DISTRICT, loginResult.getFarmer().getDistrict());
                editor.putBoolean(IS_LOGGED_IN, true);

                DBHelper dbHelper = new DBHelper(getBaseContext(), "", null, -1);
                dbHelper.addFields(loginResult.getFarmer().getFields());
                dbHelper.updateProduction(loginResult.getProductions());
                dbHelper.saveCVSList(loginResult.getCvs());
                dbHelper.saveFertilizers(loginResult.getFertilizersSubmitted());
                dbHelper.addTillers(loginResult.getTillers());
                dbHelper.addProblems(loginResult.getProblems());
                dbHelper.addRecommendations(loginResult.getRecommendations());

                editor.putString(VARIETIES, gson.toJson(loginResult.getVarieties()));
                editor.putString(CROP_CLASS, gson.toJson(loginResult.getCropClass()));
                editor.putString(FERTILIZERS, gson.toJson(loginResult.getFertilizers()));
                editor.putString(TEXTURES, gson.toJson(loginResult.getTextures()));
                editor.putString(TOPOGRAPHY, gson.toJson(loginResult.getTopography()));
                editor.putString(FARMING_SYSTEMS, gson.toJson(loginResult.getFarmingSystems()));
                editor.putString(IPADDRESS, "http://" + ip_address + "/SRAMobile/");
                editor.putInt(YEAR, loginResult.getYear());
                editor.putString(PHASES, gson.toJson(loginResult.getPhases()));
                editor.putLong(DATE, loginResult.getDate().getTime());
                editor.commit();


                Intent i = new Intent();
                i.setClass(getBaseContext(), MainActivity.class);
                startActivity(i);
            }else{
                Toast.makeText(Login.this, "Incorrect username/password.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
