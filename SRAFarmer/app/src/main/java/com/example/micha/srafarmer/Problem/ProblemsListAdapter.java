package com.example.micha.srafarmer.Problem;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.micha.srafarmer.Entity.Problem;
import com.example.micha.srafarmer.R;

import java.util.ArrayList;

public class ProblemsListAdapter extends RecyclerView.Adapter<ProblemsListAdapter.ProblemViewHolder> {
    ArrayList<Problem> problems;

    public ProblemsListAdapter (ArrayList<Problem> problems){
        this.problems = problems;
    }

    @Override
    public ProblemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_problems, parent, false);
        return new ProblemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProblemViewHolder holder, int position) {
        Problem problem = problems.get(position);
        holder.tvNameStatus.setText(problem.getName() + " (" + problem.getStatus()+")");
        holder.tvDescription.setText(problem.getDescription());

        holder.container.setTag(problem);

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener !=null){
                    Problem p = (Problem) view.getTag();
                    mOnItemClickListener.onItemClick(p);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return problems.size();
    }

    public class ProblemViewHolder extends RecyclerView.ViewHolder{
        TextView tvNameStatus, tvDescription;
        View container;

        public ProblemViewHolder(View itemView) {
            super(itemView);
            tvNameStatus = (TextView) itemView.findViewById(R.id.list_problems_nameStatus);
            tvDescription = (TextView) itemView.findViewById(R.id.list_problems_description);
            container = itemView.findViewById(R.id.list_problems_container);
        }
    }

    private OnItemClickListener mOnItemClickListener;
    public void setmOnItemClickListener(OnItemClickListener onItemClickListener){
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        public void onItemClick(Problem problem);
    }


}
