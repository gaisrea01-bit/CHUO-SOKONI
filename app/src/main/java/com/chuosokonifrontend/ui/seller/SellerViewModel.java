package com.chuosokonifrontend.ui.seller;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.chuosokonifrontend.data.api.SellerAPIService;
import com.chuosokonifrontend.data.dto.ProductDto;
import com.chuosokonifrontend.data.network.ApiClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellerViewModel extends AndroidViewModel {

    private final SellerAPIService sellerAPIService;
    private final MutableLiveData<List<ProductDto>> sellerProducts = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public SellerViewModel(@NonNull Application application) {
        super(application);
        sellerAPIService = ApiClient.getInstance(application).getSellerService();
    }

    public LiveData<List<ProductDto>> getSellerProducts() { return sellerProducts; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void fetchSellerProducts(Long sellerId) {
        isLoading.setValue(true);
        sellerAPIService.getSellerProducts(sellerId).enqueue(new Callback<List<ProductDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProductDto>> call, @NonNull Response<List<ProductDto>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    sellerProducts.setValue(response.body());
                } else {
                    errorMessage.setValue("Failed to load seller products");
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
