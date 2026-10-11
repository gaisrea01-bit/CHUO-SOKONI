package com.chuosokonifrontend.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.chuosokonifrontend.R;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotifViewHolder> {

    private List<Object> notificationList;

    public NotificationAdapter(List<Object> notificationList) {
        this.notificationList = notificationList;
    }

    public void setNotificationList(List<Object> notificationList) {
        this.notificationList = notificationList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotifViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotifViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifViewHolder holder, int position) {
        holder.tvMessage.setText("Chuo Sokoni Notification #" + (position + 1));
        holder.tvTimestamp.setText("Recent update");
    }

    @Override
    public int getItemCount() {
        return notificationList != null ? notificationList.size() : 0;
    }

    static class NotifViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTimestamp;
        public NotifViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvNotifMessage);
            tvTimestamp = itemView.findViewById(R.id.tvNotifTimestamp);
        }
    }
}
