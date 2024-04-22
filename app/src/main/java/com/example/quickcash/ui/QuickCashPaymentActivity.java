package com.example.quickcash.ui;

import static com.example.quickcash.core.AppConstants.EMPLOYEE;
import static com.example.quickcash.core.AppConstants.EMPLOYER;
import static com.example.quickcash.core.AppConstants.FIREBASE_SERVER_KEY;
import static com.example.quickcash.core.AppConstants.PUSH_NOTIFICATION_ENDPOINT;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.quickcash.R;
import com.example.quickcash.core.AppConstants;
import com.example.quickcash.firebase.FirebaseCRUD;
import com.example.quickcash.firebase.Job;
import com.example.quickcash.firebase.JobApplication;
import com.example.quickcash.firebase.Transaction;
import com.example.quickcash.util.MenuAdapter;
import com.google.firebase.database.FirebaseDatabase;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentConfirmation;
public class QuickCashPaymentActivity extends AppCompatActivity{
    private static final String TAG = QuickCashPaymentActivity.class.getName();

    private Job currentJob;
    private JobApplication application;
    private RequestQueue requestQueue;

    //launching a previously-prepared call to start the process of executing an ActivityResultContract.
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private PayPalConfiguration payPalConfig;

    //UI Elements
    private Button payNowBtn;
    private TextView employeeNameUI;
    private TextView jobTitle;
    private TextView amountUI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        init();
        configPayPal();
        initActivityLauncher();
        setListeners();
    }

    private void init() {
        //initializing ui elements
        setUpMenu();
        payNowBtn = findViewById(R.id.payNowBtn);
        employeeNameUI = findViewById(R.id.payTo);
        jobTitle = findViewById(R.id.paymentJobTitle);
        amountUI = findViewById(R.id.amountToPay);
        Intent intent = getIntent();
        currentJob = (Job) intent.getSerializableExtra("ongoingJob");
        application = (JobApplication) intent.getSerializableExtra("jobApplication");
        if (application != null && currentJob != null) {
            employeeNameUI.setText("Paying To: " + application.getApplicantEmail());
            jobTitle.setText("Job Title: " + currentJob.getTitle());
            amountUI.setText("Amount: " + currentJob.getSalary());

        } else {
            Toast.makeText(this, "Error: Job or JobApplication data is not available.", Toast.LENGTH_LONG).show();
        }
    }

    private void configPayPal() {
        //configuring paypal i.e defining we're using SANDBOX Environment and setting the paypal client id
        payPalConfig = new PayPalConfiguration()
                .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
                .clientId(AppConstants.PAYPAL_CLIENT_ID);
    }

    private void initActivityLauncher() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        assert result.getData() != null;
                        final PaymentConfirmation confirmation = result.getData().getParcelableExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                        if (confirmation != null) {
                            try {
                                // Get the payment details
                                String paymentDetails = confirmation.toJSONObject().toString(4);
                                Log.i(TAG, paymentDetails);
                                // Extract json response and display it in a text view.
                                JSONObject payObj = new JSONObject(paymentDetails);
                                String payID = payObj.getJSONObject("response").getString("id");
                                String state = payObj.getJSONObject("response").getString("state");
                                Toast.makeText(this,String.format( "Payment %s%n with payment id %s", state, payID), Toast.LENGTH_SHORT).show();
                                sendNotificationToEmployee("Payment State", String.format( "Payment %s%n with payment id %s", state, payID), true, EMPLOYEE, payID);
                                sendNotificationToEmployee("Payment State", String.format( "Payment %s%n with payment id %s", state, payID), false, EMPLOYER, payID);
                                finish();
                            } catch (JSONException e) {
                                Log.e("Error", "an extremely unlikely failure occurred: ", e);
                            }
                        }
                    } else if (result.getResultCode() == com.paypal.android.sdk.payments.PaymentActivity.RESULT_EXTRAS_INVALID) {
                        Log.d(TAG, "Launcher Result Invalid");
                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        Log.d(TAG, "Launcher Result Cancelled");
                    }
                });
    }

    private void setListeners() {
        payNowBtn.setOnClickListener(v -> processPayment());
    }

    private void processPayment() {
        final Double amount = currentJob.getSalary();
        final PayPalPayment payPalPayment = new PayPalPayment(BigDecimal.valueOf(
                amount), "CAD",application.getApplicantEmail(), PayPalPayment.PAYMENT_INTENT_SALE);

        // Create Paypal Payment activity intent
        final Intent intent = new Intent(this, com.paypal.android.sdk.payments.PaymentActivity.class);
        // Adding paypal configuration to the intent
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, payPalConfig);
        // Adding paypal payment to the intent
        intent.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        // Starting Activity Request launcher
        activityResultLauncher.launch(intent);
    }
    private void sendNotificationToEmployee(String title, String body, boolean isClickable, String topicType, String payID) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(QuickCashPaymentActivity.this);
        }

        try {
            final JSONObject notificationJSONBody = new JSONObject();
            notificationJSONBody.put("title", title);
            notificationJSONBody.put("body", body);

            //the second json object - data
            final JSONObject dataJSONBody = new JSONObject();
            dataJSONBody.put("employerEmail", application.getEmployerEmail());
            dataJSONBody.put("applicantEmail", application.getApplicantEmail());
            dataJSONBody.put("payID", payID);
            dataJSONBody.put("notificationType", "paymentConfirmation");
            dataJSONBody.put("topicType", topicType);
            dataJSONBody.put("jobTitle", application.getJobTitle());
            dataJSONBody.put("salary", currentJob.getSalary());
            // Set isClickable based on the button clicked
            dataJSONBody.put("isClickable", Boolean.toString(isClickable));
            String topic = "employees";
            if (EMPLOYER.equals(topicType)) {
                topic = "employers";
            }
            //attaching to the main json object
            final JSONObject pushNotificationJSONBody = new JSONObject();
            pushNotificationJSONBody.put("to", "/topics/" + topic);
            pushNotificationJSONBody.put("notification", notificationJSONBody);
            pushNotificationJSONBody.put("data", dataJSONBody);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    PUSH_NOTIFICATION_ENDPOINT,
                    pushNotificationJSONBody,
                    //lamda syntax
                    response ->
                            Toast.makeText(QuickCashPaymentActivity.this,
                                    "Push notification sent.",
                                    Toast.LENGTH_SHORT).show(),
                    //method reference
                    Throwable::printStackTrace) {

                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "key=" + FIREBASE_SERVER_KEY);
                    return headers;
                }
            };

            requestQueue.add(request);
        } catch (JSONException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    protected void setUpMenu() {
        RecyclerView recyclerView = findViewById(R.id.menuHolder);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MenuAdapter adapter = new MenuAdapter(getApplicationContext());
        recyclerView.setAdapter(adapter);
    }
    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class)); // Stop PayPal Service
        super.onDestroy();
    }
}
