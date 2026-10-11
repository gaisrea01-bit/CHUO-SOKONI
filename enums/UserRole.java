package tz.ac.mzumbe.chuosokoni.model.enums;

/**
 * Application roles defined by the Chuo Sokoni user model.
 *
 * The role is used by Spring Security for role-based access control.
 */
public enum UserRole {

    CUSTOMER,
    SELLER,
    INVESTOR,
    AGENT,
    ADMIN
}