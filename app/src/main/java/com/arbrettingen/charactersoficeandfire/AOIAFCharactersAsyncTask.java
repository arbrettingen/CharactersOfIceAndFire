package com.arbrettingen.charactersoficeandfire;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
import java.util.HashMap;

/**
 * AOIAFCharactersAsyncTask.java
 *
 * <P>Queries https://www.anapioficeandfire.com/ via http request for some information about all
 * characters listed, given as a JSON response and processed into a map of Character objects.
 *
 * @author Alex Brettingen
 * @version 1.0
 */

public abstract class AOIAFCharactersAsyncTask
        extends AsyncTask<URL, Integer, HashMap<String, ASOIAFCharacter>> {

    private static final String LOG_TAG = MainListActivity.class.getSimpleName();
    private static final Integer TOTAL_BOOKS = 12;
    private static final Integer TOTAL_CHARACTERS = 2138;
    private static final Integer TOTAL_HOUSES = 444;
    private static final Integer ITEMS_PER_PAGE = 50;

    private HashMap<String, String> mUrlToCharacterNameDictionary = new HashMap<>();

    private ProgressBar mProgress;

    private String mErrorTxt;

    private Activity mContext;

    /**
     * URL to query the api of Ice and Fire dataset for character information
     */
    private static final String AOIAF_ALL_CHARACTERS_REQUEST_URL =
            "https://www.anapioficeandfire.com/api/characters?pageSize=50&page=";

    public AOIAFCharactersAsyncTask(Activity myContext)
    {
        mProgress = myContext.findViewById(R.id.main_progress);
        mContext = myContext;
    }

    @Override
    protected HashMap<String, ASOIAFCharacter> doInBackground(URL... urls) {

        HashMap<String, ASOIAFCharacter> characterDictionary = new HashMap<>();


        int numPages = (TOTAL_CHARACTERS / ITEMS_PER_PAGE) + 1;

        // Extract character information into ASOIAFCharacter objects
        for (int i = 1; i <= numPages; i++) {

            URL url = createUrl(AOIAF_ALL_CHARACTERS_REQUEST_URL + String.valueOf(i));

            // Perform HTTP request to the URL and receive a JSON response back
            try {
                String jsonResponse = makeHttpRequest(url);
                characterDictionary.putAll(extractFromCharactersJson(jsonResponse, i));
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem making the HTTP request.", e);
                mErrorTxt = "Problem making the HTTP request. Please restart the application.";
                publishProgress(-1);
            }
        }


        // Return {@link ASOIAFCharacter} objects list as the result fo the {@link AOIAFAsyncTask}
        return characterDictionary;
    }

    /**
     * Update the screen with the given progress received from updateProgress().
     */
    @Override
    protected void onProgressUpdate(Integer... values) {

        if (values[0] == -1){
            ImageView bannerImg = (ImageView) mContext.findViewById(R.id.main_list_banner);
            ListView mainListView = (ListView) mContext.findViewById(R.id.main_list_list);
            TextView progressText = (TextView) mContext.findViewById(R.id.main_loading_text);
            TextView errorTextView = (TextView) mContext.findViewById(R.id.main_error_txt);
            errorTextView.setVisibility(View.VISIBLE);
            errorTextView.setText(mErrorTxt);
            bannerImg.setVisibility(View.GONE);
            mainListView.setVisibility(View.GONE);
            mProgress.setVisibility(View.GONE);
            progressText.setVisibility(View.GONE);
        }
        else {
            mProgress.setProgress(values[0]);
        }
        super.onProgressUpdate(values);
    }

    /**
     * Update the screen with the given characterlist (which was the result of the.
     */
    @Override
    protected void onPostExecute(HashMap<String, ASOIAFCharacter> characterDictionary) {
        if (characterDictionary == null) {
            return;
        }
        onResponseReceived(characterDictionary, mUrlToCharacterNameDictionary);

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
            mErrorTxt = "Error with creating URL. Please restart the application.";
            publishProgress(-1);
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
                mErrorTxt = "Error response code: " + urlConnection.getResponseCode() + ". Please restart the application.";
                publishProgress(-1);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the ASOIAF Character JSON results.", e);
            mErrorTxt = "Problem retrieving the ASOIAF Character JSON results. Please restart the application.";
            publishProgress(-1);
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
     * Convert the InputStream into a String which contains the
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
     * Returns a url String to ASOIAFCharacter map containing data parsed from the given JSON string.
     * <p>
     * This method will create entries for just the name, url, aliases, and allegiances fields in
     * the ASOIAFCharacter instance, even if they are just empty strings (aside from name).
     *
     * @param  charactersJSON  a String containing JSON text representing all characters on
     *                         the server.
     * @return  a url String to ASOIAFCharacter map containing entries for all characters on the
     * server.
     */
    private HashMap<String, ASOIAFCharacter> extractFromCharactersJson(String charactersJSON, int pageNum) {
        // If the JSON string is empty or null, then return early.
        HashMap<String, ASOIAFCharacter> charactersDictionary = new HashMap<>();

        if (TextUtils.isEmpty(charactersJSON)) {
            return charactersDictionary;
        }

        try {

            JSONArray baseJsonResponse = new JSONArray(charactersJSON);

            int currChar = (pageNum - 1) * ITEMS_PER_PAGE;

            for (int i = 0; i < baseJsonResponse.length(); i++) {
                JSONObject characterJSONObject = baseJsonResponse.getJSONObject(i);

                String name = characterJSONObject.getString("name");

                mUrlToCharacterNameDictionary.put(characterJSONObject.getString("url"), name);

                if (!name.equals("")) {
                    ArrayList<String> aliases = new ArrayList<>();
                    ArrayList<String> allegiances = new ArrayList<>();

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
                    String url = characterJSONObject.getString("url");
                    ASOIAFCharacter newCharacter = new ASOIAFCharacter(url, name, aliases,
                            allegiances);

                    charactersDictionary.put(url, newCharacter);
                    currChar++;
                    publishProgress(TOTAL_BOOKS + TOTAL_HOUSES + currChar);
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the characters JSON results", e);
            mErrorTxt = "Problem parsing the characters JSON results. Please restart the application.";
            publishProgress(-1);
        }
        return charactersDictionary;
    }

    public void onResponseReceived(HashMap<String, ASOIAFCharacter> result, HashMap<String, String> result2){}


}