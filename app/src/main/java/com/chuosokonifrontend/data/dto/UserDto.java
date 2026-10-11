package com.chuosokonifrontend.data.dto;

public class UserDto {
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private String status;
    private String roleName;
    private Long universityId;
    private String referralCodeUsed;

    public UserDto() {}

    public UserDto(Long userId, String fullName, String email, String phone, String status, String roleName, Long universityId, String referralCodeUsed) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.roleName = roleName;
        this.universityId = universityId;
        this.referralCodeUsed = referralCodeUsed;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    public Long getUniversityId() { return universityId; }
    public void setUniversityId(Long universityId) { this.universityId = universityId; }

    public String getReferralCodeUsed() { return referralCodeUsed; }
    public void setReferralCodeUsed(String referralCodeUsed) { this.referralCodeUsed = referralCodeUsed; }
}
