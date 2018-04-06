package com.example.micha.srafarmer.Inbox;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.micha.srafarmer.Entity.NotificationsLocal;
import com.example.micha.srafarmer.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class NotificationsListAdapter extends RecyclerView.Adapter<NotificationsListAdapter.NotificationViewHolder>{
    ArrayList<NotificationsLocal> notificationsLocals;

    public NotificationsListAdapter (ArrayList<NotificationsLocal> notificationsLocal){ this.notificationsLocals = notificationsLocal;}

    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_notifications, parent, false);
        return new NotificationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {

        NotificationsLocal notificationsLocal = notificationsLocals.get(position);
        holder.tvType.setText(notificationsLocal.getType());
        if (notificationsLocal.getType().equalsIgnoreCase("Reminder")) holder.tvType.setText("Recommendation");
        holder.tvMessage.setText(notificationsLocal.getMessage());
        holder.tvFieldID.setText("Field ID: " +notificationsLocal.getFieldID());
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
        holder.tvDate.setText("Date: "+ sdf.format(notificationsLocal.getDate()));

        holder.container.setTag(notificationsLocal);
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener !=null){
                    NotificationsLocal n = (NotificationsLocal) view.getTag();
                    mOnItemClickListener.onItemClick(n);
                }
            }
        });

        holder.container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mLongClickListener!=null){
                    NotificationsLocal n = (NotificationsLocal) view.getTag();
                    mLongClickListener.onLongClickListener(n);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationsLocals.size();
    }


    public class NotificationViewHolder extends RecyclerView.ViewHolder{
        TextView tvMessage, tvType, tvFieldID, tvDate;
        View container;

        public NotificationViewHolder (View itemView) {
            super(itemView);
            tvMessage = (TextView) itemView.findViewById(R.id.list_notifications_message);
            tvType = (TextView) itemView.findViewById(R.id.list_notifications_type);
            tvFieldID = (TextView) itemView.findViewById(R.id.list_notifications_fieldID);
            tvDate = (TextView) itemView.findViewById(R.id.list_notifications_date);
            container = itemView.findViewById(R.id.list_notifications_container);
        }
    }

    private OnItemClickListener mOnItemClickListener;
    public void setmOnItemClickListener(OnItemClickListener onItemClickListener){
        mOnItemClickListener = onItemClickListener;
    }
    private OnLongClickListener mLongClickListener;
    public void setmOnLongClickListener (OnLongClickListener onLongClickListener){
        mLongClickListener = onLongClickListener;
    }

    public interface OnLongClickListener{
        public void onLongClickListener(NotificationsLocal notificationsLocal);
    }

    public interface OnItemClickListener{
        public void onItemClick(NotificationsLocal notificationsLocal);
    }
}
