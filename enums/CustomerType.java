/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package tz.ac.mzumbe.chuosokoni.model.enums;

/**
 * Customer classification.
 *
 * The supplied ERD currently shows customer_type as VARCHAR rather
 * than ENUM. Keep the entity field as String unless the business
 * classification has been formally finalized.
 */
public enum CustomerType {

    INDIVIDUAL,
    BUSINESS,
    OTHER
}