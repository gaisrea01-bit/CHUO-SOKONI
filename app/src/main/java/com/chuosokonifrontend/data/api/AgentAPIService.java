package com.chuosokonifrontend.data.api;

import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AgentAPIService {

    @GET("api/v1/agents/dashboard/{userId}")
    Call<Map<String, Object>> getAgentDashboard(@Path("userId") Long userId);

    @GET("api/v1/agents/{userId}/referred-users")
    Call<List<Map<String, Object>>> getReferredUsers(@Path("userId") Long userId);

    @GET("api/v1/commissions/agent/{agentId}")
    Call<List<Map<String, Object>>> getAgentCommissions(@Path("agentId") Long agentId);
}
