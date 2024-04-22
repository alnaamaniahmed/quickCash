package com.example.quickcash.ui;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcash.R;
import com.example.quickcash.firebase.Job;
import com.example.quickcash.util.MenuAdapter;
/**
 * Activity for viewing the details of a job.
 * This activity is started when a user selects a job from the list of jobs.
 */
public class ViewJobActivity extends AppCompatActivity {

    Job job;
    TextView titleText, descriptionText, typeText, salaryText, locationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_job);
        getJob();
        setUpUI();
    }

    protected void getJob() {
        Intent intent = getIntent();
        job = (Job) intent.getSerializableExtra("job");
        if (job == null) {
            finish();  // close activity just to handle the null
        }

    }

    protected void setUpUI() {
        setUpMenu();
        setUpApplyButton();
        setUpJobDetails();

    }

    protected void setUpMenu (){
        RecyclerView recyclerView = findViewById(R.id.menuHolder);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MenuAdapter adapter = new MenuAdapter(getApplicationContext());
        recyclerView.setAdapter(adapter);
    }

    protected void setUpJobDetails() {
        titleText = findViewById(R.id.viewJobTitle);
        descriptionText = findViewById(R.id.viewDescription);
        typeText = findViewById(R.id.viewType);
        salaryText = findViewById(R.id.viewSalary);
        locationText = findViewById(R.id.locationJob);
        titleText.setText(job.getTitle());
        descriptionText.setText("Description: " + job.getDescription());
        locationText.setText("Location: " + job.getLocation());
        typeText.setText("Type: " + job.getType());
        salaryText.setText("Salary: $" + job.getSalary());
    }

    protected void setUpApplyButton() {
        Button applyButton = findViewById(R.id.applyButton);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewJobActivity.this, JobApplicationActivity.class);
                intent.putExtra("title", job.getTitle());
                intent.putExtra("description", job.getDescription());
                intent.putExtra("jobType", job.getType());
                intent.putExtra("salary", job.getSalary());
                intent.putExtra("location", job.getLocation());
                intent.putExtra("employerEmail", job.getEmployerEmail());
                intent.putExtra("questions", job.getQuestions());
                startActivity(intent);
                finish();
            }
        });
    }


}
