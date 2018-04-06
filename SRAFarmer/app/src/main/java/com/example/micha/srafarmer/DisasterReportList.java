package com.example.micha.srafarmer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.example.micha.srafarmer.Entity.Field;
import com.example.micha.srafarmer.Entity.Problem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DisasterReportList extends AppCompatActivity {

    private ArrayList<Problem> disasterReports;
    private RecyclerView rvDisasterReportList;
    private Button btnDisasterReport;

    private Field field;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disaster_report_list);
        sharedPreferences = getSharedPreferences(Login.MyPREFERENCES, MODE_PRIVATE);
        if (!sharedPreferences.getBoolean(Login.IS_LOGGED_IN, false)) {
            Intent i = new Intent();
            i.setClass(getBaseContext(), Login.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }

        field = (Field) getIntent().getExtras().get("field");

        disasterReports = (ArrayList<Problem>) getIntent().getSerializableExtra("disaster_reports");

        btnDisasterReport = (Button) findViewById(R.id.disaster_report_list_btn);
        btnDisasterReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetDisasterReport().execute();
            }
        });

        if (disasterReports != null){
            rvDisasterReportList = (RecyclerView) findViewById(R.id.disaster_report_list_rv);
            DisasterReportListAdapter disasterReportListAdapter = new DisasterReportListAdapter(disasterReports);
            rvDisasterReportList.setAdapter(disasterReportListAdapter);
            rvDisasterReportList.setLayoutManager(new LinearLayoutManager(
                    this, LinearLayoutManager.VERTICAL, false
            ));
        }
    }

    private class GetDisasterReport extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {

            Response response;
            String result = null;
            OkHttpClient client = new OkHttpClient();
            RequestBody postRequestBody = new FormBody.Builder()
                    .add("fieldID", field.getId()+"")
                    .build();
            Request request = new Request.Builder()
                    .url(sharedPreferences.getString(Login.IPADDRESS, null)+ "GetDisasterReport")
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
                Intent intent = new Intent();
                intent.putExtra("field", field);
                intent.putExtra("disasters", (Serializable) new Gson().fromJson(s, new TypeToken<List<Problem>>(){}.getType()));
                intent.setClass(getBaseContext(), DisasterReport.class);
                startActivity(intent);
            }
        }
    }
}
