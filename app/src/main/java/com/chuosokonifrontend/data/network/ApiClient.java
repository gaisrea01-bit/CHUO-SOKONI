package com.chuosokonifrontend.data.network;

import android.content.Context;
import com.chuosokonifrontend.data.api.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

public class ApiClient {
    // Computer's active Wi-Fi IP address on your local network
    private static final String DEV_BASE_URL = "http://192.168.1.192:8080/";
    private static final String PROD_BASE_URL = "https://api.chuosokoni.ac.tz/";

    // Toggle to true when deploying to production
    private static final boolean IS_PRODUCTION = false;
    private static final String BASE_URL = IS_PRODUCTION ? PROD_BASE_URL : DEV_BASE_URL;

    private static ApiClient instance;
    private final Retrofit retrofit;

    private ApiClient(Context context) {
        TokenManager tokenManager = new TokenManager(context);
        AuthInterceptor authInterceptor = new AuthInterceptor(tokenManager);
        TokenAuthentication tokenAuthentication = new TokenAuthentication(tokenManager);

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .authenticator(tokenAuthentication)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(BigDecimal.class, (JsonSerializer<BigDecimal>) (src, typeOfSrc, ctx) -> new JsonPrimitive(src))
                .registerTypeAdapter(BigDecimal.class, (JsonDeserializer<BigDecimal>) (json, typeOfT, ctx) -> new BigDecimal(json.getAsString()))
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static synchronized ApiClient getInstance(Context context) {
        if (instance == null) {
            instance = new ApiClient(context);
        }
        return instance;
    }

    public AuthAPIService getAuthService() {
        return retrofit.create(AuthAPIService.class);
    }

    public CustomerAPIService getCustomerService() {
        return retrofit.create(CustomerAPIService.class);
    }

    public SellerAPIService getSellerService() {
        return retrofit.create(SellerAPIService.class);
    }

    public InvestorAPIService getInvestorService() {
        return retrofit.create(InvestorAPIService.class);
    }

    public AgentAPIService getAgentService() {
        return retrofit.create(AgentAPIService.class);
    }

    public AdminAPIService getAdminService() {
        return retrofit.create(AdminAPIService.class);
    }

    public NotificationAPIService getNotificationService() {
        return retrofit.create(NotificationAPIService.class);
    }
}
