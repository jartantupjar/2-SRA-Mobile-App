package com.example.micha.srafarmer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.micha.srafarmer.Entity.Problem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


public class DisasterReportListAdapter extends RecyclerView.Adapter<DisasterReportListAdapter.ProblemViewHolder>{
    ArrayList<Problem> disasterReports;

    public DisasterReportListAdapter (ArrayList<Problem> disasterReports){
        this.disasterReports = disasterReports;
    }

    @Override
    public ProblemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_disaster_reports, parent, false);
        return new ProblemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProblemViewHolder holder, int position) {
        Problem disasterReport = disasterReports.get(position);
        holder.tvName.setText(disasterReport.getName());
        holder.tvDescription.setText(disasterReport.getDescription());
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
        holder.tvDate.setText("Date: "+ sdf.format(disasterReport.getDate()));
    }

    @Override
    public int getItemCount() {
        return disasterReports.size();
    }

    public class ProblemViewHolder extends RecyclerView.ViewHolder{
        TextView tvName, tvDescription, tvDate;
        public ProblemViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.list_disaster_reports_name);
            tvDescription = (TextView) itemView.findViewById(R.id.list_disaster_reports_description);
            tvDate = (TextView) itemView.findViewById(R.id.list_disaster_reports_date);
        }
    }

}
