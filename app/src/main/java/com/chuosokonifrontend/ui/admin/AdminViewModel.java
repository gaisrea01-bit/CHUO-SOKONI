package com.chuosokonifrontend.ui.admin;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.chuosokonifrontend.data.api.AdminAPIService;
import com.chuosokonifrontend.data.network.ApiClient;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminViewModel extends AndroidViewModel {

    private final AdminAPIService adminAPIService;
    private final MutableLiveData<Map<String, Object>> adminDashboard = new MutableLiveData<>();
    private final MutableLiveData<List<Object>> verificationQueues = new MutableLiveData<>();
    private final MutableLiveData<List<Map<String, Object>>> disputes = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public AdminViewModel(@NonNull Application application) {
        super(application);
        adminAPIService = ApiClient.getInstance(application).getAdminService();
    }

    public LiveData<Map<String, Object>> getAdminDashboard() { return adminDashboard; }
    public LiveData<List<Object>> getVerificationQueues() { return verificationQueues; }
    public LiveData<List<Map<String, Object>>> getDisputes() { return disputes; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void fetchAdminDashboard() {
        isLoading.setValue(true);
        adminAPIService.getAdminDashboard().enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull Response<Map<String, Object>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    adminDashboard.setValue(response.body());
                } else {
                    errorMessage.setValue("Failed to load admin dashboard");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void fetchVerificationQueues() {
        isLoading.setValue(true);
        adminAPIService.getVerificationQueues().enqueue(new Callback<List<Object>>() {
            @Override
            public void onResponse(@NonNull Call<List<Object>> call, @NonNull Response<List<Object>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    verificationQueues.setValue(response.body());
                } else {
                    errorMessage.setValue("Failed to load verification queues");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Object>> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void fetchDisputes() {
        isLoading.setValue(true);
        adminAPIService.getAllDisputes().enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(@NonNull Call<List<Map<String, Object>>> call, @NonNull Response<List<Map<String, Object>>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    disputes.setValue(response.body());
                } else {
                    errorMessage.setValue("Failed to load disputes");
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
