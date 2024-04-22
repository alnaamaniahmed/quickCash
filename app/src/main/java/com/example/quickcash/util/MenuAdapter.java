package com.example.quickcash.util;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcash.R;
import com.example.quickcash.ui.AboutAppActivity;
import com.example.quickcash.ui.AccountSettingsActivity;
import com.example.quickcash.ui.DashboardActivity;
import com.example.quickcash.ui.logInActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    Context context;

    public MenuAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MenuAdapter.MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.view_header,parent,false);
        // view is passed to the view holder i.e the constructor above
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        holder.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });

        holder.homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(holder.context, DashboardActivity.class);
                holder.context.startActivity(intent);
            }
        });

        holder.accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(holder.context, AccountSettingsActivity.class);
                holder.context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 1; // Only *ONE* menu bar is required
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        private final ImageView menuButton;
        private final ImageView homeButton;
        private final ImageView accountButton;
        private final Context context;


        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            menuButton = itemView.findViewById(R.id.menuButton);
            homeButton = itemView.findViewById(R.id.homeButton);
            accountButton = itemView.findViewById(R.id.accountButton);

            context = itemView.getContext();
        }
    }

    protected void showPopup(View v) {
        PopupMenu popupMenu = new PopupMenu(context, v);
        popupMenu.setOnMenuItemClickListener(onMenuItemClickListener);
        popupMenu.inflate(R.menu.menu_main);
        popupMenu.show();
    }

    protected PopupMenu.OnMenuItemClickListener onMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int itemId = item.getItemId();

            if (itemId == R.id.logout) {
                logOutUser();
            }
            else if (itemId == R.id.about) {
                moveToAboutApp();
            }
            return true;
        }
    };

    protected void moveToAboutApp() {
        Intent intent = new Intent(context, AboutAppActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void logOutUser() {
        // Clear any session data
        //TODO Figure out how to uncomment these 2 lines of code without causing crashes
        //SharedPreferences preferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        //preferences.edit().clear().apply();

        // Sign out from Firebase
        FirebaseAuth.getInstance().signOut();

        // Display message to user
        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Redirect to Login Activity
        startLogInActivity();
    }

    protected void startLogInActivity() {
        Intent intent = new Intent(context, logInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
