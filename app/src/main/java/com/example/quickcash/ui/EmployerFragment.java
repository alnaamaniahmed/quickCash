package com.example.quickcash.ui;

import android.app.AlertDialog;
import java.io.Serializable;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * ALL FRAGMENT CODE COPIED FROM ASSIGNMENT 3; AUTHORED BY MASUD RAHMAN
 * A simple {@link Fragment} subclass.
 * Use the {@link EmployerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmployerFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int MARGIN_CONSTANT = 10;
    private static final int PADDING_CONSTANT = 8;
    private LinearLayout applicationsList;

    private LinearLayout hiringJobsList;

    private LinearLayout ongoingJobList;
    private FirebaseAuth auth;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EmployerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ManagerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EmployerFragment newInstance(String param1, String param2) {
        EmployerFragment fragment = new EmployerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View employerView = inflater.inflate(R.layout.fragment_employer, container, false);
        // Inflate the layout for this fragment
        Button postJobButton = (Button) employerView.findViewById(R.id.postJobButton);
        postJobButton.setOnClickListener(this);
        Button preferredEmployeesButton = (Button) employerView.findViewById(R.id.preferredEmployeesButton);
        preferredEmployeesButton.setOnClickListener(this);
        //fetch and display applications
        applicationsList = employerView.findViewById(R.id.applicationsList);
        fetchApplications();
        hiringJobsList = employerView.findViewById(R.id.hiringList);
        fetchHiringJobs();
        ongoingJobList = employerView.findViewById(R.id.ongoingList);
        fetchOngoingJobs();
        return employerView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.postJobButton) {
            loadPostJobActivity();
        } else if (id == R.id.preferredEmployeesButton) {
            navigateToPreferredEmployeesActivity();
        } else {
            Log.w("EmployerFragment", "Unhandled onClick event for view id: " + id);
        }
    }

    /**
     * Loads the activity for posting a job.
     */
    protected void loadPostJobActivity(){
        Intent intent = new Intent(getActivity(), PostJobActivity.class);
        startActivity(intent);
    }
    private void navigateToPreferredEmployeesActivity() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            startActivity(new Intent(getActivity(), PreferredEmployeesActivity.class)
                    .putExtra("employerEmail", user.getEmail()));
        } else {
            Log.w("EmployerFragment", "User must be logged in to view preferred employees.");
        }
    }

    /**
     * Fetches applications related to the current employer from the database and populates the applications list.
     */
    private void fetchApplications() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String currentEmployerEmail = user.getEmail();
            DatabaseReference jobsRef = FirebaseDatabase.getInstance().getReference("Jobs");
            jobsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    applicationsList.removeAllViews(); // Clear the list to prevent duplication
                    for (DataSnapshot jobSnapshot : dataSnapshot.getChildren()) {
                        Job job = jobSnapshot.getValue(Job.class);
                        // Now we're in the Applications node of that job
                        if (job == null || job.getApplications() == null) {
                            continue;
                        }
                        Map<String, JobApplication> applications = job.getApplications();
                        for (Map.Entry<String, JobApplication> entry : applications.entrySet()) {
                            JobApplication jobApplication = entry.getValue();
                            if (jobApplication != null && jobApplication.getEmployerEmail().equals(currentEmployerEmail) &&
                                    !jobApplication.isDeclined() && !jobApplication.isDenied() && !jobApplication.isShortListed() &&
                                    !jobApplication.isPending() && !jobApplication.isHired() && !jobApplication.isCompleted()) {
                                // Only add to the list if the application is not declined, denied, shortlisted, pending, hired, or completed
                                addApplicationView(jobApplication);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("EmployerFragment", "loadJobs:onCancelled", databaseError.toException());
                }
            });
        } else {
            //user is not logged in
            Log.w("EmployerFragment", "User is not logged in");
        }
    }

    private void fetchHiringJobs(){
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String currentEmployerEmail = user.getEmail();
            DatabaseReference jobsRef = FirebaseDatabase.getInstance().getReference("Jobs");
            jobsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Job job;
                    JobApplication jobApplication;
                    // Clear the list to prevent duplication
                    hiringJobsList.removeAllViews();
                    for (DataSnapshot jobSnapshot : dataSnapshot.getChildren()) {
                        job = jobSnapshot.getValue(Job.class);
                        if(job == null){
                            continue;
                        }
                        boolean hasRelevantApplications = false;
                        Map<String, JobApplication> filteredApplications = new HashMap<>();
                        DataSnapshot applicationsSnapshot = jobSnapshot.child("Applications");
                        // Now we're in the Applications node of that job
                        for (DataSnapshot applicationSnapshot : applicationsSnapshot.getChildren()) {
                            jobApplication = applicationSnapshot.getValue(JobApplication.class);
                            if (jobApplication != null && jobApplication.getEmployerEmail().equals(currentEmployerEmail) &&
                                    (jobApplication.isShortListed() || jobApplication.isPending())) {
                                hasRelevantApplications = true;
                                filteredApplications.put(applicationSnapshot.getKey(), jobApplication);
                            }
                        }
                        if(hasRelevantApplications){
                            Job filteredJob = new Job();
                            filteredJob.setTitle(job.getTitle());
                            filteredJob.setDescription(job.getDescription());
                            filteredJob.setType(job.getType());
                            filteredJob.setSalary(job.getSalary());
                            filteredJob.setLocation(job.getLocation());
                            filteredJob.setEmployerEmail(job.getEmployerEmail());
                            filteredJob.setApplications(filteredApplications);

                            addJobView(filteredJob);
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("EmployerFragment", "loadJobs:onCancelled", databaseError.toException());
                }
            });
        } else {
            //user is not logged in
            Log.w("EmployerFragment", "User is not logged in");
        }
    }

    private void fetchOngoingJobs(){
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String currentEmployerEmail = user.getEmail();
            DatabaseReference jobsRef = FirebaseDatabase.getInstance().getReference("Jobs");
            jobsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Clear the list to prevent duplication
                    ongoingJobList.removeAllViews();
                    for (DataSnapshot jobSnapshot : dataSnapshot.getChildren()) {
                        Job job = jobSnapshot.getValue(Job.class);
                        if(job == null || job.getApplications() == null){
                            continue;
                        }

                        // Now we're in the Applications node of that job
                        for (Map.Entry<String, JobApplication> entry : job.getApplications().entrySet()) {
                            JobApplication jobApplication = entry.getValue();
                            if (jobApplication != null && jobApplication.getEmployerEmail().equals(currentEmployerEmail) && jobApplication.isHired()) {
                                addOngoingJobView(job, jobApplication);
                            }
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("EmployerFragment", "loadJobs:onCancelled", databaseError.toException());
                }
            });
        } else {
            //user is not logged in
            Log.w("EmployerFragment", "User is not logged in");
        }
    }

    private void addApplicationView(JobApplication jobApplication) {
        TextView textView = new TextView(getContext());
        String displayText = jobApplication.getJobTitle() + " by " + jobApplication.getApplicantEmail();
        textView.setText(displayText);
        textView.setBackgroundResource(R.drawable.rounded_corners_light_gray);
        textView.setPadding(PADDING_CONSTANT,PADDING_CONSTANT,PADDING_CONSTANT,PADDING_CONSTANT);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_grey));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(layoutParams);
        layoutParams.setMargins(MARGIN_CONSTANT,MARGIN_CONSTANT,MARGIN_CONSTANT,MARGIN_CONSTANT);
        textView.setOnClickListener(v -> navigateToApplicationDetailsActivity(jobApplication));
        applicationsList.addView(textView);
    }

    private void addJobView(Job job) {
        Context context = getContext();
        if (context == null) {
            // Context is not available
            Log.e("EmployerFragment", "Context is not available in addJobView");
            // Optionally, you could inform the user or take other actions
            return;
        }
        TextView textView = new TextView(context);
        String displayText = job.getTitle();
        textView.setText(displayText);
        textView.setBackgroundResource(R.drawable.rounded_corners_light_gray);
        textView.setPadding(PADDING_CONSTANT,PADDING_CONSTANT,PADDING_CONSTANT,PADDING_CONSTANT);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_grey));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(layoutParams);
        layoutParams.setMargins(MARGIN_CONSTANT,MARGIN_CONSTANT,MARGIN_CONSTANT,MARGIN_CONSTANT);
        textView.setOnClickListener(v -> navigateToHiringDetailsActivity(job));
        hiringJobsList.addView(textView);
    }
    private void addOngoingJobView(Job job, JobApplication jobApplication) {
        TextView textView = new TextView(getContext());
        String displayText = job.getTitle();
        textView.setText(displayText);
        textView.setBackgroundResource(R.drawable.rounded_corners_light_gray);
        textView.setPadding(PADDING_CONSTANT,PADDING_CONSTANT,PADDING_CONSTANT,PADDING_CONSTANT);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_grey));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(layoutParams);
        layoutParams.setMargins(MARGIN_CONSTANT,MARGIN_CONSTANT,MARGIN_CONSTANT,MARGIN_CONSTANT);
        textView.setOnClickListener(v -> navigateToOngoingActivity(job, jobApplication));
        ongoingJobList.addView(textView);
    }
    private void navigateToOngoingActivity(Job job, JobApplication jobApplication) {
        Intent intent = new Intent(getActivity(), OngoingActivity.class);
        intent.putExtra("ongoingJob", (Serializable) job); // Pass the Job object
        intent.putExtra("jobApplication", (Serializable) jobApplication); // Pass the single Application object
        startActivity(intent);
    }
    private void navigateToApplicationDetailsActivity(JobApplication jobApplication) {
        Intent intent = new Intent(getActivity(), ApplicationDetailsActivity.class);
        intent.putExtra("jobApplication",  (Serializable) jobApplication);
        startActivity(intent);
    }

    private void navigateToHiringDetailsActivity(Job job){
        Intent intent = new Intent(getActivity(), HiringDetailsActivity.class);
        intent.putExtra("hiringJob", (Serializable) job);
        startActivity(intent);
    }
}