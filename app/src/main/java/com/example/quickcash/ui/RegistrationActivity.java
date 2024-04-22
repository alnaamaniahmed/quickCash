package com.example.quickcash.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quickcash.R;
import com.example.quickcash.core.AppConstants;
import com.example.quickcash.firebase.Employee;
import com.example.quickcash.firebase.Employer;
import com.example.quickcash.firebase.FirebaseCRUD;
import com.example.quickcash.firebase.UserAccount;
import com.example.quickcash.util.CredentialValidator;
import com.example.quickcash.util.KeyboardHider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Registration activity allows new user to enter credentials and create an account granted that:
 * (1) A user is not already logged in
 * (2) All entered credentials are valid
 * (3) email is not already in use on the database
 */
public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseDatabase database = null;
    FirebaseCRUD crud = null;
    FirebaseAuth mAuth;
    static final String TAG = "ACTIVITY_REG";
    static String errorMessage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        initializeDatabaseAccess();
        setUpUI();
    }

    protected void initializeDatabaseAccess() {
        database = FirebaseDatabase.getInstance(AppConstants.databaseURL);
        crud = new FirebaseCRUD(database);
    }

    protected void setUpUI() {
        setUpRegisterButton();
        setUpLogInLink();
        KeyboardHider.setUp(findViewById(R.id.mainView), this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    protected void setUpRegisterButton() {
        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);
    }

    protected void setUpLogInLink(){
        TextView loginLink = findViewById(R.id.loginLink);
        loginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrationActivity.this, logInActivity.class);
            startActivity(intent);
        });

    }

    protected String getEmail() {
        EditText emailField = findViewById(R.id.emailField);
        return emailField.getText().toString().trim();
    }

    protected String getPassword() {
        EditText passwordField = findViewById(R.id.passwordField);
        return passwordField.getText().toString().trim();
    }

    protected String getName() {
        EditText nameField = findViewById(R.id.nameField);
        return nameField.getText().toString().trim();
    }

    protected String getPhone() {
        EditText phoneField = findViewById(R.id.phoneField);
        return phoneField.getText().toString().trim();
    }

    protected String getRole() {
        Spinner roleSpinner = findViewById(R.id.roleSpinner);
        return roleSpinner.getSelectedItem().toString().trim();
    }

    protected void setErrorMessage(){
        TextView errorText = findViewById(R.id.errorText);
        errorText.setText(errorMessage);
    }

    @Override
    public void onClick(View v) {
        String email = getEmail();
        String password = getPassword();
        String name = getName();
        String phoneNumber = getPhone();
        String role = getRole();
        UserAccount newAccount;
        boolean allCredentialsAreValid;
        CredentialValidator validator = new CredentialValidator();

        allCredentialsAreValid = validator.isValidEmailAddress(email)
                && validator.isValidPassword(password)
                && validator.isValidPhoneNumber(phoneNumber)
                && validator.isValidRole(role);

        if (allCredentialsAreValid) {
            if (role.equalsIgnoreCase("Employee")) {
                newAccount = new Employee(name, email, phoneNumber);
            }
            else {
                newAccount = new Employer(name, email, phoneNumber);
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //Email not in use - create new account on database
                            if (task.isSuccessful()){
                                Log.d(TAG, "New user created");
                                FirebaseUser user = mAuth.getCurrentUser();
                                sendEmailVerification(user);
                                crud.createUser(newAccount);
                                Intent intent = new Intent(RegistrationActivity.this, WelcomeActivity.class);
                                startActivity(intent);
                            }
                            //Email already exists in database
                            else {
                                errorMessage = getResources().getString(R.string.EMAIL_ALREADY_IN_USE);
                                setErrorMessage();
                                Log.d(TAG, "Registration failed: " + errorMessage);
                                //TODO display errorMessage on snack bar instead / switch to login?
                            }
                        }
                    });
        }
        else {
            if(validator.isEmpty(email)){
                errorMessage = getResources().getString(R.string.EMPTY_EMAIL_ADDRESS);
            }
            else if(!validator.isValidEmailAddress(email)){
                errorMessage = getResources().getString(R.string.INVALID_EMAIL_ADDRESS);
            }
            else if(validator.isEmpty(password)){
                errorMessage = getResources().getString(R.string.EMPTY_PASSWORD);
            }
            else if(!validator.isValidPassword(password)){
                errorMessage = getResources().getString(R.string.INVALID_PASSWORD);
            }
            else if(name.isEmpty()){
                errorMessage = getResources().getString(R.string.EMPTY_NAME);
            }
            else if(phoneNumber.isEmpty()){
                errorMessage = getResources().getString(R.string.EMPTY_PHONE_NUMBER);
            }
            else if(!validator.isValidPhoneNumber(phoneNumber)){
                errorMessage = getResources().getString(R.string.INVALID_PHONE_NUMBER);
            }
            else if(!validator.isValidRole(role)){
                errorMessage = getResources().getString(R.string.INVALID_ROLE);
            }

            }
        setErrorMessage();
    }

    protected void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "Email verification sent");
            }
        });
    }
}