package com.example.quickcash;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import com.example.quickcash.R;
import com.example.quickcash.ui.QuickCashPaymentActivity;

@RunWith(AndroidJUnit4.class)
public class QuickCashPaymentEspressoTest {

    @Rule
    public ActivityScenarioRule<QuickCashPaymentActivity> activityRule =
            new ActivityScenarioRule<>(QuickCashPaymentActivity.class);

    @Before
    public void setUp() {
        // Prepare your test environment here (e.g., mock data, set intent)
    }

    @Test
    public void testUIElementsDisplay() {
        onView(withId(R.id.payTo)).check(matches(isDisplayed()));
        onView(withId(R.id.paymentJobTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.amountToPay)).check(matches(isDisplayed()));
        onView(withId(R.id.payNowBtn)).check(matches(isDisplayed()));
        // Add more assertions to validate the actual text based on the intent data
    }

    @Test
    public void testPaymentProcess() {
        // Simulate button click and check if PayPal activity is started
        onView(withId(R.id.payNowBtn)).check(matches(isClickable()));
        // Assert the PayPal activity is launched, you might need to mock the PayPal SDK for this
    }

    // Add more tests as needed
}