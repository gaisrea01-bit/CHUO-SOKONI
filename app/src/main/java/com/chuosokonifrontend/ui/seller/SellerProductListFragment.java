package com.chuosokonifrontend.ui.seller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.chuosokonifrontend.R;
import com.chuosokonifrontend.data.api.SellerAPIService;
import com.chuosokonifrontend.data.dto.ProductDto;
import com.chuosokonifrontend.data.network.ApiClient;
import com.chuosokonifrontend.data.network.TokenManager;
import com.google.android.material.button.MaterialButton;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellerProductListFragment extends Fragment {

    private RecyclerView recyclerView;
    private SellerAPIService sellerAPIService;
    private TokenManager tokenManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_seller_product_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tokenManager = new TokenManager(requireContext());
        sellerAPIService = ApiClient.getInstance(requireContext()).getSellerService();

        recyclerView = view.findViewById(R.id.recyclerViewSellerProducts);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        }

        MaterialButton btnAddProduct = view.findViewById(R.id.btnAddProduct);
        if (btnAddProduct != null) {
            btnAddProduct.setOnClickListener(v -> Toast.makeText(requireContext(), "Opening Add/Edit Product", Toast.LENGTH_SHORT).show());
        }

        loadSellerProducts();
    }

    private void loadSellerProducts() {
        Long sellerId = tokenManager.getUserId();
        if (sellerId == null || sellerId == -1L) {
            sellerId = 1L; // Fallback demo ID
        }

        sellerAPIService.getSellerProducts(sellerId).enqueue(new Callback<List<ProductDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProductDto>> call, @NonNull Response<List<ProductDto>> response) {
                if (isAdded()) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<ProductDto> products = response.body();
                        // Products loaded successfully from Spring Boot backend
                    } else {
                        Toast.makeText(requireContext(), "Failed to load seller products", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ProductDto>> call, @NonNull Throwable t) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
