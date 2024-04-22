package com.example.quickcash.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcash.R;
import com.example.quickcash.core.AppConstants;
import com.example.quickcash.firebase.Job;
import com.example.quickcash.util.JobAdapter;
import com.example.quickcash.util.MenuAdapter;
import com.example.quickcash.util.WrapLinearLayoutManager;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/**
 * This class allows a user to scroll through jobs in a recycler view
 * Jobs can be sorted by newness, title, location*, type and salary
 * //TODO Clicking a job item takes the employee to the job's page
 * //TODO Keywords??? Search bar might not be feasible
 */
public class SearchJobActivity extends AppCompatActivity {

    RecyclerView jobRecycler;
    JobAdapter adapter;
    Spinner searchSpinner;
    int filterIndex;
    DatabaseReference jobsRef;
    RecyclerView.LayoutManager inOrder, inReverse;
    Query queryCurrent, queryNewest, queryTitle,
            queryHalifax, queryDartmouth, queryBedford,
            queryPartTime, queryFullTime, queryContract, querySalary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_job);
        initializeVariables();
        setUpUI();
    }

    protected void initializeVariables() {
        getSearchExtras();
        initializeDatabaseReferences();
        initializeQueries();
    }

    protected void getSearchExtras() {
        Intent intent = getIntent();
        filterIndex = intent.getIntExtra("filterIndex", 0);
    }

    protected void initializeDatabaseReferences() {
        jobsRef = FirebaseDatabase.getInstance(AppConstants.databaseURL).getReference("Jobs");
    }

    protected void initializeQueries() {
        queryNewest = jobsRef;
        queryTitle = jobsRef.orderByChild("title");
        queryHalifax = jobsRef.orderByChild("location").equalTo(AppConstants.HALIFAX);
        queryDartmouth = jobsRef.orderByChild("location").equalTo(AppConstants.DARTMOUTH);
        queryBedford = jobsRef.orderByChild("location").equalTo(AppConstants.BEDFORD);
        queryPartTime = jobsRef.orderByChild("type").equalTo(AppConstants.PART_TIME);
        queryFullTime = jobsRef.orderByChild("type").equalTo(AppConstants.FULL_TIME);
        queryContract = jobsRef.orderByChild("type").equalTo(AppConstants.CONTRACT);
        querySalary = jobsRef.orderByChild("salary");
    }

    protected void setUpUI() {
        setUpMenu();
        setUpRefreshButton();
        setUpSearchSpinner();
        setUpRecyclerView();
        populateRecyclerView(applyCurrentFilter());
    }

    protected void setUpMenu (){
        RecyclerView recyclerView = findViewById(R.id.menuHolder);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MenuAdapter adapter = new MenuAdapter(getApplicationContext());
        recyclerView.setAdapter(adapter);
    }

    protected void setUpRefreshButton() {
        ImageButton searchButton = findViewById(R.id.refreshButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadSearchJobActivity();
            }
        });
    }

    protected void setUpSearchSpinner() {
        searchSpinner = findViewById(R.id.searchSpinner);
        searchSpinner.setSelection(filterIndex);
        searchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterIndex = searchSpinner.getSelectedItemPosition();
                Log.d("FILTER SPINNER", "Sorting by " + searchSpinner.getSelectedItem().toString().trim());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("FILTER SPINNER", "Nothing selected");
            }
        });
    }

    private void setUpRecyclerView () {
        jobRecycler = findViewById(R.id.jobRecycler);
        inOrder = new WrapLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        inReverse = new WrapLinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        jobRecycler.setLayoutManager(inOrder);
    }

    private void populateRecyclerView(Query searchQuery) {
        final FirebaseRecyclerOptions<Job> searchResults = new FirebaseRecyclerOptions.Builder<Job>()
                .setQuery(searchQuery, Job.class).build();

        adapter = new JobAdapter(searchResults);

        jobRecycler.setAdapter(adapter);
    }

    protected Query applyCurrentFilter(){
        switch (filterIndex) {
            case 1: // Newest
                jobRecycler.setLayoutManager(inReverse);
                return queryNewest;
            case 2: // Halifax
                jobRecycler.setLayoutManager(inOrder);
                return queryHalifax;
            case 3: // Dartmouth
                jobRecycler.setLayoutManager(inOrder);
                return queryDartmouth;
            case 4: // Bedford
                jobRecycler.setLayoutManager(inOrder);
                return queryBedford;
            case 5: // Part time
                jobRecycler.setLayoutManager(inOrder);
                return queryPartTime;
            case 6: // Full time
                jobRecycler.setLayoutManager(inOrder);
                return queryFullTime;
            case 7: // Contract
                jobRecycler.setLayoutManager(inOrder);
                return queryContract;
            case 8: // Salary hi-lo
                jobRecycler.setLayoutManager(inReverse);
                return querySalary;
            case 9: // Salary lo-hi
                jobRecycler.setLayoutManager(inOrder);
                return querySalary;
        }

        return queryTitle; //Default
    }

    protected void reloadSearchJobActivity(){
        int currentIndex = searchSpinner.getSelectedItemPosition();

        Intent intent = new Intent(this, SearchJobActivity.class);
        intent.putExtra("filterIndex", currentIndex);
        startActivity(intent);
    }

    // Lifecycle method called when the activity is started.
    // Start listening for changes in the data and update the UI accordingly.
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    // Lifecycle method called when the activity is stopped.
    // Stop listening for changes in the data to conserve resources.
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


}
