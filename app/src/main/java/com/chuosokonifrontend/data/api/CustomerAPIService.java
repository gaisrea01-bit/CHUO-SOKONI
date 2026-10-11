package com.chuosokonifrontend.data.api;

import com.chuosokonifrontend.data.dto.ProductDto;
import com.chuosokonifrontend.data.dto.PaymentDto;
import com.chuosokonifrontend.data.dto.DisputeDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CustomerAPIService {

    @GET("api/products")
    Call<List<ProductDto>> browseProducts();

    @GET("api/products/{id}")
    Call<ProductDto> getProductDetails(@Path("id") Long productId);

    @POST("api/orders")
    Call<Object> placeOrder(@Body Object orderRequest);

    @GET("api/orders/customer/{customerId}")
    Call<List<Object>> getCustomerOrders(@Path("customerId") Long customerId);

    @POST("api/payments")
    Call<PaymentDto> processPayment(@Body PaymentDto paymentDto);

    @POST("api/disputes")
    Call<DisputeDto> raiseDispute(@Body DisputeDto disputeDto);
}
