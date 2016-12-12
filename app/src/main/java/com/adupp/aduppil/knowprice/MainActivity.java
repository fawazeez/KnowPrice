package com.adupp.aduppil.knowprice;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.adupp.aduppil.knowprice.sync.KnowPriceSyncAdapter;
import com.adupp.aduppil.knowprice.utils.Constants;
import com.adupp.aduppil.knowprice.utils.FetchAddress;
import com.adupp.aduppil.knowprice.utils.Utility;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements CategoryFragment.Callback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String OFFERFRAGMENT_TAG = "OFTAG";
    protected Location mLastLocation;
    private AddressResultReceiver mResultReceiver;
    private boolean mAddressRequested = true;
    private boolean mTwoPane;
    private String mCountry;
    private String mCity;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private String mCountryOutput;
    private String mCityOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCountry = Utility.getPreferredCountry(this);
        mCity = Utility.getPreferredCity(this);
        setContentView(R.layout.activity_main);
        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3940256099942544~6300978111");
        if (findViewById(R.id.offer_detail_container) != null) {

            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.offer_detail_container, new OfferActivityFragment(), OFFERFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

        CategoryFragment categoryFragment = ((CategoryFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_category));
        categoryFragment.setmPane(mTwoPane);
        KnowPriceSyncAdapter.initializeSyncAdapter(this);



//        if (mGoogleApiClient.isConnected() && mLastLocation != null) {
//            startIntentService();
//        }
//
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_offer, menu);
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultcode = apiAvailability.isGooglePlayServicesAvailable(this);
        MenuItem item = menu.findItem(R.id.action_LocationSync);
        if (resultcode != ConnectionResult.SUCCESS)
            item.setVisible(false);
        else
            item.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_shoppingList) {
            startActivity(new Intent(this, ShoppingListActivity.class));
            return true;
        }
        if (id == R.id.action_LocationSync) {
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        syncUserLocation(false);
    }

    @Override
    public void onItemSelected(Uri catUri) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(OfferActivityFragment.OFFER_URI, catUri);

            OfferActivityFragment fragment = new OfferActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.offer_detail_container, fragment, OFFERFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, OfferActivity.class)
                    .setData(catUri);
//            startActivity(intent);
            ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this);
            ActivityCompat.startActivity(this,intent,activityOptionsCompat.toBundle());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCountry.equals(getString(R.string.pref_country_default)) && mCity.equals(getString(R.string.pref_city_default)))
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {

        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

//        mLocationRequest = LocationRequest.create();
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setInterval(10);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            // Determine whether a Geocoder is available.
            if (!Geocoder.isPresent()) {
                Toast.makeText(this, R.string.no_geocoder_available,
                        Toast.LENGTH_LONG).show();
                return;
            }


            if (mAddressRequested) {
                startIntentService();
            }
        }
//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

//    @Override
//    public void onLocationChanged(Location location) {
//        Toast.makeText(this,Double.toString(location.getLatitude()),Toast.LENGTH_LONG).show();
//    }

    protected void startIntentService() {
        Intent intent = new Intent(this, FetchAddress.class);
        mResultReceiver = new AddressResultReceiver(new Handler(),this);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);
    }

    private class AddressResultReceiver extends ResultReceiver{
      Context mContext;
        public AddressResultReceiver(Handler handler,Context context) {
            super(handler);
            mContext= context;
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            mCountryOutput = resultData.getString(Constants.RESULT_COUNTRY_KEY);
            mCityOutput = resultData.getString(Constants.RESULT_CITY_KEY);
            Log.i(LOG_TAG,mCountryOutput);
            if (resultCode == Constants.SUCCESS_RESULT) {
            Utility.setPreferredLocation(mContext,mCountryOutput,mCityOutput);
                syncUserLocation(true);
            }
        }
    }

    private void syncUserLocation(boolean sync) {

        if (sync)
            Toast.makeText(this,getString(R.string.locationSyncInfo),Toast.LENGTH_SHORT).show();

        String country = Utility.getPreferredCountry(this);
        String city = Utility.getPreferredCity(this);
        if (country != null && city != null) {
            if (!country.equals(mCountry) && city.equals(mCity))
                Toast.makeText(this,getString(R.string.updateCity),Toast.LENGTH_SHORT).show();
             if (!country.equals(mCountry) || !city.equals(mCity) || sync){
                CategoryFragment ff = (CategoryFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_category);
                if (null != ff) {
                    ff.onLocationChanged();
                }
                OfferActivityFragment df = (OfferActivityFragment) getSupportFragmentManager().findFragmentByTag(OFFERFRAGMENT_TAG);
                if (null != df) {
                    df.onLocationChanged(country,city);
                }
                mCountry = country;
                mCity = city;
            }
        }
    }
}
