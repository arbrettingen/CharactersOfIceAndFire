package com.arbrettingen.charactersoficeandfire.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import com.arbrettingen.charactersoficeandfire.data.HousesContract.HousesEntry;
import com.arbrettingen.charactersoficeandfire.data.CharacterContract.CharacterEntry;
import com.arbrettingen.charactersoficeandfire.data.BooksContract.BooksEntry;

/**
 *
 */

/**
 * ASOIAFProvider.java
 *
 * <P>Provides the application with SQL db interaction based on provided Uri, which is matched inside
 * the class and executed accordingly.
 *
 * @author Alex Brettingen
 * @version 1.0
 */

public class ASOIAFProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = ASOIAFProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the characters table */
    private static final int CHARACTERS = 100;

    /** URI matcher code for the content URI for a single character in the characters table */
    private static final int CHARACTER_ID = 101;

    /** URI matcher code for the content URI for the houses table */
    private static final int HOUSES = 200;

    /** URI matcher code for the content URI for a single house in the houses table */
    private static final int HOUSE_ID = 201;

    /** URI matcher code for the content URI for the books table */
    private static final int BOOKS = 300;

    /** URI matcher code for the content URI for a single book in the books table */
    private static final int BOOK_ID = 301;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.example.android.xxx/xxx" will map to the
        // integer code {@link #XXX}. This URI is used to provide access to MULTIPLE rows
        // of the characters table.
        sUriMatcher.addURI(CharacterContract.CONTENT_AUTHORITY, CharacterContract.PATH_CHARACTERS, CHARACTERS);

        // The content URI of the form "content://com.example.android.xxx/xxx/#" will map to the
        // integer code {@link #XXX_ID}. This URI is used to provide access to ONE single row
        // of the xxx table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.example.android.xxx/xxx/3" matches, but
        // "content://com.example.android.xxx/xxx" (without a number at the end) doesn't match.
        sUriMatcher.addURI(CharacterContract.CONTENT_AUTHORITY, CharacterContract.PATH_CHARACTERS + "/#", CHARACTER_ID);

        sUriMatcher.addURI(HousesContract.CONTENT_AUTHORITY, HousesContract.PATH_HOUSES, HOUSES);
        sUriMatcher.addURI(HousesContract.CONTENT_AUTHORITY, HousesContract.PATH_HOUSES + "/#", HOUSE_ID);

        sUriMatcher.addURI(BooksContract.CONTENT_AUTHORITY, BooksContract.PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(BooksContract.CONTENT_AUTHORITY, BooksContract.PATH_BOOKS + "/#", BOOK_ID);
    }

    /** Database helper object */
    private ASOIAFDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ASOIAFDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case CHARACTERS:
                // For the CHARACTERS code, query the characters table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the characters table.
                cursor = database.query(CharacterContract.CharacterEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case CHARACTER_ID:
                // For the CHARACTER_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.xxx/xxx/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                selection = CharacterEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the characters table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(CharacterEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case HOUSES:
                cursor = database.query(HousesContract.HousesEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case HOUSE_ID:
                selection = HousesContract.HousesEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the houses table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(HousesContract.HousesEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOKS:
                cursor = database.query(BooksContract.BooksEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BOOK_ID:
                selection = BooksContract.BooksEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the houses table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(BooksContract.BooksEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CHARACTERS:
                return insertCharacter(uri, contentValues);
            case HOUSES:
                return insertHouse(uri, contentValues);
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a character into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertCharacter(Uri uri, ContentValues values) {
        // Check that the body is not null
        String name = values.getAsString(CharacterContract.CharacterEntry.COLUMN_CHARACTER_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Character requires name text.");
        }

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new character with the given values
        long id = database.insert(CharacterContract.CharacterEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the affirmation content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Insert a house into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertHouse(Uri uri, ContentValues values) {
        //Check that the house name is not null
        String house = values.getAsString(HousesEntry.COLUMN_HOUSE_NAME);
        if (house == null) {
            throw new IllegalArgumentException("House entry requires name text.");
        }

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new character with the given values
        long id = database.insert(HousesContract.HousesEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the affirmation content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Insert a book into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertBook(Uri uri, ContentValues values) {
        // Check that the book name is not null
        String book = values.getAsString(BooksEntry.COLUMN_BOOK_NAME);
        if (book == null) {
            throw new IllegalArgumentException("Book entry requires name text.");
        }

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new character with the given values
        long id = database.insert(BooksEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the affirmation content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CHARACTERS:
                return updateCharacter(uri, contentValues, selection, selectionArgs);
            case CHARACTER_ID:
                // For the CHARACTER_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = CharacterEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateCharacter(uri, contentValues, selection, selectionArgs);
            case HOUSES:
                return updateHouse(uri, contentValues, selection, selectionArgs);
            case HOUSE_ID:
                selection = HousesEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateHouse(uri, contentValues, selection, selectionArgs);
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                selection = BooksEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }


    /**
     * Update houses in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more houses).
     * Return the number of rows that were successfully updated.
     */
    private int updateHouse(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(HousesEntry.COLUMN_HOUSE_NAME)) {
            String name = values.getAsString(HousesEntry.COLUMN_HOUSE_NAME);
            if (name == null) {
                throw new IllegalArgumentException("House name requires a text");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(CharacterEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    /**
     * Update characters in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more characters).
     * Return the number of rows that were successfully updated.
     */
    private int updateCharacter(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(CharacterEntry.COLUMN_CHARACTER_NAME)) {
            String name = values.getAsString(CharacterEntry.COLUMN_CHARACTER_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Character name requires a text");
            }
        }

        if (values.containsKey(CharacterEntry.COLUMN_CHARACTER_URL)) {
            String url = values.getAsString(CharacterEntry.COLUMN_CHARACTER_URL);
            if (url == null) {
                throw new IllegalArgumentException("Character url requires text");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(CharacterEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    /**
     * Update books in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more books).
     * Return the number of rows that were successfully updated.
     */
    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(BooksEntry.COLUMN_BOOK_NAME)) {
            String name = values.getAsString(BooksEntry.COLUMN_BOOK_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Book name requires a text");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(CharacterEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CHARACTERS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(CharacterEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CHARACTER_ID:
                // Delete a single row given by the ID in the URI
                selection = CharacterEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(CharacterEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case HOUSES:
                rowsDeleted = database.delete(HousesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case HOUSE_ID:
                selection = HousesEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(HousesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOKS:
                rowsDeleted = database.delete(BooksEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                selection = BooksEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(BooksEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CHARACTERS:
                return CharacterEntry.CONTENT_LIST_TYPE;
            case CHARACTER_ID:
                return CharacterEntry.CONTENT_ITEM_TYPE;
            case HOUSES:
                return HousesEntry.CONTENT_LIST_TYPE;
            case HOUSE_ID:
                return HousesEntry.CONTENT_ITEM_TYPE;
            case BOOKS:
                return BooksEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BooksEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

}
