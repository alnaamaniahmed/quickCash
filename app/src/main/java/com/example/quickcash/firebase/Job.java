package com.example.quickcash.firebase;

import java.io.Serializable;
import java.util.*;

public class Job implements Serializable {
    Map<String, JobApplication> Applications;
    String title;
    String description;
    String type;
    Double salary;
    String location;
    String employerEmail;

    ArrayList<String> questions;

    public Job () {
        // Empty constructor; attributes assigned via UI elements
        questions = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEmployerEmail() {
        return employerEmail;
    }

    public void setEmployerEmail(String employerEmail) {
        this.employerEmail = employerEmail;
    }
    public Map<String, JobApplication> getApplications() {
        return Applications;
    }

    public void setApplications(Map<String, JobApplication> applications) {
        this.Applications = applications;
    }
    public ArrayList<String> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<String> questions) {
        this.questions = questions;
    }
}
