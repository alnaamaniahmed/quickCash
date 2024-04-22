package com.example.quickcash;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.quickcash.ui.OfferDetailsActivity;
import com.example.quickcash.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class OfferDetailsEspressoTest {

    @Rule
    public ActivityScenarioRule<OfferDetailsActivity> activityRule = new ActivityScenarioRule<>(OfferDetailsActivity.class);

    @Test
    public void ensureAcceptButtonIsClickable() {
        onView(withId(R.id.acceptOfferButton)).check(matches(isClickable()));
    }

    @Test
    public void ensureDeclineButtonIsClickable() {
        onView(withId(R.id.declineOfferButton)).check(matches(isClickable()));
    }
}
