package com.arbrettingen.charactersoficeandfire;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arbrettingen.charactersoficeandfire.data.ASOIAFDbHelper;

import com.arbrettingen.charactersoficeandfire.data.CharacterContract;
import com.arbrettingen.charactersoficeandfire.data.CharacterContract.CharacterEntry;
import com.arbrettingen.charactersoficeandfire.data.HousesContract.HousesEntry;
import com.arbrettingen.charactersoficeandfire.data.BooksContract.BooksEntry;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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

public class MainListActivity extends AppCompatActivity implements
        android.app.LoaderManager.LoaderCallbacks<Cursor>{

    /** Identifier for the characters data loader. Loads characters from SQLite table into the application */
    private static final int MAIN_CHARACTERS_LOADER = 0;

    /** Identifier for the characters data loader. Fetches characters from API into SQLite table and application */
    private static final int MAIN_CHARACTERS_FETCH = 1;

    /** Identifier for the houses data loader. Loads houses from SQLite table into the application */
    private static final int MAIN_HOUSES_LOADER = 2;

    /** Identifier for the houses data loader. Fetches houses from API into SQLite table and application */
    private static final int MAIN_HOUSES_FETCH = 3;

    /** Identifier for the books data loader. Loads books from SQLite table into the application */
    private static final int MAIN_BOOKS_LOADER = 4;

    /** Identifier for the books data loader. Fetches books from API into SQLite table and application */
    private static final int MAIN_BOOKS_FETCH = 5;

    public static final String LOG_TAG = MainListActivity.class.getSimpleName();
    private static final Integer TOTAL_BOOKS = 12;
    private static final Integer TOTAL_CHARACTERS = 2138;
    private static final Integer TOTAL_HOUSES = 444;

    private ProgressBar mProgress;
    private TextView mProgressText;
    private ActionBar mActionBar;
    private TextView mErrorTextView;
    private ImageView mSearchBtn;
    private ImageView mSyncBtn;
    private ListView mMainListView;
    private ImageView mBanner;

    private HashMap<String, ASOIAFCharacter> mMasterUrlToCharacterDictionary;
    private HashMap<String, String> mUrlToCharacterNameDictionary = new HashMap<>();
    private ArrayList<ASOIAFCharacter> mActiveCharacterList;
    private HashMap<String, String> mUrlToBookNamesDictionary = new HashMap<>();
    private HashMap<String, String> mUrlToHouseNamesDictionary = new HashMap<>();
    /**
     * used to convert house objects into their region string, which is used to determine house icon
     */
    private HashMap<String, String> mHouseUrlToRegionDictionary;

    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);

        mMainListView = (ListView) findViewById(R.id.main_list_list);
        mBanner = (ImageView) findViewById(R.id.main_list_banner);


        mProgress = (ProgressBar) findViewById(R.id.main_progress);
        mProgress.setMax(TOTAL_BOOKS + TOTAL_CHARACTERS + TOTAL_HOUSES);
        mProgress.setProgress(0);
        mProgressText = (TextView) findViewById(R.id.main_loading_text);
        
        if (mMasterUrlToCharacterDictionary == null){
            //Acquire the size of the main dB used in the application to determine if there is data
            //to load.
            File f = getApplicationContext().getDatabasePath(ASOIAFDbHelper.DATABASE_NAME);
            long dbSize = f.length();

            if (dbSize > 0){
                getLoaderManager().initLoader(MAIN_CHARACTERS_LOADER, null, this);
                getLoaderManager().initLoader(MAIN_HOUSES_LOADER, null, this);
                getLoaderManager().initLoader(MAIN_BOOKS_LOADER, null, this);

            } else{
                getLoaderManager().initLoader(MAIN_CHARACTERS_FETCH, null, this);
                getLoaderManager().initLoader(MAIN_HOUSES_FETCH, null, this);
                getLoaderManager().initLoader(MAIN_BOOKS_FETCH, null, this);
            }
        }

        //mProgressText = (TextView) findViewById(R.id.main_loading_text);
        //mErrorTextView = (TextView) findViewById(R.id.main_error_txt);

        mActionBar = getSupportActionBar();
        mActionBar.setCustomView(R.layout.action_bar_browse);

        mActionBar.setDisplayOptions(android.app.ActionBar.DISPLAY_SHOW_CUSTOM | android.app.ActionBar.DISPLAY_SHOW_HOME);


    }

    /**
     * Update the screen to display information from the given ASOIAFCharacter.
     */
    private void updateUi(ArrayList<ASOIAFCharacter> characterList, int actionBar) {



        mErrorTextView = (TextView) findViewById(R.id.main_error_txt);

        if (mMasterUrlToCharacterDictionary == null || mMasterUrlToCharacterDictionary.isEmpty()){
            mErrorTextView.setVisibility(View.VISIBLE);
            mMainListView.setVisibility(View.GONE);
            mProgress.setVisibility(View.GONE);
            mProgressText.setVisibility(View.GONE);
        }
        else {
            mErrorTextView.setVisibility(View.GONE);
            mMainListView.setVisibility(View.VISIBLE);
            Collections.sort(characterList);
            mActiveCharacterList = characterList;

            mBanner.setVisibility(View.VISIBLE);


            ASOIAFCharacterAdapter mACharacterAdapter = new ASOIAFCharacterAdapter(getApplicationContext(), R.layout.main_list_item, characterList, mHouseUrlToRegionDictionary);
            mMainListView.setVisibility(View.VISIBLE);
            mMainListView.setAdapter(mACharacterAdapter);
            mMainListView.setOnItemClickListener(new MainListItemListener());

            if (actionBar == R.layout.action_bar_browse) {
                mActionBar = getSupportActionBar();
                mActionBar.setCustomView(actionBar);
                mSearchBtn = (ImageView) findViewById(R.id.btn_action_search);
                mSearchBtn.setOnClickListener(new SearchButtonClickListener());
                mSyncBtn = (ImageView) findViewById(R.id.btn_action_reload);
                mSyncBtn.setOnClickListener(new SyncButtonClickListener());
            }
        }


    }

    public void networkErrorState(String errorMessage){
        TextView errorTextView = (TextView) findViewById(R.id.main_error_txt);
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if (i == MAIN_CHARACTERS_FETCH) {
            //fetch characters from api and put them into sql dB and lists
            mMasterUrlToCharacterDictionary = new HashMap<>();

            AOIAFCharactersAsyncTask charactersTask = new AOIAFCharactersAsyncTask(this) {

                @Override
                public void onResponseReceived(HashMap<String, ASOIAFCharacter> result, HashMap<String, String> result2) {
                    mMasterUrlToCharacterDictionary = result;
                    mUrlToCharacterNameDictionary = result2;

                    Collection<ASOIAFCharacter> charColleciton = result.values();
                    ArrayList<ASOIAFCharacter> charList = new ArrayList<ASOIAFCharacter>(charColleciton);

                    mActiveCharacterList = charList;
                }
            };

            charactersTask.execute();
        }
        if (i == MAIN_CHARACTERS_LOADER) {
            //load chars from sql table into application lists

            //Define a projection that specifies the columns from the table we care about.
            String[] projection = {
                    CharacterEntry._ID,
                    CharacterEntry.COLUMN_CHARACTER_GENDER,
                    CharacterEntry.COLUMN_CHARACTER_PLAYEDBY,
                    CharacterEntry.COLUMN_CHARACTER_SEASONS,
                    CharacterEntry.COLUMN_CHARACTER_BOOKS,
                    CharacterEntry.COLUMN_CHARACTER_ALIASES,
                    CharacterEntry.COLUMN_CHARACTER_ALLEGIANCES,
                    CharacterEntry.COLUMN_CHARACTER_BORN,
                    CharacterEntry.COLUMN_CHARACTER_CULTURE,
                    CharacterEntry.COLUMN_CHARACTER_DIED,
                    CharacterEntry.COLUMN_CHARACTER_FATHER,
                    CharacterEntry.COLUMN_CHARACTER_SPOUSE,
                    CharacterEntry.COLUMN_CHARACTER_TITLES,
                    CharacterEntry.COLUMN_CHARACTER_MOTHER,
                    CharacterEntry.COLUMN_CHARACTER_NAME,
                    CharacterEntry.COLUMN_CHARACTER_URL,
            };

            // This loader will execute the ContentProvider's query method on a background thread
            return new android.content.CursorLoader(this,   // Parent activity context
                    CharacterEntry.CONTENT_URI,   // Provider content URI to query
                    projection,             // Columns to include in the resulting Cursor
                    null,                   // No selection clause
                    null,                   // No selection arguments
                    null);                  // Default sort order
        }
        if (i == MAIN_HOUSES_FETCH) {
            //fetch houses from api and put them into sql dB and lists
            AOIAFHousesAsyncTask houseTask = new AOIAFHousesAsyncTask(this) {
                @Override
                public void onResponseReceived(ArrayList<HashMap<String, String>> result) {
                    mUrlToHouseNamesDictionary = result.get(0);
                    mHouseUrlToRegionDictionary = result.get(1);
                }
            };
            houseTask.execute();
        }
        if (i == MAIN_HOUSES_LOADER) {
            //load houses from sql table into application lists

            String[] projection = {
                    HousesEntry._ID,
                    HousesEntry.COLUMN_HOUSE_NAME,
                    HousesEntry.COLUMN_HOUSE_REGION,
                    HousesEntry.COLUMN_HOUSE_URL
            };

            return new android.content.CursorLoader(this, HousesEntry.CONTENT_URI, projection, null, null, null);
        }
        if (i == MAIN_BOOKS_FETCH) {
            //fetch books from api and put them into sql dB and lists
            AOIAFBooksAsyncTask booksTask = new AOIAFBooksAsyncTask(this) {
                @Override
                public void onResponseReceived(HashMap<String, String> result) {
                    mUrlToBookNamesDictionary = result;

                    mProgress = (ProgressBar) findViewById(R.id.main_progress);
                    mProgressText = (TextView) findViewById(R.id.main_loading_text);

                    mProgress.setVisibility(View.GONE);
                    mProgressText.setVisibility(View.GONE);

                    Collection<ASOIAFCharacter> charColleciton = mMasterUrlToCharacterDictionary.values();
                    mActiveCharacterList = new ArrayList<ASOIAFCharacter>(charColleciton);

                    updateUi(mActiveCharacterList, R.layout.action_bar_browse);

                }
            };
            booksTask.execute();
        }
        if (i == MAIN_BOOKS_LOADER) {
            //load books from sql tble into application lists
            String[] projection = {
                    BooksEntry._ID,
                    BooksEntry.COLUMN_BOOK_NAME,
                    BooksEntry.COLUMN_BOOK_URL
            };

            return new android.content.CursorLoader(this, BooksEntry.CONTENT_URI, projection, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == MAIN_CHARACTERS_LOADER) {
            //load chars from sql table into application lists

            if (mMasterUrlToCharacterDictionary == null){
                mMasterUrlToCharacterDictionary = new HashMap<>();
                mUrlToCharacterNameDictionary = new HashMap<>();
                mActiveCharacterList = new ArrayList<>();
            }

            mMasterUrlToCharacterDictionary.clear();
            mUrlToCharacterNameDictionary.clear();
            mActiveCharacterList.clear();

            if (data == null){
                return;
            }

            if (data.getCount() == 0){
                return;
            }



            int characterUrlIndex = data.getColumnIndex(CharacterEntry.COLUMN_CHARACTER_URL);
            int characterNameIndex = data.getColumnIndex(CharacterEntry.COLUMN_CHARACTER_NAME);
            int characterAliasesIndex = data.getColumnIndex(CharacterEntry.COLUMN_CHARACTER_ALIASES);
            int characterAllegiancesIndex = data.getColumnIndex(CharacterEntry.COLUMN_CHARACTER_ALLEGIANCES);
            int characterBooksIndex = data.getColumnIndex(CharacterEntry.COLUMN_CHARACTER_BOOKS);
            int characterBornIndex = data.getColumnIndex(CharacterEntry.COLUMN_CHARACTER_BORN);
            int characterCultureIndex = data.getColumnIndex(CharacterEntry.COLUMN_CHARACTER_CULTURE);
            int characterDiedIndex = data.getColumnIndex(CharacterEntry.COLUMN_CHARACTER_DIED);
            int characterFatherIndex = data.getColumnIndex(CharacterEntry.COLUMN_CHARACTER_FATHER);
            int characterGenderIndex = data.getColumnIndex(CharacterEntry.COLUMN_CHARACTER_GENDER);
            int characterMotherIndex = data.getColumnIndex(CharacterEntry.COLUMN_CHARACTER_MOTHER);
            int characterPlayedByIndex = data.getColumnIndex(CharacterEntry.COLUMN_CHARACTER_PLAYEDBY);
            int characterSeasonsIndex = data.getColumnIndex(CharacterEntry.COLUMN_CHARACTER_SEASONS);
            int characterTitlesIndex = data.getColumnIndex(CharacterEntry.COLUMN_CHARACTER_TITLES);
            int characterSpouseIndex = data.getColumnIndex(CharacterEntry.COLUMN_CHARACTER_SPOUSE);


            data.moveToFirst();

            while (!data.isAfterLast()){

                ASOIAFCharacter newCharacter = new ASOIAFCharacter(data.getString(characterUrlIndex),
                        data.getString(characterNameIndex), data.getString(characterGenderIndex),
                        data.getString(characterCultureIndex), data.getString(characterBornIndex),
                        data.getString(characterDiedIndex), characterStringToList(data.getString(characterTitlesIndex)),
                        characterStringToList(data.getString(characterAliasesIndex)), data.getString(characterFatherIndex),
                        data.getString(characterMotherIndex), data.getString(characterSpouseIndex),
                        characterStringToList(data.getString(characterAllegiancesIndex)),
                        characterStringToList(data.getString(characterBooksIndex)),
                        characterStringToList(data.getString(characterSeasonsIndex)),
                        characterStringToList(data.getString(characterPlayedByIndex)));

                mMasterUrlToCharacterDictionary.put(newCharacter.getmUrl(), newCharacter);
                mUrlToCharacterNameDictionary.put(newCharacter.getmUrl(), newCharacter.getmName());

                data.moveToNext();

            }



            Collection<ASOIAFCharacter> charColleciton = mMasterUrlToCharacterDictionary.values();
            ArrayList<ASOIAFCharacter> charList = new ArrayList<ASOIAFCharacter>(charColleciton);
            mActiveCharacterList = charList;


        }
        else if (loader.getId() == MAIN_HOUSES_LOADER) {
            //load houses from sql table into application lists


            if (mUrlToHouseNamesDictionary == null){
                mUrlToHouseNamesDictionary = new HashMap<>();
            }
            if (mHouseUrlToRegionDictionary == null){
                mHouseUrlToRegionDictionary = new HashMap<>();
            }
            mUrlToHouseNamesDictionary.clear();
            mHouseUrlToRegionDictionary.clear();

            if (data == null){
                return;
            }

            if (data.getCount() == 0){
                return;
            }


            int houseUrlIndex = data.getColumnIndex(HousesEntry.COLUMN_HOUSE_URL);
            int houseNameIndex = data.getColumnIndex(HousesEntry.COLUMN_HOUSE_NAME);
            int houseRegionIndex = data.getColumnIndex(HousesEntry.COLUMN_HOUSE_REGION);

            data.moveToFirst();
            while (!data.isAfterLast()){
                mUrlToHouseNamesDictionary.put(data.getString(houseUrlIndex), data.getString(houseNameIndex));
                mHouseUrlToRegionDictionary.put(data.getString(houseUrlIndex), data.getString(houseRegionIndex));
                data.moveToNext();
            }


        }
        else if (loader.getId() == MAIN_BOOKS_LOADER) {
            //load books from sql table into application lists

            mUrlToBookNamesDictionary.clear();

            if (data == null){
                return;
            }

            if (data.getCount() == 0){
                return;
            }

            int bookUrlIndex = data.getColumnIndex(BooksEntry.COLUMN_BOOK_URL);
            int bookNameIndex = data.getColumnIndex(BooksEntry.COLUMN_BOOK_NAME);

            data.moveToFirst();
            while (!data.isAfterLast()){
                mUrlToBookNamesDictionary.put(data.getString(bookUrlIndex), data.getString(bookNameIndex));
                data.moveToNext();
            }

            Log.e(LOG_TAG, "can you see this?!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

            mProgress.setVisibility(View.GONE);
            mProgressText.setVisibility(View.GONE);
            updateUi(mActiveCharacterList, R.layout.action_bar_browse);
        } else{
            updateUi(mActiveCharacterList, R.layout.action_bar_browse);
        }

    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {

    }

    private ArrayList<String> characterStringToList(String characterAttribute){
        if (characterAttribute == null || characterAttribute.length() == 0){
            return new ArrayList<String>();
        }
        String[] arrayTmp = characterAttribute.split(",");
        ArrayList<String> retList = new ArrayList<>(Arrays.asList(arrayTmp));
        for (int i = 0; i < retList.size(); i++){
            retList.set(i, retList.get(i).trim());
        }
        return retList;
    }

    /**
     * Main ListView listener launches CharacterDetailActivity using character at clicked position
     */
    private class MainListItemListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent characterIntent = new Intent(getApplicationContext(), CharacterDetailActivity.class);

            ASOIAFCharacter clickedCharacter = mActiveCharacterList.get(i);

            String charUrl = mActiveCharacterList.get(i).getmUrl();

            characterIntent.putExtra("Character URL", clickedCharacter.getmUrl());
            characterIntent.putExtra("Character Name", clickedCharacter.getmName());
            characterIntent.putExtra("Character Gender", clickedCharacter.getmGender());
            characterIntent.putExtra("Character Culture", clickedCharacter.getmCulture());
            characterIntent.putExtra("Character Born", clickedCharacter.getmYearBorn());
            characterIntent.putExtra("Character Died", clickedCharacter.getmYearDied());
            characterIntent.putExtra("Character Titles", clickedCharacter.getmTitles());
            characterIntent.putExtra("Character Aliases", clickedCharacter.getmAliases());
            characterIntent.putExtra("Character Father", clickedCharacter.getmFather());
            characterIntent.putExtra("Character Mother", clickedCharacter.getmMother());
            characterIntent.putExtra("Character Spouse", clickedCharacter.getmSpouse());
            characterIntent.putExtra("Character Allegiances", clickedCharacter.getmAllegiances());
            characterIntent.putExtra("Character Books", clickedCharacter.getmBooks());
            characterIntent.putExtra("Character Seasons", clickedCharacter.getmTVSeasons());
            characterIntent.putExtra("Character PlayedBy", clickedCharacter.getmPlayedBy());

            characterIntent.putExtra("Character URL to Name", mUrlToCharacterNameDictionary);
            characterIntent.putExtra("House URL to Region",mHouseUrlToRegionDictionary);
            characterIntent.putExtra("House URL to House", mUrlToHouseNamesDictionary);
            characterIntent.putExtra("Book URL to Book", mUrlToBookNamesDictionary);


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

    /**
     * Sync Button Click Listener asks user if they'd like to requery online database for updated list
     * of characters.
     */
    private class SyncButtonClickListener implements ImageView.OnClickListener {
        @Override
        public void onClick(View view) {

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    switch (i) {
                        case DialogInterface.BUTTON_POSITIVE:

                            mBanner.setVisibility(View.GONE);
                            mMainListView.setVisibility(View.GONE);
                            mProgress = (ProgressBar) findViewById(R.id.main_progress);
                            mProgressText = (TextView) findViewById(R.id.main_loading_text);
                            mProgress.setVisibility(View.VISIBLE);
                            mProgressText.setVisibility(View.VISIBLE);
                            mProgress.setMax(TOTAL_BOOKS + TOTAL_CHARACTERS + TOTAL_HOUSES);
                            mProgress.setProgress(0);


                            getLoaderManager().initLoader(MAIN_CHARACTERS_FETCH, null, MainListActivity.this);
                            getLoaderManager().initLoader(MAIN_HOUSES_FETCH, null, MainListActivity.this);
                            getLoaderManager().initLoader(MAIN_BOOKS_FETCH, null, MainListActivity.this);

                            dialog.dismiss();
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.dismiss();
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainListActivity.this, R.style.myDialog));
            builder.setMessage("Would you like to re-sync list of characters?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
        }
    }

}



