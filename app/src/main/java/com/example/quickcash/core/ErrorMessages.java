package com.example.quickcash.core;

import com.example.quickcash.util.JobValidator;

public class ErrorMessages {
    // UI ERROR MESSAGES
    //PostJob
    public static final String INVALID_JOB_TITLE = "Job title must be at least "
            + JobValidator.MIN_TITLE_LENGTH + " characters long";
    public static final String EMPTY_JOB_TITLE = "Please enter a job title";
    public static final String INVALID_JOB_DESCRIPTION = "Job description must be at least "
            + JobValidator.MIN_DESCRIPTION_LENGTH + " characters long";
    public static final String EMPTY_JOB_DESCRIPTION = "Please enter a job description";
    public static final String UNSELECTED_JOB_TYPE = "Please select a job type ";
    public static final String EMPTY_SALARY = "Please enter a salary amount";
    public static final String QUESTIONS_NOT_ANSWERED = "Questions not answered";
    public static final String RESUME_NOT_UPLOADED = "Resume not uploaded";



}
