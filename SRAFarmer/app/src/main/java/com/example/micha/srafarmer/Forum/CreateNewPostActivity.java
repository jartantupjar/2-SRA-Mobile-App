package com.example.micha.srafarmer.Forum;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.micha.srafarmer.AlarmReceiver;
import com.example.micha.srafarmer.DBHelper;
import com.example.micha.srafarmer.Entity.Field;
import com.example.micha.srafarmer.Entity.Post;
import com.example.micha.srafarmer.HelpFragment;
import com.example.micha.srafarmer.Login;
import com.example.micha.srafarmer.MainActivity;
import com.example.micha.srafarmer.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateNewPostActivity extends AppCompatActivity {
    private Field field;
    private TextView tvFieldID, tvLocation, tvImages;
    private EditText etTitle, etMessage;
    private Spinner spnPhase;
    private Button btnSubmit, btnTakePhoto;

    private ArrayList<String> imageURI;

    private String title, message, phase;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_post);
        sharedPreferences = getSharedPreferences(Login.MyPREFERENCES, MODE_PRIVATE);
        if (!sharedPreferences.getBoolean(Login.IS_LOGGED_IN, false)) {
            Intent i = new Intent();
            i.setClass(getBaseContext(), Login.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Submit New Post");
        setSupportActionBar(myToolbar);

        field = (Field) getIntent().getExtras().get("field");

        tvFieldID = (TextView) findViewById(R.id.create_new_post_fieldID);
        tvLocation = (TextView) findViewById(R.id.create_new_post_location);
        spnPhase = (Spinner) findViewById(R.id.create_new_post_phase);
        tvFieldID.setText(field.getId()+"");
        tvLocation.setText(field.getBarangay() +", "+ field.getMunicipality());

        tvImages = (TextView) findViewById(R.id.create_new_post_images);

        etTitle = (EditText) findViewById(R.id.create_new_post_title);
        etMessage = (EditText) findViewById(R.id.create_new_post_message);

        imageURI = new ArrayList<>();

        tvImages.setText(imageURI.size()+"");

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.phases, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnPhase.setAdapter(adapter);

        btnTakePhoto = (Button) findViewById(R.id.create_new_post_takephoto);
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                     takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                     startActivityForResult(takePictureIntent, 1);
                    }
                }

            }
        });

        btnSubmit = (Button) findViewById(R.id.create_new_post_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageURI.size()!=0) {
                    phase = spnPhase.getSelectedItem().toString();
                    new SubmitPost().execute();
                }
                else
                    Toast.makeText(CreateNewPostActivity.this, "Photos are required.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        imageURI = savedInstanceState.getStringArrayList("imageURI");
        etTitle.setText(savedInstanceState.getString("title"));
        etMessage.setText(savedInstanceState.getString("message"));
        tvImages.setText(imageURI.size()+"");
        field = (Field) savedInstanceState.getSerializable("field");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==1 && resultCode == RESULT_OK){
            tvImages.setText(imageURI.size()+"");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("title", etTitle.getText().toString());
        outState.putString("message", etMessage.getText().toString());
        outState.putStringArrayList("imageURI", imageURI);
        outState.putSerializable("field",field);
        super.onSaveInstanceState(outState);
    }

    private class SubmitPost extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            title = etTitle.getText().toString();
            message= etMessage.getText().toString();
        }

        @Override
        protected String doInBackground(String... strings) {
            Response response;
            String result = null;
            OkHttpClient client = new OkHttpClient();

            MultipartBody.Builder multipartBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("title",title)
                    .addFormDataPart("message", message)
                    .addFormDataPart("fieldID", field.getId()+"")
                    .addFormDataPart("phase", phase)
                    .addFormDataPart("date", sharedPreferences.getLong(Login.DATE, -1)+"");
            for (int i =0; i < imageURI.size(); i++){
                multipartBody.addFormDataPart(i+"", new File(imageURI.get(i)).getName(),
                        RequestBody.create(MediaType.parse("image/jpeg"), new File(imageURI.get(i))));
            }

            MultipartBody requestBody = multipartBody.build();
            Request request = new Request.Builder()
                    .url(sharedPreferences.getString(Login.IPADDRESS, null) + "SubmitPost")
                    .post(requestBody)
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
            Intent i = new Intent(getBaseContext(), MainActivity.class);
            startActivity(i);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        imageURI.add(image.getAbsolutePath());
        return image;
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
                args.putString("help", "To upload images, tap on 'Take Photo' button.\n" +
                        "            \n" +
                        "Your camera app will open for you to take a picture.\n" +
                        "\n" +
                        "The number of images taken is indicated next to the button.");
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
