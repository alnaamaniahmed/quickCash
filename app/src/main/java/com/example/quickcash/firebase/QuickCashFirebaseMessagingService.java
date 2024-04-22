package com.example.quickcash.firebase;
import static com.example.quickcash.core.AppConstants.EMPLOYEES_TOPIC;
import static com.example.quickcash.core.AppConstants.EMPLOYER;
import static com.example.quickcash.core.AppConstants.EMPLOYERS_TOPIC;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.app.PendingIntent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.example.quickcash.R;
import com.example.quickcash.core.AppConstants;
import com.example.quickcash.ui.AboutAppActivity;
import com.example.quickcash.ui.ApplicationDetailsActivity;
import com.example.quickcash.ui.ChatActivity;
import com.example.quickcash.ui.OfferDetailsActivity;
import com.example.quickcash.ui.PaymentInvoiceActivity;
import com.example.quickcash.ui.RatingActivity;
import com.example.quickcash.ui.ViewJobActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class QuickCashFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getNotification() == null) {
            return;
        }
        Map<String, String> data = remoteMessage.getData();
        final String title = remoteMessage.getNotification().getTitle();
        final String body = remoteMessage.getNotification().getBody();
        String isClickableString = data.get("isClickable");
        boolean isClickable = isClickableString != null && "true".equals(isClickableString.trim());
        String notificationType = data.get("notificationType");
        String topicType = data.get("topicType");
        String channelId = getChannelIdForTopicType(topicType);
        Intent intent = null;
        int requestCode = 0;
        PendingIntent pendingIntent = null;
        

        if (isClickable) {
            if ("offer".equals(notificationType) && !"chat".equals(notificationType)) {
                intent = new Intent(this, OfferDetailsActivity.class);
                intent.putExtra("jobId", data.get("jobId"));
                intent.putExtra("jobTitle", data.get("jobTitle"));
                intent.putExtra("jobLocation", data.get("jobLocation"));
                intent.putExtra("employer", data.get("employer"));
                intent.putExtra("jobDescription", data.get("jobDescription"));
                intent.putExtra("applicantEmail", data.get("applicantEmail"));
                String salaryString = data.get("salary");
                if (salaryString != null) {
                    try {
                        double salaryValue = Double.parseDouble(salaryString);
                        intent.putExtra("salary", salaryValue);
                    } catch (NumberFormatException e) {
                        Log.e("QuickCashFirebaseMsg", "Failed to parse salary value: " + salaryString);
                    }
                }
                requestCode = 2; // unique request code for offer notification
            }else if ("chat".equals(notificationType)) {
                intent = new Intent(this, ChatActivity.class);
                intent.putExtra("EMPLOYER_EMAIL", data.get("employerEmail"));
                intent.putExtra("EMPLOYEE_EMAIL", data.get("employeeEmail"));
                intent.putExtra("EMPLOYEE_NAME", data.get("employeeName"));
                intent.putExtra("CHAT_ROOM_ID", data.get("chatRoomId"));
                requestCode = 1;
            }else if ("markComplete".equals(notificationType)) {
                intent = new Intent(this, RatingActivity.class);
                intent.putExtra("userEmail", data.get("userEmail"));
                intent.putExtra("userType", data.get("userType"));
                requestCode = 3;
            }
            else if("paymentConfirmation".equals(notificationType)){
                intent = new Intent(this, PaymentInvoiceActivity.class);
                intent.putExtra("employerEmail", data.get("employerEmail"));
                intent.putExtra("applicantEmail", data.get("applicantEmail"));
                intent.putExtra("payID", data.get("payID"));
                intent.putExtra("jobTitle", data.get("jobTitle"));
                String salaryString = data.get("salary");
                if (salaryString != null) {
                    try {
                        double salaryValue = Double.parseDouble(salaryString);
                        intent.putExtra("salary", salaryValue);
                    } catch (NumberFormatException e) {
                        Log.e("QuickCashFirebaseMsg", "Failed to parse salary value: " + salaryString);
                    }
                }
                requestCode = 5;
            }
            else if("jobPosting".equals(notificationType)){
                Job job = new Job();
                job.setTitle(data.get("jobTitle"));
                job.setDescription(data.get("jobDescription"));
                job.setLocation(data.get("jobLocation"));
                job.setType(data.get("jobType"));
                job.setSalary(Double.parseDouble(data.get("salary")));
                job.setEmployerEmail(data.get("employerEmail"));
                String questionsData = data.get("questions");
                if (questionsData != null) {
                    try {
                        JSONArray questionsJsonArray = new JSONArray(questionsData);
                        ArrayList<String> questionsList = new ArrayList<>();
                        for (int i = 0; i < questionsJsonArray.length(); i++) {
                            questionsList.add(questionsJsonArray.getString(i));
                        }
                        job.setQuestions(questionsList);
                    } catch (JSONException e) {
                        // handle any error
                    }
                }

                intent = new Intent(this, ViewJobActivity.class);
                intent.putExtra("job", job);
                requestCode= 4;
            }
        }
        if (intent != null) {
            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                flags |= PendingIntent.FLAG_IMMUTABLE;
            }
            pendingIntent = PendingIntent.getActivity(this, requestCode, intent, flags);
        }

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.quick_cash_icon_alpha)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        if (pendingIntent != null) {
            notificationBuilder.setContentIntent(pendingIntent);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int id = (int) System.currentTimeMillis();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Employer".equals(topicType) ? "Employer Notifications" : "Employee Notifications";
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            notificationBuilder.setChannelId(channelId);
        }

        notificationManager.notify(id, notificationBuilder.build());
    }
    private String getChannelIdForTopicType(String topicType) {
        return "Employer".equals(topicType) ? EMPLOYERS_TOPIC : EMPLOYEES_TOPIC;
    }
}