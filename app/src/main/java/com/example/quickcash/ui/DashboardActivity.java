package com.example.quickcash.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcash.R;
import com.example.quickcash.core.AppConstants;
import com.example.quickcash.firebase.FirebaseCRUD;
import com.example.quickcash.util.MenuAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * DashboardActivity serves as the main screen for users after they log in,
 * displaying different panels based on the user's role (employee or employer).
 */
public class DashboardActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    protected FirebaseDatabase database;
    protected FirebaseCRUD crud;
    private String userEmail;
    private String userRole;
    /**
     * Initializes the activity, sets up the database access, and determines the user role.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        firebaseAuth = FirebaseAuth.getInstance();
        initializeDatabaseAccess();
        setUpMenu();
        // Check if the user is not logged in, redirect to Login Activity
        if (firebaseAuth.getCurrentUser() != null) {
            // Get the current user's email
            userEmail = firebaseAuth.getCurrentUser().getEmail();
            // Determine if user is Employee or Employer
            determineUserRole();
        }
        else {
            startLogInActivity();
        }
    }


    /**
     * Initializes database access components.
     */
    protected void initializeDatabaseAccess() {
        database = FirebaseDatabase.getInstance(AppConstants.databaseURL);
        crud = new FirebaseCRUD(database);
    }
    /**
     * Sets up the menu for the activity.
     */
    protected void setUpMenu (){
        RecyclerView recyclerView = findViewById(R.id.menuHolder);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MenuAdapter adapter = new MenuAdapter(getApplicationContext());
        recyclerView.setAdapter(adapter);
    }

    /**
     * Starts the login activity and clears the current task stack.
     */
    private void startLogInActivity() {
        Intent intent = new Intent(this, logInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Determines the role of the user (employee or employer) based on their email.
     */
    protected void determineUserRole() {
        Query roleQuery = queryEmailByRole(AppConstants.EMPLOYEE);

        roleQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // User is an employee
                if (snapshot.exists()) {
                    userRole = AppConstants.EMPLOYEE;
                }
                // User is an employer by process of elimination
                else {
                    userRole = AppConstants.EMPLOYER;
                }
                Log.d("DashboardActivity","Query successful: user role = " + userRole);
                // Launch fragment respective to user role
                manageUserPanel();
                fetchUserDetails();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DashboardActivity","Query failed");
            }
        });
    }
    /**
     * Fetches the user details from the database.
     */
    private void fetchUserDetails() {
        DatabaseReference userRef = database.getReference("User Accounts").child(userRole);
        userRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String name = userSnapshot.child("name").getValue(String.class);
                        TextView welcomeTextView = findViewById(R.id.welcomeTextView);
                        String displayName = name;
                        welcomeTextView.setText("Welcome, " + displayName);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DashboardActivity", "Data fetch cancelled or failed: " + databaseError.getMessage());
            }
        });
    }
    /**
     * Queries the database for a user's email within a specified role.
     *
     * @param  role to query for (e.g., "Employee" or "Employer").
     * @return A Firebase query object for the specified role and email.
     */
    protected Query queryEmailByRole (String role) {
        return database.getReference("User Accounts")
                .child(role).orderByChild("email")
                .equalTo(userEmail);
    }
    /**
     * Manages the display of the user panel based on the user's role.
     */
    protected void manageUserPanel() {
        clearOldPanels();

        if (userRole.equals(AppConstants.EMPLOYEE)) {
            manageEmployeePanelDisplay(true);
            Log.d(getClass().getSimpleName(),"Loading employee fragment");
        }
        else if (userRole.equals(AppConstants.EMPLOYER)) {
            manageEmployerPanelDisplay(true);
            Log.d(getClass().getSimpleName(),"Loading employer fragment");
        }
    }

    protected void clearOldPanels() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        for (Fragment fragment : manager.getFragments()) {
            transaction.remove(fragment);
        }
        transaction.commit();
    }

    protected FragmentTransaction getFragmentTransaction() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        return transaction;
    }

    protected void manageEmployeePanelDisplay(boolean isEmployee) {
        FragmentTransaction transaction = getFragmentTransaction();
        EmployeeFragment employeeFragment = new EmployeeFragment();
        if (isEmployee) {
            showPanel(transaction, R.id.employeeContainer, employeeFragment);
        }
        else {
            removePanel(employeeFragment, transaction);
        }
        transaction.commit();
    }

    protected void manageEmployerPanelDisplay(boolean isEmployer) {
        FragmentTransaction transaction = getFragmentTransaction();
        EmployerFragment employerFragment = new EmployerFragment();
        if (isEmployer) {
            showPanel(transaction, R.id.employerContainer, employerFragment);
        }
        else {
            removePanel(employerFragment, transaction);
        }
        transaction.commit();
    }

    protected void showPanel(FragmentTransaction transaction, int containerID, Fragment fragment) {
        transaction.replace(containerID, fragment);
    }

    protected void removePanel(Fragment fragment, FragmentTransaction transaction) {
        transaction.remove(fragment);
    }

}