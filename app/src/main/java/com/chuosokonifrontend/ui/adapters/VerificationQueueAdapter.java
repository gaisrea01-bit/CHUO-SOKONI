package com.chuosokonifrontend.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.chuosokonifrontend.R;
import java.util.List;

public class VerificationQueueAdapter extends RecyclerView.Adapter<VerificationQueueAdapter.VerifViewHolder> {

    private List<Object> queueList;

    public VerificationQueueAdapter(List<Object> queueList) {
        this.queueList = queueList;
    }

    public void setQueueList(List<Object> queueList) {
        this.queueList = queueList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VerifViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_verification, parent, false);
        return new VerifViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VerifViewHolder holder, int position) {
        holder.tvUserName.setText("User / Seller #" + (position + 1));
        holder.tvDocType.setText("Document Verification Pending");
    }

    @Override
    public int getItemCount() {
        return queueList != null ? queueList.size() : 0;
    }

    static class VerifViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvDocType;
        public VerifViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvVerifUserName);
            tvDocType = itemView.findViewById(R.id.tvVerifDocType);
        }
    }
}
