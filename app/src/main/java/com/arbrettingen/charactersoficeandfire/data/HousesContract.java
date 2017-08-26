package com.arbrettingen.charactersoficeandfire.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for an ASOIAF house
 */

public class HousesContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private HousesContract() {}

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
    public static final String PATH_HOUSES = "houses";

    /**
     * Inner class that defines constant values for the houses database table.
     * Each entry in the table represents a single house.
     */
    public static final class HousesEntry implements BaseColumns {

        /** The content URI to access the house data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_HOUSES);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of houses.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HOUSES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single house.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HOUSES;

        /** Name of database table for houses */
        public final static String TABLE_NAME = "houses";

        /**
         * Unique ID number for the house (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * House name text
         *
         * Type: TEXT
         */
        public final static String COLUMN_HOUSE_NAME ="name";

        /**
         * House region text
         *
         * Type: TEXT
         */
        public final static String COLUMN_HOUSE_REGION ="region";

    }

}
