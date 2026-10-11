package com.chuosokonifrontend.ui.customer;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.chuosokonifrontend.data.dto.ProductDto;
import com.chuosokonifrontend.data.network.ApiClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerViewModel extends AndroidViewModel {

    private final MutableLiveData<List<ProductDto>> productsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public CustomerViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<ProductDto>> getProducts() { return productsLiveData; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void fetchProducts() {
        isLoading.setValue(true);
        ApiClient.getInstance(getApplication()).getCustomerService().browseProducts().enqueue(new Callback<List<ProductDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProductDto>> call, @NonNull Response<List<ProductDto>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    productsLiveData.setValue(response.body());
                } else {
                    errorMessage.setValue("Failed to load products");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ProductDto>> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }
}
