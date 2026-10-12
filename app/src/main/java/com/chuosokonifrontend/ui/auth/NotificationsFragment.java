package com.chuosokonifrontend.ui.auth;

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
import com.chuosokonifrontend.data.api.NotificationAPIService;
import com.chuosokonifrontend.data.local.CachedNotification;
import com.chuosokonifrontend.data.local.ChuoSokoniDatabase;
import com.chuosokonifrontend.data.network.ApiClient;
import com.chuosokonifrontend.data.network.TokenManager;
import com.chuosokonifrontend.ui.adapters.NotificationAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TokenManager tokenManager;
    private NotificationAPIService notificationAPIService;
    private NotificationAdapter notificationAdapter;
    private ChuoSokoniDatabase database;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tokenManager = new TokenManager(requireContext());
        notificationAPIService = ApiClient.getInstance(requireContext()).getNotificationService();
        database = ChuoSokoniDatabase.getDatabase(requireContext());

        recyclerView = view.findViewById(R.id.recyclerViewNotifications);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            notificationAdapter = new NotificationAdapter(null);
            recyclerView.setAdapter(notificationAdapter);
        }

        loadCachedNotifications();
        syncRealTimeNotifications();
    }

    private void loadCachedNotifications() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<CachedNotification> cachedList = database.notificationDao().getAllCachedNotifications();
            List<Object> notifs = new ArrayList<>(cachedList);
            if (isAdded() && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (!notifs.isEmpty() && notificationAdapter != null) {
                        notificationAdapter.setNotificationList(notifs);
                    }
                });
            }
        });
    }

    private void syncRealTimeNotifications() {
        Long userId = tokenManager.getUserId();
        if (userId == null || userId == -1L) {
            userId = 1L; // Demo fallback ID
        }

        notificationAPIService.getUserNotifications(userId).enqueue(new Callback<List<Object>>() {
            @Override
            public void onResponse(@NonNull Call<List<Object>> call, @NonNull Response<List<Object>> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    List<Object> notifs = response.body();
                    if (notificationAdapter != null) {
                        notificationAdapter.setNotificationList(notifs);
                    }
                    saveNotificationsToLocalCache(notifs);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Object>> call, @NonNull Throwable t) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Offline Mode: Showing cached notifications", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveNotificationsToLocalCache(List<Object> notifs) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<CachedNotification> cachedList = new ArrayList<>();
            long idCounter = 1L;
            for (Object obj : notifs) {
                CachedNotification cn = new CachedNotification(
                        idCounter++,
                        "INFO",
                        obj.toString(),
                        false,
                        "recent"
                );
                cachedList.add(cn);
            }
            database.notificationDao().clearCachedNotifications();
            database.notificationDao().insertNotifications(cachedList);
        });
    }
}
