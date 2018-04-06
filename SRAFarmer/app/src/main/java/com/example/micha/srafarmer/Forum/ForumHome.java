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
import android.widget.Toast;

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

public class ForumHome extends AppCompatActivity {

    private EditText etSearch;
    private Button btnCreateNewPost, btnMyPosts, btnSearch, btnPrevious, btnNext;
    private RecyclerView recyclerView;

    private int currentPage;
    private String searchText;
    private boolean search;
    private ForumPostsAdapter forumPostsAdapter;

    private Post selectedPost;

    private SharedPreferences sharedPreferences;

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
        setContentView(R.layout.activity_forum_home);
        ArrayList<Post> posts = (ArrayList<Post>) getIntent().getExtras().getSerializable("posts");
        search = false;
        currentPage = 1;

        btnCreateNewPost = (Button) findViewById(R.id.forum_home_createnewpost);
        btnMyPosts = (Button) findViewById(R.id.forum_home_myposts);
        etSearch = (EditText) findViewById(R.id.forum_home_searchtext);
        btnSearch = (Button) findViewById(R.id.forum_home_searchbutton);
        btnPrevious = (Button) findViewById(R.id.forum_home_previouspage);
        btnNext = (Button) findViewById(R.id.forum_home_next);




        btnCreateNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra("class", "createnewpost");
                i.setClass(getBaseContext(),Fields.class);
                startActivity(i);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPage = 1;
                if (etSearch.getText().toString().isEmpty()){
                    search = false;
                    new GetForumPosts().execute();
                } else{
                    search = true;
                    searchText = etSearch.getText().toString();
                    new SearchForum().execute();
                }
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (search){
                    if (currentPage>1){
                        currentPage--;
                        new SearchForum().execute();
                    }
                }else {
                    if (currentPage > 1) {
                        currentPage--;
                        new GetForumPosts().execute();
                    }
                }
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (search) {
                    currentPage++;
                    new SearchForum().execute();
                } else{
                    currentPage++;
                    new GetForumPosts().execute();
                }
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.forum_home_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false
        ));

        forumPostsAdapter = new ForumPostsAdapter(posts);
        forumPostsAdapter.setmOnItemClickListener(new ForumPostsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Post post) {
              selectedPost = post;
                new GetComments().execute();
            }
        });
        recyclerView.setAdapter(forumPostsAdapter);

        btnMyPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    .add("page_num", 1+"")
                    .add("farmer_name", sharedPreferences.getString(Login.NAME, null))
                    .add("date", sharedPreferences.getLong(Login.DATE, -1)+"")
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
                Intent intent = new Intent();
                intent.putExtra("posts", (Serializable) gson.fromJson(s, new TypeToken<List<Post>>(){}.getType()));
                intent.setClass(getBaseContext(), MyPosts.class);
                startActivity(intent);
            } else{
                Toast.makeText(getBaseContext(), "You have no posts.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetForumPosts extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            Response response;
            String result = null;
            OkHttpClient client = new OkHttpClient();
            RequestBody postRequestBody = new FormBody.Builder()
                    .add("pagenum", currentPage+"")
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
            if (s.equalsIgnoreCase("null")) {
                Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy").create();
                forumPostsAdapter.posts = gson.fromJson(s, new TypeToken<List<Post>>() {
                }.getType());
                forumPostsAdapter.notifyDataSetChanged();
            } else{
                currentPage--;
            }
        }
    }

    private class SearchForum extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            Response response;
            String result = null;
            OkHttpClient client = new OkHttpClient();
            RequestBody postRequestBody = new FormBody.Builder()
                    .add("search", searchText)
                    .add("pagenum", currentPage+"")
                    .add("date", sharedPreferences.getLong(Login.DATE, -1)+"")
                    .build();
            Request request = new Request.Builder()
                    .url(sharedPreferences.getString(Login.IPADDRESS, null) + "SearchForum")
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
                forumPostsAdapter.posts = gson.fromJson(s, new TypeToken<List<Post>>() {
                }.getType());
                forumPostsAdapter.notifyDataSetChanged();
            } else currentPage--;
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
