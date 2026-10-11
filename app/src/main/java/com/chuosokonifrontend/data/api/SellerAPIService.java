package com.chuosokonifrontend.data.api;

import com.chuosokonifrontend.data.dto.ProductDto;
import com.chuosokonifrontend.data.dto.NegotiationDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface SellerAPIService {

    @GET("api/sellers/{sellerId}/dashboard")
    Call<Object> getSellerDashboard(@Path("sellerId") Long sellerId);

    @GET("api/products/seller/{sellerId}")
    Call<List<ProductDto>> getSellerProducts(@Path("sellerId") Long sellerId);

    @POST("api/products")
    Call<ProductDto> addProduct(@Body ProductDto productDto);

    @PUT("api/products/{productId}")
    Call<ProductDto> updateProduct(@Path("productId") Long productId, @Body ProductDto productDto);

    @GET("api/negotiations/seller/{sellerId}")
    Call<List<NegotiationDto>> getSellerNegotiations(@Path("sellerId") Long sellerId);

    @POST("api/negotiations/{negotiationId}/respond")
    Call<NegotiationDto> respondToNegotiation(@Path("negotiationId") Long negotiationId, @Body Object response);
}
