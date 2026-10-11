/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package tz.ac.mzumbe.chuosokoni.model.enums;

/**
 * Customer order lifecycle.
 */
public enum OrderStatus {

    PENDING,
    CONFIRMED,
    PROCESSING,
    READY_FOR_FULFILLMENT,
    SHIPPED,
    DELIVERED,
    RECEIVED,
    COMPLETED,
    CANCELLED,
    DISPUTED,
    REFUNDED
}