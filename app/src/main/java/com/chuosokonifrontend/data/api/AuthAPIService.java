package com.chuosokonifrontend.data.api;

import com.chuosokonifrontend.data.dto.UserDto;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.POST;

public interface AuthAPIService {

    @POST("api/v1/auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("api/v1/auth/register")
    Call<Map<String, Object>> register(@Body RegisterRequest request);

    @GET("api/users/{id}")
    Call<UserDto> getUserById(@Path("id") Long id);

    class RegisterRequest {
        private String fullName;
        private String email;
        private String password;
        private String phoneNumber;
        private Integer universityId;
        private String roleName;
        private String referralCode;

        public RegisterRequest(String fullName, String email, String password, String phoneNumber, String roleName) {
            this.fullName = fullName;
            this.email = email;
            this.password = password;
            this.phoneNumber = phoneNumber;
            this.roleName = roleName;
            this.universityId = 1; // Default university ID
        }

        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
        public String getPassword() { return password; }
        public String getPhoneNumber() { return phoneNumber; }
        public Integer getUniversityId() { return universityId; }
        public String getRoleName() { return roleName; }
        public String getReferralCode() { return referralCode; }
    }

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
