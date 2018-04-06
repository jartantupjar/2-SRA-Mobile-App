package com.example.micha.srafarmer.Problem;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.micha.srafarmer.Entity.Recommendation;
import com.example.micha.srafarmer.R;

import java.util.ArrayList;

public class ProblemRecommendationsAdapter extends RecyclerView.Adapter<ProblemRecommendationsAdapter.ProblemRecommendationsViewHolder> {
    ArrayList<Recommendation> recommendations;

    public ProblemRecommendationsAdapter (ArrayList<Recommendation> recommendations){
        this.recommendations = recommendations;
    }

    @Override
    public ProblemRecommendationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_problems_recommendations, parent, false);
        return new ProblemRecommendationsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProblemRecommendationsViewHolder holder, int position) {
        Recommendation recommendation = recommendations.get(position);
        holder.name.setText(recommendation.getRecommendation());
        holder.description.setText(recommendation.getDescription());
        holder.type.setText("Type: "+recommendation.getType());

        holder.checkBox.setTag(recommendation);
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener!=null){
                    Recommendation r = (Recommendation) view.getTag();
                    if (holder.checkBox.isChecked()) {
                        mOnItemClickListener.onItemClick(r, true);
                    } else{
                        mOnItemClickListener.onItemClick(r, false);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return recommendations.size();
    }

    public class ProblemRecommendationsViewHolder extends RecyclerView.ViewHolder{

        View container;
        TextView name, description, type;
        CheckBox checkBox;

        public ProblemRecommendationsViewHolder(View itemView) {
            super(itemView);
            container= itemView.findViewById(R.id.list_problems_recommendations_container);
            name = (TextView) itemView.findViewById(R.id.list_problems_recommendations_name);
            description = (TextView) itemView.findViewById(R.id.list_problems_recommendations_description);
            type = (TextView) itemView.findViewById(R.id.list_problems_recommendations_type);
            checkBox = (CheckBox) itemView.findViewById(R.id.list_problems_recommendations_checkbox);
        }
    }
    private OnItemClickListener mOnItemClickListener;
    public void setmOnItemClickListener(OnItemClickListener onItemClickListener){
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        public void onItemClick(Recommendation recommendation, Boolean checked);
    }
}
