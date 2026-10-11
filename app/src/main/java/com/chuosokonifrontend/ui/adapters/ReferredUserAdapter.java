package com.chuosokonifrontend.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.chuosokonifrontend.R;
import java.util.List;

public class ReferredUserAdapter extends RecyclerView.Adapter<ReferredUserAdapter.ReferredViewHolder> {

    private List<Object> referredList;

    public ReferredUserAdapter(List<Object> referredList) {
        this.referredList = referredList;
    }

    public void setReferredList(List<Object> referredList) {
        this.referredList = referredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReferredViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_referred_user, parent, false);
        return new ReferredViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReferredViewHolder holder, int position) {
        holder.tvName.setText("Referred Student #" + (position + 1));
        holder.tvDate.setText("Joined recently");
    }

    @Override
    public int getItemCount() {
        return referredList != null ? referredList.size() : 0;
    }

    static class ReferredViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDate;
        public ReferredViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvReferredUserName);
            tvDate = itemView.findViewById(R.id.tvReferredUserDate);
        }
    }
}
