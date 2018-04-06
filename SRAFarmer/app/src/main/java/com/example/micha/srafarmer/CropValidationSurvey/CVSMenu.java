package com.example.micha.srafarmer.CropValidationSurvey;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.micha.srafarmer.DBHelper;
import com.example.micha.srafarmer.Entity.Fertilizer;
import com.example.micha.srafarmer.Entity.Field;
import com.example.micha.srafarmer.Entity.Phase;
import com.example.micha.srafarmer.Login;
import com.example.micha.srafarmer.R;
import com.google.gson.Gson;

public class CVSMenu extends AppCompatActivity {
    private Button btnIntialSurvey, btnFertilizers, btnTillers, btnMilling;
    private TextView tvFieldID, tvLocation;
    private Field field;

    private Intent intent;
    private DBHelper dbHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cvs);
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


        tvFieldID = (TextView) findViewById(R.id.cvs_fieldID);
        tvLocation = (TextView) findViewById(R.id.cvs_location);

        tvFieldID.setText("Field ID: " + field.getId());
        tvLocation.setText(field.getBarangay() +", "+ field.getMunicipality());


        intent = new Intent();
        intent.putExtra("field", field);

        btnIntialSurvey = (Button) findViewById(R.id.cvs_initialSurvey);
        btnIntialSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setClass(getBaseContext(), InitialCVS.class);
                startActivity(intent);
            }
        });

        btnFertilizers = (Button) findViewById(R.id.cvs_fertilizers);
        btnFertilizers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dbHelper.getCVS(sharedPreferences.getInt(Login.YEAR, -1), field.getId())!=null){
                    if (isPhase("Planting") || isPhase("Germination")) {
                        intent.setClass(getBaseContext(), Fertilizers.class);
                        startActivity(intent);
                    } else Toast.makeText(getBaseContext(), "Not in Planting/Germination phase.", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(getBaseContext(), "Please fill up the Initial CVSMenu.", Toast.LENGTH_SHORT).show();
            }
        });

        btnTillers = (Button) findViewById(R.id.cvs_tillers);
        btnTillers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dbHelper.getCVS(sharedPreferences.getInt(Login.YEAR, -1), field.getId())!=null){
                    if (isPhase("Tillering") || isPhase("Stalk Elongation")) {
                        intent.setClass(getBaseContext(), Tillers.class);
                        startActivity(intent);
                    } else Toast.makeText(getBaseContext(), "Not in Tillering/Stalk Elongation phase.", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(getBaseContext(), "Please fill up the Initial CVSMenu.", Toast.LENGTH_SHORT).show();
            }
        });

        btnMilling = (Button) findViewById(R.id.cvs_milling);
        btnMilling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dbHelper.getCVS(sharedPreferences.getInt(Login.YEAR, -1), field.getId())!=null){
                    if (isPhase("Milling")) {
                        intent.setClass(getBaseContext(), Milling.class);
                        startActivity(intent);
                    } else Toast.makeText(getBaseContext(), "Not in Milling phase.", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(getBaseContext(), "Please fill up the Initial CVSMenu.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean isPhase(String phase) {
        Gson gson = new Gson();
        Phase[] phases = gson.fromJson(sharedPreferences.getString(Login.PHASES, null), Phase[].class);
        for (int i = 0; i < phases.length; i++) {
            if (phases[i].getPhase().equalsIgnoreCase(phase)) return true;
        }
        return false;
    }

}
