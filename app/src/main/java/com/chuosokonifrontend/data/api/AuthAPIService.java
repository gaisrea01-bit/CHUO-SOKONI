package com.chuosokonifrontend.data.api;

import com.chuosokonifrontend.data.dto.UserDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.POST;

public interface AuthAPIService {

    @POST("api/auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("api/auth/register")
    Call<UserDto> register(@Body UserDto userDto);

    @GET("api/users/{id}")
    Call<UserDto> getUserById(@Path("id") Long id);

    class LoginRequest {
        private String email;
        private String password;

        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    class AuthResponse {
        private String token;
        private UserDto user;

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public UserDto getUser() { return user; }
        public void setUser(UserDto user) { this.user = user; }
    }
}
