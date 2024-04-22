package com.example.quickcash.ui;

import static com.example.quickcash.core.AppConstants.EMPLOYEE;
import static com.example.quickcash.core.AppConstants.EMPLOYER;
import static com.example.quickcash.core.AppConstants.FIREBASE_SERVER_KEY;
import static com.example.quickcash.core.AppConstants.PUSH_NOTIFICATION_ENDPOINT;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.quickcash.R;
import com.example.quickcash.firebase.Job;
import com.example.quickcash.firebase.JobApplication;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HiringApplicantAdapter extends RecyclerView.Adapter<HiringApplicantAdapter.ApplicantsHiringViewHolder> {
    Context context;
    List<JobApplication> applications;
    private OnApplicantClickListener listener;
    private RequestQueue requestQueue;
    private String employerEmail;

    public HiringApplicantAdapter(Context context, List<JobApplication> applications, OnApplicantClickListener listener, String employerEmail) {
        this.context = context;
        this.applications = applications;
        this.listener = listener;
        this.employerEmail = employerEmail;
    }

    /**
     * Adapter for displaying a list of applicants for hiring in a RecyclerView.
     */

    @NonNull
    @Override
    public HiringApplicantAdapter.ApplicantsHiringViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View view = inflater.inflate(R.layout.hiring_recycler_view_item, parent, false);
        return new HiringApplicantAdapter.ApplicantsHiringViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HiringApplicantAdapter.ApplicantsHiringViewHolder holder, int position) {
        JobApplication jobApplication = applications.get(position);
        holder.applicant.setText(jobApplication.getApplicantEmail());
        holder.applicationStatus.setText(jobApplication.getStatus());

        holder.applicant.setOnClickListener(view -> {

            // Get the current Application object from the adapter's data set
            JobApplication currentJobApplication = applications.get(holder.getAdapterPosition());
            Intent applicationDetailsIntent = new Intent(context, ApplicationDetailsActivity.class);
            // we pass the entire Application object as a Parcelable
            applicationDetailsIntent.putExtra("jobApplication", currentJobApplication);
            context.startActivity(applicationDetailsIntent);

        });
        holder.contactButton.setOnClickListener(view -> {
            // Create an Intent to start ChatActivity
            Intent chatIntent = new Intent(context, ChatActivity.class);
            chatIntent.putExtra("EMPLOYEE_EMAIL", jobApplication.getApplicantEmail());
            chatIntent.putExtra("EMPLOYER_EMAIL", employerEmail);
            context.startActivity(chatIntent);
        });

        holder.acceptButton.setOnClickListener(view -> {
            DatabaseReference jobsRef = FirebaseDatabase.getInstance().getReference("Jobs");
            jobsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot jobSnapshot : dataSnapshot.getChildren()) {
                        Job job = jobSnapshot.getValue(Job.class);
                        if (job != null && job.getApplications() != null && job.getTitle().equals(jobApplication.getJobTitle())) {
                            for (Map.Entry<String, JobApplication> entry : job.getApplications().entrySet()) {
                                String key = entry.getKey();
                                JobApplication fetchedJobApp = entry.getValue();
                                if (fetchedJobApp != null && fetchedJobApp.getApplicantEmail().equals(jobApplication.getApplicantEmail()) && jobApplication.getEmployerEmail().equals(fetchedJobApp.getEmployerEmail())) {
                                    // Update the status to "pending"
                                    DatabaseReference appRef = jobSnapshot.child("Applications").child(key).getRef();
                                    appRef.child("status").setValue("pending")
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(context, "Application status updated to pending.", Toast.LENGTH_SHORT).show();
                                                sendNotification("Job Offer", "Congratz you have been accepted for " + fetchedJobApp.getJobTitle(), true, jobSnapshot.getKey(), job.getLocation(), job.getEmployerEmail(), job.getTitle(), job.getSalary(), job.getDescription(), fetchedJobApp.getApplicantEmail(), EMPLOYEE);
                                            })
                                            .addOnFailureListener(e -> Toast.makeText(context, "Failed to update application status.", Toast.LENGTH_SHORT).show());
                                    break; // Exit once the correct application is updated
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(context, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
    /**
     * Sends a notification to the applicant about the job offer.
     *
     * @param title           The title of the notification.
     * @param body            The body text of the notification.
     * @param isClickable     Indicates if the notification is clickable.
     * @param jobId           The job ID related to the notification.
     * @param jobLocation     The location of the job.
     * @param employerEmail   The email address of the employer.
     * @param jobTitle        The title of the job.
     * @param salary          The salary for the job.
     * @param description     The description of the job.
     * @param applicantEmail  The email address of the applicant.
     */
    private void sendNotification(String title, String body, boolean isClickable, String jobId, String jobLocation, String employerEmail, String jobTitle, double salary, String description, String applicantEmail, String topicType) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        //try catch block for JSON exception
        try {
            //the first json object - to
            final JSONObject notificationJSONBody = new JSONObject();
            notificationJSONBody.put("title", title);
            notificationJSONBody.put("body",  body);

            //the second json object - data
            final JSONObject dataJSONBody = new JSONObject();
            dataJSONBody.put("notificationType", "offer");
            dataJSONBody.put("jobId", jobId);
            dataJSONBody.put("jobLocation", jobLocation);
            dataJSONBody.put("isClickable", Boolean.toString(isClickable));
            dataJSONBody.put("salary", salary);
            dataJSONBody.put("employer", employerEmail);
            dataJSONBody.put("jobTitle", jobTitle);
            dataJSONBody.put("jobDescription", description);
            dataJSONBody.put("applicantEmail", applicantEmail);
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
                            Toast.makeText(context,
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
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if(applications == null){
            return 0;
        }
        else {
            return applications.size();
        }
    }
    /**
     * Interface for handling clicks on applicants.
     */
    public interface OnApplicantClickListener{
        void onApplicantClick(JobApplication jobApplication);
    }
    /**
     * ViewHolder class for applicants' hiring information.
     */
    public static class ApplicantsHiringViewHolder extends RecyclerView.ViewHolder{

        TextView applicant;
        TextView applicationStatus;
        ImageButton acceptButton, contactButton;
        public ApplicantsHiringViewHolder(@NonNull View itemView) {
            super(itemView);
            applicant = itemView.findViewById(R.id.hiring_applicant_name);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            contactButton = itemView.findViewById(R.id.contactButton);
            applicationStatus = itemView.findViewById(R.id.applicationStatus);
        }
    }
}