package com.adupp.aduppil.knowprice.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by fawaz on 11/14/2016.
 */

public class KnowPriceContract {
    public static final String CONTENT_AUTHORITY = "com.adupp.aduppil.knowprice";


    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_CATEGORY = "category";
    public static final String PATH_SHOPPING_LIST = "shoppinglist";
    public static final String PATH_ITEM = "item";
    public static final String PATH_LOCATION = "location";

    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    public static final class CategoryEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;

        public static final String TABLE_NAME = "category";

        public static final String COLUMN_CATEGORY_NAME = "category_name";
        public static final String COLUMN_CATEGORY_IMAGE = "category_image";
        public static final String COLUMN_CATEGORY_FAV = "category_fav";

        public static Uri buildCategoryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildCategoryFullUri() {

            return CONTENT_URI;
        }
    }
    public static final class ShoppingListEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SHOPPING_LIST).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SHOPPING_LIST;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SHOPPING_LIST;

        public static final String TABLE_NAME = "shoppinglist";
        public static final String COLUMN_ITEM_NAME = "item_name";
        public static final String COLUMN_ITEM_CATEGORY_KEY = "item_category_key";
        public static final String COLUMN_ITEM_QTY = "item_qty";
        public static final String COLUMN_ITEM_PUR_DATE = "item_pur_date";
        public static final String COLUMN_ITEM_UOM = "item_uom";
        public static final String COLUMN_ITEM_LOCATION = "item_location";

        public static Uri buildShoppinglistUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
    public static final class ItemEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ITEM).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEM;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEM;

        public static final String TABLE_NAME = "item";

        public static final String COLUMN_ITEM_QTY = "item_qty";
        public static final String COLUMN_ITEM_CATEGORY_KEY = "item_category_key";
        public static final String COLUMN_ITEM_NAME = "item_name";
        public static final String COLUMN_ITEM_IMAGE = "item_image";
        public static final String COLUMN_ITEM_ACT_PRICE = "item_act_price";
        public static final String COLUMN_ITEM_SALE_PRICE = "item_sale_price";
        public static final String COLUMN_ITEM_QTY_LU = "item_qty_lu";
        public static final String COLUMN_ITEM_LOCATION = "item_location";
        public static final String COLUMN_START_DATE = "item_start_date";
        public static final String COLUMN_END_DATE = "item_end_date";

        public static Uri builditemUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildOffersForShoppingList(String country,String city) {
            return CONTENT_URI.buildUpon().appendPath(country).appendPath(city).build();
        }

        public static Uri builditemCategoryUri(long category) {

            return CONTENT_URI.buildUpon().appendPath(Long.toString(category)).build();
        }

        public static long getCategoryfromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }
    public static final class LocationEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        public static final String TABLE_NAME = "location";

        public static final String COLUMN_COUNTRY_NAME = "country_name";
        public static final String COLUMN_CITY_NAME = "city_name";
        public static final String COLUMN_SELECTED = "selected";

        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
