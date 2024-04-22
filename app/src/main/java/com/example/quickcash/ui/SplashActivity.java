package com.example.quickcash.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quickcash.R;
import com.example.quickcash.core.AppConstants;
import com.example.quickcash.firebase.FirebaseQueries;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * SplashActivity is the first activity that is launched when the app starts.
 * It checks if the user is already authenticated and redirects to the appropriate screen.
 */
public class SplashActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    String role, email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Intent intent;
        if (currentUser != null) {
            email = currentUser.getEmail();
            database = FirebaseDatabase.getInstance();
            initiateDashboardRedirect();
        }
        else {
            Log.d(getClass().getSimpleName(), "No current user; redirecting to Login");
            intent = new Intent(SplashActivity.this, logInActivity.class);
            startActivity(intent);
            finish();
        }
    }

    protected void initiateDashboardRedirect() {
        Query queryRole = database.getReference("User Accounts")
                .child(AppConstants.EMPLOYEE).orderByChild("email").equalTo(email);
        queryRole.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    role = AppConstants.EMPLOYEE;
                }
                else {
                    role = AppConstants.EMPLOYER;
                }
                getUserName();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    protected void getUserName() {
        FirebaseQueries fq = new FirebaseQueries(database);
        fq.getUserDetail(role, "email", email, new FirebaseQueries.getValueCallback() {
            @Override
            public void onValueReceived(String returnString) {
                redirectToDashboard(returnString);
            }
        });
    }

    protected void redirectToDashboard(String username) {
        Intent intent;
        Log.d(getClass().getSimpleName(), "Redirecting user " + email + " to Dashboard");
        Toast.makeText(this, "Logged in as " + username, Toast.LENGTH_SHORT).show();
        intent = new Intent(SplashActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }
}
