package com.sandeep.expensetracker.entity;

/**
 * Fixed set of expense categories supported by the application.
 * Kept as an enum (rather than a DB table) since the category list
 * is a closed, well-known set for this domain.
 */
public enum Category {
    FOOD,
    SHOPPING,
    BILLS,
    FUEL,
    TRAVEL,
    MEDICAL,
    EDUCATION,
    ENTERTAINMENT,
    INVESTMENT,
    OTHER
}
