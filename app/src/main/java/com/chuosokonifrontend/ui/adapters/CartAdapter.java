package com.chuosokonifrontend.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.chuosokonifrontend.R;
import com.chuosokonifrontend.data.dto.ProductDto;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<ProductDto> cartItems;

    public CartAdapter(List<ProductDto> cartItems) {
        this.cartItems = cartItems;
    }

    public void setCartItems(List<ProductDto> cartItems) {
        this.cartItems = cartItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        ProductDto item = cartItems.get(position);
        holder.tvName.setText(item.getName() != null ? item.getName() : "Item");
        holder.tvPrice.setText("TZS " + (item.getUnitPrice() != null ? item.getUnitPrice() : "0.00"));
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCartItemName);
            tvPrice = itemView.findViewById(R.id.tvCartItemPrice);
        }
    }
}
