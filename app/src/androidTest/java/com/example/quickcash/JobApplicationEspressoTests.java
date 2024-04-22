package com.example.quickcash;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.app.Activity;
import android.content.Intent;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.result.ActivityResult;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;

import com.example.quickcash.core.ErrorMessages;
import com.example.quickcash.ui.JobApplicationActivity;
import com.example.quickcash.util.KeyboardHider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

public class JobApplicationEspressoTests {

    public ActivityScenario<JobApplicationActivity> scenario = null;

    @Before
    public void setup() {
        scenario = ActivityScenario.launch(JobApplicationActivity.class);
        scenario.onActivity(activity -> {
        });
    }

    @After
    public void tearDown() {
        scenario.close();
        scenario = null;
    }

    @Test
    public void checkIfPageDisplays() {
        onView(withId(R.id.errorMessage)).check(matches(withText("")));
    }


    @Test
    public void checkQuestionsNotAnswered() {
        onView(withId(R.id.applyButton)).perform(click());
        onView(withId(R.id.errorMessage)).check(matches(withText(ErrorMessages.QUESTIONS_NOT_ANSWERED)));
    }


    @Test
    public void checkResumeNotUploaded() {
        onView(withId(R.id.jobAnswer)).perform(typeText("this is an answer"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.applyButton)).perform(click());
        onView(withId(R.id.errorMessage)).check(matches(withText(ErrorMessages.RESUME_NOT_UPLOADED)));
    }

}
