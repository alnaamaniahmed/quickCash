package com.example.quickcash.firebase;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quickcash.core.AppConstants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

/**
 * Class used to Create, Read, Update, & Delete data from Firebase Realtime Database
 * TODO: Figure out how to read/authenticate users & write tests(?)
 */
public class FirebaseCRUD {
    private FirebaseDatabase database;
    FirebaseQueries fq;
    private DatabaseReference userAccountRef = null;
    private DatabaseReference employeeRef = null;
    private DatabaseReference employerRef = null;
    private DatabaseReference jobRef = null;
    private DatabaseReference appRef = null;
    private String email, password, name, phone, role;

    private StorageReference storageRef;
    public FirebaseCRUD(FirebaseDatabase database) {
        this.database = database;
        fq = new FirebaseQueries(database);
        initializeReferences();
        initializeListeners();
        initializeStorage();
    }

    protected void initializeReferences() {
        userAccountRef = database.getReference ("User Accounts");
        employeeRef = userAccountRef.child(AppConstants.EMPLOYEE);
        employerRef = userAccountRef.child(AppConstants.EMPLOYER);
        jobRef = database.getReference ("Jobs");
        appRef = database.getReference ("Applications");
    }

    protected void initializeListeners() {

        setUserRefListener();
        setJobRefListener();
    }
    protected void initializeStorage() {
        // Initialize Firebase Storage reference
        storageRef = FirebaseStorage.getInstance().getReference();
    }

    protected void setUserRefListener () {
        //May require OnChildListener instead of ValueEventListener?
        userAccountRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    String email = messageSnapshot.child("email").getValue(String.class);
                    String password = messageSnapshot.child("password").getValue(String.class);
                    String name = messageSnapshot.child("name").getValue(String.class);
                    String phone = messageSnapshot.child("phone").getValue(String.class);
                    String role = messageSnapshot.child("role").getValue(String.class);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    protected void setJobRefListener () {
        //May require OnChildListener instead of ValueEventListener?
        jobRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    String jobTitle = messageSnapshot.child("title").getValue(String.class);
                    String jobType = messageSnapshot.child("type").getValue(String.class);
                    String jobSalary = messageSnapshot.child("salary").getValue(String.class);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void createUser(UserAccount user) {
        String role = user.getRole();
        Map<String, Object> userMap = new HashMap<>();

        userMap.put("email", user.getEmail());
        userMap.put("name", user.getName());
        userMap.put("phone", user.getPhone());
        userMap.put("ratingCount", user.getRatingCount());
        userMap.put("normRating", user.getNormRating());

        if (role.equalsIgnoreCase(AppConstants.EMPLOYEE)) {
            employeeRef.push().setValue(userMap)
                    .addOnSuccessListener(aVoid -> { /*Success Toast?*/ })
                    .addOnFailureListener(aVoid -> { /*FailureToast?*/ });
        }
        else {
            employerRef.push().setValue(userMap)
                    .addOnSuccessListener(aVoid -> { /*Success Toast?*/ })
                    .addOnFailureListener(aVoid -> { /*FailureToast?*/ });
        }
    }

    public void createJob (Job job) {
        Map<String, Object> jobMap = new HashMap<>();

        jobMap.put("title", job.getTitle());
        jobMap.put("description", job.getDescription());
        jobMap.put("type", job.getType());
        jobMap.put("salary", job.getSalary());
        jobMap.put("location", job.getLocation());
        jobMap.put("employerEmail", job.getEmployerEmail());
        jobMap.put("questions", job.getQuestions());

        jobRef.push().setValue(jobMap);
    }
    // Method to fetch a file from Firebase Storage
    public void fetchFileFromStorage(String filePath, OnSuccessListener<Uri> onSuccessListener, OnFailureListener onFailureListener) {
        StorageReference fileRef = storageRef.child(filePath);
        fileRef.getDownloadUrl().addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }

    public void createApplication (JobApplication application) {
        fq.getJob(application.getEmployerEmail(), application.getJobTitle(), new FirebaseQueries.getValueCallback() {
            @Override
            public void onValueReceived(String returnString) {
                Map<String, Object> appMap = new HashMap<>();
                String path = "Jobs/" + returnString + "/Applications";
                DatabaseReference appRef = database.getReference(path);
                appMap.put("jobTitle", application.getJobTitle());
                appMap.put("applicantEmail", application.getApplicantEmail());
                appMap.put("employerEmail", application.getEmployerEmail());
                appMap.put("answerList", application.getAnswerList());
                appMap.put("status", application.getStatus());
                appRef.push().setValue(appMap);
            }
        });
    }

    public void updatePreferredJobs (String email, String prefs) {
        fq.getEmployeeID(email, new FirebaseQueries.getValueCallback() {
            @Override
            public void onValueReceived(String returnString) {
                String path = "User Accounts/Employee/" + returnString + "/preferredJobs";
                DatabaseReference prefRef = database.getReference(path);
                prefRef.setValue(prefs);
            }
        });
    }
}