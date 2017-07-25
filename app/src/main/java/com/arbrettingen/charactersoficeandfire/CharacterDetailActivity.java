package com.arbrettingen.charactersoficeandfire;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
            };
            characterDetailTask.execute();
        }
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
