package com.example.quickcash.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quickcash.R;
import com.example.quickcash.core.AppConstants;
import com.example.quickcash.firebase.FirebaseCRUD;
import com.example.quickcash.util.CredentialValidator;
import com.example.quickcash.util.KeyboardHider;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Activity for password reset functionality allowing users to reset their password via email.
 */
public class PasswordResetActivity extends AppCompatActivity {

    FirebaseDatabase database = null;
    FirebaseCRUD crud = null;
    FirebaseAuth mAuth;
    static final String TAG = "ACTIVITY_PASSRESET";

    boolean emailSent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);
        initializeDatabaseAccess();
        setUpUI();
    }

    protected void setUpUI(){
        loadPrStatusLabel();
        loadResetButton();
        KeyboardHider.setUp(findViewById(R.id.mainView), this);
    }

    protected void initializeDatabaseAccess() {
        database = FirebaseDatabase.getInstance(AppConstants.databaseURL);
        crud = new FirebaseCRUD(database);
    }


    protected void loadPrStatusLabel(){
        TextView label = findViewById(R.id.prStatusLabel);
        label.setText(R.string.EMPTY_STRING);
    }

    protected void loadResetButton(){
        Button resetButton = findViewById(R.id.ResetPassword);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView label = findViewById(R.id.prStatusLabel);
                CredentialValidator cv = new CredentialValidator();
                if(getRecoveryEmail().equals("")) label.setText(R.string.EMPTY_EMAIL_ADDRESS);
                else if(!cv.isValidEmailAddress(getRecoveryEmail())) label.setText(R.string.INVALID_EMAIL_ADDRESS);
                else{
                    sendEmail(getRecoveryEmail(), label, v);
                }
            }
        });
    }

    private void sendEmail(String email, TextView label, View v){
        mAuth = FirebaseAuth.getInstance();
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Email sent.");
                        label.setText(R.string.EMPTY_STRING);
                        String message = "Please check "+ getRecoveryEmail() +"\'s inbox for password reset";
                        Snackbar.make(v, message, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else {
                        label.setText(R.string.EMAIL_NOT_FOUND);
                        Exception exception = task.getException();
                        if (exception instanceof FirebaseAuthInvalidUserException) {
                            Log.e(TAG, "Email does not exist.", exception);
                        } else {
                            // Other errors
                            Log.e(TAG, "Failed to send reset email.", exception);
                        }
                    }
                });
    }

    protected String getRecoveryEmail(){
        EditText recoveryEmailField = findViewById(R.id.recoveryEmailInput);
        return recoveryEmailField.getText().toString().trim();
    }
}
