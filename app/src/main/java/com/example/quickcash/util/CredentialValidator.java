package com.example.quickcash.util;

public class CredentialValidator {
    public boolean isEmpty(String str) {
        return str.isEmpty();
    }

    public boolean isValidEmailAddress(String emailAddress) {
        return emailAddress.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z]+\\.[a-zA-Z]+$");
    }

    public boolean isValidPhoneNumber(String phoneNumber){
        return phoneNumber.length() == 10;
    }

    public boolean isValidPassword(String password){
        return password.length() >= 8;
    }
    public boolean isValidRole(String role){
        return role.equalsIgnoreCase("Employer") || role.equalsIgnoreCase("Employee");
    }

}
