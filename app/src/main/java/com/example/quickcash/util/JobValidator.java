package com.example.quickcash.util;

import com.example.quickcash.core.AppConstants;

public class JobValidator {
    public static final int MIN_TITLE_LENGTH = 10;
    public static final int MIN_DESCRIPTION_LENGTH = 10;

    public boolean isValidTitle(String title){
        if (title.length() >= MIN_TITLE_LENGTH) {
            return true;
        }
        return false;
    }

    public boolean isValidDescription(String description){
        if (description.length() >= MIN_DESCRIPTION_LENGTH) {
            return true;
        }
        return false;
    }

    public boolean isValidJobType(String jobType) {
        if (jobType.equals(AppConstants.PART_TIME)
                || jobType.equals(AppConstants.FULL_TIME)
                || jobType.equals(AppConstants.CONTRACT)) {
            return true;
        }
        return false;
    }

    public boolean isValidLocation(){
        return false;
    }
}
