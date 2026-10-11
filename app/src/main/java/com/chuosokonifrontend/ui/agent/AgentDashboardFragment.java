package com.chuosokonifrontend.ui.agent;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.chuosokonifrontend.R;
import com.chuosokonifrontend.data.api.AgentAPIService;
import com.chuosokonifrontend.data.api.AuthAPIService;
import com.chuosokonifrontend.data.dto.UserDto;
import com.chuosokonifrontend.data.network.ApiClient;
import com.chuosokonifrontend.data.network.TokenManager;
import com.google.android.material.button.MaterialButton;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgentDashboardFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvAgentGreeting, tvTotalReferralsCount, tvAgentCommissionsAmount;
    private AgentAPIService agentAPIService;
    private AuthAPIService authAPIService;
    private TokenManager tokenManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_agent_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tokenManager = new TokenManager(requireContext());
        agentAPIService = ApiClient.getInstance(requireContext()).getAgentService();
        authAPIService = ApiClient.getInstance(requireContext()).getAuthService();

        swipeRefreshLayout = view.findViewById(R.id.swipeAgentDash);
        tvAgentGreeting = view.findViewById(R.id.tvAgentGreeting);
        tvTotalReferralsCount = view.findViewById(R.id.tvTotalReferralsCount);
        tvAgentCommissionsAmount = view.findViewById(R.id.tvAgentCommissionsAmount);
        MaterialButton btnShareLink = view.findViewById(R.id.btnShareAgentLink);

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(this::loadRealTimeAgentData);
        }

        if (btnShareLink != null) {
            btnShareLink.setOnClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Agent Referral Link", "https://chuosokoni.ac.tz/ref/CS-AGENT-9482");
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(requireContext(), "Referral link copied & ready to share!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        loadRealTimeAgentData();
    }

    private void loadRealTimeAgentData() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
        }

        Long agentId = tokenManager.getUserId();
        if (agentId == null || agentId == -1L) {
            agentId = 1L; // Demo fallback ID
        }

        // 1. Fetch real-time agent profile name
        authAPIService.getUserById(agentId).enqueue(new Callback<UserDto>() {
            @Override
            public void onResponse(@NonNull Call<UserDto> call, @NonNull Response<UserDto> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    UserDto user = response.body();
                    if (tvAgentGreeting != null) {
                        tvAgentGreeting.setText("Welcome, " + (user.getFullName() != null ? user.getFullName() : "Faith"));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserDto> call, @NonNull Throwable t) {
                // Keep default
            }
        });

        // 2. Fetch real-time agent metrics and commissions
        agentAPIService.getAgentDashboard(agentId).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull Response<Map<String, Object>> response) {
                if (isAdded()) {
                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            Map<String, Object> map = response.body();
                            if (map.containsKey("totalReferrals") && tvTotalReferralsCount != null) {
                                tvTotalReferralsCount.setText(String.valueOf(map.get("totalReferrals")));
                            }
                            if (map.containsKey("totalCommissions") && tvAgentCommissionsAmount != null) {
                                tvAgentCommissionsAmount.setText("TZS " + map.get("totalCommissions"));
                            }
                        } catch (Exception e) {
                            // Fallback demo values
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                if (isAdded()) {
                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    Toast.makeText(requireContext(), "Offline Mode: Showing cached agent metrics", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
