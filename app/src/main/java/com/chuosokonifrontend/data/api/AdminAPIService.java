package com.chuosokonifrontend.data.api;

import com.chuosokonifrontend.data.dto.UserDto;
import com.chuosokonifrontend.data.dto.DisputeDto;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Body;

public interface AdminAPIService {

    @GET("api/v1/admin/dashboard/overview")
    Call<Map<String, Object>> getAdminDashboard();

    @GET("api/v1/admin/dashboard/financials")
    Call<Map<String, Object>> getFinancialSummary();

    @GET("api/v1/admin/dashboard/product-queue")
    Call<Map<String, Object>> getProductReviewQueueMetrics();

    @GET("api/v1/admin/dashboard/referrals")
    Call<Map<String, Object>> getReferralProgramMetrics();

    @GET("api/admin/verification-queues")
    Call<List<Object>> getVerificationQueues();

    @PUT("api/admin/users/{userId}/verify")
    Call<UserDto> verifyUser(@Path("userId") Long userId, @Body Object approvalStatus);

    @GET("api/disputes/pending")
    Call<List<Map<String, Object>>> getAllDisputes();

    @PUT("api/disputes/{disputeId}/resolve")
    Call<Map<String, Object>> resolveDispute(@Path("disputeId") Long disputeId, @Body Map<String, Object> resolution);
}
