package com.arbrettingen.charactersoficeandfire;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

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
 * Queries https://www.anapioficeandfire.com/ via http request for information about a specific
 * character, given as a JSON response and processed into a Character object.
 */

public abstract class AOIAFCharacterDetailAsyncTask extends AsyncTask<URL, Integer, ASOIAFCharacter> {

    private static final String LOG_TAG = AOIAFCharacterDetailAsyncTask.class.getSimpleName();

    private String mUrl;

    public AOIAFCharacterDetailAsyncTask(Context myContext, String charUrl,
                                         HashMap<String, String> houeUrlToRegionName,
                                         HashMap<String, String> houseUrlToHouseName,
                                         HashMap<String, String> bookUrlToBookName) {
        mUrl = charUrl;
    }

    @Override
    protected ASOIAFCharacter doInBackground(URL... urls) {
        ASOIAFCharacter newCharacter;

        // Extract character information into ASOIAFCharacter objects

        URL url = createUrl(mUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        try {
            String jsonResponse = makeHttpRequest(url);
            newCharacter = extractCharacterDetailFromJSON(jsonResponse);
            return newCharacter;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        return null;
    }

    /**
     * Update the screen with the given characterlist (which was the result of the
     * {@link MainListActivity}).
     */
    @Override
    protected void onPostExecute(ASOIAFCharacter newCharacter) {
        if (newCharacter == null) {
            return;
        }
        onResponseReceived(newCharacter);

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

    private ASOIAFCharacter extractCharacterDetailFromJSON(String characterJSON){
        // If the JSON string is empty or null, then return early.
        ASOIAFCharacter newCharacter;

        if (TextUtils.isEmpty(characterJSON)) {
            return null;
        }

        try {

            JSONObject baseJsonResponse = new JSONObject(characterJSON);

            String name = baseJsonResponse.getString("name");

            ArrayList<String> titles = new ArrayList<>();
            ArrayList<String> aliases = new ArrayList<>();
            ArrayList<String> allegiances = new ArrayList<>();
            ArrayList<String> books = new ArrayList<>();
            ArrayList<String> tVSeasons = new ArrayList<>();
            ArrayList<String> playedBy = new ArrayList<>();

            JSONArray titlesJSONArray = baseJsonResponse.getJSONArray("titles");
            if (titlesJSONArray.length() > 0) {
                for (int j = 0; j < titlesJSONArray.length(); j++) {
                    titles.add(titlesJSONArray.getString(j));
                }
            }
            JSONArray aliasesJSONArray = baseJsonResponse.getJSONArray("aliases");
            if (aliasesJSONArray.length() > 0) {
                for (int j = 0; j < aliasesJSONArray.length(); j++) {
                    aliases.add(aliasesJSONArray.getString(j));
                }
            }
            JSONArray allegiancesJSONArray = baseJsonResponse.getJSONArray("allegiances");
            if (allegiancesJSONArray.length() > 0) {
                for (int j = 0; j < allegiancesJSONArray.length(); j++) {
                    allegiances.add(allegiancesJSONArray.getString(j));
                }
            }
            JSONArray booksJSONArray = baseJsonResponse.getJSONArray("books");
            if (booksJSONArray.length() > 0) {
                for (int j = 0; j < booksJSONArray.length(); j++) {
                    books.add(booksJSONArray.getString(j));
                }
            }
            JSONArray tVSeasonsJSONArray = baseJsonResponse.getJSONArray("tvSeries");
            if (tVSeasonsJSONArray.length() > 0) {
                for (int j = 0; j < tVSeasonsJSONArray.length(); j++) {
                    tVSeasons.add(tVSeasonsJSONArray.getString(j));
                }
            }
            JSONArray playedByJSONArray = baseJsonResponse.getJSONArray("playedBy");
            if (playedByJSONArray.length() > 0) {
                for (int j = 0; j < playedByJSONArray.length(); j++) {
                    playedBy.add(playedByJSONArray.getString(j));
                }
            }
            String url = baseJsonResponse.getString("url");
            String gender = baseJsonResponse.getString("gender");
            String culture = baseJsonResponse.getString("culture");
            String yearBorn = baseJsonResponse.getString("born");
            String yearDied = baseJsonResponse.getString("died");
            String father = baseJsonResponse.getString("father");
            String mother = baseJsonResponse.getString("mother");
            String spouse = baseJsonResponse.getString("spouse");

            newCharacter = new ASOIAFCharacter(url, name, gender,
                    culture, yearBorn, yearDied, titles, aliases, father, mother,
                    spouse, allegiances, books, tVSeasons, playedBy);

            return newCharacter;

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the characters JSON results", e);
        }
        return null;
    }

    private  void onResponseReceived(ASOIAFCharacter result){}

}

