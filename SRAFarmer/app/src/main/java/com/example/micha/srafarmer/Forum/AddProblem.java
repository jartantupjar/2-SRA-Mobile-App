package com.example.micha.srafarmer.Forum;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.micha.srafarmer.Entity.Field;
import com.example.micha.srafarmer.Entity.Post;
import com.example.micha.srafarmer.Login;
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

public class AddProblem extends AppCompatActivity {

    private Post post;
    private Field field;
    private TextView tvFieldID, tvLocation, tvTitle, tvMessage, tvImages;
    private ArrayList<String> imageURI;
    private Button btnTakePhoto, btnSubmit;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_problem);
        sharedPreferences = getSharedPreferences(Login.MyPREFERENCES, MODE_PRIVATE);
        if (!sharedPreferences.getBoolean(Login.IS_LOGGED_IN, false)) {
            Intent i = new Intent();
            i.setClass(getBaseContext(), Login.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }

        field = (Field) getIntent().getExtras().get("field");
        post = (Post) getIntent().getExtras().get("post");

        tvFieldID = (TextView) findViewById(R.id.add_problem_fieldID);
        tvLocation = (TextView) findViewById(R.id.add_problem_location);
        tvFieldID.setText(field.getId()+"");
        tvLocation.setText(field.getBarangay() +", "+ field.getMunicipality());

        tvTitle = (TextView) findViewById(R.id.add_problem_title);
        tvMessage = (TextView) findViewById(R.id.add_problem_message);

        tvTitle.setText(post.getTitle());
        tvMessage.setText(post.getMessage());

        imageURI = new ArrayList<>();
        tvImages = (TextView) findViewById(R.id.add_problem_images);
        tvImages.setText(imageURI.size()+"");

        btnTakePhoto = (Button) findViewById(R.id.add_problem_takephoto);
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

        btnSubmit = (Button) findViewById(R.id.add_problem_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageURI.size()!=0)
                new SubmitProblem().execute();
                else
                    Toast.makeText(getBaseContext(), "Photos are required.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class SubmitProblem extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            Response response;
            String result = null;
            OkHttpClient client = new OkHttpClient();

            MultipartBody.Builder multipartBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("problemID",post.getProblemID()+"")
                    .addFormDataPart("fieldID", field.getId()+"")
                    .addFormDataPart("date", sharedPreferences.getLong(Login.DATE, -1)+"");
            for (int i =0; i < imageURI.size(); i++){
                multipartBody.addFormDataPart(i+"", new File(imageURI.get(i)).getName(),
                        RequestBody.create(MediaType.parse("image/jpeg"), new File(imageURI.get(i))));
            }
            MultipartBody requestBody = multipartBody.build();
            Request request = new Request.Builder()
                    .url(sharedPreferences.getString(Login.IPADDRESS, null) + "SubmitProblem")
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
            new GetForumPosts().execute();
        }
    }
    private class GetForumPosts extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            Response response;
            String result = null;
            OkHttpClient client = new OkHttpClient();
            RequestBody postRequestBody = new FormBody.Builder()
                    .add("pagenum", 1+"")
                    .add("date", sharedPreferences.getLong(Login.DATE, -1)+"")
                    .build();
            Request request = new Request.Builder()
                    .url(sharedPreferences.getString(Login.IPADDRESS, null) + "GetForumPosts")
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
            if (!s.equalsIgnoreCase("null")){
                Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy").create();
                Intent i = new Intent();
                Toast.makeText(getBaseContext(), "Problem has been submitted.", Toast.LENGTH_SHORT).show();
                i.putExtra("posts", (Serializable) gson.fromJson(s, new TypeToken<List<Post>>(){}.getType()));
                i.setClass(getBaseContext(), ForumHome.class);
                startActivity(i);
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        imageURI = savedInstanceState.getStringArrayList("imageURI");
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
        outState.putStringArrayList("imageURI", imageURI);
        outState.putSerializable("field", field);
        super.onSaveInstanceState(outState);
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
}
