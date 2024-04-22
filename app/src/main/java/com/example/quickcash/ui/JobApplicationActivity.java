package com.example.quickcash.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcash.R;
import com.example.quickcash.core.AppConstants;
import com.example.quickcash.core.ErrorMessages;
import com.example.quickcash.firebase.FirebaseCRUD;
import com.example.quickcash.firebase.FirebaseQueries;
import com.example.quickcash.firebase.JobApplication;
import com.example.quickcash.util.MenuAdapter;
import com.example.quickcash.util.QuestionAdapter;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * This class allows an Employer to post a job to to Realtime Database
 * All UI criteria must be met for job to be created successfully
 * i.e., Title, Description, Type, Location, and Salary
 */
public class JobApplicationActivity extends AppCompatActivity implements View.OnClickListener {
    Uri imageuri = null;
    final String TAG = this.getClass().getSimpleName(); //Used as arg in Log messages
    String title = "Plumber (16-03-2024)";
    String description = "Lorem ipsum and a quick brown fox jumps over uhhhh.. a wall?";
    String jobType = "Part time";
    double salary = 2222.22;
    String location = "Halifax";
    String employerEmail = "fakeboss@gmail.com";
    List<String> questions = new ArrayList<String>();
    String errorMessage = "";
    FirebaseAuth mAuth;
    FirebaseDatabase database = null;
    FirebaseCRUD crud = null;
    String Uid;
    StorageReference filepath;
    FirebaseQueries fq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeDatabaseAccess();
        Intent intent = getIntent();
        if(intent != null && intent.getStringExtra("title") != null){
            title = intent.getStringExtra("title");
            description = intent.getStringExtra("description");
            jobType = intent.getStringExtra("jobType");
            salary = intent.getDoubleExtra("salary", 0);
            location = intent.getStringExtra("location");
            employerEmail = intent.getStringExtra("employerEmail");
            fq.getJobQuestions(title, new FirebaseQueries.getListCallback() {
                @Override
                public void onListReceived(ArrayList<String> returnList) {
                    questions = returnList;
                    setContentView(R.layout.activity_job_application);
                    setUpPDFUpload();
                    setUpUI();
                }
            });
        }
        else{
            questions.add("This is a sample question");
            setContentView(R.layout.activity_job_application);
            setUpPDFUpload();
            setUpUI();
        }
    }

    private void setErrorMessage(String errorMessage) {
        TextView errorMsg = findViewById(R.id.errorMessage);
        errorMsg.setText(errorMessage);
    }

    protected void setUpPDFUpload(){
        Button upload = findViewById(R.id.uploadPDF);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                // We will be redirected to choose pdf
                galleryIntent.setType("application/pdf");
                startActivityForResult(galleryIntent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            imageuri = data.getData();

            //final String timestamp = "" + System.currentTimeMillis(); could be useful for later

            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Resumes").child(employerEmail).child(title);
            final String messagePushID = mAuth.getCurrentUser().getEmail();

            String pathString = messagePushID + "." + "pdf";
            filepath = storageReference.child(pathString);
            Button uploadpdf = findViewById(R.id.uploadPDF);
            uploadpdf.setText(imageuri.getLastPathSegment());
        }
    }

    /**
     * Initializes database access and authentication services.
     */
    protected void initializeDatabaseAccess() {
        database = FirebaseDatabase.getInstance(AppConstants.databaseURL);
        crud = new FirebaseCRUD(database);
        mAuth = FirebaseAuth.getInstance();
        fq = new FirebaseQueries(database);
    }
    /**
     * Sets up the user interface, including buttons, text views, and recycler views.
     */
    protected void setUpUI() {
        setUpMenu();
        setUpQuestionRecycler();
        setUpApplyButton();
        setUpCancelButton();
        fetchUIText();
    }

    protected void setUpMenu (){
        RecyclerView recyclerView = findViewById(R.id.menuHolder);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MenuAdapter adapter = new MenuAdapter(getApplicationContext());
        recyclerView.setAdapter(adapter);
    }

    protected void setUpQuestionRecycler() {
        if(questions == null || questions.isEmpty()){
            TextView label = findViewById(R.id.jobQuestionsLabel);
            label.setText("");
            return;
        }
        RecyclerView recyclerView = findViewById(R.id.questionRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new QuestionAdapter(getApplicationContext(), questions));
    }

    protected void setUpApplyButton() {
        Button applyButton = findViewById(R.id.applyButton);
        applyButton.setOnClickListener(this);
    }

    protected void setUpCancelButton() {
        Button cancelButton = findViewById(R.id.cancelJobButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnUserToDashboard();
            }
        });
    }

    protected void setJobTitle() {
        TextView jobTitle = findViewById(R.id.jobTitle);
        String string = "Title: " + title;
        jobTitle.setText(string);
    }

    protected void setJobType() {
        TextView jobTypeText = findViewById(R.id.jobType);
        String string = "Job Type: " + jobType;
        jobTypeText.setText(string);
    }

    protected void setJobDescription() {
        TextView jobDescription = findViewById(R.id.jobDescription);
        jobDescription.setText(description);
    }

    protected void setSalary() {
        TextView salaryText = findViewById(R.id.salary);
        salaryText.setText(String.format("%s", salary));
    }

    protected void setLocation() {
        TextView locationText = findViewById(R.id.jobLocationText);
        locationText.setText(location);
    }

    protected void setEmployerEmail () {
        fq.getUserDetail("Employer", "email", employerEmail, new FirebaseQueries.getValueCallback() {
            @Override
            public void onValueReceived(String returnString) {
                TextView employerEmailText = findViewById(R.id.employerEmail);
                String string = "Employer: " + returnString + " <" + employerEmail + ">";
                employerEmailText.setText(string);
                Log.d("QUERY", "User name: " + returnString);
            }
        });
    }

    protected void fetchUIText() {
        setJobTitle();
        setJobType();
        setJobDescription();
        setSalary();
        setLocation();
        setEmployerEmail();
    }

    protected void setErrorMessage() {
        TextView errorText = findViewById(R.id.jobErrorMessage);
        errorText.setText(errorMessage);
    }

    protected void returnUserToDashboard() {
        Intent intent = new Intent(JobApplicationActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish(); // Close the post job activity after redirecting the user
    }

    protected void makeJobAppToast () {
        Toast.makeText(this, "Job Application successful!", Toast.LENGTH_SHORT).show();
    }

    boolean areAllChecked() {
        if(questions == null || questions.isEmpty()){
            return true;
        }

        Log.d("CHECK", "QUESTIONS ARE BEING CHECKED");

        RecyclerView recyclerView = findViewById(R.id.questionRecycler);
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        QuestionAdapter qAdapter = (QuestionAdapter)adapter;
        ArrayList<String> answers = qAdapter.getAllAnswers();

        for(int i = 0; i < answers.size(); i++){
            Log.d("ANSWERS", qAdapter.getAllAnswers().get(i));
        }
        for(int i = 0; i < answers.size(); i++){
            if(answers.get(i).isEmpty()) return false;
        }
        return true;
    }

    ArrayList<String> getAnswers(){
        if(questions == null || questions.isEmpty()){
            return null;
        }
        RecyclerView recyclerView = findViewById(R.id.questionRecycler);
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        QuestionAdapter qAdapter = (QuestionAdapter)adapter;
        return qAdapter.getAllAnswers();
    }

    @Override
    public void onClick(View v) {
        if(!areAllChecked()){
            Toast.makeText(JobApplicationActivity.this, "You must answer all questions", Toast.LENGTH_SHORT).show();
            setErrorMessage(ErrorMessages.QUESTIONS_NOT_ANSWERED);
            return;
        }

        if(filepath == null){
            Toast.makeText(JobApplicationActivity.this, "You must select a resume file to upload", Toast.LENGTH_SHORT).show();
            setErrorMessage(ErrorMessages.RESUME_NOT_UPLOADED);
            return;
        }
        filepath.putFile(imageuri).continueWithTask(new Continuation() {
            @Override
            public Object then(@NonNull Task task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return filepath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri uri = task.getResult();
                    String myurl;
                    myurl = uri.toString();
                    Log.d("URL", myurl);
                    Toast.makeText(JobApplicationActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                    crud.createApplication(new JobApplication(title, mAuth.getCurrentUser().getEmail(),employerEmail, getAnswers(), "undecided"));
                    makeJobAppToast();
                    returnUserToDashboard();
                } else {
                    Toast.makeText(JobApplicationActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
