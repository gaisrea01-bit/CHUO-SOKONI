/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package tz.ac.mzumbe.chuosokoni.model.enums;

/**
 * Payment channel/method used by a customer.
 *
 * The ERD currently shows payment_method as VARCHAR.
 */
public enum PaymentMethod {

    MOBILE_MONEY,
    BANK_TRANSFER,
    CARD,
    OTHER
}