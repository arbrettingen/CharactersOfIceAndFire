package com.arbrettingen.charactersoficeandfire;

import android.app.Instrumentation;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.arbrettingen.charactersoficeandfire.R.id.btn_action_search;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MainListActivityInstrumentedTest {

    @Rule
    public ActivityTestRule<MainListActivity> mActivityRule =
            new ActivityTestRule<>(MainListActivity.class);


    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.arbrettingen.charactersoficeandfire", appContext.getPackageName());
    }

    @Test
    public void ensureSearchButtonClickAndTextWatcher() {
        //press button then type text
        onView(withId(btn_action_search)).perform(click());

        //onView(withId(R.id.action_search_txt)).check(matches(notNullValue()));
        //onView(withId(R.id.action_search_txt)).perform(typeText("Eddard Stark"));
        //onView(withId(R.id.action_search_txt)).check(matches(withText("Eddard Stark")));
    }


}
