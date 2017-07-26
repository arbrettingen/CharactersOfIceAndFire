package com.arbrettingen.charactersoficeandfire;

import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * ASOIAFCharacter.java
 *
 * <P>{@ASOIAFCharacter} represents a Character from the ASOIAF universe. It holds the details
 * of that character such as name, allegiances, titles, etc.
 *
 * @author Alex Brettingen
 * @version 1.0
 */

public class ASOIAFCharacter implements Comparable<ASOIAFCharacter> {

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

    public ASOIAFCharacter(String url, String name, String gender, String culture, String yearBorn,
                           String yearDied, ArrayList<String> titles, ArrayList<String> aliases,
                           String father, String mother, String spouse,
                           ArrayList<String> allegiances, ArrayList<String> books,
                           ArrayList<String> tVSeasons, ArrayList<String> playedBy) {
        mUrl = url;
        mName = name;
        mGender = gender;
        mCulture = culture;
        mYearBorn = yearBorn;
        mYearDied = yearDied;
        mTitles = titles;
        mAliases = aliases;
        mFather = father;
        mMother = mother;
        mSpouse = spouse;
        mAllegiances = allegiances;
        mBooks = books;
        mTVSeasons = tVSeasons;
        mPlayedBy = playedBy;
    }

    public ASOIAFCharacter(String url, String name, ArrayList<String> aliases,
                           ArrayList<String> allegiances){
        mUrl = url;
        mName = name;
        mGender = "";
        mCulture = "";
        mYearBorn = "";
        mYearDied = "";
        mTitles = new ArrayList<>();
        mAliases = aliases;
        mFather = "";
        mMother = "";
        mSpouse = "";
        mAllegiances = allegiances;
        mBooks = new ArrayList<>();
        mTVSeasons = new ArrayList<>();
        mPlayedBy = new ArrayList<>();
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmGender() {
        return mGender;
    }

    public void setmGender(String mGender) {
        this.mGender = mGender;
    }

    public String getmCulture() {
        return mCulture;
    }

    public void setmCulture(String mCulture) {
        this.mCulture = mCulture;
    }

    public String getmYearBorn() {
        return mYearBorn;
    }

    public void setmYearBorn(String mYearBorn) {
        this.mYearBorn = mYearBorn;
    }

    public String getmYearDied() {
        return mYearDied;
    }

    public void setmYearDied(String mYearDied) {
        this.mYearDied = mYearDied;
    }

    public ArrayList<String> getmTitles() {
        return mTitles;
    }

    public void setmTitles(ArrayList<String> mTitles) {
        this.mTitles = mTitles;
    }

    public ArrayList<String> getmAliases() {
        return mAliases;
    }

    public void setmAliases(ArrayList<String> mAliases) {
        this.mAliases = mAliases;
    }

    public String getmFather() {
        return mFather;
    }

    public void setmFather(String mFather) {
        this.mFather = mFather;
    }

    public String getmMother() {
        return mMother;
    }

    public void setmMother(String mMother) {
        this.mMother = mMother;
    }

    public String getmSpouse() {
        return mSpouse;
    }

    public void setmSpouse(String mSpouse) {
        this.mSpouse = mSpouse;
    }

    public ArrayList<String> getmAllegiances() {
        return mAllegiances;
    }

    public void setmAllegiances(ArrayList<String> mAllegiances) {
        this.mAllegiances = mAllegiances;
    }

    public ArrayList<String> getmBooks() {
        return mBooks;
    }

    public void setmBooks(ArrayList<String> mBooks) {
        this.mBooks = mBooks;
    }

    public ArrayList<String> getmTVSeasons() {
        return mTVSeasons;
    }

    public void setmTVSeasons(ArrayList<String> mTVSeasons) {
        this.mTVSeasons = mTVSeasons;
    }

    public ArrayList<String> getmPlayedBy() {
        return mPlayedBy;
    }

    public void setmPlayedBy(ArrayList<String> mPlayedBy) {
        this.mPlayedBy = mPlayedBy;
    }


    @Override
    public int compareTo(@NonNull ASOIAFCharacter asoiafCharacter) {
        return this.getmName().compareTo(asoiafCharacter.getmName());
    }
}
