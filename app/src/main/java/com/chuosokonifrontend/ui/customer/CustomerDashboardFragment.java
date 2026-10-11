package com.chuosokonifrontend.ui.customer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.chuosokonifrontend.R;
import com.chuosokonifrontend.data.api.CustomerAPIService;
import com.chuosokonifrontend.data.dto.ProductDto;
import com.chuosokonifrontend.data.local.CachedProduct;
import com.chuosokonifrontend.data.local.ChuoSokoniDatabase;
import com.chuosokonifrontend.data.network.ApiClient;
import com.chuosokonifrontend.ui.adapters.ProductAdapter;
import com.google.android.material.button.MaterialButton;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerDashboardFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private CustomerAPIService customerAPIService;
    private ProductAdapter productAdapter;
    private ChuoSokoniDatabase database;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customer_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        customerAPIService = ApiClient.getInstance(requireContext()).getCustomerService();
        database = ChuoSokoniDatabase.getDatabase(requireContext());

        swipeRefreshLayout = view.findViewById(R.id.swipeCustomerDash);
        recyclerView = view.findViewById(R.id.recyclerViewCustomerCatalog);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);

        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            productAdapter = new ProductAdapter(null, product ->
                Toast.makeText(requireContext(), "Selected: " + product.getName(), Toast.LENGTH_SHORT).show()
            );
            recyclerView.setAdapter(productAdapter);
        }

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(this::syncDashboardProductsFromBackend);
        }

        MaterialButton btnBrowse = view.findViewById(R.id.btnBrowseProducts);
        if (btnBrowse != null) {
            btnBrowse.setOnClickListener(v -> Toast.makeText(requireContext(), "Exploring Best Deals", Toast.LENGTH_SHORT).show());
        }

        // Load real-time data from local cache first, then sync with backend
        loadProductsFromLocalCache();
        syncDashboardProductsFromBackend();
    }

    private void loadProductsFromLocalCache() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<CachedProduct> cachedList = database.productDao().getAllCachedProducts();
            List<ProductDto> productList = new ArrayList<>();
            for (CachedProduct cp : cachedList) {
                ProductDto dto = new ProductDto(
                        cp.getProductId(),
                        1L,
                        1L,
                        cp.getName(),
                        cp.getDescription(),
                        cp.getUnitPrice() != null ? new BigDecimal(cp.getUnitPrice()) : BigDecimal.ZERO,
                        cp.getStockQuantity(),
                        cp.getStatus()
                );
                productList.add(dto);
            }

            if (isAdded() && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (!productList.isEmpty() && productAdapter != null) {
                        productAdapter.setProductList(productList);
                    }
                });
            }
        });
    }

    private void syncDashboardProductsFromBackend() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
        }
        if (progressBar != null && productAdapter != null && productAdapter.getItemCount() == 0) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if (tvEmptyState != null) {
            tvEmptyState.setVisibility(View.GONE);
        }

        customerAPIService.browseProducts().enqueue(new Callback<List<ProductDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProductDto>> call, @NonNull Response<List<ProductDto>> response) {
                if (isAdded()) {
                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    if (response.isSuccessful() && response.body() != null) {
                        List<ProductDto> products = response.body();
                        if (products.isEmpty()) {
                            if (tvEmptyState != null) {
                                tvEmptyState.setVisibility(View.VISIBLE);
                            }
                        } else {
                            if (productAdapter != null) {
                                productAdapter.setProductList(products);
                            }
                            // Save to Room local cache for offline real-time placement
                            saveProductsToLocalCache(products);
                        }
                    } else {
                        Toast.makeText(requireContext(), "Failed to sync catalog", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ProductDto>> call, @NonNull Throwable t) {
                if (isAdded()) {
                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    Toast.makeText(requireContext(), "Offline Mode: Showing cached products", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveProductsToLocalCache(List<ProductDto> products) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<CachedProduct> cachedList = new ArrayList<>();
            for (ProductDto dto : products) {
                CachedProduct cp = new CachedProduct(
                        dto.getProductId() != null ? dto.getProductId() : 0L,
                        dto.getName(),
                        dto.getDescription(),
                        dto.getUnitPrice() != null ? dto.getUnitPrice().toString() : "0.00",
                        dto.getStockQuantity(),
                        dto.getStatus()
                );
                cachedList.add(cp);
            }
            database.productDao().clearCachedProducts();
            database.productDao().insertProducts(cachedList);
        });
    }
}
