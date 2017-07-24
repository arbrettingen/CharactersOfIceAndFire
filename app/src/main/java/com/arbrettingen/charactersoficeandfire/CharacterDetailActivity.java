package com.arbrettingen.charactersoficeandfire;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    private ASOIAFCharacter mCharacter;
    private String father;
    private String mother;
    private String spouse;

    //views
    private LinearLayout mParentLayout;
    private TextView mNameText;
    private TextView mGenderText;
    private LinearLayout mAllegiancesLayout;
    private TextView mAllegiancesText;
    private LinearLayout mAliasesLayout;
    private TextView mAliasesText;
    private LinearLayout mBornLayout;
    private TextView mBornText;
    private LinearLayout mDiedLayout;
    private TextView mDiedText;
    private LinearLayout mTitlesLayout;
    private TextView mTitlesText;
    private LinearLayout mFatherLayout;
    private TextView mFatherText;
    private LinearLayout mMotherLayout;
    private TextView mMotherText;
    private LinearLayout mSpouseLayout;
    private TextView mSpouseText;
    private LinearLayout mCultureLayout;
    private TextView mCultureText;
    private LinearLayout mBooksLayout;
    private TextView mBooksText;
    private LinearLayout mSeasonsLayout;
    private TextView mSeasonsText;
    private LinearLayout mPlayedByLayout;
    private TextView mPlayedByText;

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

        Intent thisIntent = getIntent();

        for (String key : thisIntent.getExtras().keySet()) {
            Object o = thisIntent.getExtras().get(key);
        }

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

        String charUrl = (String) thisIntent.getExtras().get("Character Url");

        if (thisIntent.hasExtra("Character Url")) {
            AOIAFCharacterDetailAsyncTask characterDetailTask = new AOIAFCharacterDetailAsyncTask(getApplicationContext(),
                    charUrl , mHouseUrlToRegionDictionary,
                    mUrlToHouseNamesDictionary, mUrlToBookNamesDictionary) {
                @Override
                public void onResponseReceived(ASOIAFCharacter result) {
                    mCharacter = result;
                    if (mCharacter == null) {
                        Intent i = new Intent(getApplicationContext(), MainListActivity.class);
                        startActivity(i);
                    }
                    //any family data stored is currently in url format, must convert to actual names
                    if (mCharacter.getmFather() != null && !mCharacter.getmFather().equals("")) {
                        if (mUrlToCharacterNameDictionary.containsKey(mCharacter.getmFather())) {
                            father = mUrlToCharacterNameDictionary.get(mCharacter.getmFather());
                        }
                    }
                    if (mCharacter.getmMother() != null && !mCharacter.getmMother().equals("")) {
                        if (mUrlToCharacterNameDictionary.containsKey(mCharacter.getmMother())) {
                            mother = mUrlToCharacterNameDictionary.get(mCharacter.getmMother());
                        }
                    }
                    if (mCharacter.getmSpouse() != null && !mCharacter.getmSpouse().equals("")) {
                        if (mUrlToCharacterNameDictionary.containsKey(mCharacter.getmSpouse())) {
                            spouse = mUrlToCharacterNameDictionary.get(mCharacter.getmSpouse());
                        }
                    }

                    updateUi();

                }
            };
            characterDetailTask.execute();
        }
    }

    private void updateUi() {

        //initialize views
        mParentLayout = (LinearLayout) findViewById(R.id.detail_parent_layout);
        mParentLayout.setVisibility(View.VISIBLE);

        mNameText = (TextView) findViewById(R.id.detail_name_text);
        mGenderText = (TextView) findViewById(R.id.detail_gender_txt);
        mAllegiancesLayout = (LinearLayout) findViewById(R.id.detail_allegiances_layout);
        mAllegiancesText = (TextView) findViewById(R.id.detail_allegiances_text);
        mAliasesLayout = (LinearLayout) findViewById(R.id.detail_alias_layout);
        mAliasesText = (TextView) findViewById(R.id.detail_alias_text);
        mBornLayout = (LinearLayout) findViewById(R.id.detail_born_layout);
        mBornText = (TextView) findViewById(R.id.detail_born_text);
        mDiedLayout = (LinearLayout) findViewById(R.id.detail_died_layout);
        mDiedText = (TextView) findViewById(R.id.detail_died_text);
        mTitlesLayout = (LinearLayout) findViewById(R.id.detail_titles_layout);
        mTitlesText = (TextView) findViewById(R.id.detail_titles_text);
        mFatherLayout = (LinearLayout) findViewById(R.id.detail_father_layout);
        mFatherText = (TextView) findViewById(R.id.detail_father_text);
        mMotherLayout = (LinearLayout) findViewById(R.id.detail_mother_layout);
        mMotherText = (TextView) findViewById(R.id.detail_mother_text);
        mSpouseLayout = (LinearLayout) findViewById(R.id.detail_spouse_layout);
        mSpouseText = (TextView) findViewById(R.id.detail_spouse_text);
        mCultureLayout = (LinearLayout) findViewById(R.id.detail_culture_layout);
        mCultureText = (TextView) findViewById(R.id.detail_culture_text);
        mBooksLayout = (LinearLayout) findViewById(R.id.detail_books_layout);
        mBooksText = (TextView) findViewById(R.id.detail_books_text);
        mSeasonsLayout = (LinearLayout) findViewById(R.id.detail_seasons_layout);
        mSeasonsText = (TextView) findViewById(R.id.detail_seasons_text);
        mPlayedByLayout = (LinearLayout) findViewById(R.id.detail_played_layout);
        mPlayedByText = (TextView) findViewById(R.id.detail_played_text);

        mNameText.setText(mCharacter.getmName());
        mGenderText.setText(mCharacter.getmGender());

        if (mCharacter.getmAllegiances().size() > 0) {
            String allegiances = "";
            for (int i = 0; i < mCharacter.getmAllegiances().size(); i++) {
                String allegiance = mCharacter.getmAllegiances().get(i);
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

        if (mCharacter.getmAliases().size() > 0) {
            String aliases = "";
            for (int i = 0; i < mCharacter.getmAliases().size(); i++) {
                aliases = aliases + mCharacter.getmAliases().get(i) + ", ";
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

        if (mCharacter.getmYearBorn().equals("")) {
            mBornLayout.setVisibility(View.GONE);
        } else {
            mBornText.setText(mCharacter.getmYearBorn());
        }

        if (mCharacter.getmYearDied().equals("")) {
            mDiedLayout.setVisibility(View.GONE);
        } else {
            mDiedText.setText(mCharacter.getmYearDied());
        }

        if (mCharacter.getmTitles().size() > 0) {
            String titles = "";
            for (int i = 0; i < mCharacter.getmTitles().size(); i++) {
                titles = titles + mCharacter.getmTitles().get(i) + ", ";
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

        if (father != null) {
            mFatherText.setText(father);
        } else {
            mFatherLayout.setVisibility(View.GONE);
        }
        if (mother != null) {
            mMotherText.setText(mother);
        } else {
            mMotherLayout.setVisibility(View.GONE);
        }
        if (spouse != null) {
            mSpouseText.setText(spouse);
        } else {
            mSpouseLayout.setVisibility(View.GONE);
        }

        if (mCharacter.getmCulture().equals("")) {
            mCultureLayout.setVisibility(View.GONE);
        } else {
            mCultureText.setText(mCharacter.getmCulture());
        }

        if (mCharacter.getmBooks().size() > 0) {
            String books = "";
            for (int i = 0; i < mCharacter.getmBooks().size(); i++) {
                String book = mCharacter.getmBooks().get(i);
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

        if (mCharacter.getmTVSeasons().size() > 0) {
            String seasons = "";
            for (int i = 0; i < mCharacter.getmTVSeasons().size(); i++) {
                seasons = seasons + mCharacter.getmTVSeasons().get(i) + ", ";
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

        if (mCharacter.getmPlayedBy().size() > 0) {
            String playedBy = "";
            for (int i = 0; i < mCharacter.getmPlayedBy().size(); i++) {
                playedBy = playedBy + mCharacter.getmPlayedBy().get(i) + ", ";
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

}
