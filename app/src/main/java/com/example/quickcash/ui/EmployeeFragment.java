package com.example.quickcash.ui;

import android.content.Context;
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

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.quickcash.R;
import com.example.quickcash.firebase.JobApplication;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * ALL FRAGMENT CODE COPIED FROM ASSIGNMENT 3; AUTHORED BY MASUD RAHMAN
 * A simple {@link Fragment} subclass.
 * Use the {@link EmployeeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmployeeFragment extends Fragment {

    private LinearLayout appliedList;
    private FirebaseAuth auth;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int MARGIN_CONSTANT = 10;
    private static final int PADDING_CONSTANT = 8;

    private String mParam1;
    private String mParam2;

    public EmployeeFragment() {
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
    public static EmployeeFragment newInstance(String param1, String param2) {
        EmployeeFragment fragment = new EmployeeFragment();
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
        View employeeView = inflater.inflate(R.layout.fragment_employee, container, false);
        setUpUI(employeeView);
        appliedList = employeeView.findViewById(R.id.appliedList);
        fetchApplications();
        // Inflate the layout for this fragment
        return employeeView;
    }
    /**
     * Sets up the user interface for the employee fragment.
     *
     * @param view The root view of the fragment.
     */
    protected void setUpUI (View view) {
        setUpFindJobButton(view);
    }
    /**
     * Sets up the button that allows employees to search for jobs.
     *
     * @param view The root view of the fragment.
     */
    protected void setUpFindJobButton(View view) {
        Button findJobButton = view.findViewById(R.id.findJobButton);
        findJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSearchJobActivity();
            }
        });
    }
    /**
     * Starts the activity where employees can search for jobs.
     */
    protected void loadSearchJobActivity(){
        Intent intent = new Intent(getActivity(), SearchJobActivity.class);
        startActivity(intent);
    }

    private void fetchApplications() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String currentEmployeeEmail = user.getEmail();
            DatabaseReference jobsRef = FirebaseDatabase.getInstance().getReference("Jobs");
            jobsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    appliedList.removeAllViews(); // Clear the list to prevent duplication
                    for (DataSnapshot jobSnapshot : dataSnapshot.getChildren()) {
                        DataSnapshot applicationsSnapshot = jobSnapshot.child("Applications");
                        // Now we're in the Applications node of that job
                        for (DataSnapshot appliedSnapshot : applicationsSnapshot.getChildren()) {
                            JobApplication application = appliedSnapshot.getValue(JobApplication.class);
                            if (application != null && application.getEmployerEmail() != null &&
                                    application.getApplicantEmail().equals(currentEmployeeEmail)) {
                                addApplicationView(application);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("EmployeeFragment", "loadJobs:onCancelled", databaseError.toException());
                }
            });
        } else {
            //user is not logged in
            Log.w("EmployeeFragment", "User is not logged in");
        }
    }

    private void addApplicationView(JobApplication application) {
        Context context = getContext();
        if (context != null) {
            TextView textView = new TextView(context);
            String displayText = application.getJobTitle() + " status: " + application.getStatus();
            textView.setText(displayText);
            textView.setBackgroundResource(R.drawable.rounded_corners_light_gray);
            textView.setPadding(PADDING_CONSTANT, PADDING_CONSTANT, PADDING_CONSTANT, PADDING_CONSTANT);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setTextColor(ContextCompat.getColor(context, R.color.dark_grey));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(layoutParams);
            layoutParams.setMargins(MARGIN_CONSTANT, MARGIN_CONSTANT, MARGIN_CONSTANT, MARGIN_CONSTANT);
            appliedList.addView(textView);
        } else {
            Log.e("EmployeeFragment", "Context is null in addApplicationView");
        }
    }

    private void navigateToApplicationDetailsActivity(JobApplication application) {
        Intent intent = new Intent(getActivity(), ApplicationDetailsActivity.class);
        intent.putExtra("application", (Parcelable) application);//
        startActivity(intent);
    }

}
