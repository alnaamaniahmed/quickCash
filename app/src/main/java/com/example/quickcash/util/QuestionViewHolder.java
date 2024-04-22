package com.example.quickcash.util;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcash.R;

public class QuestionViewHolder extends RecyclerView.ViewHolder {

    public TextView jobQuestion;
    public EditText jobAnswer;
    public QuestionViewHolder(@NonNull View itemView) {
        super(itemView);
        jobQuestion = itemView.findViewById(R.id.jobQuestion);
        jobAnswer = itemView.findViewById(R.id.jobAnswer);
    }
}
