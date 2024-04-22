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

import com.example.quickcash.core.ErrorMessages;
import com.example.quickcash.ui.SearchJobActivity;
import com.example.quickcash.ui.logInActivity;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class JobSearchEspressoTest {

    public ActivityScenario<SearchJobActivity> scenario = null;

    @Before
    public void setup() {
        scenario = ActivityScenario.launch(SearchJobActivity.class);
        scenario.onActivity(activity -> {
        });
    }
    @After
    public void tearDown() {
        scenario.close();
        scenario = null;
    }

    @Test
    public void checkIfSubmittedJobIsVisible() {
        onView(withId(R.id.jobTitleText)).check(matches(withText("This is a Job")));
        onView(withId(R.id.jobTypeText)).check(matches(withText("Type: Contract")));
        onView(withId(R.id.jobSalaryText)).check(matches(withText("Salary: $42")));
    }

}
