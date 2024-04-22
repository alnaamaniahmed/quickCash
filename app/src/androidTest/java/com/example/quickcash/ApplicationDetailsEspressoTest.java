package com.example.quickcash;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.quickcash.ui.ApplicationDetailsActivity;
import com.example.quickcash.firebase.JobApplication;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import android.content.Intent;
import android.os.Parcelable;

@RunWith(AndroidJUnit4.class)
public class ApplicationDetailsEspressoTest {

    public ActivityScenario<ApplicationDetailsActivity> scenario;
    @Before
    public void setup() {
        // Create a mock Application object with necessary data
        JobApplication mockJobApplication = new JobApplication();
        mockJobApplication.setJobTitle("Software Engineer");
        mockJobApplication.setApplicantEmail("applicant@example.com");
        mockJobApplication.setEmployerEmail("employer@example.com");

        // Create an Intent and put the mock Application object as an extra
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ApplicationDetailsActivity.class);
        intent.putExtra("jobApplication", mockJobApplication);

        // Launch the ApplicationDetailsActivity with the intent
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
    public void checkApplicantNameDisplayed() {
        // Check if the applicant name TextView is displayed
        onView(withId(R.id.ApplicantName)).check(matches(isDisplayed()));
    }
    @Test
    public void checkJobTitleDisplayed() {
        //Check if the job title TextView is displayed
        onView(withId(R.id.labelApplyingFor)).check(matches(isDisplayed()));
    }

    @Test
    public void checkCoverLetterLinkDisplayed() {
        // Check if the CoverLetter TextView is displayed
        onView(withId(R.id.coverLetterTextView)).check(matches(isDisplayed()));
    }

    @Test
    public void checkResumeLinkDisplayed() {
        // Check if the resume TextView is displayed
        onView(withId(R.id.resumeTextView)).check(matches(isDisplayed()));
    }

    @Test
    public void checkAdditionalFilesLinkDisplayed() {
        // Check if the additional files TextView is displayed
        onView(withId(R.id.additionalFilesTextView)).check(matches(isDisplayed()));
    }
    @Test
    public void checkDenyButtonFunctionality() {
        onView(withId(R.id.denyButton))
                .check(matches(isDisplayed()))
                .perform(click());
    }
    @Test
    public void checkShortlistButtonFunctionality() {
        onView(withId(R.id.shortlistButton))
                .check(matches(isDisplayed()))
                .perform(click());
    }
    @Test
    public void checkBackToDashboardNavigatesCorrectly() {
        // button click for navigating back to the dashboard
        onView(withId(R.id.backToDashboardButton))
                .check(matches(isDisplayed()))
                .perform(click());
    }
}
