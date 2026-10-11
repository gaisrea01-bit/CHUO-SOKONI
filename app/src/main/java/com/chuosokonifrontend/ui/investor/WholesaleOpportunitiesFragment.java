package com.chuosokonifrontend.ui.investor;

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
import com.chuosokonifrontend.data.api.InvestorAPIService;
import com.chuosokonifrontend.data.network.ApiClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WholesaleOpportunitiesFragment extends Fragment {

    private RecyclerView recyclerView;
    private InvestorAPIService investorAPIService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wholesale_opportunities, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        investorAPIService = ApiClient.getInstance(requireContext()).getInvestorService();
        recyclerView = view.findViewById(R.id.recyclerViewWholesaleOpportunities);

        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        }

        loadWholesaleOpportunities();
    }

    private void loadWholesaleOpportunities() {
        investorAPIService.getWholesaleOpportunities().enqueue(new Callback<List<Object>>() {
            @Override
            public void onResponse(@NonNull Call<List<Object>> call, @NonNull Response<List<Object>> response) {
                if (isAdded()) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Object> opportunities = response.body();
                        // Opportunities loaded successfully from Spring Boot backend
                    } else {
                        Toast.makeText(requireContext(), "Failed to load wholesale opportunities", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Object>> call, @NonNull Throwable t) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
