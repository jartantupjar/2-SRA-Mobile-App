package com.example.micha.srafarmer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toolbar;

import com.example.micha.srafarmer.Entity.Field;
import com.example.micha.srafarmer.Forum.AddProblem;
import com.example.micha.srafarmer.Forum.CreateNewPostActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.io.Serializable;
import java.util.ArrayList;

public class Fields extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<Polygon> polygons;
    private ArrayList<Field> fields;

    private int currField;
    private SharedPreferences sharedPreferences;

    private Button btnNext, btnPrevious, btnSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fields);
        sharedPreferences = getSharedPreferences(Login.MyPREFERENCES, MODE_PRIVATE);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        btnNext = (Button) findViewById(R.id.fields_next);
        btnPrevious = (Button) findViewById(R.id.fields_previous);
        btnSelect = (Button) findViewById(R.id.fields_select);

        currField = 0;

        fields = new DBHelper(getBaseContext(), null, null, -1).getFields();
        if (fields != null) {
            LatLng center = new LatLng(fields.get(currField).getCoords().get(0).getLat(), fields.get(currField).getCoords().get(0).getLng());
            mMap.setMinZoomPreference(10);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(center));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

            polygons = new ArrayList<>();
            PolygonOptions polygonOptions;
            for (int i = 0; i < fields.size(); i++) {
                polygonOptions = new PolygonOptions();
                for (int j = 0; j < fields.get(i).getCoords().size(); j++) {
                    polygonOptions.add(new LatLng(fields.get(i).getCoords().get(j).getLat(), fields.get(i).getCoords().get(j).getLng()));
                }
                if (i==0)
                    polygonOptions.fillColor(0x7F00FF00);

                else
                    polygonOptions.fillColor(Color.TRANSPARENT);
                polygonOptions.clickable(true);
                polygons.add(mMap.addPolygon(polygonOptions));

            }

            if (fields.size()>1) {

                btnNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        btnNext.setClickable(false);
                        btnPrevious.setClickable(false);
                        polygons.get(currField).setFillColor(Color.TRANSPARENT);
                        if (currField == fields.size() - 1) currField = 0;
                        else currField++;
                        polygons.get(currField).setFillColor(0x7F00FF00);
                        LatLng center = new LatLng(fields.get(currField).getCoords().get(0).getLat(), fields.get(currField).getCoords().get(0).getLng());
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(center));
                        btnNext.setClickable(true);
                        btnPrevious.setClickable(true);
                    }
                });
                btnPrevious.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        btnNext.setClickable(false);
                        btnPrevious.setClickable(false);
                        polygons.get(currField).setFillColor(Color.TRANSPARENT);
                        if (currField == 0) currField = fields.size() - 1;
                        else currField--;
                        polygons.get(currField).setFillColor(0x7F00FF00);
                        LatLng center = new LatLng(fields.get(currField).getCoords().get(0).getLat(), fields.get(currField).getCoords().get(0).getLng());
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(center));
                        btnNext.setClickable(true);
                        btnPrevious.setClickable(true);
                    }
                });
                mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
                    @Override
                    public void onPolygonClick(Polygon polygon) {
                        for (int i = 0; i < polygons.size(); i++)
                            if (polygons.get(i).getPoints().equals(polygon.getPoints())) {
                                if (currField != i)
                                    polygons.get(currField).setFillColor(Color.TRANSPARENT);
                                currField = i;
                                polygons.get(currField).setFillColor(0x7F00FF00);
                                LatLng center = new LatLng(polygon.getPoints().get(0).latitude, polygon.getPoints().get(0).longitude);
                                mMap.animateCamera(CameraUpdateFactory.newLatLng(center));
                                return;
                            }
                    }
                });
            }
            btnSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent();
                    String nextActivity = getIntent().getExtras().getString("class");
                    if (nextActivity.equalsIgnoreCase("createnewpost")){
                        i.setClass(getBaseContext(), CreateNewPostActivity.class);
                    }
                    if (nextActivity.equalsIgnoreCase("FieldsMenu")){
                        i.setClass(getBaseContext(), FieldsMenu.class);
                    }
                    i.putExtra("field", fields.get(currField));
                    startActivity(i);
                }
            });

        }
        else{
            Intent i = new Intent();
            i.setClass(getBaseContext(), MainActivity.class);
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
                args.putString("help", "Highlight a field by taping on it.\n" +
                        "            \n" +
                        "Scroll through your fields using the arrow buttons at the bottom.\n" +
                        "\n" +
                        "The selected field if the highlighted field.");
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
