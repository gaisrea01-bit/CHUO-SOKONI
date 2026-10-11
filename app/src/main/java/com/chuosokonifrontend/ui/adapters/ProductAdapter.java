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

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<ProductDto> productList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ProductDto product);
    }

    public ProductAdapter(List<ProductDto> productList, OnItemClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    public void setProductList(List<ProductDto> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductDto product = productList.get(position);
        holder.tvName.setText(product.getName() != null ? product.getName() : "Product");
        holder.tvPrice.setText("TZS " + (product.getUnitPrice() != null ? product.getUnitPrice() : "0.00"));
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(product);
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
        }
    }
}
