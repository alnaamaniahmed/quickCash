package com.example.quickcash.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcash.R;
import com.example.quickcash.firebase.Job;
import com.example.quickcash.firebase.JobApplication;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
/**
 * Activity to display the hiring details and manage applicants for an employer.
 */
public class HiringDetailsActivity extends AppCompatActivity {

    private RecyclerView applicantRecyclerView;
    private List<JobApplication> jobApplications;
    private String employerEmail;
    private FirebaseDatabase database;
    private Job currentJob;


    /**
     * Initializes the activity, sets up the RecyclerView, and populates it with application data.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied.
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hiring_details);
        // Initialize Firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        employerEmail = (currentUser != null) ? currentUser.getEmail() : null;
        currentJob = (Job) getIntent().getSerializableExtra("hiringJob");
        jobApplications = new ArrayList<>();
        if (currentJob != null && currentJob.getApplications() != null) {
            jobApplications.addAll(currentJob.getApplications().values());
        }
        // Find the RecyclerView
        applicantRecyclerView = findViewById(R.id.applicants_recycler_view);
        // Create a LayoutManager
        applicantRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        HiringApplicantAdapter adapter = new HiringApplicantAdapter(this, jobApplications, onApplicantClickListener(), employerEmail);
        applicantRecyclerView.setAdapter(adapter);
        // Ensure you are receiving a list of JobApplication objects from the intent
    }
    /**
     * Provides a click listener for each applicant in the RecyclerView to view their details.
     *
     * @return A click listener that opens {@link ApplicationDetailsActivity} for the selected application.
     */
    public HiringApplicantAdapter.OnApplicantClickListener onApplicantClickListener(){
        return jobApplication -> {
            Intent intent = new Intent(HiringDetailsActivity.this, ApplicationDetailsActivity.class);
            intent.putExtra("jobApplication", jobApplication);
            startActivity(intent);
        };
    }
}