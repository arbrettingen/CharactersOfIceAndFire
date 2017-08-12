package com.arbrettingen.charactersoficeandfire.ui;

import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.test.espresso.intent.Checks;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Helper methods to make navigating views and viewmatchers within the context of espresso much
 * more simple.
 */

public class EspressoHelpers {

    /**************
     * TEXT ENTRY *
     **************/

    public static void enterTextIntoViewWithHint(String aTextToEnter, @StringRes int aHintResID) {
        onView(withHint(aHintResID)).perform(typeText(aTextToEnter));
    }

    public static void enterTextIntoViewWithID(String aTextToEnter, @IdRes int aViewID) {
        onView(withId(aViewID)).perform(typeText(aTextToEnter));
    }

    /*************
     * SCROLLING *
     *************/

    public static void scrollToViewWithID(@IdRes int aViewIDRes) {
        onView(withId(aViewIDRes)).perform(scrollTo());
    }

    /***********
     * TAPPING *
     ***********/

    public static void tapViewWithText(String aText) {
        onView(withText(aText)).perform(click());
    }

    public static void tapViewWithText(@StringRes int aTextResID) {
        onView(withText(aTextResID)).perform(click());
    }

    public static void tapViewWithID(@IdRes int aViewResID) {
        onView(withId(aViewResID)).perform(click());
    }



}
