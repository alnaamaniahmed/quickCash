package com.example.quickcash.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcash.R;

import java.util.ArrayList;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionViewHolder> {

    Context context;
    List<String> questions;
    ArrayList<String> answers;

    int index = 0;

    public QuestionAdapter(Context context, List<String> questions) {
        this.context = context;
        this.questions = questions;
        answers = new ArrayList<String>();
        for(int i = 0; i < questions.size(); i++){
            answers.add("");
        }
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new QuestionViewHolder(LayoutInflater.from(context).inflate(R.layout.job_question_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        holder.jobQuestion.setText(questions.get(position));
        holder.jobAnswer.setTag(position);

        holder.jobAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int adapterPosition = (int) holder.jobAnswer.getTag();
                answers.set(adapterPosition, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public ArrayList<String> getAllAnswers() {
        return answers;
    }

}
