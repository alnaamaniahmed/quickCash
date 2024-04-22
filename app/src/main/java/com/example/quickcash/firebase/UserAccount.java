package com.example.quickcash.firebase;

import java.io.Serializable;

/**
 * Class used to store user account information
 * Acts as intermediary between .java and .JSON data
 */
public abstract class UserAccount implements Serializable{
    protected String email;
    protected String name;
    protected String phone;
    protected String preferredJobs;
    protected float normRating;
    protected int ratingCount;

    public UserAccount(String name, String email, String phone) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.normRating = 0;
        this.ratingCount = 0;
    }
    public UserAccount(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return getClass().getSimpleName();
    }

    public String getPreferredJobs() {
        return preferredJobs;
    }

    public void setPreferredJobs(String preferredJobs) {
        this.preferredJobs = preferredJobs;
    }

    public float getNormRating() {
        return normRating;
    }

    public int getRatingCount() {
        return ratingCount;
    }
}
