package com.example.quickcash;

import android.content.Intent;
import android.os.Parcelable;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.quickcash.firebase.JobApplication;
import com.example.quickcash.ui.HiringDetailsActivity;

import androidx.test.espresso.contrib.RecyclerViewActions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;

@RunWith(AndroidJUnit4.class)
public class HiringDetailsEspressoTest {

    @Before
    public void setup() {

        Intent intent = new Intent(androidx.test.core.app.ApplicationProvider.getApplicationContext(), HiringDetailsActivity.class);

        ActivityScenario.launch(intent);
    }

    @Test
    public void recyclerViewDisplayTest() {
        onView(withId(R.id.applicants_recycler_view)).check(matches(isDisplayed()));
    }
}