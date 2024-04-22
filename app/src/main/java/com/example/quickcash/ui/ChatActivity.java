package com.example.quickcash.ui;
import static com.example.quickcash.core.AppConstants.EMPLOYEE;
import static com.example.quickcash.core.AppConstants.EMPLOYER;
import static com.example.quickcash.core.AppConstants.FIREBASE_SERVER_KEY;
import static com.example.quickcash.core.AppConstants.PUSH_NOTIFICATION_ENDPOINT;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.quickcash.R;
import com.example.quickcash.util.ChatAdapter;
import com.example.quickcash.firebase.Chat;
import com.example.quickcash.util.KeyboardHider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ChatActivity provides a user interface for chatting between employers and employees.
 */
public class ChatActivity extends AppCompatActivity {
    private RequestQueue requestQueue;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private EditText messageEditText;
    private Button sendButton;
    private DatabaseReference chatRef;
    private List<Chat> chatMessages;
    private ValueEventListener messageListener;

    private String employerName;
    private String employeeName;
    private String employeeEmail; // Declare employeeEmail at the class level
    private String currentUserName; // Declare currentUserName at the class level
    private String chatRoomId;
    /**
     * Initializes the activity, sets up the chat interface and event listeners.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     * being shut down, this Bundle contains the most recent data provided by onSaveInstanceState.
     * Otherwise, it is null.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Log.d("ChatActivity", "Intent data: EMPLOYEE_EMAIL=" + getIntent().getStringExtra("EMPLOYEE_EMAIL") +
                ", EMPLOYEE_NAME=" + getIntent().getStringExtra("EMPLOYEE_NAME") +
                ", EMPLOYER_EMAIL=" + getCurrentEmployerName() +
                ", CHAT_ROOM_ID=" + getIntent().getStringExtra("CHAT_ROOM_ID"));
        // Initialize chat messages list
        chatMessages = new ArrayList<>();

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageEditText = findViewById(R.id.chatMessageET);
        sendButton = findViewById(R.id.chatSendBtn);
        KeyboardHider.setUp(findViewById(R.id.mainLayout), this);



        // Get the employee's email from the intent
        employeeEmail = getIntent().getStringExtra("EMPLOYEE_EMAIL");
        employeeName = getIntent().getStringExtra("EMPLOYEE_NAME");
        String employerEmail = getCurrentEmployerName();
        chatRoomId = getIntent().getStringExtra("CHAT_ROOM_ID");
        if (chatRoomId == null || chatRoomId.isEmpty()) {
            initializeChatRoom(employeeEmail, employerEmail);
        } else {
            setupChat(chatRoomId);
        }

        sendButton.setOnClickListener(view -> sendMessage(employeeEmail, employerEmail));
    }

    private void initializeChatRoom(String employeeEmail, String employerEmail) {
        DatabaseReference userAccountsRef = FirebaseDatabase.getInstance().getReference("User Accounts");
        userAccountsRef.child(EMPLOYEE).orderByChild("email").equalTo(employeeEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userAccountsRef.child(EMPLOYER).orderByChild("email").equalTo(employerEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot employerSnapshot) {
                            if (employerSnapshot.exists()) {
                                employeeName = dataSnapshot.getChildren().iterator().next().child("name").getValue(String.class);
                                Log.d("ChatActivity", "Employee Name: " + employeeName); // Add this log
                                employerName = employerSnapshot.getChildren().iterator().next().child("name").getValue(String.class);
                                Log.d("ChatActivity", "Employer Name: " + employerName); // Add this log
                                chatRoomId = employerName + "_" + employeeName;
                                Log.d("ChatActivity", "Chat Room ID: " + chatRoomId);
                                setupChat(chatRoomId);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e("ChatActivity", "Database error: " + databaseError.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ChatActivity", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void setupChat(String chatRoomId) {
        // Determine if the current user is the employee or employer and set the currentUserName accordingly
        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        if (currentUserEmail != null && currentUserEmail.equals(employeeEmail)) {
            // If the current user is the employee, set currentUserName to employeeName
            currentUserName = employeeName;
        } else {
            // Otherwise, set it to employerName
            currentUserName = employerName;
        }

        chatAdapter = new ChatAdapter(chatMessages, currentUserName);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);
        chatRef = FirebaseDatabase.getInstance().getReference("Chats").child(chatRoomId);

        messageListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatMessages.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    chatMessages.add(chat);
                }
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ChatActivity", "Failed to load chat messages: " + databaseError.getMessage());
            }
        };
        chatRef.addValueEventListener(messageListener);
    }

    private void sendMessage(String employeeEmail, String employerEmail) {
        Log.d("ChatActivity", "Employee Name in sendMessage: " + employeeName);
        String messageText = messageEditText.getText().toString().trim();
        if (!messageText.isEmpty()) {
            String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            boolean isSenderEmployee = currentUserEmail != null && currentUserEmail.equals(employeeEmail);
            String senderName = isSenderEmployee ? employeeName : employerName;  // Set sender name based on who is sending the message
            Chat chat = new Chat(messageText, senderName);
            chatRef.push().setValue(chat).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    messageEditText.setText("");
                    if (!isSenderEmployee) {
                        sendNotificationToEmployee(employerEmail, employeeEmail, messageText, true, EMPLOYEE);
                    }
                } else {
                    Log.e("ChatActivity", "Failed to send message");
                }
            });
        }
    }
    /**
     * Called when the activity is starting and initializes the chat listener.
     */
    @Override
    protected void onStart() {
        super.onStart();
        // Attach the message listener to the chat reference
        if (messageListener != null) {
            chatRef.addValueEventListener(messageListener);
        }
    }
    /**
     * Called when the activity is stopping and removes the chat listener.
     */
    @Override
    protected void onStop() {
        super.onStop();
        // Detach the message listener from the chat reference
        if (messageListener != null) {
            chatRef.removeEventListener(messageListener);
        }
    }
    /**
     * Gets the current employer's name (email in this case) from the FirebaseAuth instance.
     *
     * @return The email of the currently authenticated employer.
     */
    private String getCurrentEmployerName() {
        // method to return the employer's name
        return FirebaseAuth.getInstance().getCurrentUser().getEmail();
    }
    private void sendNotificationToEmployee(String employerEmail, String employeeEmail, String message, boolean isClickable, String topicType) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }

        try {
            final JSONObject notificationJSONBody = new JSONObject();
            notificationJSONBody.put("title", "New message from " + employerName);
            notificationJSONBody.put("body", message);
            final JSONObject dataJSONBody = new JSONObject();
            dataJSONBody.put("notificationType", "chat");
            dataJSONBody.put("employerEmail", employerEmail);
            dataJSONBody.put("employeeEmail", employeeEmail);
            dataJSONBody.put("employeeName", employeeName);
            dataJSONBody.put("message", message);
            dataJSONBody.put("chatRoomId", chatRoomId);
            dataJSONBody.put("isClickable", Boolean.toString(isClickable));
            dataJSONBody.put("topicType", topicType);
            final JSONObject pushNotificationJSONBody = new JSONObject();
            String topic = "employees";
            if (EMPLOYER.equals(topicType)) {
                topic = "employers";
            }
            pushNotificationJSONBody.put("to", "/topics/" + topic);
            pushNotificationJSONBody.put("notification", notificationJSONBody);
            pushNotificationJSONBody.put("data", dataJSONBody);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, PUSH_NOTIFICATION_ENDPOINT, pushNotificationJSONBody,
                    response -> Log.d("ChatActivity", "Notification sent successfully."),
                    error -> Log.e("ChatActivity", "Error sending notification", error)
            ) {
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
            Log.e("ChatActivity", "JSON exception", e);
        }
    }

}