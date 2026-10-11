/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package tz.ac.mzumbe.chuosokoni.model.enums;

/**
 * Status of the provider-level payment transaction record.
 */
public enum PaymentTransactionStatus {

    PENDING,
    PROCESSING,
    SUCCESS,
    FAILED,
    REVERSED,
    CANCELLED
}