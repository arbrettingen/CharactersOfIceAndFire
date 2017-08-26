package com.arbrettingen.charactersoficeandfire.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.arbrettingen.charactersoficeandfire.data.BooksContract.BooksEntry;
import com.arbrettingen.charactersoficeandfire.data.CharacterContract.CharacterEntry;
import com.arbrettingen.charactersoficeandfire.data.HousesContract.HousesEntry;

/**
 * Database helper for characters of IAF app. Manages database creation and version management.
 */

public class ASOIAFDbHelper extends SQLiteOpenHelper {

    /** Name of the database file */
    private static final String DATABASE_NAME = "asoiaf.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of ASOIAFDbHelper.
     *
     * @param context of the app
     */
    public ASOIAFDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the characters table

        String SQL_CREATE_CHARACTERS_TABLE = "CREATE TABLE " + CharacterEntry.TABLE_NAME + " ("
                + CharacterEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CharacterEntry.COLUMN_CHARACTER_NAME + " TEXT NOT NULL, "
                + CharacterEntry.COLUMN_CHARACTER_URL + "TEXT NOT NULL, "
                + CharacterEntry.COLUMN_CHARACTER_GENDER + "TEXT, "
                + CharacterEntry.COLUMN_CHARACTER_CULTURE + "TEXT, "
                + CharacterEntry.COLUMN_CHARACTER_BORN + "TEXT, "
                + CharacterEntry.COLUMN_CHARACTER_DIED + "TEXT, "
                + CharacterEntry.COLUMN_CHARACTER_TITLES + "TEXT, "
                + CharacterEntry.COLUMN_CHARACTER_ALIASES + "TEXT, "
                + CharacterEntry.COLUMN_CHARACTER_FATHER + "TEXT, "
                + CharacterEntry.COLUMN_CHARACTER_MOTHER + "TEXT, "
                + CharacterEntry.COLUMN_CHARACTER_SPOUSE + "TEXT, "
                + CharacterEntry.COLUMN_CHARACTER_ALLEGIANCES + "TEXT, "
                + CharacterEntry.COLUMN_CHARACTER_BOOKS + "TEXT, "
                + CharacterEntry.COLUMN_CHARACTER_SEASONS + "TEXT, "
                + CharacterEntry.COLUMN_CHARACTER_PLAYEDBY + ");";

        db.execSQL(SQL_CREATE_CHARACTERS_TABLE);

        String SQL_CREATE_HOUSES_TABLE = "CREATE TABLE " + HousesEntry.TABLE_NAME + " ("
                + HousesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + HousesEntry.COLUMN_HOUSE_NAME + " TEXT NOT NULL, "
                + HousesEntry.COLUMN_HOUSE_REGION + " TEXT);";

        db.execSQL(SQL_CREATE_HOUSES_TABLE);

        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + BooksEntry.TABLE_NAME + " ("
                + BooksEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BooksEntry.COLUMN_BOOK_NAME + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }

}
