package com.example.quickcash.firebase;

import com.example.quickcash.core.AppConstants;

public class Rating {
    private int ratingCount;
    private float normRating;
    public Rating(){
        ratingCount = 0;
        normRating = 0;
    }
    public Rating(int ratingCount, float normRating){
        this.ratingCount = ratingCount;
        this.normRating = normRating;
    }

    public float getNormRating(){
        return normRating;
    }

    public float getRating(){
        return normRating * AppConstants.MAX_RATING;
    }
    
    public void addRating(float newRating){
        if(normRating == 0){
            normRating = newRating/AppConstants.MAX_RATING;
            ratingCount++;
            return;
        }
        float totalRating = getNormRating() * ratingCount;
        ratingCount++;
        totalRating += (newRating/AppConstants.MAX_RATING);
        normRating = totalRating/ratingCount;
    }

    public void addRawRating(float newRating){
        if(normRating == 0){
            normRating = newRating;
            ratingCount++;
            return;
        }
        float totalRating = getNormRating() * ratingCount;
        ratingCount++;
        totalRating += newRating;
        normRating = totalRating/ratingCount;
    }

    public int getRatingCount() {
        return ratingCount;
    }
}
