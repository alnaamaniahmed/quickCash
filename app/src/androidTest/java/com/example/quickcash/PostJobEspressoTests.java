package com.example.quickcash;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;

import com.example.quickcash.core.ErrorMessages;
import com.example.quickcash.ui.PostJobActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PostJobEspressoTests {

    public ActivityScenario<PostJobActivity> scenario = null;

    @Before
    public void setup() {
        scenario = ActivityScenario.launch(PostJobActivity.class);
        scenario.onActivity(activity -> {
        });
    }

    @After
    public void tearDown() {
        scenario.close();
        scenario = null;
    }

    @Test
    public void checkIfTitleIsInvalid() {
        onView(withId(R.id.jobTitleField)).perform(typeText("Bad Title"));
        onView(withId(R.id.jobDescriptionField)).perform(typeText("Get 'er done!"));
        onView(withId(R.id.partTimeRButton)).perform(click());
        onView(withId(R.id.salaryField)).perform(typeText("17000.00"));
        onView(withId(R.id.publishJobButton)).perform(click());
        onView(withId(R.id.jobErrorMessage)).check(matches(withText(ErrorMessages.INVALID_JOB_TITLE)));
    }

    @Test
    public void checkIfTitleIsEmpty() {
        onView(withId(R.id.jobDescriptionField)).perform(typeText("Get 'er done!"));
        onView(withId(R.id.partTimeRButton)).perform(click());
        onView(withId(R.id.salaryField)).perform(typeText("17000.00"));
        onView(withId(R.id.publishJobButton)).perform(click());
        onView(withId(R.id.jobErrorMessage)).check(matches(withText(ErrorMessages.EMPTY_JOB_TITLE)));
    }

    @Test
    public void checkIfDescriptionIsInvalid() {
        onView(withId(R.id.jobTitleField)).perform(typeText("Good Title"));
        onView(withId(R.id.jobDescriptionField)).perform(typeText("Do a job."));
        onView(withId(R.id.contractRButton)).perform(click());
        onView(withId(R.id.salaryField)).perform(typeText("420.69"));
        onView(withId(R.id.publishJobButton)).perform(click());
        onView(withId(R.id.jobErrorMessage)).check(matches(withText(ErrorMessages.INVALID_JOB_DESCRIPTION)));
    }

    @Test
    public void checkIfDescriptionIsEmpty() {
        onView(withId(R.id.jobTitleField)).perform(typeText("Good Title"));
        onView(withId(R.id.contractRButton)).perform(click());
        onView(withId(R.id.salaryField)).perform(typeText("420.69"));
        onView(withId(R.id.publishJobButton)).perform(click());
        onView(withId(R.id.jobErrorMessage)).check(matches(withText(ErrorMessages.EMPTY_JOB_DESCRIPTION)));
    }

    @Test
    public void checkIfJobTypeIsUnselected() {
        onView(withId(R.id.jobTitleField)).perform(typeText("Good Title"));
        onView(withId(R.id.jobDescriptionField)).perform(typeText("Get 'er done!"));
        onView(withId(R.id.salaryField)).perform(typeText("1000"));
        onView(withId(R.id.publishJobButton)).perform(click());
        onView(withId(R.id.jobErrorMessage)).check(matches(withText(ErrorMessages.UNSELECTED_JOB_TYPE)));
    }

    @Test
    public void checkIfLocationIsAssigned() {
        //TODO Complete test after implementing maps
    }

    @Test
    public void checkIfSalaryIsEmpty() {
        onView(withId(R.id.jobTitleField)).perform(typeText("Good Title"));
        onView(withId(R.id.jobDescriptionField)).perform(typeText("Get 'er done!"));
        onView(withId(R.id.fullTimeRButton)).perform(click());
        onView(withId(R.id.publishJobButton)).perform(click());
        onView(withId(R.id.jobErrorMessage)).check(matches(withText(ErrorMessages.EMPTY_SALARY)));
    }
    
}
