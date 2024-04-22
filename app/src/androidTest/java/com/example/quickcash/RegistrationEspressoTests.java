package com.example.quickcash;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import androidx.test.core.app.ActivityScenario;

import com.example.quickcash.ui.RegistrationActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Espresso tests for Registration activity
 */
public class RegistrationEspressoTests {

    public ActivityScenario<RegistrationActivity> scenario;

    @Before
    public void setup() {
        scenario = ActivityScenario.launch(RegistrationActivity.class);
        scenario.onActivity(activity -> {
        });
    }
    @After
    public void tearDown() {
        scenario.close();
        scenario = null;
    }

    @Test
    public void invalidEmail() {
        onView(withId(R.id.emailField)).perform(typeText("yeah"));
        onView(withId(R.id.passwordField)).perform(typeText("password"));
        onView(withId(R.id.nameField)).perform(typeText("Espresso Test"));
        onView(withId(R.id.phoneField)).perform(typeText("9021234567"));
        onView(withId(R.id.roleSpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Employee"))).perform(click());
        onView(withId(R.id.registerButton)).perform(click());
        onView(withId(R.id.errorText)).check(matches(withText(R.string.INVALID_EMAIL_ADDRESS)));
    }

    @Test
    public void emptyEmail() {
        onView(withId(R.id.emailField)).perform(typeText(""));
        onView(withId(R.id.passwordField)).perform(typeText("password"));
        onView(withId(R.id.nameField)).perform(typeText("Espresso Test"));
        onView(withId(R.id.phoneField)).perform(typeText("9021234567"));
        onView(withId(R.id.roleSpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Employee"))).perform(click());
        onView(withId(R.id.registerButton)).perform(click());
        onView(withId(R.id.errorText)).check(matches(withText(R.string.EMPTY_EMAIL_ADDRESS)));
    }

    @Test
    public void invalidPassword() {
        onView(withId(R.id.emailField)).perform(typeText("espresso@test.com"));
        onView(withId(R.id.passwordField)).perform(typeText("pw"));
        onView(withId(R.id.nameField)).perform(typeText("Espresso Test"));
        onView(withId(R.id.phoneField)).perform(typeText("9021234567"));
        onView(withId(R.id.roleSpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Employee"))).perform(click());
        onView(withId(R.id.registerButton)).perform(click());
        onView(withId(R.id.errorText)).check(matches(withText(R.string.INVALID_PASSWORD)));
    }

    @Test
    public void emptyPassword() {
        onView(withId(R.id.emailField)).perform(typeText("espresso@test.com"));
        onView(withId(R.id.passwordField)).perform(typeText(""));
        onView(withId(R.id.nameField)).perform(typeText("Espresso Test"));
        onView(withId(R.id.phoneField)).perform(typeText("9021234567"));
        onView(withId(R.id.roleSpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Employee"))).perform(click());
        onView(withId(R.id.registerButton)).perform(click());
        onView(withId(R.id.errorText)).check(matches(withText(R.string.EMPTY_PASSWORD)));
    }

    @Test
    public void invalidRole() {
        onView(withId(R.id.emailField)).perform(typeText("espresso@test.com"));
        onView(withId(R.id.passwordField)).perform(typeText("password"));
        onView(withId(R.id.nameField)).perform(typeText("Espresso Test"));
        onView(withId(R.id.phoneField)).perform(typeText("9021234567"));
        onView(withId(R.id.roleSpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Select your role"))).perform(click());
        onView(withId(R.id.registerButton)).perform(click());
        onView(withId(R.id.errorText)).check(matches(withText(R.string.INVALID_ROLE)));
    }

    @Test
    public void emptyPhoneNumber() {
        onView(withId(R.id.emailField)).perform(typeText("espresso@test.com"));
        onView(withId(R.id.passwordField)).perform(typeText("password"));
        onView(withId(R.id.nameField)).perform(typeText("Espresso Test"));
        onView(withId(R.id.phoneField)).perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.roleSpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Employee"))).perform(click());
        onView(withId(R.id.registerButton)).perform(click());
        onView(withId(R.id.errorText)).check(matches(withText(R.string.EMPTY_PHONE_NUMBER)));
    }
    @Test
    public void invalidPhoneNumber() {
        onView(withId(R.id.emailField)).perform(typeText("espresso@test.com"));
        onView(withId(R.id.passwordField)).perform(typeText("password"));
        onView(withId(R.id.nameField)).perform(typeText("Espresso Test"));
        onView(withId(R.id.phoneField)).perform(typeText("8"), closeSoftKeyboard());
        onView(withId(R.id.roleSpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Employee"))).perform(click());
        onView(withId(R.id.registerButton)).perform(click());
        onView(withId(R.id.errorText)).check(matches(withText(R.string.INVALID_PHONE_NUMBER)));
    }

    /**Test is behaving inconsistently
     @Test
     public void emailAlreadyInUse() {
     onView(withId(R.id.emailField)).perform(typeText("espresso@test.com"));
     onView(withId(R.id.passwordField)).perform(typeText("password"));
     onView(withId(R.id.nameField)).perform(typeText("Espresso Test"));
     onView(withId(R.id.phoneField)).perform(typeText("9021234567"));
     onView(withId(R.id.roleSpinner)).perform(click());
     onData(allOf(is(instanceOf(String.class)), is("Employee"))).perform(click());
     onView(withId(R.id.registerButton)).perform(click());
     onView(withId(R.id.errorText)).check(matches(withText(R.string.EMAIL_ALREADY_IN_USE)));    }
     **/
}
