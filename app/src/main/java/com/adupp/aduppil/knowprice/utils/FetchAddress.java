package com.adupp.aduppil.knowprice.utils;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.adupp.aduppil.knowprice.MainActivity;
import com.adupp.aduppil.knowprice.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by fawaz on 12/8/2016.
 */

public class FetchAddress extends IntentService {

    private final String TAG = FetchAddress.class.getSimpleName();

public FetchAddress()
{
    super("FetchAddress");
}
    public FetchAddress(String name) {
        super(name);
    }

    protected ResultReceiver mReceiver;

    private void deliverResultToReceiver(int resultCode, String country,String city) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_COUNTRY_KEY, country);
        bundle.putString(Constants.RESULT_CITY_KEY, city);
        mReceiver.send(resultCode, bundle);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String errorMessage = "";

        // Get the location passed to this service through an extra.
        Location location = intent.getParcelableExtra(
                Constants.LOCATION_DATA_EXTRA);
        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.service_not_available);
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude(), illegalArgumentException);
        }
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
                Log.e(TAG, errorMessage);
            }
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage,"");
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
                Log.i(TAG,address.getAddressLine(i));
            }
            int lastAddressIndex =  address.getMaxAddressLineIndex();
            deliverResultToReceiver(Constants.SUCCESS_RESULT,address.getAddressLine(lastAddressIndex),address.getAddressLine(lastAddressIndex-1));

//            Log.i(TAG, getString(R.string.address_found));
//            deliverResultToReceiver(Constants.SUCCESS_RESULT,
//                    TextUtils.join(System.getProperty("line.separator"),
//                            addressFragments));
        }
    }
}
