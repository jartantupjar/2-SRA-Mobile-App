package com.example.micha.srafarmer.CropValidationSurvey;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.micha.srafarmer.AlarmReceiver;
import com.example.micha.srafarmer.DBHelper;
import com.example.micha.srafarmer.Entity.Fertilizer;
import com.example.micha.srafarmer.Entity.Field;
import com.example.micha.srafarmer.HelpFragment;
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

public class Fertilizers extends AppCompatActivity {
    private Spinner spnFertilizers;
    private TextView tvFieldID, tvLocation;
    private EditText etFirstDose, etSecondDose;
    private Button btnSave;

    private DBHelper dbHelper;
    private Field field;
    private SharedPreferences sharedPreferences;

    private String fertilizerKind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fertilizers);
        sharedPreferences = getSharedPreferences(Login.MyPREFERENCES, MODE_PRIVATE);
        if (!sharedPreferences.getBoolean(Login.IS_LOGGED_IN, false)) {
            Intent i = new Intent();
            i.setClass(getBaseContext(), Login.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Fertilizers");
        setSupportActionBar(myToolbar);

        field = (Field) getIntent().getExtras().get("field");
        dbHelper = new DBHelper(getBaseContext(), "", null, -1);

        tvFieldID = (TextView) findViewById(R.id.fertilizers_fieldID);
        tvLocation = (TextView) findViewById(R.id.fertilizers_location);

        tvFieldID.setText("Field ID: " + field.getId());
        tvLocation.setText(field.getBarangay() +", "+ field.getMunicipality());

        spnFertilizers = (Spinner) findViewById(R.id.fertilizers_fertilizers);

        Gson gson = new Gson();

        final String[] fertilizers = gson.fromJson(sharedPreferences.getString(Login.FERTILIZERS, null), String[].class);

        ArrayAdapter<CharSequence> fertilizersAdapter = new ArrayAdapter<CharSequence>(getBaseContext(),
                android.R.layout.simple_spinner_item, fertilizers);
        fertilizersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnFertilizers.setAdapter(fertilizersAdapter);
        spnFertilizers.setSelection(0);
        spnFertilizers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Fertilizer fertilizer = dbHelper.getFertilizer(sharedPreferences.getInt(Login.YEAR, -1), field.getId(), spnFertilizers.getSelectedItem().toString());
                if (fertilizer!=null){
                etFirstDose.setText(fertilizer.getFirstDose()+"");
                etSecondDose.setText(fertilizer.getSecondDose()+"");}
                else{
                    etFirstDose.setText(0+"");
                    etSecondDose.setText(0+"");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        etFirstDose = (EditText) findViewById(R.id.fertilizers_first_dose);
        etSecondDose = (EditText) findViewById(R.id.fertilizers_second_dose);

        btnSave = (Button) findViewById(R.id.fertilizers_save);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fertilizerKind = spnFertilizers.getSelectedItem().toString();
                Fertilizer fertilizer = new Fertilizer();
                fertilizer.setYear(sharedPreferences.getInt(Login.YEAR, -1));
                fertilizer.setFieldID(field.getId());
                fertilizer.setFertilizer(fertilizerKind);
                if (!TextUtils.isEmpty(etFirstDose.getText()))
                    fertilizer.setFirstDose(Double.parseDouble(etFirstDose.getText().toString()));
                if (!TextUtils.isEmpty(etSecondDose.getText()))
                    fertilizer.setSecondDose(Double.parseDouble(etSecondDose.getText().toString()));
                dbHelper.saveFertilizer(fertilizer);
                new UpdateCVS().execute();
            }
        });
    }

    private class UpdateCVS  extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            Response response;
            String result = null;
            OkHttpClient client = new OkHttpClient();
            Fertilizer fertilizer = dbHelper.getFertilizer(sharedPreferences.getInt(Login.YEAR, -1), field.getId(), fertilizerKind);
            RequestBody postRequestBody = new FormBody.Builder()
                    .add("fertilizer", new Gson().toJson(fertilizer))
                    .build();
            Request request = new Request.Builder()
                    .url(sharedPreferences.getString(Login.IPADDRESS, null) + "UpdateFertilizers")
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                Intent i = new Intent(getBaseContext(), Login.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                new DBHelper(getBaseContext(),"",null,-1).deleteAll();
                Intent alarmReceiverIntent = new Intent(getBaseContext(), AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.
                        getBroadcast(getBaseContext(), 1, alarmReceiverIntent, 0);
                AlarmManager alarmManager = (AlarmManager) getBaseContext().getSystemService(Service.ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
                startActivity(i);
                return true;
            case R.id.action_help:
                HelpFragment hf = new HelpFragment();
                Bundle args = new Bundle();
                args.putString("help", "Choose a fertilizer and enter the number of bags used per dosage.\n" +
                        "\n" +
                        "Enter 0 if none.\n" +
                        "\n" +
                        "Tap the save button to save inputs for possible future edits.");
                hf.setArguments(args);
                hf.show(getFragmentManager(), "Help");
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}
