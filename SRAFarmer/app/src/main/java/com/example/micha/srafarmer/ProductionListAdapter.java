package com.example.micha.srafarmer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.micha.srafarmer.Entity.Production;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ProductionListAdapter extends RecyclerView.Adapter<ProductionListAdapter.ProductionViewHolder>{
    ArrayList<Production> productionList;

    public ProductionListAdapter (ArrayList<Production> productionList){
        this.productionList = productionList;
    }

    @Override
    public ProductionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_production, parent, false);
        return new ProductionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProductionViewHolder holder, int position) {
        Production production = productionList.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
        holder.tvDate.setText("Date: " + sdf.format(production.getDate()));
        holder.tvArea.setText("Area Harvested (ha): " + production.getAreaHarvested());
        holder.tvTc.setText("Tons Cane: " + production.getTonsCane());
        holder.tvLkg.setText("Lkg: " + production.getLkg());
    }

    @Override
    public int getItemCount() {
        return productionList.size();
    }

    public class ProductionViewHolder extends RecyclerView.ViewHolder{
        TextView tvDate, tvArea, tvLkg, tvTc;
        public ProductionViewHolder(View itemView) {
            super(itemView);
            tvDate = (TextView) itemView.findViewById(R.id.list_production_date);
            tvArea = (TextView) itemView.findViewById(R.id.list_production_area_harvested);
            tvLkg = (TextView) itemView.findViewById(R.id.list_production_lkg);
            tvTc = (TextView) itemView.findViewById(R.id.list_production_tons_cane);
        }
    }
}
