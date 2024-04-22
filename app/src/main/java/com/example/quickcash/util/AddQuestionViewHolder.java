package com.example.quickcash.util;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcash.R;

public class AddQuestionViewHolder extends RecyclerView.ViewHolder {
    public EditText jobQuestion;
    public ImageButton removeQuestionButton;
    public AddQuestionViewHolder(@NonNull View itemView) {
        super(itemView);
        jobQuestion = itemView.findViewById(R.id.questionField);
        removeQuestionButton = itemView.findViewById(R.id.removeQuestionButton);
    }
}
