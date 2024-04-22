package com.example.quickcash.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcash.R;
import com.example.quickcash.firebase.Job;
import com.example.quickcash.map.MapsActivity;
import com.example.quickcash.ui.ViewJobActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class JobAdapter extends FirebaseRecyclerAdapter<Job, JobAdapter.jobViewHolder>{
/**
 * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
 * {@link FirebaseRecyclerOptions} for configuration options.
 *
 * @param options
 */
    public JobAdapter(@NonNull FirebaseRecyclerOptions<Job> options){
        super(options);
    }

    @NonNull
    @Override
    public jobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // The view holder helps in inflating the view
        final View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.job_item_view,parent,false);
        // view is passed to the view holder i.e the constructor above
        return new jobViewHolder(view);
    }

    public class jobViewHolder extends RecyclerView.ViewHolder {
    private final TextView jobTitle;
    private final TextView jobType;
    private final TextView jobSalary;
    private final Button viewJobButton;
    private final ImageView viewOnMapButton;
    private final Context context;

    //this binds to the item view
    public jobViewHolder(@NonNull View itemView) {
        super(itemView);
        jobTitle = itemView.findViewById(R.id.jobTitleText);
        jobType = itemView.findViewById(R.id.jobTypeText);
        jobSalary = itemView.findViewById(R.id.jobSalaryText);
        viewJobButton = itemView.findViewById(R.id.viewJobButton);
        viewOnMapButton = itemView.findViewById(R.id.viewOnMapButton);
        context = itemView.getContext();
    }
}
    @Override
    protected void onBindViewHolder(@NonNull jobViewHolder holder, int position, @NonNull Job job) {
        holder.jobTitle.setText(job.getTitle());
        holder.jobType.setText("Type: " + job.getType());
        holder.jobSalary.setText("Salary: $" + job.getSalary() + "0");

        holder.viewJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(holder.context, ViewJobActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("job", job);
                intent.putExtras(bundle);
                holder.context.startActivity(intent);
            }
        });

        holder.viewOnMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(holder.context, MapsActivity.class);
                Bundle extras = new Bundle();
                extras.putString("jobLocation", job.getLocation());
                extras.putString("jobTitle", job.getTitle());
                extras.putString("jobSalary", job.getSalary().toString());
                intent.putExtras(extras);
                holder.context.startActivity(intent);
            }
        });
    }
}


