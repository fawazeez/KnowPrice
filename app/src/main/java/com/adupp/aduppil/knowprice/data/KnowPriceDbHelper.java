package com.adupp.aduppil.knowprice.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.adupp.aduppil.knowprice.data.KnowPriceContract.*;
/**
 * Created by fawaz on 11/14/2016.
 */

public class KnowPriceDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 7;

    static final String DATABASE_NAME = "knowprice.db";
    public KnowPriceDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + LocationEntry.TABLE_NAME + " (" +
                LocationEntry._ID + " INTEGER PRIMARY KEY," +
                LocationEntry.COLUMN_COUNTRY_NAME + " TEXT  NOT NULL, " +
                LocationEntry.COLUMN_CITY_NAME + " TEXT  NOT NULL, " +
                LocationEntry.COLUMN_SELECTED + " REAL NOT NULL, " +
                " UNIQUE (" + LocationEntry.COLUMN_COUNTRY_NAME + ", " +
                LocationEntry.COLUMN_CITY_NAME + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_CATEGORY_TABLE = "CREATE TABLE " + CategoryEntry.TABLE_NAME + " (" +
                CategoryEntry._ID + " INTEGER PRIMARY KEY," +
                CategoryEntry.COLUMN_CATEGORY_NAME + " TEXT UNIQUE NOT NULL, " +
                CategoryEntry.COLUMN_CATEGORY_IMAGE + " TEXT NOT NULL, " +
                CategoryEntry.COLUMN_CATEGORY_FAV + " REAL NOT NULL " +
                " );";

        final String SQL_CREATE_SHOPPINGLIST_TABLE = "CREATE TABLE " + ShoppingListEntry.TABLE_NAME + " (" +
                ShoppingListEntry._ID + " INTEGER PRIMARY KEY," +
                ShoppingListEntry.COLUMN_ITEM_NAME + " TEXT UNIQUE NOT NULL, " +
                ShoppingListEntry.COLUMN_ITEM_CATEGORY_KEY + " INTEGER , " +
                ShoppingListEntry.COLUMN_ITEM_QTY + " INTEGER , " +
                ShoppingListEntry.COLUMN_ITEM_UOM + " TEXT  , " +
                ShoppingListEntry.COLUMN_ITEM_LOCATION + " INTEGER , " +
                ShoppingListEntry.COLUMN_ITEM_PUR_DATE + " INTEGER NOT NULL " +
                " );";

        final String SQL_CREATE_ITEM_TABLE = "CREATE TABLE " + ItemEntry.TABLE_NAME + " (" +

                ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                ItemEntry.COLUMN_ITEM_QTY + " INTEGER NOT NULL, " +
                ItemEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL, " +
                ItemEntry.COLUMN_ITEM_LOCATION + " INTEGER NOT NULL, " +
                ItemEntry.COLUMN_ITEM_ACT_PRICE + " REAL NOT NULL," +
                ItemEntry.COLUMN_ITEM_IMAGE + " REAL," +
                ItemEntry.COLUMN_ITEM_SALE_PRICE + " REAL NOT NULL, " +
                ItemEntry.COLUMN_ITEM_CATEGORY_KEY + " INTEGER NOT NULL, " +
                ItemEntry.COLUMN_ITEM_QTY_LU + " REAL, " +
                ItemEntry.COLUMN_START_DATE + " INTEGER NOT NULL, " +
                ItemEntry.COLUMN_END_DATE + " INTEGER NOT NULL, " +

                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + ItemEntry.COLUMN_ITEM_LOCATION + ") REFERENCES " +
                LocationEntry.TABLE_NAME + " (" + LocationEntry._ID + "), " +
                " FOREIGN KEY (" + ItemEntry.COLUMN_ITEM_CATEGORY_KEY + ") REFERENCES " +
                CategoryEntry.TABLE_NAME + " (" + CategoryEntry._ID + "), " +
                " UNIQUE (" + ItemEntry.COLUMN_ITEM_NAME + ", " +
                ItemEntry.COLUMN_ITEM_LOCATION + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_LOCATION_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CATEGORY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SHOPPINGLIST_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ITEM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CategoryEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ShoppingListEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ItemEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
