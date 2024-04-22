package com.example.quickcash.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quickcash.R;

import java.util.List;
/**
 * Adapter for managing a collection of preferred employee emails in a RecyclerView.
 * This adapter is responsible for providing views that represent each preferred employee
 * and handling the removal of an employee from the preferred list.
 *
 * Usage of this adapter requires it to be set on a RecyclerView instance and provided
 * with a context and a list of preferred employee emails. The context is expected to be
 * an instance of PreferredEmployeesActivity to handle removal callbacks.
 */
public class PreferredEmployeeAdapter extends RecyclerView.Adapter<PreferredEmployeeAdapter.PreferredEmployeeViewHolder> {
    private final Context context;
    private final List<String> preferredEmployeesEmails;

    public PreferredEmployeeAdapter(Context context, List<String> preferredEmployeesEmails) {
        this.context = context;
        this.preferredEmployeesEmails = preferredEmployeesEmails;
    }

    @NonNull
    @Override
    public PreferredEmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.preferred_employees_view_item, parent, false);
        return new PreferredEmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PreferredEmployeeViewHolder holder, int position) {
        String email = preferredEmployeesEmails.get(position);
        holder.employeeEmail.setText(email);

        // Add click listeners or other event handling here
        holder.removePreferButton.setOnClickListener(view -> {
            ((PreferredEmployeesActivity) context).removePreferredEmployee(email);
        });
    }
    @Override
    public int getItemCount() {
        return preferredEmployeesEmails.size();
    }

    public static class PreferredEmployeeViewHolder extends RecyclerView.ViewHolder {
        TextView employeeEmail;
        ImageButton removePreferButton;

        public PreferredEmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            employeeEmail = itemView.findViewById(R.id.preferredEmployeeEmail);
            removePreferButton = itemView.findViewById(R.id.removePreferButton);
        }
    }
}
