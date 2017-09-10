package com.arbrettingen.charactersoficeandfire;

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arbrettingen.charactersoficeandfire.data.CharacterContract;

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
import com.arbrettingen.charactersoficeandfire.data.HousesContract.HousesEntry;

/**
 * AOIAFHousesAsyncTask.java
 *
 * <P>Queries https://www.anapioficeandfire.com/ via http request for information about all listed
 * houses, given as a JSON response and processed into an SQLite table
 *
 * @author Alex Brettingen
 * @version 1.0
 */


public abstract class AOIAFHousesAsyncTask
        extends AsyncTask<URL, Integer, ArrayList<HashMap<String, String>>>{

    private static final String LOG_TAG = AOIAFHousesAsyncTask.class.getSimpleName();
    private static final Integer TOTAL_CHARACTERS = 2138;
    private static final Integer TOTAL_BOOKS = 12;
    private static final Integer TOTAL_HOUSES = 444;
    private static final Integer ITEMS_PER_PAGE = 50;
    /**
     * URL to query the api of Ice and Fire dataset for house information
     */
    private static final String AOIAF_ALL_HOUSES_REQUEST_URL =
            "https://www.anapioficeandfire.com/api/houses?pageSize=50&page=";


    private ProgressBar mProgress;
    private Activity mContext;
    private String mErrorTxt;

    private HashMap<String, String> mUrlToHouseNamesDictionary = new HashMap<>();
    private HashMap<String, String> mHouseUrlToRegionDictionary = new HashMap<>();

    public AOIAFHousesAsyncTask(Activity myContext) {
        mProgress = (ProgressBar) myContext.findViewById(R.id.main_progress);
        mContext = myContext;
    }


    @Override
    protected ArrayList<HashMap<String, String>> doInBackground(URL... urls) {

        URL url;
        String jsonResponse = "";
        ArrayList<HashMap<String, String>> ASOIAFData = new ArrayList<>();

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
                mErrorTxt = "Problem making the HTTP request. Please restart the application.";
                publishProgress(-1);
            }

        }

        ASOIAFData.add(mUrlToHouseNamesDictionary);
        ASOIAFData.add(mHouseUrlToRegionDictionary);

        // Return {@link ASOIAFCharacter} objects list as the result fo the {@link AOIAFAsyncTask}
        return ASOIAFData;
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
        else{
            mProgress.setProgress(values[0]);
        }
        super.onProgressUpdate(values);
    }

    /**
     * Update the screen with the given characterlist (which was the result of the doinbackground)
     */
    @Override
    protected void onPostExecute(ArrayList<HashMap<String, String>> data) {
        onResponseReceived(data);
        super.onPostExecute(data);
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
            Log.e(LOG_TAG, "Problem retrieving the ASOIAF JSON results.", e);
            mErrorTxt = "Problem retrieving the ASOIAF JSON results. Please restart the application.";
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
     * Populates a url String to String house name map containing data parsed from the given JSON
     * string. Inserts data into SQLite table.
     * <p>
     * Also populates a url String to region name String map.
     *
     * @param  housesJSON  a String containing JSON text representing all houses on
     *                         the server.
     * @param   pageNum an integer representing the page of the response the JSON String is
     *                  representing.
     */

    private void extractFromHousesJson(String housesJSON, int pageNum) {
        // If the JSON string is empty or null, then return early.
        HashMap<String, String> urlToHouseNamesDictionary = new HashMap<>();
        HashMap<String, String> houseUrlToRegionDictionary = new HashMap<>();
        ArrayList<HashMap<String, String>> data = new ArrayList<>();
        if (TextUtils.isEmpty(housesJSON)) {
            return;
        }
        try {

            JSONArray baseJsonResponse = new JSONArray(housesJSON);

            int currHouse = (pageNum - 1) * ITEMS_PER_PAGE;

            for (int i = 0; i < baseJsonResponse.length(); i++) {
                JSONObject houseJSONObject = baseJsonResponse.getJSONObject(i);
                mUrlToHouseNamesDictionary.put(houseJSONObject.getString("url"), houseJSONObject.getString("name"));
                mHouseUrlToRegionDictionary.put(houseJSONObject.getString("url"), houseJSONObject.getString("region"));
                insertHouseIntoSQLTable(houseJSONObject.getString("url"), houseJSONObject.getString("name"), houseJSONObject.getString("region"));
                currHouse++;
                publishProgress(TOTAL_CHARACTERS + currHouse);
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the characters JSON results", e);
            mErrorTxt = "Problem parsing the houses JSON results. Please restart the application.";
            publishProgress(-1);
        }

        data.add(urlToHouseNamesDictionary);
        data.add(houseUrlToRegionDictionary);
    }

    private void insertHouseIntoSQLTable(String url, String name, String region){

        if (url == null || name == null){
            return;
        }
        // Create a ContentValues object where column names are the keys,
        // and house attributes are the values.
        ContentValues values = new ContentValues();
        values.put(HousesEntry.COLUMN_HOUSE_NAME, name);
        values.put(HousesEntry.COLUMN_HOUSE_URL, url);
        values.put(HousesEntry.COLUMN_HOUSE_REGION, region);


        // Use the HouseEntry#CONTENT_URI} to indicate that we want to insert
        // into the characters database table.
        // Receive the new content URI that will allow us to access row's data in the future.
        Uri newUri = mContext.getContentResolver().insert(HousesEntry.CONTENT_URI, values);

    }

    public void onResponseReceived(ArrayList<HashMap<String, String>> result) {}

}
