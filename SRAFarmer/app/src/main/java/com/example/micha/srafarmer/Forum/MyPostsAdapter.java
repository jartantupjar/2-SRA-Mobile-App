package com.example.micha.srafarmer.Forum;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.micha.srafarmer.Entity.Post;
import com.example.micha.srafarmer.R;

import java.util.ArrayList;

public class MyPostsAdapter extends RecyclerView.Adapter<MyPostsAdapter.PostsViewHolder> {
    ArrayList<Post> posts;

    public MyPostsAdapter (ArrayList<Post> posts){ this.posts = posts;}

    @Override
    public PostsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_myposts, parent, false);
        return new PostsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PostsViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.title.setText(post.getTitle());
        holder.status.setText("Status: " + post.getStatus());

        holder.container.setTag(post);
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener!=null){
                    Post p = (Post) view.getTag();
                    mOnItemClickListener.onItemClick(p);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class PostsViewHolder extends RecyclerView.ViewHolder{

        View container;
        TextView title, status;

        public PostsViewHolder(View itemView) {
            super(itemView);
            container= itemView.findViewById(R.id.list_myposts_container);
            title = (TextView) itemView.findViewById(R.id.list_myposts_title);
            status = (TextView) itemView.findViewById(R.id.list_myposts_status);
        }
    }

    private OnItemClickListener mOnItemClickListener;
    public void setmOnItemClickListener(OnItemClickListener onItemClickListener){
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        public void onItemClick(Post post);
    }
}
