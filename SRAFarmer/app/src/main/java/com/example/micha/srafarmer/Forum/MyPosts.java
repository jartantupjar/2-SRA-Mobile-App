package com.example.micha.srafarmer.Forum;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.micha.srafarmer.Entity.Comment;
import com.example.micha.srafarmer.Entity.Post;
import com.example.micha.srafarmer.Login;
import com.example.micha.srafarmer.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

public class MyPosts extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    private int currentPage;

    private RecyclerView recyclerView;
    private Button btnPrevious, btnNext;
    private MyPostsAdapter myPostsAdapter;

    private Post selectedPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(Login.MyPREFERENCES, MODE_PRIVATE);
        if (!sharedPreferences.getBoolean(Login.IS_LOGGED_IN, false)) {
            Intent i = new Intent();
            i.setClass(getBaseContext(), Login.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
        setContentView(R.layout.activity_my_posts);

        currentPage = 0;

        btnPrevious = (Button) findViewById(R.id.my_posts_previouspage);
        btnNext = (Button) findViewById(R.id.my_posts_nextpage);

        recyclerView = (RecyclerView) findViewById(R.id.my_posts_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false
        ));
        myPostsAdapter = new MyPostsAdapter((ArrayList<Post>) getIntent().getExtras().get("posts"));
        myPostsAdapter.setmOnItemClickListener(new MyPostsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Post post) {
                selectedPost = post;
                new GetComments().execute();
            }
        });
        recyclerView.setAdapter(myPostsAdapter);
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPage>1){
                    currentPage--;
                    new GetPostsByFarmer().execute();
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPage++;
                new GetPostsByFarmer().execute();
            }
        });
    }
    private class GetPostsByFarmer extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            Response response;
            String result = null;
            OkHttpClient client = new OkHttpClient();
            RequestBody postRequestBody = new FormBody.Builder()
                    .add("page_num", currentPage+"")
                    .add("farmer_name", sharedPreferences.getString(Login.NAME, null))
                    .build();
            Request request = new Request.Builder()
                    .url(sharedPreferences.getString(Login.IPADDRESS, null)+ "GetPostsByFarmer")
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
                myPostsAdapter.posts =  gson.fromJson(s, new TypeToken<List<Post>>(){}.getType());
                myPostsAdapter.notifyDataSetChanged();
            } else{
                currentPage--;
            }
        }
    }

    private class GetComments extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            Response response;
            String result = null;
            OkHttpClient client = new OkHttpClient();
            RequestBody postRequestBody = new FormBody.Builder()
                    .add("post", selectedPost.getId()+"")
                    .add("pagenum", 1+"")
                    .add("farmers_name", sharedPreferences.getString(Login.NAME, null))
                    .add("date", sharedPreferences.getLong(Login.DATE, -1)+"")
                    .build();
            Request request = new Request.Builder()
                    .url(sharedPreferences.getString(Login.IPADDRESS, null) + "GetComments")
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
            Intent intent = new Intent();
            if (!s.equalsIgnoreCase("null")){
                Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy").create();
                intent.putExtra("comments", (Serializable) gson.fromJson(s, new TypeToken<List<Comment>>() {
                }.getType()));
            }
            intent.putExtra("post", selectedPost);
            intent.setClass(getBaseContext(), ForumPost.class);
            startActivity(intent);
        }
    }
}
