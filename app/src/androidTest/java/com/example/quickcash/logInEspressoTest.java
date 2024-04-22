package com.example.quickcash;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertFalse;


import com.example.quickcash.ui.logInActivity;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class logInEspressoTest {

    public ActivityScenario<logInActivity> scenario = null;

    @Before
    public void setup() {
        scenario = ActivityScenario.launch(logInActivity.class);
        scenario.onActivity(activity -> {
        });
    }
    @After
    public void tearDown() {
        scenario.close();
        scenario = null;
    }

    @Test
    public void checkIfEmailIsInvalid() {
        onView(withId(R.id.emailInput)).perform(typeText("@dal.ca"));
        onView(withId(R.id.passwordInput)).perform(typeText("password"));
        onView(withId(R.id.roleSpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Employee"))).perform(click());
        onView(withId(R.id.signInButton)).perform(click());
        onView(withId(R.id.statusLabel)).check(matches(withText(R.string.INVALID_EMAIL_ADDRESS)));
    }

    @Test
    public void checkIfEmailIsEmpty() {
        onView(withId(R.id.emailInput)).perform(typeText(""));
        onView(withId(R.id.passwordInput)).perform(typeText("password"));
        onView(withId(R.id.roleSpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Employee"))).perform(click());
        onView(withId(R.id.signInButton)).perform(click());
        onView(withId(R.id.statusLabel)).check(matches(withText(R.string.EMPTY_EMAIL_ADDRESS)));
    }

    @Test
    public void checkIfPasswordEmpty() {
        onView(withId(R.id.emailInput)).perform(typeText("espresso@test.com"));
        onView(withId(R.id.passwordInput)).perform(typeText(""));
        onView(withId(R.id.roleSpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Employee"))).perform(click());
        onView(withId(R.id.signInButton)).perform(click());
        onView(withId(R.id.statusLabel)).check(matches(withText(R.string.EMPTY_PASSWORD)));
    }

    @Test
    public void checkIfPasswordIsInValid() {
        onView(withId(R.id.emailInput)).perform(typeText("espresso@test.com"));
        onView(withId(R.id.passwordInput)).perform(typeText("abcd"));
        onView(withId(R.id.roleSpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Employee"))).perform(click());
        onView(withId(R.id.signInButton)).perform(click());
        onView(withId(R.id.statusLabel)).check(matches(withText(R.string.INVALID_PASSWORD)));
    }

    @Test
    public void checkIfRoleIsInvalid() {
        onView(withId(R.id.emailInput)).perform(typeText("espresso@test.com"));
        onView(withId(R.id.passwordInput)).perform(typeText("password"));
        onView(withId(R.id.roleSpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Select your role"))).perform(click());
        onView(withId(R.id.signInButton)).perform(click());
        onView(withId(R.id.statusLabel)).check(matches(withText(R.string.INVALID_ROLE)));
    }
}