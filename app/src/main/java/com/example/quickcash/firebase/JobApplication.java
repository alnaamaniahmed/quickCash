package com.example.quickcash.firebase;

import java.io.Serializable;
import java.util.ArrayList;

public class JobApplication implements Serializable {
    private String jobTitle;
    private String applicantEmail;
    private String employerEmail;
    private ArrayList<String> answerList;
    private String status;

    public JobApplication() {
        // Default constructor for Firebase
    }
    public JobApplication(String jobTitle, String applicantEmail, String employerEmail, ArrayList<String> answerList, String status) {
        this.jobTitle = jobTitle;
        this.applicantEmail = applicantEmail;
        this.employerEmail = employerEmail;
        this.answerList = answerList;
        this.status = status;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getApplicantEmail() {
        return applicantEmail;
    }

    public String getEmployerEmail() {
        return employerEmail;
    }

    public ArrayList<String> getAnswerList() {
        return answerList;
    }
    public boolean isShortListed(){
        return "shortlisted".equals(status);
    }
    public boolean isPending(){
        return "pending".equals(status);
    }
    public boolean isCompleted(){
        return "completed".equals(status);
    }
    public boolean isDeclined(){
        return "declined".equals(status);

    }
    public boolean isDenied(){
        return "denied".equals(status);

    }
    public boolean isHired() {
        return "accepted".equals(status);
    }

    public String getStatus() {
        return status;
    }
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public void setApplicantEmail(String applicantEmail) {
        this.applicantEmail = applicantEmail;
    }

    public void setEmployerEmail(String employerEmail) {
        this.employerEmail = employerEmail;
    }

    public void setAnswerList(ArrayList<String> answerList) {
        this.answerList = answerList;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}