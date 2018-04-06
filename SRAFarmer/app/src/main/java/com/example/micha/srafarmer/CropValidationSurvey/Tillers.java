package com.example.micha.srafarmer.CropValidationSurvey;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.micha.srafarmer.DBHelper;
import com.example.micha.srafarmer.Entity.Field;
import com.example.micha.srafarmer.Entity.Tiller;
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

public class Tillers extends AppCompatActivity {

    private TextView tvFieldID, tvLocation;

    private EditText etTillerCount;
    private Button btnAddTiller;

    private DBHelper dbHelper;
    private Field field;
    private SharedPreferences sharedPreferences;

    private Tiller tiller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tillers);
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

        tvFieldID = (TextView) findViewById(R.id.tillers_fieldID);
        tvLocation = (TextView) findViewById(R.id.tillers_location);

        tvFieldID.setText("Field ID: " + field.getId());
        tvLocation.setText(field.getBarangay() +", "+ field.getMunicipality());

        etTillerCount = (EditText) findViewById(R.id.tillers_count);


        btnAddTiller = (Button) findViewById(R.id.tillers_add);
        btnAddTiller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tiller = new Tiller();
                tiller.setYear(sharedPreferences.getInt(Login.YEAR, -1));
                tiller.setFieldID(field.getId());
                tiller.setCount(Integer.parseInt(etTillerCount.getText().toString()));

                dbHelper.addTiller(tiller);
                new UploadTillers().execute();
            }
        });
    }

    private class UploadTillers  extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            Response response;
            String result = null;
            OkHttpClient client = new OkHttpClient();
            RequestBody postRequestBody = new FormBody.Builder()
                    .add("tillers", new Gson().toJson(dbHelper.getTillers(sharedPreferences.getInt(Login.YEAR, -1), field.getId())))
                    .build();
            Request request = new Request.Builder()
                    .url(sharedPreferences.getString(Login.IPADDRESS, null) + "UploadTillers")
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
