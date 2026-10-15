package com.chuosokonifrontend.ui.auth;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.chuosokonifrontend.data.api.AuthAPIService;
import com.chuosokonifrontend.data.network.ApiClient;
import com.chuosokonifrontend.data.network.TokenManager;
import com.chuosokonifrontend.util.ValidationUtils;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthViewModel extends AndroidViewModel {

    private final TokenManager tokenManager;
    private final AuthAPIService authAPIService;

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> registerSuccess = new MutableLiveData<>(false);

    public AuthViewModel(@NonNull Application application) {
        super(application);
        tokenManager = new TokenManager(application);
        authAPIService = ApiClient.getInstance(application).getAuthService();
    }

    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getLoginSuccess() { return loginSuccess; }
    public LiveData<Boolean> getRegisterSuccess() { return registerSuccess; }

    public void login(String identifier, String password) {
        if (!ValidationUtils.isNotEmpty(identifier) || !ValidationUtils.isNotEmpty(password)) {
            errorMessage.setValue("Please enter valid credentials");
            return;
        }

        isLoading.setValue(true);
        AuthAPIService.LoginRequest request = new AuthAPIService.LoginRequest(identifier, password);
        authAPIService.login(request).enqueue(new Callback<AuthAPIService.AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthAPIService.AuthResponse> call, @NonNull Response<AuthAPIService.AuthResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    AuthAPIService.AuthResponse authResp = response.body();
                    if (authResp.getToken() != null) {
                        tokenManager.saveToken(authResp.getToken());
                        if (authResp.getUser() != null && authResp.getUser().getUserId() != null) {
                            tokenManager.saveUserId(authResp.getUser().getUserId());
                        }
                        loginSuccess.setValue(true);
                    } else {
                        errorMessage.setValue("Authentication token missing");
                    }
                } else {
                    try {
                        String err = response.errorBody() != null ? response.errorBody().string() : "Invalid credentials or server error";
                        errorMessage.setValue("Login failed: " + err);
                    } catch (Exception e) {
                        errorMessage.setValue("Invalid credentials or server error");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthAPIService.AuthResponse> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void register(String fullName, String phone, String password, String roleName) {
        if (!ValidationUtils.isNotEmpty(fullName) || !ValidationUtils.isValidPassword(password)) {
            errorMessage.setValue("Please enter a valid full name and password (min 6 chars)");
            return;
        }

        isLoading.setValue(true);
        String email = phone.contains("@") ? phone : phone + "@chuosokoni.ac.tz";
        AuthAPIService.RegisterRequest req = new AuthAPIService.RegisterRequest(fullName, email, password, phone, roleName != null ? roleName : "CUSTOMER");

        authAPIService.register(req).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull Response<Map<String, Object>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    registerSuccess.setValue(true);
                } else {
                    try {
                        String err = response.errorBody() != null ? response.errorBody().string() : "Registration failed. Try again.";
                        errorMessage.setValue("Registration failed: " + err);
                    } catch (Exception e) {
                        errorMessage.setValue("Registration failed. Try again.");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }
}
