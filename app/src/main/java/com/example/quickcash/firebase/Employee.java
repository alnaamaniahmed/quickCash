package com.example.quickcash.firebase;

import java.io.Serializable;

public class Employee extends UserAccount implements Serializable {
    public Employee(String name, String email, String phoneNumber) {
        super(name, email, phoneNumber);
    }

    public Employee() {}
}
