package com.arbrettingen.charactersoficeandfire;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * CharacterDetailActivity.java
 *
 * <P>Displays all available information about a character from the ASOIAF universe, based on the
 * character object at the position of the given Intent extra. All displayed information is extracted
 * from this character object's data.
 *
 * @author Alex Brettingen
 * @version 1.0
 */

public class CharacterDetailActivity extends AppCompatActivity {

    private static final String AOIAF_BOOK_REQUEST_URL =
            "https://www.anapioficeandfire.com/api/books/";

    private static final String AOIAF_HOUSE_REQUEST_URL =
            "https://www.anapioficeandfire.com/api/houses/";

    private String mUrl;
    private String mName;
    private String mGender;
    private String mCulture;
    private String mYearBorn;
    private String mYearDied;
    private ArrayList<String> mTitles = new ArrayList<>();
    private ArrayList<String> mAliases = new ArrayList<>();
    private String mFather;
    private String mMother;
    private String mSpouse;
    private ArrayList<String> mAllegiances = new ArrayList<>();
    private ArrayList<String> mBooks = new ArrayList<>();
    private ArrayList<String> mTVSeasons = new ArrayList<>();
    private ArrayList<String> mPlayedBy = new ArrayList<>();

    private HashMap<String, String> mUrlToCharacterNameDictionary = new HashMap<>();
    private HashMap<String, String> mUrlToBookNamesDictionary = new HashMap<>();
    private HashMap<String, String> mUrlToHouseNamesDictionary = new HashMap<>();
    /**
     * used to convert house objects into their region string, which is used to determine house icon
     */
    private HashMap<String, String> mHouseUrlToRegionDictionary = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_detail);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        editor.putBoolean("First Launch", false);
        editor.putBoolean("Resync Characters", false);
        editor.apply();

        Intent thisIntent = getIntent();

        if (thisIntent.hasExtra("House URL to Region")){
            mHouseUrlToRegionDictionary = (HashMap<String, String>) thisIntent.getExtras().get("House URL to Region");
        }
        if (thisIntent.hasExtra("House URL to House")){
            mUrlToHouseNamesDictionary = (HashMap<String, String>) thisIntent.getExtras().get("House URL to House");
        }
        if (thisIntent.hasExtra("Book URL to Book")){
            mUrlToBookNamesDictionary = (HashMap<String, String>) thisIntent.getExtras().get("Book URL to Book");
        }
        if (thisIntent.hasExtra("Character URL to Name")){
            mUrlToCharacterNameDictionary = (HashMap<String, String>) thisIntent.getExtras().get("Character URL to Name");
        }
        if (thisIntent.hasExtra("Character URL")){
            mUrl = thisIntent.getStringExtra("Character URL");
        }
        if (thisIntent.hasExtra("Character Name")){
            mName = thisIntent.getStringExtra("Character Name");
        }
        if (thisIntent.hasExtra("Character Gender")){
            mGender = thisIntent.getStringExtra("Character Gender");
        }
        if (thisIntent.hasExtra("Character Culture")){
            mCulture = thisIntent.getStringExtra("Character Culture");
        }
        if (thisIntent.hasExtra("Character Born")){
            mYearBorn = thisIntent.getStringExtra("Character Born");
        }
        if (thisIntent.hasExtra("Character Died")){
            mYearDied = thisIntent.getStringExtra("Character Died");
        }
        if (thisIntent.hasExtra("Character Titles")){
            mTitles = (ArrayList<String>) thisIntent.getExtras().get("Character Titles");
        }
        if (thisIntent.hasExtra("Character Aliases")){
            mAliases = (ArrayList<String>) thisIntent.getExtras().get("Character Aliases");
        }
        if (thisIntent.hasExtra("Character Father")){
            mFather = mUrlToCharacterNameDictionary.get(thisIntent.getStringExtra("Character Father"));
        }
        if (thisIntent.hasExtra("Character Mother")){
            mMother = mUrlToCharacterNameDictionary.get(thisIntent.getStringExtra("Character Mother"));
        }
        if (thisIntent.hasExtra("Character Spouse")){
            mSpouse = mUrlToCharacterNameDictionary.get(thisIntent.getStringExtra("Character Spouse"));
        }
        if (thisIntent.hasExtra("Character Allegiances")){
            mAllegiances = (ArrayList<String>) thisIntent.getExtras().get("Character Allegiances");
        }
        if (thisIntent.hasExtra("Character Books")){
            mBooks = (ArrayList<String>) thisIntent.getExtras().get("Character Books");
        }
        if (thisIntent.hasExtra("Character Seasons")){
            mTVSeasons = (ArrayList<String>) thisIntent.getExtras().get("Character Seasons");
        }
        if (thisIntent.hasExtra("Character PlayedBy")){
            mPlayedBy = (ArrayList<String>) thisIntent.getExtras().get("Character PlayedBy");
        }
        updateUi();
    }

    private void updateUi() {

        //initialize views
        LinearLayout mParentLayout = (LinearLayout) findViewById(R.id.detail_parent_layout);
        mParentLayout.setVisibility(View.VISIBLE);

        TextView mNameText = (TextView) findViewById(R.id.detail_name_text);
        TextView mGenderText = (TextView) findViewById(R.id.detail_gender_txt);
        LinearLayout mAllegiancesLayout = (LinearLayout) findViewById(R.id.detail_allegiances_layout);
        TextView mAllegiancesText = (TextView) findViewById(R.id.detail_allegiances_text);
        LinearLayout mAliasesLayout = (LinearLayout) findViewById(R.id.detail_alias_layout);
        TextView mAliasesText = (TextView) findViewById(R.id.detail_alias_text);
        LinearLayout mBornLayout = (LinearLayout) findViewById(R.id.detail_born_layout);
        TextView mBornText = (TextView) findViewById(R.id.detail_born_text);
        LinearLayout mDiedLayout = (LinearLayout) findViewById(R.id.detail_died_layout);
        TextView mDiedText = (TextView) findViewById(R.id.detail_died_text);
        LinearLayout mTitlesLayout = (LinearLayout) findViewById(R.id.detail_titles_layout);
        TextView mTitlesText = (TextView) findViewById(R.id.detail_titles_text);
        LinearLayout mFatherLayout = (LinearLayout) findViewById(R.id.detail_father_layout);
        TextView mFatherText = (TextView) findViewById(R.id.detail_father_text);
        LinearLayout mMotherLayout = (LinearLayout) findViewById(R.id.detail_mother_layout);
        TextView mMotherText = (TextView) findViewById(R.id.detail_mother_text);
        LinearLayout mSpouseLayout = (LinearLayout) findViewById(R.id.detail_spouse_layout);
        TextView mSpouseText = (TextView) findViewById(R.id.detail_spouse_text);
        LinearLayout mCultureLayout = (LinearLayout) findViewById(R.id.detail_culture_layout);
        TextView mCultureText = (TextView) findViewById(R.id.detail_culture_text);
        LinearLayout mBooksLayout = (LinearLayout) findViewById(R.id.detail_books_layout);
        TextView mBooksText = (TextView) findViewById(R.id.detail_books_text);
        LinearLayout mSeasonsLayout = (LinearLayout) findViewById(R.id.detail_seasons_layout);
        TextView mSeasonsText = (TextView) findViewById(R.id.detail_seasons_text);
        LinearLayout mPlayedByLayout = (LinearLayout) findViewById(R.id.detail_played_layout);
        TextView mPlayedByText = (TextView) findViewById(R.id.detail_played_text);

        mNameText.setText(mName);
        mGenderText.setText(mGender);

        if (mAllegiances.size() > 0) {
            String allegiances = "";
            for (int i = 0; i < mAllegiances.size(); i++) {
                String allegiance = mAllegiances.get(i);
                if (!allegiance.contains("anapioficeandfire")){
                    allegiance = repairAllegianceUrl(allegiance);
                }
                allegiances = allegiances + mUrlToHouseNamesDictionary.get(allegiance) + ", ";
            }
            if (allegiances.length() > 0) {
                allegiances = allegiances.substring(0, allegiances.length() - 2);
            }
            if (allegiances.equals("")) {
                mAllegiancesLayout.setVisibility(View.GONE);
            } else {
                mAllegiancesText.setText(allegiances);
            }
        } else {
            mAllegiancesLayout.setVisibility(View.GONE);
        }

        if (mAliases.size() > 0) {
            String aliases = "";
            for (int i = 0; i < mAliases.size(); i++) {
                aliases = aliases + mAliases.get(i) + ", ";
            }
            if (aliases.length() > 0) {
                aliases = aliases.substring(0, aliases.length() - 2);
            }
            if (aliases.equals("")) {
                mAliasesLayout.setVisibility(View.GONE);
            } else {
                mAliasesText.setText(aliases);
            }
        } else {
            mAliasesLayout.setVisibility(View.GONE);
        }

        if (mYearBorn.equals("")) {
            mBornLayout.setVisibility(View.GONE);
        } else {
            mBornText.setText(mYearBorn);
        }

        if (mYearDied.equals("")) {
            mDiedLayout.setVisibility(View.GONE);
        } else {
            mDiedText.setText(mYearDied);
        }

        if (mTitles.size() > 0) {
            String titles = "";
            for (int i = 0; i < mTitles.size(); i++) {
                titles = titles + mTitles.get(i) + ", ";
            }
            if (titles.length() > 0) {
                titles = titles.substring(0, titles.length() - 2);
            }
            if (titles.equals("")) {
                mTitlesLayout.setVisibility(View.GONE);
            } else {
                mTitlesText.setText(titles);
            }
        } else {
            mTitlesLayout.setVisibility(View.GONE);
        }

        if (mFather == null || mFather.equals("")) {
            mFatherLayout.setVisibility(View.GONE);
        } else {
            mFatherText.setText(mFather);
        }
        if (mMother == null || mMother.equals("")) {
            mMotherLayout.setVisibility(View.GONE);
        } else {
            mMotherText.setText(mMother);
        }
        if (mSpouse == null || mSpouse.equals("")) {
            mSpouseLayout.setVisibility(View.GONE);
        } else {
            mSpouseText.setText(mSpouse);
        }

        if (mCulture.equals("")) {
            mCultureLayout.setVisibility(View.GONE);
        } else {
            mCultureText.setText(mCulture);
        }

        if (mBooks.size() > 0) {
            String books = "";
            for (int i = 0; i < mBooks.size(); i++) {
                String book = mBooks.get(i);
                if (!book.contains("anapioficeandfire")){
                    book = repairBookUrl(book);
                }
                books = books + mUrlToBookNamesDictionary.get(book) + ", ";
            }
            if (books.length() > 0) {
                books = books.substring(0, books.length() - 2);
            }
            if (books.equals("")) {
                mBooksLayout.setVisibility(View.GONE);
            } else {
                mBooksText.setText(books);
            }
        } else {
            mBooksLayout.setVisibility(View.GONE);
        }

        if (mTVSeasons.size() > 0) {
            String seasons = "";
            for (int i = 0; i < mTVSeasons.size(); i++) {
                seasons = seasons + mTVSeasons.get(i) + ", ";
            }
            if (seasons.length() > 0) {
                seasons = seasons.substring(0, seasons.length() - 2);
            }
            if (seasons.equals("")) {
                mSeasonsLayout.setVisibility(View.GONE);
            } else {
                mSeasonsText.setText(seasons);
            }
        } else {
            mSeasonsLayout.setVisibility(View.GONE);
        }

        if (mPlayedBy.size() > 0) {
            String playedBy = "";
            for (int i = 0; i < mPlayedBy.size(); i++) {
                playedBy = playedBy + mPlayedBy.get(i) + ", ";
            }
            if (playedBy.length() > 0) {
                playedBy = playedBy.substring(0, playedBy.length() - 2);
            }
            if (playedBy.equals("")) {
                mPlayedByLayout.setVisibility(View.GONE);
            } else {
                mPlayedByText.setText(playedBy);
            }
        } else {
            mPlayedByLayout.setVisibility(View.GONE);
        }

    }

    private String repairAllegianceUrl(String allegianceUrl){
        String ret = allegianceUrl.substring(19);
        return AOIAF_HOUSE_REQUEST_URL + ret;
    }

    private String repairBookUrl(String bookUrl){
        String ret = bookUrl.substring(18);
        return AOIAF_BOOK_REQUEST_URL + ret;
    }

    @Override
    public void onBackPressed() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("First Launch", false);
        editor.putBoolean("Resync Characters", false);
        editor.putBoolean("Back Pressed", true);
        editor.apply();
        Intent i = new Intent(getApplicationContext(), MainListActivity.class);
        startActivity(i);
    }
}
