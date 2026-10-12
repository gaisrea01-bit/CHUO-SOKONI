package com.chuosokonifrontend.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

        // 1. Name
        if (holder.tvName != null) {
            holder.tvName.setText(product.getName() != null ? product.getName() : "Product");
        }

        // 2. Price
        if (holder.tvPrice != null) {
            holder.tvPrice.setText("TZS " + (product.getUnitPrice() != null ? product.getUnitPrice() : "0.00"));
        }

        // 3. Picture
        if (holder.ivProductImage != null) {
            holder.ivProductImage.setImageResource(R.drawable.baseline_insert_photo_24);
        }

        // 4. Quantity in Stock
        if (holder.tvStockQuantity != null) {
            holder.tvStockQuantity.setText("Stock: " + (product.getStockQuantity() != null ? product.getStockQuantity() : 0) + " units");
        }

        // 5. Rating
        if (holder.tvRating != null) {
            holder.tvRating.setText("4.5");
        }

        // 6. Status
        if (holder.tvStatus != null) {
            holder.tvStatus.setText(product.getStatus() != null ? product.getStatus() : "Active");
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(product);
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvName, tvPrice, tvStockQuantity, tvRating, tvStatus;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            tvStockQuantity = itemView.findViewById(R.id.tvStockQuantity);
            tvRating = itemView.findViewById(R.id.tvProductRating);
            tvStatus = itemView.findViewById(R.id.tvProductStatus);
        }
    }
}
