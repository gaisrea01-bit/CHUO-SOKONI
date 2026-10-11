/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package tz.ac.mzumbe.chuosokoni.model.enums;

/**
 * Product lifecycle status.
 *
 * Product approval/rejection and availability are represented
 * independently through these lifecycle values.
 */
public enum ProductStatus {

    DRAFT,
    PENDING_APPROVAL,
    APPROVED,
    REJECTED,
    ACTIVE,
    INACTIVE,
    SUSPENDED,
    SOLD_OUT
}
