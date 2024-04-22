package com.example.quickcash.util;

import static com.example.quickcash.core.AppConstants.EMPLOYEES_TOPIC;
import static com.example.quickcash.core.AppConstants.EMPLOYERS_TOPIC;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quickcash.R;
import com.example.quickcash.ui.DashboardActivity;
import com.example.quickcash.ui.logInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * This class is called in onCreate() for any activity whose toolbar has menu functionality
 */
public class MenuManager extends AppCompatActivity {

    static Context currentContext;

    public View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Updates currentContext respective to the calling Activity
            currentContext = v.getContext();
            // Display main menu
            showPopup(v);
        }
    };

    protected PopupMenu.OnMenuItemClickListener onMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int itemId = item.getItemId();

//            if (itemId == R.id.home) {
//                startDashboardActivity();
//            }
//            else if (itemId == R.id.accountSettings) {
//                Toast.makeText(currentContext, "<< UNDER CONSTRUCTION >>", Toast.LENGTH_SHORT).show();
//            }
            if (itemId == R.id.logout) {
                logoutUser();
            }
            if (itemId == R.id.about) {
                Toast.makeText(currentContext, "<< UNDER CONSTRUCTION >>", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };

    protected void showPopup(View v) {
        PopupMenu popupMenu = new PopupMenu(currentContext, v);
        //popupMenu.setForceShowIcon(true);
        popupMenu.setOnMenuItemClickListener(onMenuItemClickListener);
        popupMenu.inflate(R.menu.menu_main);
        popupMenu.show();
    }

    private void logoutUser() {
        // Clear any session data
        //TODO Figure out how to uncomment these 2 lines of code without causing crashes
        //SharedPreferences preferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        //preferences.edit().clear().apply();

        // Sign out from Firebase
        FirebaseAuth.getInstance().signOut();

        // Unsubscribe from FCM topic
        unsubscribeFromTopic(EMPLOYEES_TOPIC);
        unsubscribeFromTopic(EMPLOYERS_TOPIC);
        // Display message to user
        Toast.makeText(currentContext, "Logged out successfully", Toast.LENGTH_SHORT).show();


        // Redirect to Login Activity
        startLogInActivity();
    }
    private void unsubscribeFromTopic(String topic) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("Unsubscription", "Unsubscription from topic failed");
                    } else {
                        Log.d("Unsubscription", "Unsubscribed from topic: " + topic);
                    }
                });
    }

    protected void startDashboardActivity() {
        Intent intent = new Intent(currentContext, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        currentContext.startActivity(intent);
        finish();
    }

    protected void startLogInActivity() {
        Intent intent = new Intent(currentContext, logInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        currentContext.startActivity(intent);
        finish();
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }
}