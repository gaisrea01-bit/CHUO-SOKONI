package com.chuosokonifrontend.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.chuosokonifrontend.R;
import java.util.List;

public class WholesaleOpportunityAdapter extends RecyclerView.Adapter<WholesaleOpportunityAdapter.WholesaleViewHolder> {

    private List<Object> opportunityList;

    public WholesaleOpportunityAdapter(List<Object> opportunityList) {
        this.opportunityList = opportunityList;
    }

    public void setOpportunityList(List<Object> opportunityList) {
        this.opportunityList = opportunityList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WholesaleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wholesale_opportunity, parent, false);
        return new WholesaleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WholesaleViewHolder holder, int position) {
        holder.tvName.setText("Student Wholesale Batch #" + (position + 1));
        holder.tvReturn.setText("Expected Return: 15% ROI");
    }

    @Override
    public int getItemCount() {
        return opportunityList != null ? opportunityList.size() : 0;
    }

    static class WholesaleViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvReturn;
        public WholesaleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvWholesaleItemName);
            tvReturn = itemView.findViewById(R.id.tvWholesaleItemReturn);
        }
    }
}
