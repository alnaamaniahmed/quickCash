package com.example.quickcash.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcash.R;
import com.example.quickcash.core.AppConstants;
import com.example.quickcash.firebase.FirebaseCRUD;
import com.example.quickcash.firebase.FirebaseQueries;
import com.example.quickcash.firebase.UserAccount;
import com.example.quickcash.util.MenuAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Activity for managing user account settings.
 */
public class AccountSettingsActivity extends AppCompatActivity {

    String role, email, preferredJobs, rating;
    String TAG = getClass().getSimpleName();
    TextView roleText, emailText, nameText, phoneText, ratingText;
    ArrayList<Switch> preferredJobSwitches;
    UserAccount account;
    FirebaseUser user;
    DatabaseReference userRef;
    Query queryRole;
    FirebaseCRUD crud;
    FirebaseQueries fq;
    CompoundButton.OnCheckedChangeListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        connectWithFirebase();
        setUpUI();
        fetchUserSettings();
    }

    protected void connectWithFirebase() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        email = user.getEmail();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        crud = new FirebaseCRUD(database);
        fq = new FirebaseQueries(database);
        userRef = database.getReference("User Accounts");
        listener  = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String preferredJobs = adjustedJobPreferences();
                Log.d(TAG, "Updated job preferences = " + preferredJobs);
                crud.updatePreferredJobs(email, preferredJobs);
            }
        };
    }

    protected void setUpUI() {
        setUpMenu();
        setUpUserText();
        setUpPreferredJobSwitches();
    }

    protected void setUpMenu() {
        RecyclerView recyclerView = findViewById(R.id.menuHolder);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MenuAdapter adapter = new MenuAdapter(getApplicationContext());
        recyclerView.setAdapter(adapter);
    }

    protected void setUpUserText() {
        emailText = findViewById(R.id.emailText);
        roleText = findViewById(R.id.roleText);
        nameText = findViewById(R.id.nameText);
        phoneText = findViewById(R.id.phoneText);
        ratingText = findViewById(R.id.ratingText);
    }

    protected void setUpPreferredJobSwitches() {
        preferredJobSwitches = new ArrayList<>();
        preferredJobSwitches.add(findViewById(R.id.halifaxSwitch));
        preferredJobSwitches.add(findViewById(R.id.darmouthSwitch));
        preferredJobSwitches.add(findViewById(R.id.bedfordSwitch));
        preferredJobSwitches.add(findViewById(R.id.partTimeSwitch));
        preferredJobSwitches.add(findViewById(R.id.fullTimeSwitch));
        preferredJobSwitches.add(findViewById(R.id.contractSwitch));

        preferredJobs = "";
        for (Switch s : preferredJobSwitches) {
            s.setOnCheckedChangeListener(listener);
        }
    }

    private String adjustedJobPreferences() {
        StringBuilder prefs = new StringBuilder();
        for (Switch s : preferredJobSwitches) {
            if (s.isChecked()) {
                prefs.append(" ");
                prefs.append(s.getText().toString().trim());
                Log.d(TAG, "\"" + s.getText().toString().trim() + "\" switch is checked");
            }
        }

        return prefs.toString();
    }

    protected void fetchUserSettings() {
        queryRole = userRef.child(AppConstants.EMPLOYEE).orderByChild("email").equalTo(email);
        queryRole.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    role = AppConstants.EMPLOYEE;
                    setUserDetails();
                    setUpPreferredJobs();
                }
                else {
                    role = AppConstants.EMPLOYER;
                    disableEmployeeUI();
                    setUserDetails();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    protected void setUserDetails() {
        fq.getUserAccount(role, email, new FirebaseQueries.getUserCallback() {
            @Override
            public void onValueReceived(UserAccount returnAccount) {
                account = returnAccount;
                Log.d(TAG, account.getName() + "'s account retrieved");

                nameText.setText(account.getName());
                emailText.setText(account.getEmail());
                roleText.setText(account.getRole());
                phoneText.setText(account.getPhone());
                String rating = String.format("%.2f", account.getNormRating() * (float)AppConstants.MAX_RATING) + "/" + String.format("%.2f",AppConstants.MAX_RATING);
                Log.d(TAG, "Rating: " + rating);
                ratingText.setText(rating);
            }
        });
    }

    protected void setUpPreferredJobs() {
        fq.getEmployeeJobPreferences(email, new FirebaseQueries.getValueCallback() {
            @Override
            public void onValueReceived(String returnString) {
                if (returnString != null) {
                    for (Switch s : preferredJobSwitches) {
                        String pref = s.getText().toString().trim();
                        if (returnString.contains(pref)) {
                            s.setOnCheckedChangeListener(null);
                            s.setChecked(true);
                            s.setOnCheckedChangeListener(listener);
                        }
                    }
                } else {
                    Log.d(TAG, "Preferred jobs data is null.");
                }
            }
        });
    }

    protected void disableEmployeeUI() {
        ConstraintLayout preferredJobsLayout = findViewById(R.id.preferredJobsLayout);
        preferredJobsLayout.setVisibility(View.INVISIBLE);
    }
}
