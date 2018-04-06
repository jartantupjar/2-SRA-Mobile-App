package com.example.micha.srafarmer.Forum;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.micha.srafarmer.Entity.Comment;
import com.example.micha.srafarmer.R;

import java.util.ArrayList;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder>{
    ArrayList<Comment> comments;

    public CommentsAdapter (ArrayList<Comment> comments){ this.comments = comments;}

    @Override
    public CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_comments, parent, false);
        return new CommentsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CommentsViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.farmerName.setText(comment.getFarmersName());
        holder.message.setText(comment.getMessage());
        holder.date.setText("Posted: "+ comment.getDate());    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class CommentsViewHolder extends RecyclerView.ViewHolder{

        TextView farmerName, message, date;

        public CommentsViewHolder(View itemView) {
            super(itemView);
            farmerName= (TextView) itemView.findViewById(R.id.list_comments_name);
            message = (TextView) itemView.findViewById(R.id.list_comments_message);
            date = (TextView) itemView.findViewById(R.id.list_comments_date);
        }
    }

    private OnItemClickListener mOnItemClickListener;
    public void setmOnItemClickListener(OnItemClickListener onItemClickListener){
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        public void onItemClick(Comment comment, Button like);
    }
}
