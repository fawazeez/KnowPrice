package com.adupp.aduppil.knowprice.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import com.adupp.aduppil.knowprice.MainActivity;
import com.adupp.aduppil.knowprice.R;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by fawaz on 11/25/2016.
 */

public class Utility {
    public static String getPreferredCountry(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_country_key),
                context.getString(R.string.pref_country_default));
    }

    public static String getPreferredCity(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_city_key),
                context.getString(R.string.pref_city_default));
    }
    public static String getPreferredCountryadapter(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_MULTI_PROCESS);
        return prefs.getString(context.getString(R.string.pref_country_key),
                context.getString(R.string.pref_country_default));
    }

    public static String getPreferredCityadpater(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_MULTI_PROCESS);
        return prefs.getString(context.getString(R.string.pref_city_key),
                context.getString(R.string.pref_city_default));
    }



    public static void setPreferredLocation(Context context,String country,String city) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.pref_country_key),country);
        editor.putString(context.getString(R.string.pref_city_key),city);
        editor.apply();
    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 90);
        return noOfColumns;
    }

    static public boolean isNetworkAvailable(Context c) {
        ConnectivityManager connectivityManager=  (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();


    }

    public static String getFriendlyDayString(Context context, long mDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        java.util.Date strDate = new java.util.Date();
        String tdyDate = "o";
        try {
//                                strDate = sdf.parse("20161205");
            tdyDate = sdf.format(strDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
     return "today";
    }

    public static Long curDate()  {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        java.util.Date strDate = new java.util.Date();
        return Long.parseLong(dateFormat.format(strDate));
    }

    public static long dateAdd(long mDays)  {

        SimpleDateFormat dateFormat  = new SimpleDateFormat("yyyyMMdd");
        java.util.Date curDate = new java.util.Date();
        long addDays = mDays*  (24 * 60 * 60 * 1000);
        java.util.Date newDate = new Date(curDate.getTime() + addDays );
        return Long.parseLong(dateFormat.format(newDate));
    }

        public static String getDateDiff(long mDate) throws ParseException {

        SimpleDateFormat dateFormat  = new SimpleDateFormat("yyyyMMdd");
        java.util.Date strDate = dateFormat.parse(Long.toString(mDate));
        java.util.Date curDate = new java.util.Date();
        long difference = Math.abs(strDate.getTime() - curDate.getTime());
        long differenceDates = difference / (24 * 60 * 60 * 1000);
        return   Long.toString(differenceDates);
    }
}
