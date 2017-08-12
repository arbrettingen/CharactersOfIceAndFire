package com.arbrettingen.charactersoficeandfire.activity;

import android.app.Instrumentation;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.Checks;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.arbrettingen.charactersoficeandfire.ASOIAFCharacter;
import com.arbrettingen.charactersoficeandfire.CharacterDetailActivity;
import com.arbrettingen.charactersoficeandfire.MainListActivity;
import com.arbrettingen.charactersoficeandfire.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.arbrettingen.charactersoficeandfire.R.id.btn_action_search;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;
import static org.junit.Assert.fail;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 */
@RunWith(AndroidJUnit4.class)
public class MainUIInstrumentedTest {

    //@Rule
    //public ActivityTestRule<MainListActivity> mActivityTestRule =
    //        new ActivityTestRule<>(MainListActivity.class);

    @Rule
    public IntentsTestRule<MainListActivity> mIntentsTestRule =
            new IntentsTestRule<>(MainListActivity.class);

    @Test
    public void mainListActivityTest(){

        //search button testing
        ViewInteraction searchButton = onView(allOf(
                withId(R.id.btn_action_search), isEnabled()));
        searchButton.perform(click());

        //search text testing
        ViewInteraction searchEditText = onView(allOf(
                withId(R.id.action_search_txt), isDisplayed()));
        searchEditText.check(matches(isDisplayed()));

        searchEditText.perform(replaceText("Eddard Stark"));

        searchEditText.perform(pressImeActionButton());

        searchEditText.check(matches(withText("Eddard Stark")));

        //search cancel button testing

        ViewInteraction searchCancelButton = onView(allOf(withId(R.id.btn_action_search_cancel), isDisplayed()));
        searchCancelButton.check(matches(isDisplayed()));

        searchCancelButton.perform(click());

        ViewInteraction titleText = onView(allOf(
                withId(R.id.bar_browse_txt), isDisplayed()));
        titleText.check(matches(isDisplayed()));

        //list item testing // DISABLE ANIMATIONS

        onData(instanceOf(ASOIAFCharacter.class))  //works
                .inAdapterView(withId(R.id.main_list_list))
                .atPosition(0)
                .check(matches(isDisplayed()));

    }

    @Test
    public void characterDetailActivityTest(){

        ViewInteraction characterListItem = onData(withCharacterListItemNameText("Eddard Stark")) //works
                .inAdapterView(withId(R.id.main_list_list))
                .check(matches(isDisplayed()));
        characterListItem.perform(click());

        intended(hasComponent("com.arbrettingen.charactersoficeandfire.CharacterDetailActivity"));

        //validate that all items that should appear are visible, and all those that should
        // disappear have done so
        ViewInteraction nameText = onView(withId(R.id.detail_name_text));
        nameText.check(matches(isDisplayed()));

        ViewInteraction genderText = onView(withId(R.id.detail_gender_txt));
        genderText.check(matches(isDisplayed()));

        ViewInteraction allegiancesText = onView(withId(R.id.detail_allegiances_text));
        allegiancesText.check(matches(isDisplayed()));

        ViewInteraction aliasesText = onView(withId(R.id.detail_alias_text));
        aliasesText.check(matches(isDisplayed()));

        ViewInteraction bornText = onView(withId(R.id.detail_born_text));
        bornText.check(matches(isDisplayed()));

        ViewInteraction diedText = onView(withId(R.id.detail_died_text));
        diedText.check(matches(isDisplayed()));

        ViewInteraction titlesText = onView(withId(R.id.detail_titles_text));
        titlesText.check(matches(isDisplayed()));

        ViewInteraction cultureText = onView(withId(R.id.detail_culture_text));
        cultureText.check(matches(isDisplayed()));

        ViewInteraction booksText = onView(withId(R.id.detail_books_text));
        booksText.perform(scrollTo());
        booksText.check(matches(isDisplayed()));

        ViewInteraction TVText = onView(withId(R.id.detail_seasons_text));
        TVText.perform(scrollTo());
        TVText.check(matches(isDisplayed()));

        ViewInteraction portrayedText = onView(withId(R.id.detail_played_text));
        portrayedText.perform(scrollTo());
        portrayedText.check(matches(isDisplayed()));

        ViewInteraction fatherText = onView(withId(R.id.detail_father_text));
        fatherText.check(matches(not(isDisplayed())));

        ViewInteraction motherText = onView(withId(R.id.detail_mother_text));
        motherText.check(matches(not(isDisplayed())));

        ViewInteraction spouseText = onView(withId(R.id.detail_spouse_text));
        spouseText.check(matches(not(isDisplayed())));


    }

    public static Matcher<Object> withCharacterListItemNameText(final String yourListItemText) { //works
        Checks.checkNotNull(yourListItemText);
        return new BoundedMatcher<Object, ASOIAFCharacter>(ASOIAFCharacter.class) {
            @Override
            public boolean matchesSafely(ASOIAFCharacter item) {
                return yourListItemText.matches(item.getmName());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with text: " + yourListItemText);
                //yourListItemText.describeTo(description);
            }
        };
    }


}
