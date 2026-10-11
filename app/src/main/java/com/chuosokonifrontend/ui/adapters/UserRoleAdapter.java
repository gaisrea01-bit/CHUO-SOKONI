package com.chuosokonifrontend.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.chuosokonifrontend.R;
import java.util.List;

public class UserRoleAdapter extends RecyclerView.Adapter<UserRoleAdapter.UserRoleViewHolder> {

    private List<Object> userRoleList;

    public UserRoleAdapter(List<Object> userRoleList) {
        this.userRoleList = userRoleList;
    }

    public void setUserRoleList(List<Object> userRoleList) {
        this.userRoleList = userRoleList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserRoleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_role, parent, false);
        return new UserRoleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserRoleViewHolder holder, int position) {
        holder.tvName.setText("Platform User Account #" + (position + 1));
        holder.tvRole.setText("Role: CUSTOMER / SELLER");
    }

    @Override
    public int getItemCount() {
        return userRoleList != null ? userRoleList.size() : 0;
    }

    static class UserRoleViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvRole;
        public UserRoleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvUserRoleName);
            tvRole = itemView.findViewById(R.id.tvUserRoleType);
        }
    }
}
