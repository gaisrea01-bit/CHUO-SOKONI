package com.chuosokonifrontend.ui.investor;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.chuosokonifrontend.data.api.InvestorAPIService;
import com.chuosokonifrontend.data.network.ApiClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvestorViewModel extends AndroidViewModel {

    private final InvestorAPIService investorAPIService;
    private final MutableLiveData<List<Object>> wholesaleOpportunities = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public InvestorViewModel(@NonNull Application application) {
        super(application);
        investorAPIService = ApiClient.getInstance(application).getInvestorService();
    }

    public LiveData<List<Object>> getWholesaleOpportunities() { return wholesaleOpportunities; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void fetchWholesaleOpportunities() {
        isLoading.setValue(true);
        investorAPIService.getWholesaleOpportunities().enqueue(new Callback<List<Object>>() {
            @Override
            public void onResponse(@NonNull Call<List<Object>> call, @NonNull Response<List<Object>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    wholesaleOpportunities.setValue(response.body());
                } else {
                    errorMessage.setValue("Failed to load wholesale opportunities");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Object>> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }
}
