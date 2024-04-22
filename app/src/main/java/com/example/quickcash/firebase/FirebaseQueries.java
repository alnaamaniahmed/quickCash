package com.example.quickcash.firebase;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quickcash.core.AppConstants;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class FirebaseQueries {

    private FirebaseDatabase database;
    private static String returnString = "";
    private static ArrayList<String> returnList = new ArrayList<String>();

    public FirebaseQueries(FirebaseDatabase database){
        this.database = database;
    }

    public interface getValueCallback {
        void onValueReceived(String returnString);
    }

    public interface getListCallback {
        void onListReceived(ArrayList<String> returnList);
    }

    public interface getUserCallback {
        void onValueReceived(UserAccount returnAccount);
    }

    public void getUserDetail(String role, String valueType, String toMatch, getValueCallback callback){
        Query query = database.getReference("User Accounts").child(role);
        query.orderByChild(valueType).equalTo(toMatch).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String prevChildKey) {
                returnString = snapshot.child("name").getValue(String.class);
                Log.d("QUERY", returnString);
                callback.onValueReceived(returnString);
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

            // ...
        });
    }

    public void getJobQuestions(String jobTitle, getListCallback callback){
        Query query = database.getReference("Jobs");
        query.orderByChild("title").equalTo(jobTitle).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String prevChildKey) {
                GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                returnList = snapshot.child("questions").getValue(t);
                callback.onListReceived(returnList);
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

            // ...
        });
    }

    public void getJob(String employerEmail, String jobTitle, getValueCallback callback){
        Query query = database.getReference("Jobs");
        query.orderByChild("employerEmail").equalTo(employerEmail).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String prevChildKey) {
                if(snapshot.child("title").getValue(String.class).equals(jobTitle)){
                    returnString = snapshot.getKey();
                    Log.d("QUERY", returnString);
                    callback.onValueReceived(returnString);
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

            // ...
        });
    }

    public void getEmployeeID(String employeeEmail, getValueCallback callback){
        Query query = database.getReference("User Accounts").child(AppConstants.EMPLOYEE);
        query.orderByChild("email").equalTo(employeeEmail).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String prevChildKey) {
                if(snapshot.child("email").getValue(String.class).equals(employeeEmail)){
                    returnString = snapshot.getKey();
                    Log.d("QUERY", returnString);
                    callback.onValueReceived(returnString);
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

            // ...
        });
    }

    public void getEmployeeJobPreferences(String employerEmail, getValueCallback callback){
        Query query = database.getReference("User Accounts").child(AppConstants.EMPLOYEE);
        query.orderByChild("email").equalTo(employerEmail).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String prevChildKey) {
                returnString = snapshot.child("preferredJobs").getValue(String.class);
                // Check if returnString is null before logging
                if (returnString != null) {
                    Log.d("QUERY", returnString);
                } else {
                    Log.d("QUERY", "Preferred jobs data is null.");
                }
                callback.onValueReceived(returnString);
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

            // ...
        });
    }

    public void getUserAccount (String role, String email, getUserCallback callback) {
        Query query = database.getReference("User Accounts").child(role);

        if (role.equals(AppConstants.EMPLOYEE)) {
            query.orderByChild("email").equalTo(email).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Employee employee = snapshot.getValue(Employee.class);
                    callback.onValueReceived(employee);
                }

                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else {
            query.orderByChild("email").equalTo(email).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Employer employer = snapshot.getValue(Employer.class);
                    callback.onValueReceived(employer);
                }

                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
