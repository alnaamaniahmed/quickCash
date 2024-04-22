package com.example.quickcash.ui;

import static com.example.quickcash.core.AppConstants.EMPLOYEE;
import static com.example.quickcash.core.AppConstants.EMPLOYEES_TOPIC;
import static com.example.quickcash.core.AppConstants.EMPLOYER;
import static com.example.quickcash.core.AppConstants.EMPLOYERS_TOPIC;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quickcash.R;
import com.example.quickcash.core.AppConstants;
import com.example.quickcash.firebase.FirebaseCRUD;
import com.example.quickcash.util.CredentialValidator;
import com.example.quickcash.util.KeyboardHider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;
/**
 * Activity for handling user login functionality.
 */
public class logInActivity extends AppCompatActivity implements View.OnClickListener{
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database = null;
    FirebaseCRUD crud = null;
    Boolean userEmailAndRoleMatch = false;
    String errorMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();
        this.initializeDatabaseAccess();
        setUpUI();
    }
    /**
     * Initializes database access and authentication services.
     */
    protected void initializeDatabaseAccess() {
        database = FirebaseDatabase.getInstance(AppConstants.databaseURL);
        crud = new FirebaseCRUD(database);
    }

    /**
     * Sets up the user interface, including buttons, spinners, and input fields.
     */
    protected void setUpUI(){
        this.loadRoleSpinner();
        this.setupSignInButton();
        this.setupSignUpLink();
        this.setupPasswordResetLink();
        KeyboardHider.setUp(findViewById(R.id.mainView), this);
    }
    /**
     * Populates the role spinner with user roles.
     */
    protected void loadRoleSpinner() {
        Spinner roleSpinner = findViewById(R.id.roleSpinner);
        List<String> roles = new ArrayList<>();
        roles.add("Select your role");
        roles.add("Employee");
        roles.add("Employer");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, roles);
        spinnerAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        roleSpinner.setAdapter(spinnerAdapter);
    }
    /**
     * Configures the sign-up text view to open the registration activity when clicked.
     */
    protected void setupSignUpLink() {
        TextView signUpLink = findViewById(R.id.signUp);
        signUpLink.setOnClickListener(v -> {
            Intent intent = new Intent(logInActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });

    }
    /**
     * Configures the password reset text view to open the password reset activity when clicked.
     */
    protected void setupPasswordResetLink() {
        TextView forgotPasswordLink = findViewById(R.id.forgotPassword);
        forgotPasswordLink.setOnClickListener(v -> {
            Intent intent = new Intent(logInActivity.this, PasswordResetActivity.class);
            startActivity(intent);
        });
    }

    protected void setupSignInButton() {
        Button signInButton = findViewById(R.id.signInButton);
        signInButton.setOnClickListener(this);
    }
    protected String getEmailAddress() {
        EditText emailBox = findViewById(R.id.emailInput);
        return emailBox.getText().toString().trim();
    }

    protected String getPassword() {
        EditText passwordBox = findViewById(R.id.passwordInput);
        return passwordBox.getText().toString().trim();
    }

    protected void setStatusMessage(String message) {
        TextView statusLabel = findViewById(R.id.statusLabel);
        statusLabel.setText(message.trim());
    }
    protected String getRole() {
        Spinner roleSpinner = findViewById(R.id.roleSpinner);
        return roleSpinner.getSelectedItem().toString().trim();
    }
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    /**
     * Performs the sign-in process using FirebaseAuth.
     * and automatically subscribes the employee to the "employees" topic
     * @param email    The user's email address.
     * @param password The user's password.
     */
    protected void signIn(String email, String password){
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()){
                        if(EMPLOYEE.equals(getRole())){
                            // Subscribes the user to a topic named "employees"
                            FirebaseMessaging.getInstance().subscribeToTopic(EMPLOYEES_TOPIC)
                                    .addOnCompleteListener(task1 -> {
                                        if (!task1.isSuccessful()) {
                                            Log.w("Subscription", "Subscription to topic failed");
                                        }
                                    });
                        } else if (EMPLOYER.equals(getRole())){
                            // Subscribes the user to a topic named "employers"
                            FirebaseMessaging.getInstance().subscribeToTopic(EMPLOYERS_TOPIC)
                                    .addOnCompleteListener(task1 -> {
                                        if (task.isSuccessful()) {
                                            Log.d("Subscription", "Successfully subscribed to employers topic");
                                        } else {
                                            Log.w("Subscription", "Subscription to employers topic failed", task.getException());
                                        }
                                    });
                        }

                        redirectUserToDashboard();
                    } else {
                        setStatusMessage("Authentication failed.");
                    }
                });
    }
    private void redirectUserToDashboard() {
        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(logInActivity.this, DashboardActivity.class);
        startActivity(intent);
        // Close the login activity after redirecting the user
        finish();
    }

    @Override
    public void onClick(View view) {
        String emailAddress = getEmailAddress();
        String password = getPassword();
        errorMessage = "";
        String role = getRole();
        CredentialValidator validator = new CredentialValidator();

        if(validator.isEmpty(emailAddress)){
            errorMessage = getResources().getString(R.string.EMPTY_EMAIL_ADDRESS);
        }
        else if(!validator.isValidEmailAddress(emailAddress)){
            errorMessage = getResources().getString(R.string.INVALID_EMAIL_ADDRESS);
        }
        else if(validator.isEmpty(password)){
            errorMessage = getResources().getString(R.string.EMPTY_PASSWORD);
        }
        else if(!validator.isValidPassword(password)){
            errorMessage = getResources().getString(R.string.INVALID_PASSWORD);
        }
        else if(!validator.isValidRole(role)){
            errorMessage = getResources().getString(R.string.INVALID_ROLE);
        }

        if(errorMessage.isEmpty()){
            validateEmailWithRole(emailAddress, role);
        }
        else {
            setStatusMessage(errorMessage);
        }
        hideKeyboard(view);
        setStatusMessage(errorMessage);
    }

    protected void validateEmailWithRole (String emailAddress, String role) {
        Query query = database.getReference("User Accounts").child(role)
                .orderByChild("email").equalTo(emailAddress);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userEmailAndRoleMatch = true;
                    Log.d("LOGIN","Role and email validated in RTDB; authenticating sign-in");
                    signIn(emailAddress, getPassword());
                }
                else {
                    userEmailAndRoleMatch = false;
                    //TODO Replace Toast with Snackbar
                    errorMessage = "There is no " + role + " account registered to this email";
                    setStatusMessage(errorMessage);
                    Log.e("LOGIN","There is no " + role + " account registered to this email");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("LOGIN",error.getMessage());
            }
        });
    }
}
