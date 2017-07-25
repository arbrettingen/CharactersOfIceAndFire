package com.arbrettingen.charactersoficeandfire;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

/**
 * MainListActivity.java
 *
 * <P>Reads input from the api @ anapioficeandfire.com and parses it into a list of characters
 * displayed on the given ListView, each of which can be clicked on to launch CharacterDetailActivity
 *
 * @author Alex Brettingen
 * @version 1.0
 */

public class MainListActivity extends AppCompatActivity {


    public static final String LOG_TAG = MainListActivity.class.getSimpleName();
    private static final Integer TOTAL_BOOKS = 12;
    private static final Integer TOTAL_CHARACTERS = 2138;
    private static final Integer TOTAL_HOUSES = 444;

    private ProgressBar mProgress;
    private TextView mProgressText;
    private ActionBar mActionBar;

    private HashMap<String, ASOIAFCharacter> mMasterUrlToCharacterDictionary;
    private HashMap<String, String> mUrlToCharacterNameDictionary;
    private ArrayList<ASOIAFCharacter> mActiveCharacterList;
    private HashMap<String, String> mUrlToBookNamesDictionary;
    private HashMap<String, String> mUrlToHouseNamesDictionary;
    /**
     * used to convert house objects into their region string, which is used to determine house icon
     */
    private HashMap<String, String> mHouseUrlToRegionDictionary;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);

        mProgress = (ProgressBar) findViewById(R.id.main_progress);
        mProgress.setMax(TOTAL_BOOKS + TOTAL_CHARACTERS + TOTAL_HOUSES);
        mProgress.setProgress(0);

        mProgressText = (TextView) findViewById(R.id.main_loading_text);

        mActionBar = getSupportActionBar();
        mActionBar.setCustomView(R.layout.action_bar_browse);

        mActionBar.setDisplayOptions(android.app.ActionBar.DISPLAY_SHOW_CUSTOM | android.app.ActionBar.DISPLAY_SHOW_HOME);

        if (mMasterUrlToCharacterDictionary == null) {

            AOIAFLaunchAsyncTask bookAndHouseTask = new AOIAFLaunchAsyncTask(this) {
            };

            bookAndHouseTask.execute();

            mMasterUrlToCharacterDictionary = new HashMap<>();

            AOIAFCharactersAsyncTask charactersTask = new AOIAFCharactersAsyncTask(this) {
            };

            charactersTask.execute();


        }
    }

    /**
     * Update the screen to display information from the given {@link ASOIAFCharacter}.
     */
    private void updateUi(ArrayList<ASOIAFCharacter> characterList, int actionBar) {
        Collections.sort(characterList);
        ListView mMainListView = (ListView) findViewById(R.id.main_list_list);
        ASOIAFCharacterAdapter mACharacterAdapter = new ASOIAFCharacterAdapter(getApplicationContext(), R.layout.main_list_item, characterList, mHouseUrlToRegionDictionary);

        mActiveCharacterList = characterList;

        mMainListView.setAdapter(mACharacterAdapter);
        mMainListView.setOnItemClickListener(new MainListItemListener());


        if (actionBar == R.layout.action_bar_browse) {
            mActionBar = getSupportActionBar();
            mActionBar.setCustomView(actionBar);
            ImageView mSearchBtn = (ImageView) findViewById(R.id.btn_action_search);
            mSearchBtn.setOnClickListener(new SearchButtonClickListener());
        }
    }

    /**
     * Main ListView listener launches CharacterDetailActivity using character at clicked position
     */
    private class MainListItemListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent characterIntent = new Intent(getApplicationContext(), CharacterDetailActivity.class);

            String charUrl = mActiveCharacterList.get(i).getmUrl();

            characterIntent.putExtra("House URL to Region",mHouseUrlToRegionDictionary);
            characterIntent.putExtra("House URL to House", mUrlToHouseNamesDictionary);
            characterIntent.putExtra("Book URL to Book", mUrlToBookNamesDictionary);
            characterIntent.putExtra("Character URL to Name", mUrlToCharacterNameDictionary);

            characterIntent.putExtra("Character Url", charUrl);
            startActivity(characterIntent);
        }
    }

    /**
     * Search Button Click Listener swaps action bar for one with an edit text to allow user input
     * to refine the list to given characters.
     */
    private class SearchButtonClickListener implements ImageView.OnClickListener {
        @Override
        public void onClick(View view) {
            mActionBar.setCustomView(R.layout.action_bar_search);
            mActionBar.setDisplayOptions(android.app.ActionBar.DISPLAY_SHOW_CUSTOM | android.app.ActionBar.DISPLAY_SHOW_HOME);

            final EditText mSearchEditText = (EditText) findViewById(R.id.action_search_txt);

            mSearchEditText.requestFocus();

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mSearchEditText, InputMethodManager.SHOW_IMPLICIT);

            mSearchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //// TODO: 7/23/2017 fix search button

                    Collection<ASOIAFCharacter> characterCollection = mMasterUrlToCharacterDictionary.values();
                    ArrayList<ASOIAFCharacter> mSearchedCharacters = new ArrayList<>();
                    for (ASOIAFCharacter character : characterCollection) {
                        if (character.getmName().toLowerCase().contains(s.toString().toLowerCase())) {
                            mSearchedCharacters.add(character);
                        }
                    }
                    updateUi(mSearchedCharacters, R.layout.action_bar_search);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            ImageView mCancelBtn = (ImageView) findViewById(R.id.btn_action_search_cancel);
            mCancelBtn.setOnClickListener(v -> {

                Collection<ASOIAFCharacter> characterCollection = mMasterUrlToCharacterDictionary.values();
                ArrayList<ASOIAFCharacter> characterList = new ArrayList<>(characterCollection);

                InputMethodManager imm1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm1.hideSoftInputFromWindow(mSearchEditText.getWindowToken(), 0);
                updateUi(characterList, R.layout.action_bar_browse);
            });
        }
    }
}
