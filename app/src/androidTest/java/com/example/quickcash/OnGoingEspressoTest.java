package com.example.quickcash;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.quickcash.firebase.Job;
import com.example.quickcash.firebase.JobApplication;
import com.example.quickcash.ui.ApplicationDetailsActivity;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import com.example.quickcash.ui.OngoingActivity;

import android.content.Intent;
import android.os.Parcelable;

@RunWith(AndroidJUnit4.class)
public class OnGoingEspressoTest {

    public ActivityScenario<ApplicationDetailsActivity> scenario;
    @Before
    public void setup() {
        // Create mock Application object
        JobApplication mockApplication = new JobApplication();
        mockApplication.setApplicantEmail("applicant@example.com");
        mockApplication.setJobTitle("Software Engineer");

        // Create mock Job object
        Job mockJob = new Job();
        mockJob.setTitle("Software Engineer");
        mockJob.setDescription("Job description here");
        mockJob.setLocation("Halifax, NS");

        // Create an Intent for the OngoingActivity and put the mock objects as extras
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), OngoingActivity.class);
        intent.putExtra("jobApplication",  mockApplication);
        intent.putExtra("ongoingJob", mockJob);

        // Launch the OngoingActivity with the intent
        scenario = ActivityScenario.launch(intent);
    }

    @After
    public void tearDown() {
        // Clean up after each test
        if (scenario != null) {
            scenario.close();
        }
    }

    @Test
    public void checkApplicantEmailDisplayed() {
        // Check if the applicant email TextView is displayed
        onView(withId(R.id.applicantEmail)).check(matches(isDisplayed()));
    }
    @Test
    public void checkJobTitleInfoDisplayed() {
        // Check if the job title info TextView is displayed
        onView(withId(R.id.jobTitleInfo)).check(matches(isDisplayed()));
    }
    @Test
    public void checkJobLocationDisplayed() {
        // Check if the job location TextView is displayed
        onView(withId(R.id.jobLocation)).check(matches(isDisplayed()));
    }
    @Test
    public void checkMarkJobAsCompletedButtonFunctionality() {
        // Check if the mark as completed button is displayed and can be clicked
        onView(withId(R.id.MarkJobAsCompletedButton))
                .check(matches(isDisplayed()))
                .perform(click());
    }

}