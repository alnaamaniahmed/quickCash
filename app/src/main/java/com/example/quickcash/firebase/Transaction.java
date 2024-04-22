package com.example.quickcash.firebase;
import java.io.Serializable;

public class Transaction implements Serializable {
    private String employerEmail;
    private String employeeEmail;
    private String jobTitle;
    private double amount;
    private String currency;
    private String transactionId;
    private String status;

    public Transaction() {
        // Default constructor required for calls to DataSnapshot.getValue(Transaction.class)
    }
    public Transaction(String employerEmail, String employeeEmail, String jobTitle, double amount, String currency, String transactionId, String status) {
        this.employerEmail = employerEmail;
        this.employeeEmail = employeeEmail;
        this.jobTitle = jobTitle;
        this.amount = amount;
        this.currency = currency;
        this.transactionId = transactionId;
        this.status = status;
    }

    public String getEmployerEmail() {
        return employerEmail;
    }

    public void setEmployerEmail(String employerEmail) {
        this.employerEmail = employerEmail;
    }

    public String getEmployeeEmail() {
        return employeeEmail;
    }

    public void setEmployeeEmail(String employeeEmail) {
        this.employeeEmail = employeeEmail;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
