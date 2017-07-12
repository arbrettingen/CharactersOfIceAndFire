package com.arbrettingen.charactersoficeandfire;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;

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
    public static final Integer TOTAL_BOOKS = 12;
    public static final Integer TOTAL_CHARACTERS = 2138;
    public static final Integer TOTAL_HOUSES = 444;
    public static final Integer ITEMS_PER_PAGE = 50;
    /**
     * URL to query the api of Ice and Fire dataset for character information
     */
    private static final String AOIAF_ALL_CHARACTERS_REQUEST_URL =
            "https://www.anapioficeandfire.com/api/characters?pageSize=50&page=";
    /**
     * URL to query the api of Ice and Fire dataset for house information
     */
    private static final String AOIAF_ALL_HOUSES_REQUEST_URL =
            "https://www.anapioficeandfire.com/api/houses?pageSize=50&page=";
    /**
     * URL to query the api of Ice and Fire dataset for house information
     */
    private static final String AOIAF_ALL_BOOKS_REQUEST_URL =
            "https://www.anapioficeandfire.com/api/books?pageSize=" + TOTAL_BOOKS;
    private static ArrayList<ASOIAFCharacter> mMasterCharacterList;
    public static ArrayList<ASOIAFCharacter> mActiveCharacterList;
    private static String[] mBookNames = new String[TOTAL_BOOKS];
    private static String[] mHouseNames = new String[TOTAL_HOUSES];
    private static String[] mCharNames = new String[TOTAL_CHARACTERS];
    /**
     * used to convert house objects into their region string, which is used to determine house icon
     */
    private static String[] mHouseRegions = new String[TOTAL_HOUSES];
    private ProgressBar mProgress;
    private TextView mProgressText;
    private ActionBar mActionBar;


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

        if (mMasterCharacterList == null) {
            mMasterCharacterList = new ArrayList<>();
            AOIAFAsyncTask charactersTask = new AOIAFAsyncTask();
            charactersTask.execute();
        }
    }

    /**
     * Update the screen to display information from the given {@link ASOIAFCharacter}.
     */
    private void updateUi(ArrayList<ASOIAFCharacter> characterList, int actionBar) {
        ListView mMainListView = (ListView) findViewById(R.id.main_list_list);
        ASOIAFCharacterAdapter mACharacterAdapter = new ASOIAFCharacterAdapter(getApplicationContext(), R.layout.main_list_item, characterList, mHouseRegions);

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

    public static String convertUrlToAllegianceName(String houseUrl) {
        int pos = Integer.parseInt(houseUrl.substring(45)) - 1;
        return mHouseNames[pos];
    }

    public static String convertUrlToCharacterName(String bookUrl) {
        int pos = Integer.parseInt(bookUrl.substring(49)) - 1;
        return mCharNames[pos];
    }

    private String convertUrlToBookName(String bookUrl) {
        int pos = Integer.parseInt(bookUrl.substring(44)) - 1;
        return mBookNames[pos];
    }

    /**
     * {@link AsyncTask} to perform the network request on a background thread, and then
     * update the UI with the list of characters.
     */
    public class AOIAFAsyncTask extends AsyncTask<URL, Integer, ArrayList<ASOIAFCharacter>> {

        @Override
        protected ArrayList<ASOIAFCharacter> doInBackground(URL... urls) {

            URL url = createUrl(AOIAF_ALL_BOOKS_REQUEST_URL);


            //Extract Books information first
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
                extractFromBooksJson(jsonResponse);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem making the HTTP request.", e);
            }


            //Exctract houses information second
            Integer numPages = (TOTAL_HOUSES / ITEMS_PER_PAGE) + 1;

            for (int i = 1; i <= numPages; i++) {

                url = createUrl(AOIAF_ALL_HOUSES_REQUEST_URL + String.valueOf(i));

                // Perform HTTP request to the URL and receive a JSON response back
                try {
                    jsonResponse = makeHttpRequest(url);
                    extractFromHousesJson(jsonResponse, i);
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Problem making the HTTP request.", e);
                }

            }

            ArrayList<ASOIAFCharacter> charactersList = new ArrayList<>();

            numPages = (TOTAL_CHARACTERS / ITEMS_PER_PAGE) + 1;

            // Extract character information into ASOIAFCharacter objects
            for (int i = 1; i <= numPages; i++) {

                url = createUrl(AOIAF_ALL_CHARACTERS_REQUEST_URL + String.valueOf(i));

                // Perform HTTP request to the URL and receive a JSON response back
                try {
                    jsonResponse = makeHttpRequest(url);
                    charactersList.addAll(extractFromCharactersJson(jsonResponse, i));
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Problem making the HTTP request.", e);
                }
            }


            // Return {@link ASOIAFCharacter} objects list as the result fo the {@link AOIAFAsyncTask}
            return charactersList;
        }

        /**
         * Update the screen with the given progress received from updateProgress()
         * {@link AOIAFAsyncTask}).
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            mProgress.setProgress(values[0]);
            super.onProgressUpdate(values);
        }

        /**
         * Update the screen with the given characterlist (which was the result of the
         * {@link AOIAFAsyncTask}).
         */
        @Override
        protected void onPostExecute(ArrayList<ASOIAFCharacter> characterList) {
            if (characterList == null) {
                return;
            }
            mMasterCharacterList.clear();
            mMasterCharacterList = characterList;
            Collections.sort(mMasterCharacterList);

            mProgressText.setVisibility(View.GONE);
            mProgress.setVisibility(View.GONE);

            updateUi(mMasterCharacterList, R.layout.action_bar_browse);
        }

        /**
         * Returns new URL object from the given string URL.
         */
        private URL createUrl(String stringUrl) {
            URL url;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;
            }
            return url;
        }

        /**
         * Make an HTTP request to the given URL and return a String as the response.
         */
        private String makeHttpRequest(URL url) throws IOException {

            String jsonResponse = "";

            // If the URL is null, then return early.
            if (url == null) {
                return jsonResponse;
            }

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;

            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(25000 /* milliseconds */);
                urlConnection.setConnectTimeout(30000 /* milliseconds */);
                urlConnection.connect();

                // If the request was successful (response code 200),
                // then read the input stream and parse the response.
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem retrieving the ASOIAF Character JSON results.", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    // function must handle java.io.IOException here
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        /**
         * Convert the {@link InputStream} into a String which contains the
         * whole JSON response from the server.
         */
        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        /**
         * Populate regions and house name array by parsing out information
         * about the houses from the input ASOIAFJSON string.
         */
        private void extractFromHousesJson(String housesJSON, int pageNum) {
            // If the JSON string is empty or null, then return early.
            if (TextUtils.isEmpty(housesJSON)) {
                return;
            }
            try {

                JSONArray baseJsonResponse = new JSONArray(housesJSON);

                int currHouse = (pageNum - 1) * ITEMS_PER_PAGE;

                for (int i = 0; i < baseJsonResponse.length(); i++) {
                    JSONObject houseJSONObject = baseJsonResponse.getJSONObject(i);
                    mHouseRegions[currHouse] = houseJSONObject.getString("region");
                    mHouseNames[currHouse] = houseJSONObject.getString("name");
                    currHouse++;
                    publishProgress(TOTAL_BOOKS + currHouse);
                }

            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the characters JSON results", e);
            }

        }

        /**
         * Populate books array by parsing out information
         * about the books from the input booksJSON string.
         */
        private void extractFromBooksJson(String booksJSON) {
            // If the JSON string is empty or null, then return early.
            if (TextUtils.isEmpty(booksJSON)) {
                return;
            }
            try {
                JSONArray baseJsonResponse = new JSONArray(booksJSON);

                for (int i = 0; i < baseJsonResponse.length(); i++) {
                    JSONObject bookJSONObject = baseJsonResponse.getJSONObject(i);
                    mBookNames[i] = bookJSONObject.getString("name");
                    publishProgress(i + 1);
                }

            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the characters JSON results", e);
            }

        }

        /**
         * Return a {@link ASOIAFCharacter} list by parsing out information
         * about the characters from the input ASOIAFJSON string.
         */
        private ArrayList<ASOIAFCharacter> extractFromCharactersJson(String charactersJSON, int pageNum) {
            // If the JSON string is empty or null, then return early.
            ArrayList<ASOIAFCharacter> charactersList = new ArrayList<>();

            if (TextUtils.isEmpty(charactersJSON)) {
                return charactersList;
            }

            try {

                JSONArray baseJsonResponse = new JSONArray(charactersJSON);

                int currChar = (pageNum - 1) * ITEMS_PER_PAGE;

                for (int i = 0; i < baseJsonResponse.length(); i++) {
                    JSONObject characterJSONObject = baseJsonResponse.getJSONObject(i);

                    String name = characterJSONObject.getString("name");
                    mCharNames[currChar] = name;

                    if (!name.equals("")) {
                        ArrayList<String> titles = new ArrayList<>();
                        ArrayList<String> aliases = new ArrayList<>();
                        ArrayList<String> allegiances = new ArrayList<>();
                        ArrayList<String> books = new ArrayList<>();
                        ArrayList<String> tVSeasons = new ArrayList<>();
                        ArrayList<String> playedBy = new ArrayList<>();

                        JSONArray titlesJSONArray = characterJSONObject.getJSONArray("titles");
                        if (titlesJSONArray.length() > 0) {
                            for (int j = 0; j < titlesJSONArray.length(); j++) {
                                titles.add(titlesJSONArray.getString(j));
                            }
                        }
                        JSONArray aliasesJSONArray = characterJSONObject.getJSONArray("aliases");
                        if (aliasesJSONArray.length() > 0) {
                            for (int j = 0; j < aliasesJSONArray.length(); j++) {
                                aliases.add(aliasesJSONArray.getString(j));
                            }
                        }
                        JSONArray allegiancesJSONArray = characterJSONObject.getJSONArray("allegiances");
                        if (allegiancesJSONArray.length() > 0) {
                            for (int j = 0; j < allegiancesJSONArray.length(); j++) {
                                allegiances.add(allegiancesJSONArray.getString(j));
                            }
                        }
                        JSONArray booksJSONArray = characterJSONObject.getJSONArray("books");
                        if (booksJSONArray.length() > 0) {
                            for (int j = 0; j < booksJSONArray.length(); j++) {
                                books.add(convertUrlToBookName(booksJSONArray.getString(j)));
                            }
                        }
                        JSONArray tVSeasonsJSONArray = characterJSONObject.getJSONArray("tvSeries");
                        if (tVSeasonsJSONArray.length() > 0) {
                            for (int j = 0; j < tVSeasonsJSONArray.length(); j++) {
                                tVSeasons.add(tVSeasonsJSONArray.getString(j));
                            }
                        }
                        JSONArray playedByJSONArray = characterJSONObject.getJSONArray("playedBy");
                        if (playedByJSONArray.length() > 0) {
                            for (int j = 0; j < playedByJSONArray.length(); j++) {
                                playedBy.add(playedByJSONArray.getString(j));
                            }
                        }
                        String url = characterJSONObject.getString("url");
                        String gender = characterJSONObject.getString("gender");
                        String culture = characterJSONObject.getString("culture");
                        String yearBorn = characterJSONObject.getString("born");
                        String yearDied = characterJSONObject.getString("died");
                        String father = characterJSONObject.getString("father");
                        String mother = characterJSONObject.getString("mother");
                        String spouse = characterJSONObject.getString("spouse");


                        ASOIAFCharacter newCharacter = new ASOIAFCharacter(url, name, gender,
                                culture, yearBorn, yearDied, titles, aliases, father, mother,
                                spouse, allegiances, books, tVSeasons, playedBy);

                        charactersList.add(newCharacter);
                        currChar++;
                        publishProgress(TOTAL_BOOKS + TOTAL_HOUSES + currChar);
                    }
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the characters JSON results", e);
            }
            return charactersList;
        }
    }

    /**
     * Main ListView listener launches CharacterDetailActivity using character at clicked position
     */
    private class MainListItemListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent characterIntent = new Intent(getApplicationContext(), CharacterDetailActivity.class);
            characterIntent.putExtra("Character Position", i);
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
                    ArrayList<ASOIAFCharacter> mSearchedCharacters = new ArrayList<>();
                    for (int i = 0; i < mMasterCharacterList.size(); i++) {
                        if (mMasterCharacterList.get(i).getmName().toLowerCase().contains(s.toString().toLowerCase())) {
                            mSearchedCharacters.add(mMasterCharacterList.get(i));
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
                InputMethodManager imm1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm1.hideSoftInputFromWindow(mSearchEditText.getWindowToken(), 0);
                updateUi(mMasterCharacterList, R.layout.action_bar_browse);
            });
        }
    }
}
