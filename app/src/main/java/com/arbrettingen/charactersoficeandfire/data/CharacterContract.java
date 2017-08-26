package com.arbrettingen.charactersoficeandfire.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for any ASOIAF character
 */

public class CharacterContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private CharacterContract() {}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.
     */
    public static final String CONTENT_AUTHORITY = "com.arbrettingen.charactersoficeandfire";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     */
    public static final String PATH_CHARACTERS = "characters";

    /**
     * Inner class that defines constant values for the characters database table.
     * Each entry in the table represents a single character.
     */
    public static final class CharacterEntry implements BaseColumns {

        /** The content URI to access the character data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CHARACTERS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of characters.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CHARACTERS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single character.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CHARACTERS;

        /** Name of database table for characters */
        public final static String TABLE_NAME = "characters";

        /**
         * Unique ID number for the character (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Character name text
         *
         * Type: TEXT
         */
        public final static String COLUMN_CHARACTER_NAME ="name";

        /**
         * API Url associated with character
         *
         * Type: TEXT
         */
        public final static String COLUMN_CHARACTER_URL = "url";

        /**
         * Gender associated with character
         *
         * Type: TEXT
         */
        public final static String COLUMN_CHARACTER_GENDER = "gender";

        /**
         * Culture associated with character
         *
         * Type: TEXT
         */
        public final static String COLUMN_CHARACTER_CULTURE = "culture";

        /**
         * Year and location character was born in.
         *
         * Type: TEXT
         */
        public final static String COLUMN_CHARACTER_BORN = "born";

        /**
         * Year and location character died in.
         *
         * Type: TEXT
         */
        public final static String COLUMN_CHARACTER_DIED = "died";

        /**
         * Titles associated with character
         *
         * Type: TEXT
         */
        public final static String COLUMN_CHARACTER_TITLES = "titles";

        /**
         * Aliases associated with character
         *
         * Type: TEXT
         */
        public final static String COLUMN_CHARACTER_ALIASES = "aliases";

        /**
         * Character father text
         *
         * Type: TEXT
         */
        public final static String COLUMN_CHARACTER_FATHER = "father";

        /**
         * Character mother text
         *
         * Type: TEXT
         */
        public final static String COLUMN_CHARACTER_MOTHER = "mother";

        /**
         * Character spouse text
         *
         * Type: TEXT
         */
        public final static String COLUMN_CHARACTER_SPOUSE = "spouse";

        /**
         * Allegiances associated with character
         *
         * Type: TEXT
         */
        public final static String COLUMN_CHARACTER_ALLEGIANCES = "allegiances";

        /**
         * Books character has appeared in
         *
         * Type: TEXT
         */
        public final static String COLUMN_CHARACTER_BOOKS = "books";

        /**
         * TV Seasons character has appeared in
         *
         * Type: TEXT
         */
        public final static String COLUMN_CHARACTER_SEASONS = "seasons";

        /**
         * Actors that have portrayed this character
         *
         * Type: TEXT
         */
        public final static String COLUMN_CHARACTER_PLAYEDBY = "playedby";

    }

}
