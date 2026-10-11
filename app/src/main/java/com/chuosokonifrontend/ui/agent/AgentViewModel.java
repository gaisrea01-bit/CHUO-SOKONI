package com.chuosokonifrontend.ui.agent;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.chuosokonifrontend.data.api.AgentAPIService;
import com.chuosokonifrontend.data.network.ApiClient;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgentViewModel extends AndroidViewModel {

    private final AgentAPIService agentAPIService;
    private final MutableLiveData<Map<String, Object>> agentDashboard = new MutableLiveData<>();
    private final MutableLiveData<List<Map<String, Object>>> referredUsers = new MutableLiveData<>();
    private final MutableLiveData<List<Map<String, Object>>> agentCommissions = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public AgentViewModel(@NonNull Application application) {
        super(application);
        agentAPIService = ApiClient.getInstance(application).getAgentService();
    }

    public LiveData<Map<String, Object>> getAgentDashboard() { return agentDashboard; }
    public LiveData<List<Map<String, Object>>> getReferredUsers() { return referredUsers; }
    public LiveData<List<Map<String, Object>>> getAgentCommissions() { return agentCommissions; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void fetchAgentDashboard(Long userId) {
        isLoading.setValue(true);
        agentAPIService.getAgentDashboard(userId).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull Response<Map<String, Object>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    agentDashboard.setValue(response.body());
                } else {
                    errorMessage.setValue("Failed to load agent dashboard");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void fetchReferredUsers(Long userId) {
        isLoading.setValue(true);
        agentAPIService.getReferredUsers(userId).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(@NonNull Call<List<Map<String, Object>>> call, @NonNull Response<List<Map<String, Object>>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    referredUsers.setValue(response.body());
                } else {
                    errorMessage.setValue("Failed to load referred users");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Map<String, Object>>> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void fetchAgentCommissions(Long agentId) {
        isLoading.setValue(true);
        agentAPIService.getAgentCommissions(agentId).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(@NonNull Call<List<Map<String, Object>>> call, @NonNull Response<List<Map<String, Object>>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    agentCommissions.setValue(response.body());
                } else {
                    errorMessage.setValue("Failed to load agent commissions");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Map<String, Object>>> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }
}
