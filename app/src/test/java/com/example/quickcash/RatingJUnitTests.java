package com.example.quickcash;

import com.example.quickcash.core.AppConstants;
import com.example.quickcash.firebase.Rating;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RatingJUnitTests {
    @Before
    public void setup() {

    }
    @Test
    public void checkInitialNormValue(){
        Rating rating = new Rating();
        assertEquals(0f, rating.getNormRating(), 0.0);
    }

    @Test
    public void checkFirstNormValue(){
        Rating rating = new Rating();
        rating.addRating(AppConstants.MAX_RATING);
        assertEquals(1, rating.getNormRating(), 0.0);
    }

    @Test
    public void checkFirstValue(){
        Rating rating = new Rating();
        rating.addRating(AppConstants.MAX_RATING);
        assertEquals(AppConstants.MAX_RATING, rating.getRating(), 0.0);
    }

    @Test
    public void checkCompoundingRatingsConst(){
        Rating rating = new Rating();
        rating.addRating(AppConstants.MAX_RATING/2);
        rating.addRating(AppConstants.MAX_RATING/2);
        assertEquals(AppConstants.MAX_RATING/2, rating.getRating(), 0.0);
    }

    @Test
    public void checkCompoundingRatingsChanging(){
        Rating rating = new Rating();
        rating.addRating(AppConstants.MAX_RATING/2);
        rating.addRating(AppConstants.MAX_RATING);
        assertEquals(0.75f, rating.getNormRating(), 0.0);
    }
}