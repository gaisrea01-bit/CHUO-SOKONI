package com.chuosokonifrontend.data.api;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface NotificationAPIService {
    @GET("api/notifications/user/{userId}")
    Call<List<Object>> getUserNotifications(@Path("userId") Long userId);
}
