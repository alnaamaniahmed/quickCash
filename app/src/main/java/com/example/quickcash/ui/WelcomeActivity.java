package com.example.quickcash.ui;

import static android.app.PendingIntent.getActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.quickcash.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
/**
 * Activity displayed after user signs up, prompting them to verify their email.
 */
public class WelcomeActivity extends AppCompatActivity {
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser.isEmailVerified()) {
            Intent intent = new Intent(WelcomeActivity.this, DashboardActivity.class);
            startActivity(intent);
        }
        else {
            ConstraintLayout layout = findViewById(R.id.welcomeLayout);
            layout.setVisibility(View.VISIBLE);
            setUpWelcomeMessage(currentUser);
            setUpWrongEmailLink();
            setUpResendEmailLink(currentUser);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    protected void setUpWelcomeMessage(FirebaseUser user) {
        TextView welcomeMessage = findViewById(R.id.welcomeText);

        String name = user.getEmail();
        welcomeMessage.setText("Welcome, " + name + "! Please activate the " +
                "verication email sent to your email address.");
    }

    protected void setUpWrongEmailLink() {
        TextView wrongEmailLink = findViewById(R.id.wrongEmailText);
        wrongEmailLink.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, logInActivity.class);
            FirebaseAuth.getInstance().signOut();
            startActivity(intent);
        });
    }

    protected void setUpResendEmailLink(FirebaseUser user) {
        TextView resendEmailLink = findViewById(R.id.resendEmailText);
        resendEmailLink.setOnClickListener(v -> {
            user.sendEmailVerification();
            Toast.makeText(this, "Verification email sent", Toast.LENGTH_LONG);
        });
    }
}
