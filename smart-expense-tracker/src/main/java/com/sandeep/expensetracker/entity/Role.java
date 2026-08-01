package com.sandeep.expensetracker.entity;

/**
 * Defines the access roles available in the system.
 * USER - normal application user, can manage their own data.
 * ADMIN - elevated privileges (future scope: manage all users' data).
 */
public enum Role {
    USER,
    ADMIN
}
