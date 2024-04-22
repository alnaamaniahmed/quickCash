package com.example.quickcash.ui;

import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Toast;
import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import static com.example.quickcash.core.AppConstants.*;
import com.example.quickcash.R;

import com.example.quickcash.ui.PreferredEmployeeAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
/**
 * An activity that displays a list of preferred employees for the currently authenticated employer.
 * It uses a {@link RecyclerView} to list the emails of preferred employees and allows the employer
 * to remove employees from their preferred list.
 *
 * This activity fetches the preferred employees from Firebase based on the employer's unique ID and
 * updates the UI accordingly. Removal of preferred employees is also handled, with changes being
 * reflected in the Firebase database as well as the local list used by the adapter.
 */
public class PreferredEmployeesActivity extends AppCompatActivity {

    private List<String> preferredEmployeesEmails;
    private PreferredEmployeeAdapter adapter;
    private DatabaseReference preferredRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferred_employees);

        preferredEmployeesEmails = new ArrayList<>();
        RecyclerView preferredEmployeesRecyclerView = findViewById(R.id.preferred_recycler_view);
        preferredEmployeesRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        adapter = new PreferredEmployeeAdapter(this, preferredEmployeesEmails);
        preferredEmployeesRecyclerView.setAdapter(adapter);

        fetchPreferredEmployees();
    }

    private void fetchPreferredEmployees() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference employerRef = FirebaseDatabase.getInstance()
                    .getReference("User Accounts")
                    .child(EMPLOYER);

            employerRef.orderByChild("email").equalTo(user.getEmail())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot employerSnapshot : dataSnapshot.getChildren()) {
                                    // This gives us the employer's unique ID
                                    String employerUID = employerSnapshot.getKey();

                                    // Now we can use the employer's UID to get their preferred employees
                                    preferredRef = employerRef.child(employerUID).child("PreferredEmployees");
                                    preferredRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot preferredSnapshot) {
                                            preferredEmployeesEmails.clear();
                                            for (DataSnapshot snapshot : preferredSnapshot.getChildren()) {
                                                String email = snapshot.child("EmployeeEmail").getValue(String.class);
                                                if (email != null) {
                                                    preferredEmployeesEmails.add(email);
                                                }
                                            }
                                            adapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Toast.makeText(PreferredEmployeesActivity.this, "Failed to load preferred employees: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(PreferredEmployeesActivity.this, "Employer not found.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(PreferredEmployeesActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
    public void removePreferredEmployee(String emailToRemove) {
        preferredRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String email = childSnapshot.child("EmployeeEmail").getValue(String.class);
                    if (email != null && email.equals(emailToRemove)) {
                        childSnapshot.getRef().removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(PreferredEmployeesActivity.this, "Preferred employee removed successfully.", Toast.LENGTH_SHORT).show();
                                    preferredEmployeesEmails.remove(emailToRemove);
                                    adapter.notifyDataSetChanged();
                                })
                                .addOnFailureListener(e -> Toast.makeText(PreferredEmployeesActivity.this, "Failed to remove preferred employee.", Toast.LENGTH_SHORT).show());
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PreferredEmployeesActivity.this, "Failed to remove preferred employee: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
