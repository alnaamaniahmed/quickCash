package com.example.quickcash.ui;

import static com.example.quickcash.core.AppConstants.EMPLOYER;
import static com.example.quickcash.core.AppConstants.FIREBASE_SERVER_KEY;
import static com.example.quickcash.core.AppConstants.PUSH_NOTIFICATION_ENDPOINT;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.quickcash.R;

import com.example.quickcash.firebase.Job;
import com.example.quickcash.firebase.JobApplication;
import com.example.quickcash.util.MenuAdapter;
import com.example.quickcash.util.MenuManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Activity for displaying job offer details to the user, allowing them to accept or decline the offer.
 */
public class PaymentInvoiceActivity extends AppCompatActivity {
    private RequestQueue requestQueue;
    private String jobTitle;
    private String employerEmail;
    private String applicantEmail;
    private double salary;
    private String payID;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_invoice);
        setUpMenu();

        // Initialize your views here
        TextView invoiceTotextView = findViewById(R.id.invoiceTo);
        TextView invoiceFromtextView = findViewById(R.id.invoiceFrom);
        TextView invoicePaymentJobTitletextView = findViewById(R.id.invoicePaymentJobTitle);
        TextView invoiceAmountToPaytextView= findViewById(R.id.invoiceAmountToPay);
        TextView payIDtextView= findViewById(R.id.payID);
        Intent intent = getIntent();
        if (intent != null) {
            jobTitle = intent.getStringExtra("jobTitle");
            salary = intent.getDoubleExtra("salary", 0.0);
            payID = intent.getStringExtra("payID");
            applicantEmail = intent.getStringExtra("applicantEmail");
            employerEmail = intent.getStringExtra("employerEmail");

            // set the text views with the data
            invoiceFromtextView.setText("From: " + employerEmail);
            invoiceTotextView.setText("To: " + applicantEmail);
            invoicePaymentJobTitletextView.setText("jobTitle: " + jobTitle);
            invoiceAmountToPaytextView.setText("Amount: " + salary);
            payIDtextView.setText("Payment ID: " + payID);
        }
    }

    protected void setUpMenu() {
        RecyclerView recyclerView = findViewById(R.id.menuHolder);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MenuAdapter adapter = new MenuAdapter(getApplicationContext());
        recyclerView.setAdapter(adapter);
    }
}