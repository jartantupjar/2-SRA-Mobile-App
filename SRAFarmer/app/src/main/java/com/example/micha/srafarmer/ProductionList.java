package com.example.micha.srafarmer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.example.micha.srafarmer.Entity.Field;
import com.example.micha.srafarmer.Entity.Production;

import java.util.ArrayList;

public class ProductionList extends AppCompatActivity {

    private Field field;
    private SharedPreferences sharedPreferences;
    private Button btnSubmitProduction;
    private RecyclerView rvProductionList;

    private ArrayList<Production> productionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_production_list);
        sharedPreferences = getSharedPreferences(Login.MyPREFERENCES, MODE_PRIVATE);
        if (!sharedPreferences.getBoolean(Login.IS_LOGGED_IN, false)) {
            Intent i = new Intent();
            i.setClass(getBaseContext(), Login.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
        field = (Field) getIntent().getExtras().get("field");

        btnSubmitProduction = (Button) findViewById(R.id.production_list_btn);
        btnSubmitProduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("field", field);
                intent.setClass(getBaseContext(), ProductionActivity.class);
                startActivity(intent);
            }
        });

        productionList = (ArrayList<Production>) getIntent().getSerializableExtra("production_list");
        if (productionList!=null){
            rvProductionList = (RecyclerView) findViewById(R.id.production_list_rv);
            ProductionListAdapter productionListAdapter = new ProductionListAdapter(productionList);
            rvProductionList.setAdapter(productionListAdapter);
            rvProductionList.setLayoutManager(new LinearLayoutManager(
                    this, LinearLayoutManager.VERTICAL, false
            ));
        }
    }
}
