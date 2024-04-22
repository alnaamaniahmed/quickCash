package com.example.quickcash.ui;

import static com.example.quickcash.core.AppConstants.*;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quickcash.firebase.Job;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.quickcash.R;
import com.example.quickcash.firebase.FirebaseCRUD;
import com.example.quickcash.firebase.JobApplication;
import com.example.quickcash.util.MenuAdapter;
import com.example.quickcash.util.MenuManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Activity to view and manage the details of a specific job application.
 * Allows the employer to view the applicant's details, resume, cover letter, and additional files.
 * Also enables the employer to shortlist, deny, or accept an application.
 */
public class ApplicationDetailsActivity extends AppCompatActivity {

    //provided by volley library to make a network request
    private RequestQueue requestQueue;
    private JobApplication jobApplication;
    private String jobTitle;
    private String employerEmail;
    private String applicantEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_details);
        setUpMenu();
        // Initialize views
        TextView applicantNameTextView = findViewById(R.id.ApplicantName);
        TextView labelApplyingFor = findViewById(R.id.labelApplyingFor);
        TextView coverLetterTextView = findViewById(R.id.coverLetterTextView);
        TextView resumeTextView = findViewById(R.id.resumeTextView);
        TextView additionalFilesTextView = findViewById(R.id.additionalFilesTextView);
        Button shortlistButton = findViewById(R.id.shortlistButton);
        Button denyButton = findViewById(R.id.denyButton);
        Button backToDashboardButton = findViewById(R.id.backToDashboardButton);

        // Retrieve job title, applicant Email, applicantEmail from intent
        jobApplication = (JobApplication) getIntent().getSerializableExtra("jobApplication");
        if (jobApplication != null) {
            jobTitle = jobApplication.getJobTitle();
            employerEmail = jobApplication.getEmployerEmail();
            applicantEmail = jobApplication.getApplicantEmail();
        }
        // Set job title, applicant Email to TextViews
        applicantNameTextView.setText("Applicant: " + applicantEmail);
        labelApplyingFor.setText("Applying for: " + jobTitle);

        // Example onClickListeners for "Click here to view" TextViews
        coverLetterTextView.setOnClickListener(v -> {
            // Handle CoverLetters viewing
            String storagePath = "CoverLetters/" + employerEmail + "/" + jobTitle + "/" + applicantEmail + ".pdf";
            FirebaseCRUD firebaseCRUD = new FirebaseCRUD(FirebaseDatabase.getInstance());
            firebaseCRUD.fetchFileFromStorage(storagePath,
                    uri -> {
                        // Intent to view the resume PDF
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri, "application/pdf");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);
                    },
                    exception -> {
                        // Handle any errors here
                        Log.e("CoverLettersLoadError", "No CoverLetters Attached", exception);
                        Toast.makeText(ApplicationDetailsActivity.this, "No CoverLetters Attached", Toast.LENGTH_LONG).show();
                    });
        });

        resumeTextView.setOnClickListener(v -> {
            // Handle resume viewing
            String storagePath = "Resumes/" + employerEmail + "/" + jobTitle + "/" + applicantEmail + ".pdf";
            FirebaseCRUD firebaseCRUD = new FirebaseCRUD(FirebaseDatabase.getInstance());
            firebaseCRUD.fetchFileFromStorage(storagePath,
                    uri -> {
                        // Intent to view the resume PDF
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri, "application/pdf");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);
                    },
                    exception -> {
                        // Handle any errors here
                        Log.e("ResumeLoadError", "No Resume Attached", exception);
                        Toast.makeText(ApplicationDetailsActivity.this, "No Resume Attached ", Toast.LENGTH_LONG).show();
                    });
        });

        additionalFilesTextView.setOnClickListener(v -> {
            // Handle additional files viewing
            String storagePath = "additionalFiles/" + employerEmail + "/" + jobTitle + "/" + applicantEmail + ".pdf";
            FirebaseCRUD firebaseCRUD = new FirebaseCRUD(FirebaseDatabase.getInstance());
            firebaseCRUD.fetchFileFromStorage(storagePath,
                    uri -> {
                        // Intent to view the resume PDF
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri, "application/pdf");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);
                    },
                    exception -> {
                        // Handle any errors here
                        Log.e("additionalFilesError", "No additionalFiles Attached", exception);
                        Toast.makeText(ApplicationDetailsActivity.this, "No additionalFiles Attached", Toast.LENGTH_LONG).show();
                    });
        });

        // Handling button clicks
        backToDashboardButton.setOnClickListener(view -> {
            // Navigate back to the Dashboard
            finish();
        });

        denyButton.setOnClickListener(view -> {
            // Handle application denial
            DatabaseReference jobsRef = FirebaseDatabase.getInstance().getReference("Jobs");
            jobsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot jobSnapshot : dataSnapshot.getChildren()) {
                        Job job = jobSnapshot.getValue(Job.class);
                        if (job != null && job.getApplications() != null && job.getTitle().equals(jobApplication.getJobTitle())) {
                            for (Map.Entry<String, JobApplication> entry : job.getApplications().entrySet()) {
                                String key = entry.getKey();
                                JobApplication jobApp = entry.getValue();
                                if (jobApp != null && jobApp.getApplicantEmail().equals(jobApplication.getApplicantEmail()) && jobApp.getJobTitle().equals(job.getTitle()) &&
                                        jobApp.getEmployerEmail().equals(jobApplication.getEmployerEmail())) {
                                    // Update the status to "denied"
                                    DatabaseReference appRef = jobSnapshot.child("Applications").child(key).getRef();
                                    appRef.child("status").setValue("denied")
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(ApplicationDetailsActivity.this, "Application denied.", Toast.LENGTH_SHORT).show();
                                                // Assuming sendNotification method is implemented correctly
                                                sendNotification("Denied", "Your application for the job: " + jobTitle + " has been denied.", false, EMPLOYEE);
                                                finish(); // Finish the activity to go back
                                            })
                                            .addOnFailureListener(e -> Toast.makeText(ApplicationDetailsActivity.this, "Failed to update application status.", Toast.LENGTH_SHORT).show());
                                    break; // Exit the loop once the correct application is found and processed
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(ApplicationDetailsActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        shortlistButton.setOnClickListener(view -> {
            DatabaseReference jobsRef = FirebaseDatabase.getInstance().getReference("Jobs");
            jobsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot jobSnapshot : dataSnapshot.getChildren()) {
                        Job job = jobSnapshot.getValue(Job.class);
                        if (job != null && job.getApplications() != null && job.getTitle().equals(jobApplication.getJobTitle())) {
                            for (Map.Entry<String, JobApplication> entry : job.getApplications().entrySet()) {
                                String key = entry.getKey();
                                JobApplication jobApp = entry.getValue();
                                if (jobApp != null && jobApp.getApplicantEmail().equals(jobApplication.getApplicantEmail()) && jobApp.getJobTitle().equals(job.getTitle())
                                        && jobApp.getEmployerEmail().equals(jobApplication.getEmployerEmail())) {
                                    // Check if the application is already shortlisted
                                    if ("shortlisted".equals(jobApp.getStatus())) {
                                        Toast.makeText(ApplicationDetailsActivity.this, "Application has already been shortlisted.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Update the status to "shortlisted"
                                        DatabaseReference appRef = jobSnapshot.child("Applications").child(key).getRef();
                                        appRef.child("status").setValue("shortlisted")
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(ApplicationDetailsActivity.this, "Application shortlisted.", Toast.LENGTH_SHORT).show();
                                                    sendNotification("Shortlisted", "You have been shortlisted for the job: " + jobTitle, false, EMPLOYEE);
                                                })
                                                .addOnFailureListener(e -> Toast.makeText(ApplicationDetailsActivity.this, "Failed to update application status.", Toast.LENGTH_SHORT).show());
                                    }
                                    // Exit the loop once the correct application is found and processed
                                    return;
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(ApplicationDetailsActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void sendNotification(String title, String body, boolean isClickable, String topicType) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }
        //try catch block for JSON exception
        try {
            //the first json object - to
            final JSONObject notificationJSONBody = new JSONObject();
            notificationJSONBody.put("title", title);
            notificationJSONBody.put("body", body);

            //the second json object - data
            final JSONObject dataJSONBody = new JSONObject();
            dataJSONBody.put("jobId", "HF-128369");
            dataJSONBody.put("jobLocation", "Halifax");
            dataJSONBody.put("notificationType", "shortlistedOrDenied");
            // Set isClickable based on the button clicked
            dataJSONBody.put("isClickable", Boolean.toString(isClickable));
            dataJSONBody.put("topicType", topicType);
            String topic = "employees";
            if (EMPLOYER.equals(topicType)) {
                topic = "employers";
            }
            //attaching to the main json object
            final JSONObject pushNotificationJSONBody = new JSONObject();
            pushNotificationJSONBody.put("to", "/topics/" + topic);
            pushNotificationJSONBody.put("notification", notificationJSONBody);
            pushNotificationJSONBody.put("data", dataJSONBody);

            //parameters sent in the request:
            //type of request - post- sending data to firebase
            //url - push notification endpoint
            //data - body of the notification
            //toast message
            //error listener
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    PUSH_NOTIFICATION_ENDPOINT,
                    pushNotificationJSONBody,
                    //lamda syntax
                    response ->
                            Toast.makeText(ApplicationDetailsActivity.this,
                                    "Push notification sent.",
                                    Toast.LENGTH_SHORT).show(),
                    //method reference
                    Throwable::printStackTrace) {

                @Override
                public Map<String, String> getHeaders() {
                    final Map<String, String> headers = new HashMap<>();
                    headers.put("content-type", "application/json");
                    headers.put("authorization", "key=" + FIREBASE_SERVER_KEY);
                    return headers;
                }
            };
            //add the request to the queue
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