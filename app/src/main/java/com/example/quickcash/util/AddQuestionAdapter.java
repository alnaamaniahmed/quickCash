package com.example.quickcash.util;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcash.R;

import java.util.ArrayList;
import java.util.Arrays;

public class AddQuestionAdapter extends RecyclerView.Adapter<AddQuestionViewHolder> {

    Context context;
    ArrayList<String> questions;
    int index = 0;

    public AddQuestionAdapter(Context context, ArrayList<String> questions) {
        this.context = context;
        this.questions = questions;
    }


    @NonNull
    @Override
    public AddQuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AddQuestionViewHolder(LayoutInflater.from(context).inflate(R.layout.job_add_question_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AddQuestionViewHolder holder, int position) {
        holder.jobQuestion.setText(questions.get(holder.getBindingAdapterPosition()));
        holder.jobQuestion.setTag(holder.getBindingAdapterPosition());
        holder.jobQuestion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int adapterPosition = (int) holder.jobQuestion.getTag();
                if (adapterPosition < questions.size()) {
                    questions.set(adapterPosition, s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        holder.removeQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeQuestion(holder.getBindingAdapterPosition());

                Log.d("AddQuestionAdapter", Arrays.toString(questions.toArray()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    private void removeQuestion(int position) {
        questions.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, questions.size());
    }

    public ArrayList<String> getAllQuestions() {
        return questions;
    }

}
