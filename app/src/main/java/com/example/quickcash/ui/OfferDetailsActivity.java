package com.example.quickcash.ui;

import static com.example.quickcash.core.AppConstants.EMPLOYER;
import static com.example.quickcash.core.AppConstants.FIREBASE_SERVER_KEY;
import static com.example.quickcash.core.AppConstants.PUSH_NOTIFICATION_ENDPOINT;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.quickcash.R;

import com.example.quickcash.firebase.Job;
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
 * Activity for displaying job offer details to the user, allowing them to accept or decline the offer.
 */
public class OfferDetailsActivity extends AppCompatActivity {
    private RequestQueue requestQueue;
    private String jobTitle;
    private String employerEmail;
    private String applicantEmail;
    private double salary;
    private String jobLocation;
    private String jobDescription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_details);
        setUpMenu();

        // Initialize your views here
        TextView OfferName = findViewById(R.id.OfferName);
        TextView jobOfferMessageTextView = findViewById(R.id.jobOfferMessage);
        TextView jobOfferEmployerTextView = findViewById(R.id.jobOfferEmployer);
        TextView jobOfferSalaryTextView = findViewById(R.id.jobOfferSalary);
        TextView jobOfferLocationTextView = findViewById(R.id.jobOfferLocation);
        Button acceptButton = findViewById(R.id.acceptOfferButton);
        Button declineButton = findViewById(R.id.declineOfferButton);

        Intent intent = getIntent();
        if (intent != null) {
            jobTitle = intent.getStringExtra("jobTitle");
            salary = intent.getDoubleExtra("salary", 0.0);
            jobLocation = intent.getStringExtra("jobLocation");
            jobDescription = intent.getStringExtra("jobDescription");
            employerEmail = intent.getStringExtra("employer");
            applicantEmail = intent.getStringExtra("applicantEmail");

            // set the text views with the data
            OfferName.setText(jobTitle);
            jobOfferMessageTextView.setText("Description: " + jobDescription);
            jobOfferEmployerTextView.setText("From: " + employerEmail);
            jobOfferSalaryTextView.setText("Salary: " + salary);
            jobOfferLocationTextView.setText("Location: " + jobLocation);
        }

        declineButton.setOnClickListener(view -> {
            DatabaseReference jobsRef = FirebaseDatabase.getInstance().getReference("Jobs");
            jobsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot jobSnapshot : dataSnapshot.getChildren()) {
                        Job job = jobSnapshot.getValue(Job.class);
                        if (job != null && job.getEmployerEmail().equals(employerEmail)) {
                            DataSnapshot applicationsSnapshot = jobSnapshot.child("Applications");
                            for (DataSnapshot applicationSnapshot : applicationsSnapshot.getChildren()) {
                                JobApplication jobApp = applicationSnapshot.getValue(JobApplication.class);
                                if (jobApp != null && jobApp.getApplicantEmail().equals(applicantEmail) && jobApp.getJobTitle().equals(jobTitle)) {
                                    applicationSnapshot.getRef().child("status").setValue("declined")
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(OfferDetailsActivity.this, "Offer declined.", Toast.LENGTH_SHORT).show();
                                                sendNotification("Job Offer Declined", "The job offer for '" + jobTitle + "' has been declined.", false, EMPLOYER, jobLocation, "offerDeclined");
                                                finish();
                                            })
                                            .addOnFailureListener(e -> Toast.makeText(OfferDetailsActivity.this, "Failed to update job application status.", Toast.LENGTH_SHORT).show());
                                    break;
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(OfferDetailsActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        acceptButton.setOnClickListener(view -> {
            DatabaseReference jobsRef = FirebaseDatabase.getInstance().getReference("Jobs");
            jobsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot jobSnapshot : dataSnapshot.getChildren()) {
                        Job job = jobSnapshot.getValue(Job.class);
                        if (job != null && job.getEmployerEmail().equals(employerEmail)) {
                            DataSnapshot applicationsSnapshot = jobSnapshot.child("Applications");
                            for (DataSnapshot applicationSnapshot : applicationsSnapshot.getChildren()) {
                                JobApplication jobApp = applicationSnapshot.getValue(JobApplication.class);
                                if (jobApp != null && jobApp.getApplicantEmail().equals(applicantEmail) && jobApp.getJobTitle().equals(jobTitle)) {
                                    if ("accepted".equals(jobApp.getStatus())) {
                                        Toast.makeText(OfferDetailsActivity.this, "Application has already been accepted.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        applicationSnapshot.getRef().child("status").setValue("accepted")
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(OfferDetailsActivity.this, "Offer accepted.", Toast.LENGTH_SHORT).show();
                                                    sendNotification("Job Offer Accepted", "The job offer for '" + jobTitle + "' has been accepted by " + jobApp.getApplicantEmail(), false, EMPLOYER, jobLocation, "offerAccepted");
                                                    finish();
                                                })
                                                .addOnFailureListener(e -> Toast.makeText(OfferDetailsActivity.this, "Failed to update job application status.", Toast.LENGTH_SHORT).show());
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(OfferDetailsActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void sendNotification(String title, String body, boolean isClickable, String topicType, String jobLocation, String notificationType) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }

        try {
            final JSONObject notificationJSONBody = new JSONObject();
            notificationJSONBody.put("title", title);
            notificationJSONBody.put("body", body);

            final JSONObject dataJSONBody = new JSONObject();
            dataJSONBody.put("jobLocation", jobLocation);
            dataJSONBody.put("notificationType", notificationType);
            dataJSONBody.put("isClickable", Boolean.toString(isClickable));
            dataJSONBody.put("topicType", topicType);

            String topic = "employees";
            if (EMPLOYER.equals(topicType)) {
                topic = "employers";
            }

            final JSONObject pushNotificationJSONBody = new JSONObject();
            pushNotificationJSONBody.put("to", "/topics/" + topic);
            pushNotificationJSONBody.put("notification", notificationJSONBody);
            pushNotificationJSONBody.put("data", dataJSONBody);
            Log.d("sendNotification", "Sending notification to topic: " + topic);
            Log.d("sendNotification", "Notification payload: " + pushNotificationJSONBody.toString());
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    PUSH_NOTIFICATION_ENDPOINT,
                    pushNotificationJSONBody,
                    response -> Toast.makeText(OfferDetailsActivity.this, "Push notification sent.", Toast.LENGTH_SHORT).show(),
                    error -> Log.e("OfferDetailsActivity", "Error sending notification", error)) {

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