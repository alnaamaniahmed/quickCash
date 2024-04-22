package com.example.quickcash;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;

import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import com.example.quickcash.ui.PreferredEmployeesActivity;


@RunWith(AndroidJUnit4.class)
public class PreferredEmployeesEspressoTest {

    @Before
    public void setup() {
        // Launch the PreferredEmployeesActivity
        ActivityScenario.launch(PreferredEmployeesActivity.class);
    }

    @Test
    public void recyclerViewDisplayTest() {
        // Check if the RecyclerView is displayed
        onView(withId(R.id.preferred_recycler_view)).check(matches(isDisplayed()));
    }
}