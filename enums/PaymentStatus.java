/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package tz.ac.mzumbe.chuosokoni.model.enums;

/**
 * Payment lifecycle.
 *
 * Includes the payment states required by the Chuo Sokoni workflow.
 */
public enum PaymentStatus {

    PENDING,
    PROCESSING,
    SUCCESS,
    FAILED,
    REVERSED,
    CANCELLED,
    REFUNDED
}
