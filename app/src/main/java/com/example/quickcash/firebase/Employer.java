package com.example.quickcash.firebase;

import java.io.Serializable;

public class Employer extends UserAccount implements Serializable {
    public Employer(String name, String email, String phoneNumber) {
        super(name, email, phoneNumber);
    }

    public Employer() {}
}
