package com.example.quickcash;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertFalse;


import com.example.quickcash.ui.PasswordResetActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class PasswordResetEspresso {

    public ActivityScenario<PasswordResetActivity> scenario = null;

    @Before
    public void setup() {
        scenario = ActivityScenario.launch(PasswordResetActivity.class);
        scenario.onActivity(activity -> {
        });
    }
    @After
    public void tearDown() {
        scenario.close();
        scenario = null;
    }

    @Test
    public void checkIfEmailExists() {
        onView(withId(R.id.recoveryEmailInput)).perform(typeText("espresso@test.com"));
        onView(withId(R.id.ResetPassword)).perform(click());
        onView(withId(R.id.prStatusLabel)).check(matches(withText(R.string.EMPTY_STRING)));
    }
    @Test
    public void checkIfEmailDoesNotExist() {
        onView(withId(R.id.recoveryEmailInput)).perform(typeText("unknownperson@dal.ca"));
        onView(withId(R.id.ResetPassword)).perform(click());
        onView(withId(R.id.prStatusLabel)).check(matches(withText(R.string.EMAIL_NOT_FOUND)));
    }
    @Test
    public void checkIfEmailIsInvalid() {
        onView(withId(R.id.recoveryEmailInput)).perform(typeText("akil@dal"));
        onView(withId(R.id.ResetPassword)).perform(click());
        onView(withId(R.id.prStatusLabel)).check(matches(withText(R.string.INVALID_EMAIL_ADDRESS)));
    }
    @Test
    public void checkIfEmailIsEmpty() {
        onView(withId(R.id.ResetPassword)).perform(click());
        onView(withId(R.id.prStatusLabel)).check(matches(withText(R.string.EMPTY_EMAIL_ADDRESS)));
    }
}
