package com.example.quickcash.ui;

import static com.example.quickcash.core.AppConstants.EMPLOYEE;
import static com.example.quickcash.core.AppConstants.EMPLOYER;
import static com.example.quickcash.core.AppConstants.FIREBASE_SERVER_KEY;
import static com.example.quickcash.core.AppConstants.PUSH_NOTIFICATION_ENDPOINT;

import android.app.AlertDialog;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.quickcash.R;
import com.example.quickcash.core.AppConstants;
import com.example.quickcash.firebase.Employer;
import com.example.quickcash.firebase.Job;
import com.example.quickcash.firebase.JobApplication;
import com.example.quickcash.firebase.Rating;
import com.example.quickcash.util.MenuAdapter;
import com.example.quickcash.util.MenuManager;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PaymentActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Activity that enables users to rate and provide feedback for services or employees.
 * Users can input a rating within a predefined range, and the activity handles updating
 * the rating in the Firebase database. This activity is intended for both employers to rate employees
 * and vice versa, depending on the user type logged into the app.
 *
 * The rating logic includes validation to ensure the rating falls within the acceptable range.
 * If the rating is outside this range, an error message is displayed to the user.
 * Once a valid rating is submitted, it's processed and the user's rating statistics are updated
 * in the Firebase database.
 */
public class RatingActivity extends AppCompatActivity{
    private String userEmail;
    private String userType;
    private Job currentJob;
    private JobApplication jobApplication;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        setupRating();

        Button rateButton = findViewById(R.id.rateButton);
        Intent intent = getIntent();
        currentJob = (Job) intent.getSerializableExtra("ongoingJob");
        jobApplication = (JobApplication) intent.getSerializableExtra("jobApplication");
        userEmail = getIntent().getStringExtra("userEmail");
        userType = getIntent().getStringExtra("userType");
        Log.d("RATING", "User: " + userEmail);
        Log.d("RATING", "Type: " + userType);
        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //transferToDashboard();
                if(userType.equals(EMPLOYEE)) {
                    transferToPaymentActivity();
                } else {
                    EditText ratingInput = findViewById(R.id.ratingInput);
                    if(!ratingInput.getText().toString().isEmpty()) {
                        if(Float.parseFloat(ratingInput.getText().toString()) < 0 || Float.parseFloat(ratingInput.getText().toString()) > AppConstants.MAX_RATING){
                            setError("Invalid Rating");
                        }
                        else setRating();
                        finish();
                    }
                }
            }
        });

        setError("");

        TextView ratingHeader = findViewById(R.id.RatingHeader);
        String headerText = "Rate " + userEmail + "\n(Optional)";
        ratingHeader.setText(headerText);
    }

    private void setError(String text){
        TextView ratingError = findViewById(R.id.ratingError);
        ratingError.setText(text);
    }

    private void setupRating(){
        TextView maxRating = findViewById(R.id.ratingMax);
        String maxText = "/" + Float.toString(AppConstants.MAX_RATING);
        maxRating.setText(maxText);
    }

    private void setRating(){
        Query query = FirebaseDatabase.getInstance().getReference("User Accounts").child(userType);
        query.orderByChild("email").equalTo(userEmail).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String prevChildKey) {
                Log.d("ALERT", "Child Added");
                EditText ratingInput = findViewById(R.id.ratingInput);
                Integer ratingCountValue = snapshot.child("ratingCount").getValue(Integer.class);
                Float normRatingValue = snapshot.child("normRating").getValue(Float.class);

                // Check if the values are null and handle appropriately
                int ratingCount = (ratingCountValue != null) ? ratingCountValue : 0;
                float normRating = (normRatingValue != null) ? normRatingValue : 0f;

                Rating rating = new Rating(ratingCount, normRating);

                try {
                    float newRating = Float.parseFloat(ratingInput.getText().toString());
                    rating.addRating(newRating);

                    snapshot.getRef().child("ratingCount").setValue(rating.getRatingCount())
                            .addOnSuccessListener(aVoid -> Log.d("ALERT", "ratingCount updated to " + rating.getRatingCount()))
                            .addOnFailureListener(e -> Log.e("RatingActivity", "Failed to update rating count.", e));

                    snapshot.getRef().child("normRating").setValue(rating.getNormRating())
                            .addOnSuccessListener(aVoid -> Log.d("ALERT", "normRating updated to " + rating.getNormRating()))
                            .addOnFailureListener(e -> Log.e("RatingActivity", "Failed to update norm rating.", e));
                } catch (NumberFormatException e) {
                    Toast.makeText(RatingActivity.this, "Please enter a valid number for the rating.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void transferToPaymentActivity(){
        EditText ratingInput = findViewById(R.id.ratingInput);
        if(!ratingInput.getText().toString().isEmpty()) {
            if(Float.parseFloat(ratingInput.getText().toString()) < 0 || Float.parseFloat(ratingInput.getText().toString()) > AppConstants.MAX_RATING){
                setError("Invalid Rating");
                return;
            }
            else setRating();
        }
        Intent intent = new Intent(RatingActivity.this, QuickCashPaymentActivity.class);
        intent.putExtra("ongoingJob", currentJob);
        intent.putExtra("jobApplication", jobApplication);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}