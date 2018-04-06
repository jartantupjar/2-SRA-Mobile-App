package com.example.micha.srafarmer.Forum;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.micha.srafarmer.Entity.Post;
import com.example.micha.srafarmer.R;

import java.util.ArrayList;

public class ForumPostsAdapter extends RecyclerView.Adapter<ForumPostsAdapter.PostsViewHolder>{
    ArrayList<Post> posts;

    public ForumPostsAdapter (ArrayList<Post> posts){ this.posts = posts;}

    @Override
    public PostsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_posts, parent, false);
        return new PostsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PostsViewHolder holder, int position) {

        Post post = posts.get(position);
        holder.title.setText(post.getTitle());
        holder.postedBy.setText("Posted By: "+ post.getFarmersName()+" " + post.getDatePosted());

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
        TextView title, postedBy;

        public PostsViewHolder(View itemView) {
            super(itemView);
            container= itemView.findViewById(R.id.list_posts_container);
            title = (TextView) itemView.findViewById(R.id.list_posts_title);
            postedBy = (TextView) itemView.findViewById(R.id.list_posts_postedby);
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
