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
import android.widget.EditText;
import android.widget.TextView;

import com.example.micha.srafarmer.Entity.Comment;
import com.example.micha.srafarmer.Entity.Post;
import com.example.micha.srafarmer.Fields;
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

public class ForumPost extends AppCompatActivity {

    private Post post;

    private TextView tvTitle, tvMessage, tvFarmer;
    private RecyclerView recyclerView;
    private Button btnPrevious, btnNext, btnSubmit;
    private EditText etComment;

    private int currentPage;
    private ArrayList<Comment> comments;
    private String comment;

    private SharedPreferences sharedPreferences;

    private CommentsAdapter commentsAdapter;

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
        setContentView(R.layout.activity_forum_post);

        post = (Post) getIntent().getExtras().get("post");
        currentPage = 1;



        tvTitle = (TextView) findViewById(R.id.forum_post_title);
        tvMessage = (TextView) findViewById(R.id.forum_post_message);
        tvFarmer = (TextView) findViewById(R.id.forum_post_farmer);
        recyclerView = (RecyclerView) findViewById(R.id.forum_post_rv);
        btnSubmit = (Button) findViewById(R.id.forum_post_submit);
        btnPrevious = (Button) findViewById(R.id.forum_post_previouspage);
        btnNext = (Button) findViewById(R.id.forum_post_next);
        etComment = (EditText) findViewById(R.id.forum_post_comment);

        tvTitle.setText(post.getTitle());
        tvMessage.setText(post.getMessage());
        if (post.getFarmersName()!= null)
        tvFarmer.setText("Posted by: " + post.getFarmersName());

        if (post.getStatus().equalsIgnoreCase("Pending")){
            etComment.setVisibility(View.GONE);
            btnSubmit.setVisibility(View.GONE);
        }

        comments = (ArrayList<Comment>) getIntent().getExtras().get("comments");
        if (comments!=null) {
            commentsAdapter = new CommentsAdapter(comments);
            recyclerView.setAdapter(commentsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(
                    this, LinearLayoutManager.VERTICAL, false
            ));
        } else{
            btnPrevious.setVisibility(View.GONE);
            btnNext.setVisibility(View.GONE);
        }

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPage>1){
                    currentPage--;
                    new GetComments().execute();
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    currentPage++;
                    new GetComments().execute();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SubmitComment().execute();
            }
        });
    }

    private class SubmitComment extends AsyncTask<String, Void, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            comment = etComment.getText().toString();
        }

        @Override
        protected String doInBackground(String... strings) {
            Response response;
            String result = null;
            OkHttpClient client = new OkHttpClient();
            RequestBody postRequestBody = new FormBody.Builder()
                    .add("post", post.getId()+"")
                    .add("comment", comment)
                    .add("farmers_name", sharedPreferences.getString(Login.NAME, null))
                    .add("date", sharedPreferences.getLong(Login.DATE, -1)+"")
                    .build();
            Request request = new Request.Builder()
                    .url(sharedPreferences.getString(Login.IPADDRESS, null) + "SubmitComment")
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
            currentPage=1;
            new GetComments().execute();
        }
    }

    private class GetComments extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            Response response;
            String result = null;
            OkHttpClient client = new OkHttpClient();
            RequestBody postRequestBody = new FormBody.Builder()
                    .add("post", post.getId()+"")
                    .add("pagenum", currentPage+"")
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
            Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy").create();
            if (!s.equalsIgnoreCase("null")) {
                comments = gson.fromJson(s, new TypeToken<List<Comment>>(){}.getType());
                if (commentsAdapter == null){
                    commentsAdapter = new CommentsAdapter(comments);
                    recyclerView.setAdapter(commentsAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(
                            getBaseContext(), LinearLayoutManager.VERTICAL, false
                    ));
                    btnNext.setVisibility(View.VISIBLE);
                    btnPrevious.setVisibility(View.VISIBLE);
                }
                commentsAdapter.comments = comments;
                commentsAdapter.notifyDataSetChanged();
            } else{
                currentPage--;
            }
        }
    }

}
