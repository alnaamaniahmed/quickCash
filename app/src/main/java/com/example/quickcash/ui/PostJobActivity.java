package com.example.quickcash.ui;

import static com.example.quickcash.core.AppConstants.EMPLOYEE;
import static com.example.quickcash.core.AppConstants.EMPLOYER;
import static com.example.quickcash.core.AppConstants.FIREBASE_SERVER_KEY;
import static com.example.quickcash.core.AppConstants.PUSH_NOTIFICATION_ENDPOINT;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.quickcash.R;
import com.example.quickcash.core.AppConstants;
import com.example.quickcash.core.ErrorMessages;
import com.example.quickcash.firebase.FirebaseCRUD;
import com.example.quickcash.firebase.Job;
import com.example.quickcash.util.AddQuestionAdapter;
import com.example.quickcash.util.JobValidator;
import com.example.quickcash.util.KeyboardHider;
import com.example.quickcash.util.MenuAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class allows an Employer to post a job to to Realtime Database
 * All UI criteria must be met for job to be created successfully
 * i.e., Title, Description, Type, Location, and Salary
 */
public class PostJobActivity extends AppCompatActivity implements View.OnClickListener {


    final String TAG = this.getClass().getSimpleName(); //Used as arg in Log messages
    FirebaseAuth mAuth;
    FirebaseDatabase database = null;
    FirebaseCRUD crud = null;

    String title;
    String description;
    String jobType = "";
    double salary;
    String location;
    String employerEmail;
    String errorMessage = "";
    ArrayList<String> questions;
    RecyclerView recyclerView;
    AddQuestionAdapter addQuestionAdapter;
    ImageView addQuestionbutton;
    RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_job);

        initializeDatabaseAccess();
        setUpUI();
    }

    protected void initializeDatabaseAccess() {
        database = FirebaseDatabase.getInstance(AppConstants.databaseURL);
        crud = new FirebaseCRUD(database);
    }

    protected void setUpUI() {
        setUpMenu();
        setUpRecyclerView();
        setUpAddQuestionButton();
        setUpJobTypeRGroup();
        setUpLocationSpinner();
        setUpPublishButton();
        setUpCancelButton();
        KeyboardHider.setUp(findViewById(R.id.mainView), this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    protected void setUpMenu (){
        RecyclerView recyclerView = findViewById(R.id.menuHolder);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MenuAdapter adapter = new MenuAdapter(getApplicationContext());
        recyclerView.setAdapter(adapter);
    }

    protected void setUpRecyclerView() {
        questions = new ArrayList<>();
        recyclerView = findViewById(R.id.questionRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        addQuestionAdapter = new AddQuestionAdapter(getApplicationContext(), questions);
        recyclerView.setAdapter(addQuestionAdapter);
    }

    protected void setUpAddQuestionButton(){
        addQuestionbutton = findViewById(R.id.addQuestionButton);
        addQuestionbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();

                if (questions.size() < AppConstants.MAX_APPLICATION_QUESTIONS) {
                    questions.add("");
                }
                else {
                    Toast.makeText(context, "Can't add more than "
                            + AppConstants.MAX_APPLICATION_QUESTIONS
                            + " custom questions",Toast.LENGTH_SHORT).show();
                }

                updateRecyclerView(context);
            }
        });
    }

    protected void updateRecyclerView(Context context) {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        addQuestionAdapter = new AddQuestionAdapter(context, questions);
        recyclerView.setAdapter(addQuestionAdapter);
    }

    protected void setUpLocationSpinner() {
        Spinner roleSpinner = findViewById(R.id.locationSpinner);
        List<String> roles = new ArrayList<>();
        roles.add(AppConstants.HALIFAX);
        roles.add(AppConstants.DARTMOUTH);
        roles.add(AppConstants.BEDFORD);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, roles);
        spinnerAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        roleSpinner.setAdapter(spinnerAdapter);
    }

    protected void setUpJobTypeRGroup() {
        RadioGroup radioGroup = findViewById(R.id.jobTypeRGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRB = (RadioButton) group.findViewById(checkedId);
                jobType = checkedRB.getText().toString().trim();
                Log.d(TAG, "Job type changed to " + jobType);
            }
        });
    }

    protected void setUpPublishButton() {
        Button publishButton = findViewById(R.id.publishJobButton);
        publishButton.setOnClickListener(this);
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

    protected String getJobTitle() {
        EditText jobTitleField = findViewById(R.id.jobTitleField);
        return jobTitleField.getText().toString().trim();
    }

    protected String getJobDescription() {
        EditText jobDescriptionField = findViewById(R.id.jobDescriptionField);
        return jobDescriptionField.getText().toString().trim();
    }

    protected Double getSalary() {
        EditText salaryField = findViewById(R.id.salaryField);
        String salaryString = salaryField.getText().toString().trim();
        if (salaryString.length() > 0) {
            return Double.parseDouble(salaryString);
        }
        else {
            // Salary is empty - do not attempt to parse!
            return -1.0;
        }
    }

    protected String getLocation() {
        Spinner locationSpinner = findViewById(R.id.locationSpinner);
        return locationSpinner.getSelectedItem().toString().trim();
    }

    protected String getEmployerEmail () {
        return FirebaseAuth.getInstance().getCurrentUser().getEmail().toString().trim();
    }

    protected void fetchUIText() {
        title = getJobTitle();
        description = getJobDescription();
        salary = getSalary();
        location = getLocation();
        employerEmail = getEmployerEmail();
    }

    protected void setErrorMessage() {
        TextView errorText = findViewById(R.id.jobErrorMessage);
        errorText.setText(errorMessage);
    }

    protected void returnUserToDashboard() {
        Intent intent = new Intent(PostJobActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish(); // Close the post job activity after redirecting the user
    }

    protected void makeJobPostToast () {
        Toast.makeText(this, "Job posting successful!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Helper method that negates all empty questions preceding submission
     */
    protected ArrayList<String> getQuestions () {
        ArrayList<String> applicationQuestions = new ArrayList<>();

        for (String question : questions) {
            if (!question.equals("")) {
                applicationQuestions.add(question);
            }
        }
        return applicationQuestions;
    }

    @Override
    public void onClick(View v) {
        fetchUIText();

        Job newJob = new Job();
        JobValidator validator = new JobValidator();

        boolean jobDetailsAreValid = validator.isValidTitle(title)
                && validator.isValidDescription(description)
                && validator.isValidJobType(jobType)
                && salary >= AppConstants.MIN_JOB_SALARY;

        if (jobDetailsAreValid) {
            errorMessage = "";
            questions = getQuestions();

            newJob.setTitle(title);
            newJob.setDescription(description);
            newJob.setType(jobType);
            newJob.setSalary(salary);
            newJob.setLocation(location);
            newJob.setEmployerEmail(employerEmail);
            newJob.setQuestions(questions);

            sendNotification("New Job Posting!", "A new job matching your preferences has been posted.", true, EMPLOYEE, newJob);
            crud.createJob(newJob);
            makeJobPostToast();
            returnUserToDashboard();
        }
        else {
            if (title.isEmpty()) {
                errorMessage = ErrorMessages.EMPTY_JOB_TITLE;
            }
            else if (!validator.isValidTitle(title)) {
                errorMessage = ErrorMessages.INVALID_JOB_TITLE;
            }
            else if (description.isEmpty()) {
                errorMessage = ErrorMessages.EMPTY_JOB_DESCRIPTION;
            }
            else if (!validator.isValidDescription(description)) {
                errorMessage = ErrorMessages.INVALID_JOB_DESCRIPTION;
            }
            else if (jobType.isEmpty()) {
                errorMessage = ErrorMessages.UNSELECTED_JOB_TYPE;
            }
            else if (salary < AppConstants.MIN_JOB_SALARY) {
                errorMessage = ErrorMessages.EMPTY_SALARY;
            }
        }
        setErrorMessage();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
    }

    private void sendNotification(String title, String body, boolean isClickable, String topicType, Job newJob) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }
        //try catch block for JSON exception
        try {
            //the first json object - to
            final JSONObject notificationJSONBody = new JSONObject();
            notificationJSONBody.put("title", title);
            notificationJSONBody.put("body", body);

            //the second json object - data
            final JSONObject dataJSONBody = new JSONObject();
            dataJSONBody.put("jobTitle", newJob.getTitle());
            dataJSONBody.put("jobDescription", newJob.getDescription());
            dataJSONBody.put("jobLocation", newJob.getLocation());
            dataJSONBody.put("employerEmail", newJob.getEmployerEmail());
            dataJSONBody.put("jobType", newJob.getType());
            dataJSONBody.put("salary", newJob.getSalary());
            dataJSONBody.put("questions", newJob.getQuestions());
            dataJSONBody.put("notificationType", "jobPosting");
            // Set isClickable based on the button clicked
            dataJSONBody.put("isClickable", Boolean.toString(isClickable));
            dataJSONBody.put("topicType", topicType);
            String topic = "employees";
            if (EMPLOYER.equals(topicType)) {
                topic = "employers";
            }
            //attaching to the main json object
            final JSONObject pushNotificationJSONBody = new JSONObject();
            pushNotificationJSONBody.put("to", "/topics/" + topic);
            pushNotificationJSONBody.put("notification", notificationJSONBody);
            pushNotificationJSONBody.put("data", dataJSONBody);

            //parameters sent in the request:
            //type of request - post- sending data to firebase
            //url - push notification endpoint
            //data - body of the notification
            //toast message
            //error listener
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    PUSH_NOTIFICATION_ENDPOINT,
                    pushNotificationJSONBody,
                    //lamda syntax
                    response ->
                            Toast.makeText(PostJobActivity.this,
                                    "Push notification sent.",
                                    Toast.LENGTH_SHORT).show(),
                    //method reference
                    Throwable::printStackTrace) {

                @Override
                public Map<String, String> getHeaders() {
                    final Map<String, String> headers = new HashMap<>();
                    headers.put("content-type", "application/json");
                    headers.put("authorization", "key=" + FIREBASE_SERVER_KEY);
                    return headers;
                }
            };
            //add the request to the queue
            requestQueue.add(request);
        } catch (JSONException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

}
