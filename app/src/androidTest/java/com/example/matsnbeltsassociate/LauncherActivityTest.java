package com.example.matsnbeltsassociate;


import android.content.Intent;

import androidx.test.espresso.core.internal.deps.guava.collect.Iterables;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.example.matsnbeltsassociate.activity.LauncherActivity;
import com.example.matsnbeltsassociate.activity.MainActivity;
import com.example.matsnbeltsassociate.activity.PhoneAuthActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static androidx.test.ext.truth.content.IntentSubject.assertThat;
import static com.example.matsnbeltsassociate.activity.LauncherActivity.EXTRA_MESSAGE;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LauncherActivityTest {
    /**
     * Use {@link ActivityScenarioRule} to create and launch the activity under test, and close it
     * after test completes.
     */
    @Rule
    public ActivityTestRule<LauncherActivity> activityRule = new ActivityTestRule<>(LauncherActivity.class, true, false);
//    public ActivityScenarioRule<MainActivity> activityScenarioRule
//            = new ActivityScenarioRule<>(MainActivity.class);
//    @Rule
//    public IntentsTestRule<MainActivity> intentsTestRule =
//            new IntentsTestRule<>(MainActivity.class);
    @Before
    public void setUp(){
        Intents.init();
    }
    @Test
    public void changeText_sameActivity() {
        activityRule.launchActivity(new Intent());
        intended(allOf(
                hasComponent(MainActivity.class.getName()),
                hasExtraWithKey(EXTRA_MESSAGE)
        ));
//        val textView: TextView = activityScenarioRule.getScenario().findViewById(R.id.associate_today);
//        val text = textView.text
        //onData(is(instanceOf(String.class)), containsString("ricano")));
//        onView(withId(R.id.associate_today))
//                .check(matches(withText("13-08-2019")));


//        Intents.init();
//        try {
//            //onView(withId(R.id.list));
//        } finally {
//            Intents.release();
//        }
    }
    @After
    public void tearDown() throws Exception{
        Intents.release();
    }
}
