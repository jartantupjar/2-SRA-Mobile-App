package com.example.micha.srafarmer.Recommendations;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.micha.srafarmer.Entity.Recommendation;
import com.example.micha.srafarmer.R;

import java.util.ArrayList;

public class RecommendationsListAdapter extends RecyclerView.Adapter<RecommendationsListAdapter.RecommendationViewHolder> {
    ArrayList<Recommendation> recommendations;

    public RecommendationsListAdapter (ArrayList<Recommendation> recommendations){ this.recommendations = recommendations;}

    @Override
    public RecommendationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_recommendations, parent, false);
        return new RecommendationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecommendationViewHolder holder, int position) {
        Recommendation recommendation = recommendations.get(position);
        holder.tvName.setText(recommendation.getRecommendation());
        holder.tvStatus.setText(recommendation.getStatus());

        holder.container.setTag(recommendation);
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener !=null){
                    Recommendation r = (Recommendation) view.getTag();
                    mOnItemClickListener.onItemClick(r);
                }
            }
        });
        holder.container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if (mLongClickListener!=null){
                    Recommendation r = (Recommendation) view.getTag();
                    mLongClickListener.onLongClickListener(r);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return recommendations.size();
    }


    public class RecommendationViewHolder extends RecyclerView.ViewHolder{
        TextView tvName, tvStatus;
        View container;

        public RecommendationViewHolder (View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.list_recommendations_name);
            tvStatus = (TextView) itemView.findViewById(R.id.list_recommendations_status);
            container = itemView.findViewById(R.id.list_recommendations_container);
        }
    }

    private OnItemClickListener mOnItemClickListener;
    public void setmOnItemClickListener(OnItemClickListener onItemClickListener){
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        public void onItemClick(Recommendation recommendation);
    }

    private OnLongClickListener mLongClickListener;
    public void setmOnLongClickListener (OnLongClickListener onLongClickListener){
        mLongClickListener = onLongClickListener;
    }

    public interface OnLongClickListener{
        public void onLongClickListener(Recommendation recommendation);
    }
}
