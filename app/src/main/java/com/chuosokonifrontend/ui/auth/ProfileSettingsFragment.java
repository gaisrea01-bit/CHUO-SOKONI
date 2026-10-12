package com.chuosokonifrontend.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.chuosokonifrontend.R;
import com.chuosokonifrontend.data.api.AuthAPIService;
import com.chuosokonifrontend.data.dto.UserDto;
import com.chuosokonifrontend.data.local.CachedUser;
import com.chuosokonifrontend.data.local.ChuoSokoniDatabase;
import com.chuosokonifrontend.data.network.ApiClient;
import com.chuosokonifrontend.data.network.TokenManager;
import com.google.android.material.button.MaterialButton;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileSettingsFragment extends Fragment {

    private TextView tvProfileName, tvProfileRole, tvProfileEmailPhone;
    private TokenManager tokenManager;
    private AuthAPIService authAPIService;
    private ChuoSokoniDatabase database;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tokenManager = new TokenManager(requireContext());
        authAPIService = ApiClient.getInstance(requireContext()).getAuthService();
        database = ChuoSokoniDatabase.getDatabase(requireContext());

        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileRole = view.findViewById(R.id.tvProfileRole);
        tvProfileEmailPhone = view.findViewById(R.id.tvProfileEmailPhone);
        MaterialButton btnLogout = view.findViewById(R.id.btnLogout);

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                tokenManager.clear();
                Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
            });
        }

        loadCachedUserProfile();
        syncRealTimeUserProfile();
    }

    private void loadCachedUserProfile() {
        Executors.newSingleThreadExecutor().execute(() -> {
            CachedUser cached = database.userDao().getCachedUser();
            if (cached != null && isAdded() && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (tvProfileName != null) tvProfileName.setText(cached.getFullName());
                    if (tvProfileRole != null) tvProfileRole.setText(cached.getRoleName());
                    if (tvProfileEmailPhone != null) tvProfileEmailPhone.setText(cached.getEmail() + " • " + cached.getPhone());
                });
            }
        });
    }

    private void syncRealTimeUserProfile() {
        Long userId = tokenManager.getUserId();
        if (userId == null || userId == -1L) {
            userId = 1L; // Demo fallback ID
        }

        authAPIService.getUserById(userId).enqueue(new Callback<UserDto>() {
            @Override
            public void onResponse(@NonNull Call<UserDto> call, @NonNull Response<UserDto> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    UserDto user = response.body();
                    if (tvProfileName != null) {
                        tvProfileName.setText(user.getFullName() != null ? user.getFullName() : "User");
                    }
                    if (tvProfileRole != null) {
                        tvProfileRole.setText(user.getRoleName() != null ? user.getRoleName() : "Student Entrepreneur");
                    }
                    if (tvProfileEmailPhone != null) {
                        tvProfileEmailPhone.setText((user.getEmail() != null ? user.getEmail() : "") + " • " + (user.getPhone() != null ? user.getPhone() : ""));
                    }

                    // Cache user locally in Room
                    saveUserToLocalCache(user);
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserDto> call, @NonNull Throwable t) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Offline Mode: Showing cached profile", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveUserToLocalCache(UserDto user) {
        Executors.newSingleThreadExecutor().execute(() -> {
            CachedUser cachedUser = new CachedUser(
                    user.getUserId() != null ? user.getUserId() : 1L,
                    user.getFullName() != null ? user.getFullName() : "User",
                    user.getEmail() != null ? user.getEmail() : "",
                    user.getPhone() != null ? user.getPhone() : "",
                    user.getRoleName() != null ? user.getRoleName() : "CUSTOMER"
            );
            database.userDao().clearCachedUser();
            database.userDao().insertUser(cachedUser);
        });
    }
}
