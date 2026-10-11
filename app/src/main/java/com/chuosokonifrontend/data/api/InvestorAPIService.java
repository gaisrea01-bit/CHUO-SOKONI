package com.chuosokonifrontend.data.api;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface InvestorAPIService {

    @GET("api/investors/opportunities")
    Call<List<Object>> getWholesaleOpportunities();

    @POST("api/investors/acquire")
    Call<Object> acquireStock(@Body Object acquisitionRequest);

    @GET("api/investors/{investorId}/holdings")
    Call<List<Object>> getManagedInventory(@Path("investorId") Long investorId);

    @GET("api/investors/{investorId}/returns")
    Call<List<Object>> getInvestorReturns(@Path("investorId") Long investorId);

    @GET("api/investors/{investorId}/portfolio")
    Call<Object> getInvestorPortfolio(@Path("investorId") Long investorId);
}
