package com.example.quickcash;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.quickcash.ui.ChatActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * This is a ChatActivity Espresso test that only works while the user is logged in
 */
@RunWith(AndroidJUnit4.class)
public class ChatActivityEspressoTest {

    private ActivityScenario<ChatActivity> scenario;

    @Before
    public void setup() {
        scenario = ActivityScenario.launch(ChatActivity.class);
    }

    @After
    public void tearDown() {
        if (scenario != null) {
            scenario.close();
        }
    }

    @Test
    public void testSendButtonIsClickable() {
        // Check if the send button is clickable
        onView(withId(R.id.chatSendBtn)).check(matches(isClickable()));
    }

    @Test
    public void testUserCanTypeInMessageField() {
        // Check if the message EditText is enabled for typing
        onView(withId(R.id.chatMessageET)).check(matches(isEnabled()));
        // Simulate typing and check if the text appears in the EditText
        String testMessage = "Test message";
        onView(withId(R.id.chatMessageET)).perform(typeText(testMessage));
        onView(withId(R.id.chatMessageET)).check(matches(withText(testMessage)));
    }
}