package com.adupp.aduppil.knowprice.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.util.Log;


import com.adupp.aduppil.knowprice.MainActivity;
import com.adupp.aduppil.knowprice.R;
import com.adupp.aduppil.knowprice.data.KnowPriceContract;
import com.adupp.aduppil.knowprice.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Vector;

/**
 * Created by fawaz on 11/14/2016.
 */

public class KnowPriceSyncAdapter extends AbstractThreadedSyncAdapter {

    public final String LOG_TAG = "SyncAdapter";
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final int OFFER_NOTIFICATION_ID = 3004;

    private static final String[] NOTIFY_OFFER_PROJECTION = new String[] {
           KnowPriceContract.ItemEntry.TABLE_NAME +"."+KnowPriceContract.ItemEntry.COLUMN_ITEM_NAME,
            KnowPriceContract.ShoppingListEntry.TABLE_NAME +"."+KnowPriceContract.ShoppingListEntry.COLUMN_ITEM_NAME,
            KnowPriceContract.ItemEntry.COLUMN_ITEM_SALE_PRICE,
            KnowPriceContract.ItemEntry.TABLE_NAME +"."+KnowPriceContract.ItemEntry.COLUMN_ITEM_LOCATION
    };

    private static final int INDEX_ITEM_NAME = 0;
    private static final int INDEX_CAT_NAME = 1;
    private static final int INDEX_SALE_PRICE = 2;
    private static final int INDEX_LOCATION = 3;

    ContentResolver mContentResolver;
    Context mContext;

    public KnowPriceSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
        mContext= context;
    }


    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        String countryQuery = Utility.getPreferredCountryadapter(getContext());
        Log.d(LOG_TAG, countryQuery);
        countryQuery = countryQuery.replace(" ","_").toLowerCase();
        Log.d(LOG_TAG, countryQuery);
        String cityQuery = Utility.getPreferredCityadpater(getContext());
        cityQuery = cityQuery.replace(" ","_").toLowerCase();
        Log.d(LOG_TAG, cityQuery);
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String productJsonStr = null;
//        String format = "json";
        String filename = cityQuery + ".txt";

        final String OFFER_BASE_URL =
                "http://test.mmivisionuae.com/getjson.php?";
        final String FOLDER_PARAM = "folder";
        final String FILE_PARAM = "file";
        Uri builtUri = Uri.parse(OFFER_BASE_URL).buildUpon()
                .appendQueryParameter(FOLDER_PARAM, countryQuery)
                .appendQueryParameter(FILE_PARAM, filename)
                .build();

        Log.d(LOG_TAG,builtUri.toString());

        try {
            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            productJsonStr = buffer.toString();
            getItemDataFromJson(productJsonStr, countryQuery);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getItemDataFromJson(String productJsonStr, String locationQuery) throws JSONException {
        final String OWM_LIST = "item";
        final String OWM_ITEM_NAME = "itemname";
        final String OWM_QTY    = "qty";
        final String OWM_ITEM_IMAGE = "itemimg";
        final String OWM_ITEM_CATEGORY = "category";
        final String OWM_CAT_IMAGE = "catimage";
        final String OWM_ACTUAL_PRICE = "priceact";
        final String OWM_SALE_PRICE = "priceoffer";
        final String OWM_LOCATION = "location";
        final String OWM_START_DATE = "startdate";
        final String OWM_END_DATE = "endadate";

        try {
            JSONObject offerJson = new JSONObject(productJsonStr);
            JSONArray productArray = offerJson.getJSONArray(OWM_LIST);
            Vector<ContentValues> cVVector = new Vector<ContentValues>(productArray.length());
            for(int i = 0; i < productArray.length(); i++) {


                String qty;
                int actPrice;
                int salePrice;
                String location;
                long startDate;
                long endDate;
                String category;
                String catimage;
                String itemName;
                String itemImg;

                JSONObject offer = productArray.getJSONObject(i);
                category = offer.getString(OWM_ITEM_CATEGORY);
                catimage = offer.getString(OWM_CAT_IMAGE);
                long categoryId = addCategory(category,catimage);
                qty = offer.getString(OWM_QTY);
                actPrice = offer.getInt(OWM_ACTUAL_PRICE);
                salePrice = offer.getInt(OWM_SALE_PRICE);
                itemName = offer.getString(OWM_ITEM_NAME);
                itemImg = offer.getString(OWM_ITEM_IMAGE);
                location= offer.getString(OWM_LOCATION);
                startDate= offer.getLong(OWM_START_DATE);
                endDate= offer.getLong(OWM_END_DATE);



                ContentValues itemValues = new ContentValues();
                itemValues.put(KnowPriceContract.ItemEntry.COLUMN_ITEM_CATEGORY_KEY, categoryId);
                itemValues.put(KnowPriceContract.ItemEntry.COLUMN_ITEM_QTY, qty);
                itemValues.put(KnowPriceContract.ItemEntry.COLUMN_ITEM_NAME, itemName);
                itemValues.put(KnowPriceContract.ItemEntry.COLUMN_ITEM_IMAGE, itemImg);
                itemValues.put(KnowPriceContract.ItemEntry.COLUMN_ITEM_ACT_PRICE, actPrice);
                itemValues.put(KnowPriceContract.ItemEntry.COLUMN_ITEM_SALE_PRICE, salePrice);
                itemValues.put(KnowPriceContract.ItemEntry.COLUMN_ITEM_LOCATION, location);
                itemValues.put(KnowPriceContract.ItemEntry.COLUMN_START_DATE, startDate);
                itemValues.put(KnowPriceContract.ItemEntry.COLUMN_END_DATE, endDate);



                cVVector.add(itemValues);
            }

            int inserted = 0;
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                getContext().getContentResolver().bulkInsert(KnowPriceContract.ItemEntry.CONTENT_URI, cvArray);

//                // delete old data so we don't build up an endless history
                getContext().getContentResolver().delete(KnowPriceContract.ItemEntry.CONTENT_URI,
                        KnowPriceContract.ItemEntry.COLUMN_END_DATE + " < " + Utility.curDate(),null);

                notifyOffer();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private long addCategory(String category, String catimage) {
        long categoryId;
        category = category.trim().toLowerCase();
        Cursor categoryCursor = getContext().getContentResolver().query(
                KnowPriceContract.CategoryEntry.CONTENT_URI,
                new String[]{KnowPriceContract.CategoryEntry._ID},
                KnowPriceContract.CategoryEntry.COLUMN_CATEGORY_NAME + " = ?",
                new String[]{category},
                null);

        if (categoryCursor.moveToFirst()) {
            int categoryIdIndex = categoryCursor.getColumnIndex(KnowPriceContract.CategoryEntry._ID);
            categoryId = categoryCursor.getLong(categoryIdIndex);
        } else {
            ContentValues categoryValues = new ContentValues();

            // Then add the data, along with the corresponding name of the data type,
            // so the content provider knows what kind of value is being inserted.
            categoryValues.put(KnowPriceContract.CategoryEntry.COLUMN_CATEGORY_NAME, category);
            categoryValues.put(KnowPriceContract.CategoryEntry.COLUMN_CATEGORY_IMAGE, catimage);
            categoryValues.put(KnowPriceContract.CategoryEntry.COLUMN_CATEGORY_FAV, "N");


            Uri insertedUri = getContext().getContentResolver().insert(
                    KnowPriceContract.CategoryEntry.CONTENT_URI,
                    categoryValues);
            categoryId = ContentUris.parseId(insertedUri);
        }
        categoryCursor.close();
        return categoryId;
    }

    private void notifyOffer() {

        Log.d(LOG_TAG,"Inside Notify");
        Context context = getContext();
        //checking the last update and notify if it' the first of the day
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String displayNotificationsKey = context.getString(R.string.pref_enable_notifications_key);
        boolean displayNotifications = prefs.getBoolean(displayNotificationsKey,
                Boolean.parseBoolean(context.getString(R.string.pref_enable_notifications_default)));

        if ( displayNotifications ) {
            Log.d(LOG_TAG,"display Notify");
            String lastNotificationKey = context.getString(R.string.pref_last_notification);
            long lastSync = prefs.getLong(lastNotificationKey, 0);

            if (System.currentTimeMillis() - lastSync >= DAY_IN_MILLIS) {

                Uri offerUri = KnowPriceContract.ItemEntry.buildOffersForShoppingList( Utility.getPreferredCountry(mContext),Utility.getPreferredCity(mContext));

                Log.d(LOG_TAG,offerUri.toString());
                // we'll query our contentProvider, as always
                Cursor cursor = context.getContentResolver().query(offerUri, NOTIFY_OFFER_PROJECTION, null, null, null);
                if (cursor.moveToFirst()) {
                    Log.d(LOG_TAG,cursor.getString(INDEX_ITEM_NAME));
                    Resources resources = context.getResources();
                    Bitmap largeIcon = BitmapFactory.decodeResource(resources,R.mipmap.ic_launcher);

                    String title =  context.getString(R.string.app_name);
                    String contentText = cursor.getString(INDEX_CAT_NAME) +" on SALE";
                    String bigText = String.format(context.getString(R.string.format_notification),
                            cursor.getString(INDEX_ITEM_NAME),
                            cursor.getString(INDEX_SALE_PRICE),
                            cursor.getString(INDEX_LOCATION));

                 NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getContext())
                                    .setSmallIcon(R.drawable.ic_note_add)
                                    .setColor(ContextCompat.getColor(mContext,R.color.colorPrimaryDark))
                                    .setLargeIcon(largeIcon)
                                    .setContentTitle(title).setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(bigText))
                                    .setContentText(contentText);

                    Intent resultIntent = new Intent(context, MainActivity.class);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager =
                            (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    // WEATHER_NOTIFICATION_ID allows you to update the notification later on.
                    mNotificationManager.notify(OFFER_NOTIFICATION_ID, mBuilder.build());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putLong(lastNotificationKey, System.currentTimeMillis());
                    editor.commit();
                }
                cursor.close();
            }
        }
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    private static Account  getSyncAccount(Context context) {
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));
        if ( null == accountManager.getPassword(newAccount) ) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context);
        }

        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        KnowPriceSyncAdapter.configurePeriodicSync(context,SYNC_INTERVAL,SYNC_FLEXTIME);

        ContentResolver.setSyncAutomatically(newAccount,context.getString(R.string.content_authority),true);

        syncImmediately(context);
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }


    private static void configurePeriodicSync(Context context, int syncInterval, int syncFlextime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, syncFlextime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }


}
