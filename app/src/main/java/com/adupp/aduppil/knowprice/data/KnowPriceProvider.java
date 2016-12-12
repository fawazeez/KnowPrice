package com.adupp.aduppil.knowprice.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by fawaz on 11/14/2016.
 */

public class KnowPriceProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static final int CATEGORY = 100;
//    static final int WEATHER_WITH_LOCATION = 101;
//    static final int WEATHER_WITH_LOCATION_AND_DATE = 102;
    static final int ITEM = 300;
    static final int LOCATION = 400;
    static final int SHOPPINGLIST = 500;
    static final int ITEM_FOR_CATEGORY = 600;
    static final int ITEM_FOR_NOTIFICATION = 700;

     static UriMatcher buildUriMatcher() {
         final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
         final String authority = KnowPriceContract.CONTENT_AUTHORITY;
         matcher.addURI(authority, KnowPriceContract.PATH_CATEGORY, CATEGORY);
         matcher.addURI(authority, KnowPriceContract.PATH_ITEM + "/#", ITEM_FOR_CATEGORY);
         matcher.addURI(authority, KnowPriceContract.PATH_ITEM + "/*/*", ITEM_FOR_NOTIFICATION);
         matcher.addURI(authority, KnowPriceContract.PATH_LOCATION, LOCATION);
         matcher.addURI(authority, KnowPriceContract.PATH_ITEM, ITEM);
         matcher.addURI(authority, KnowPriceContract.PATH_SHOPPING_LIST, SHOPPINGLIST);
         return matcher;
    }

    private KnowPriceDbHelper mOpenHelper;
    private static final SQLiteQueryBuilder sItemByCategoryQueryBuilder ;
    static{
        sItemByCategoryQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sItemByCategoryQueryBuilder.setTables(
                KnowPriceContract.CategoryEntry.TABLE_NAME + " INNER JOIN " +
                        KnowPriceContract.ItemEntry.TABLE_NAME +
                        " ON " + KnowPriceContract.CategoryEntry.TABLE_NAME +
                        "." + KnowPriceContract.CategoryEntry._ID +
                        " = " + KnowPriceContract.ItemEntry.TABLE_NAME +
                        "." + KnowPriceContract.ItemEntry.COLUMN_ITEM_CATEGORY_KEY);
    }


    private static final SQLiteQueryBuilder sItemForOfferQueryBuilder ;
    static{
        sItemForOfferQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sItemForOfferQueryBuilder.setTables(
                KnowPriceContract.CategoryEntry.TABLE_NAME + " INNER JOIN " +
                        KnowPriceContract.ItemEntry.TABLE_NAME +
                        " ON " + KnowPriceContract.CategoryEntry.TABLE_NAME +
                        "." + KnowPriceContract.CategoryEntry._ID +
                        " = " + KnowPriceContract.ItemEntry.TABLE_NAME +
                        "." + KnowPriceContract.ItemEntry.COLUMN_ITEM_CATEGORY_KEY + " INNER JOIN " +
                KnowPriceContract.ShoppingListEntry.TABLE_NAME +  " ON " + KnowPriceContract.ShoppingListEntry.TABLE_NAME +
                        "."  + KnowPriceContract.ShoppingListEntry.COLUMN_ITEM_NAME +
                " = " + KnowPriceContract.CategoryEntry.COLUMN_CATEGORY_NAME);
    }

    private static final String sCategroySelection =
            KnowPriceContract.ItemEntry.TABLE_NAME+
                    "." + KnowPriceContract.ItemEntry.COLUMN_ITEM_CATEGORY_KEY + " = ? ";

    @Override
    public boolean onCreate() {
        mOpenHelper = new KnowPriceDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case CATEGORY:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        KnowPriceContract.CategoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case ITEM: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        KnowPriceContract.ItemEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }case ITEM_FOR_CATEGORY: {
                retCursor = getItemByCategory(uri, projection, sortOrder);
                break;
            }case ITEM_FOR_NOTIFICATION: {
                retCursor = getItemForNotification(uri, projection, sortOrder);
                break;
            }case SHOPPINGLIST: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        KnowPriceContract.ShoppingListEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case LOCATION: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        KnowPriceContract.LocationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return retCursor;
    }

    private Cursor getItemForNotification(Uri uri, String[] projection, String sortOrder) {
        return sItemForOfferQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,null,null, null,
                null,
                sortOrder);

    }

    private Cursor getItemByCategory(Uri uri, String[] projection, String sortOrder) {
        long categoryId = KnowPriceContract.ItemEntry.getCategoryfromUri(uri);
        String[] selectionArgs;
        String selection;
        selection = sCategroySelection;
        selectionArgs = new String[]{Long.toString(categoryId)};
        return sItemByCategoryQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case CATEGORY:
                return KnowPriceContract.CategoryEntry.CONTENT_TYPE;
            case ITEM:
                return KnowPriceContract.ItemEntry.CONTENT_TYPE;
            case ITEM_FOR_CATEGORY:
                return KnowPriceContract.ItemEntry.CONTENT_TYPE;
            case ITEM_FOR_NOTIFICATION:
                return KnowPriceContract.ItemEntry.CONTENT_TYPE;
            case SHOPPINGLIST:
                return KnowPriceContract.ShoppingListEntry.CONTENT_TYPE;
            case LOCATION:
                return KnowPriceContract.LocationEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case CATEGORY: {
                long _id = db.insert(KnowPriceContract.CategoryEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = KnowPriceContract.CategoryEntry.buildCategoryUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case ITEM: {
                long _id = db.insert(KnowPriceContract.ItemEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = KnowPriceContract.ItemEntry.builditemUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case SHOPPINGLIST: {
                long _id = db.insert(KnowPriceContract.ShoppingListEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = KnowPriceContract.ShoppingListEntry.buildShoppinglistUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case LOCATION: {
                long _id = db.insert(KnowPriceContract.LocationEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = KnowPriceContract.LocationEntry.buildLocationUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEM:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(KnowPriceContract.ItemEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        if ( null == selection ) selection = "1";
        switch (match) {
            case SHOPPINGLIST:
                rowsDeleted = db.delete(
                        KnowPriceContract.ShoppingListEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ITEM:
                rowsDeleted = db.delete(
                        KnowPriceContract.ItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case SHOPPINGLIST:
                rowsUpdated = db.update(KnowPriceContract.ShoppingListEntry.TABLE_NAME, contentValues, selection,
                        selectionArgs);
                break;
            case LOCATION:
                rowsUpdated = db.update(KnowPriceContract.LocationEntry.TABLE_NAME, contentValues, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return rowsUpdated;
    }
}
