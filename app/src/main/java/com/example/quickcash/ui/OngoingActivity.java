package com.example.quickcash.ui;

import static com.example.quickcash.core.AppConstants.EMPLOYEE;
import static com.example.quickcash.core.AppConstants.EMPLOYER;
import static com.example.quickcash.core.AppConstants.FIREBASE_SERVER_KEY;
import static com.example.quickcash.core.AppConstants.PUSH_NOTIFICATION_ENDPOINT;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.quickcash.R;
import com.example.quickcash.core.AppConstants;
import com.example.quickcash.firebase.Employer;
import com.example.quickcash.firebase.Job;
import com.example.quickcash.firebase.JobApplication;
import com.example.quickcash.firebase.Rating;
import com.example.quickcash.util.MenuAdapter;
import com.example.quickcash.util.MenuManager;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OngoingActivity extends AppCompatActivity{
    private RequestQueue requestQueue;
    private JobApplication jobApplication;
    private Job currentJob;
    private String jobTitle;
    private String applicantEmail;
    private String employerEmail;
    private String jobLocation;
    private String jobDescription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing);
        setUpMenu();

        TextView textViewApplicantEmail = findViewById(R.id.applicantEmail);
        TextView textViewJobTitle = findViewById(R.id.jobTitleInfo);
        TextView textViewJobDescription = findViewById(R.id.jobDescriptionBox);
        TextView textViewJobLocation = findViewById(R.id.jobLocation);
        Button buttonMarkJobAsCompleted = findViewById(R.id.MarkJobAsCompletedButton);
        Button buttonMarkPrefer = findViewById(R.id.markPrefer);

        Intent intent = getIntent();
        currentJob = (Job) intent.getSerializableExtra("ongoingJob");
        jobApplication = (JobApplication) intent.getSerializableExtra("jobApplication");
        buttonMarkPrefer.setOnClickListener(v -> {
            addPreferredEmployeeByEmail(jobApplication.getEmployerEmail(), jobApplication.getApplicantEmail());
        });

        if (jobApplication != null && currentJob != null) {
            textViewApplicantEmail.setText("Applicant Email: " + jobApplication.getApplicantEmail());
            textViewJobTitle.setText("Job Title: " + currentJob.getTitle());
            textViewJobDescription.setText("Job Description: " + currentJob.getDescription());
            textViewJobLocation.setText("Location: " + currentJob.getLocation());

            buttonMarkJobAsCompleted.setOnClickListener(v -> {
                // Handle mark as completed action
                markCompletePrompt(currentJob, jobApplication);
            });
        } else {
            Toast.makeText(this, "Error: Job or JobApplication data is not available.", Toast.LENGTH_LONG).show();
        }
    }

    private void addPreferredEmployeeByEmail(String employerEmail, String preferredEmployeeEmail) {
        DatabaseReference employerRef = FirebaseDatabase.getInstance()
                .getReference("User Accounts")
                .child(EMPLOYER);

        // Query the database for the employer using their email
        employerRef.orderByChild("email").equalTo(employerEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            DataSnapshot employerSnapshot = dataSnapshot.getChildren().iterator().next();
                            String employerUID = employerSnapshot.getKey();

                            DatabaseReference preferredRef = employerRef.child(employerUID).child("PreferredEmployees");
                            preferredRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    // Check if the employee is already preferred
                                    boolean isAlreadyPreferred = false;
                                    for (DataSnapshot preferredSnapshot : snapshot.getChildren()) {
                                        String existingEmail = preferredSnapshot.child("EmployeeEmail").getValue(String.class);
                                        if (preferredEmployeeEmail.equals(existingEmail)) {
                                            isAlreadyPreferred = true;
                                            break;
                                        }
                                    }

                                    if (isAlreadyPreferred) {
                                        Toast.makeText(OngoingActivity.this, "Employee is already preferred.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Employee is not preferred yet, proceed with adding
                                        DatabaseReference newPreferredRef = preferredRef.push();
                                        newPreferredRef.child("EmployeeEmail").setValue(preferredEmployeeEmail)
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(OngoingActivity.this, "Employee marked as preferred.", Toast.LENGTH_SHORT).show();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(OngoingActivity.this, "Failed to mark employee as preferred.", Toast.LENGTH_SHORT).show();
                                                });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(OngoingActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(OngoingActivity.this, "Employer not found.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(OngoingActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void transferToRating(){
        sendNotificationToEmployee("Job Completed", "The job " + currentJob.getTitle() + " has been marked as completed.", true, EMPLOYEE);
        Toast.makeText(OngoingActivity.this, "Job marked as complete.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(OngoingActivity.this, RatingActivity.class);
        intent.putExtra("userEmail", jobApplication.getApplicantEmail());
        intent.putExtra("userType", EMPLOYEE);
        intent.putExtra("ongoingJob", currentJob);
        intent.putExtra("jobApplication", jobApplication);
        startActivity(intent);
        finish();
    }

    private void markCompletePrompt(Job currentJob, JobApplication jobApp){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.MARK_JOB_COMPLETE_TITLE))
                .setMessage(getString(R.string.MARK_JOB_COMPLETE_MESSAGE))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.MARK_JOB_COMPLETE_POSITIVE), (dialog, which) -> {
                    DatabaseReference jobsRef = FirebaseDatabase.getInstance().getReference("Jobs");
                    jobsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot jobSnapshot : dataSnapshot.getChildren()) {
                                DataSnapshot applicationsSnapshot = jobSnapshot.child("Applications");
                                for (DataSnapshot applicationSnapshot : applicationsSnapshot.getChildren()) {
                                    JobApplication fetchedJobApp = applicationSnapshot.getValue(JobApplication.class);
                                    if (fetchedJobApp != null && fetchedJobApp.getApplicantEmail().equals(jobApp.getApplicantEmail()) && fetchedJobApp.getEmployerEmail().equals(jobApp.getEmployerEmail())
                                            && fetchedJobApp.getJobTitle().equals(currentJob.getTitle())) {
                                        if ("completed".equals(fetchedJobApp.getStatus())) {
                                            Toast.makeText(OngoingActivity.this, "Job is already marked as completed.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            applicationSnapshot.getRef().child("status").setValue("completed")
                                                    .addOnSuccessListener(aVoid -> {
                                                        transferToRating();
                                                    })
                                                    .addOnFailureListener(e -> Toast.makeText(OngoingActivity.this, "Failed to update application status.", Toast.LENGTH_SHORT).show());
                                        }
                                        return;
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(OngoingActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton(getString(R.string.MARK_JOB_COMPLETE_NEGATIVE), (dialog, which) -> dialog.cancel())
                .show();
    }
    private void sendNotificationToEmployee(String title, String body, boolean isClickable, String topicType) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }

        try {
            final JSONObject notificationJSONBody = new JSONObject();
            notificationJSONBody.put("title", title);
            notificationJSONBody.put("body", body);

            //the second json object - data
            final JSONObject dataJSONBody = new JSONObject();
            dataJSONBody.put("userEmail", jobApplication.getEmployerEmail());
            dataJSONBody.put("userType", EMPLOYER);
            dataJSONBody.put("notificationType", "markComplete");
            dataJSONBody.put("topicType", topicType);
            // Set isClickable based on the button clicked
            dataJSONBody.put("isClickable", Boolean.toString(isClickable));
            String topic = "employees";
            if (EMPLOYER.equals(topicType)) {
                topic = "employers";
            }
            //attaching to the main json object
            final JSONObject pushNotificationJSONBody = new JSONObject();
            pushNotificationJSONBody.put("to", "/topics/" + topic);
            pushNotificationJSONBody.put("notification", notificationJSONBody);
            pushNotificationJSONBody.put("data", dataJSONBody);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    PUSH_NOTIFICATION_ENDPOINT,
                    pushNotificationJSONBody,
                    //lamda syntax
                    response ->
                            Toast.makeText(OngoingActivity.this,
                                    "Push notification sent.",
                                    Toast.LENGTH_SHORT).show(),
                    //method reference
                    Throwable::printStackTrace) {

                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "key=" + FIREBASE_SERVER_KEY);
                    return headers;
                }
            };

            requestQueue.add(request);
        } catch (JSONException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    protected void setUpMenu() {
        RecyclerView recyclerView = findViewById(R.id.menuHolder);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MenuAdapter adapter = new MenuAdapter(getApplicationContext());
        recyclerView.setAdapter(adapter);
    }
}