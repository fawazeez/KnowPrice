package com.adupp.aduppil.knowprice;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;

import com.adupp.aduppil.knowprice.adapter.CategoryAdapter;
import com.adupp.aduppil.knowprice.adapter.ItemAdapter;
import com.adupp.aduppil.knowprice.data.KnowPriceContract;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * A placeholder fragment containing a simple view.
 */
public class OfferActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = OfferActivityFragment.class.getSimpleName();
    static final String OFFER_URI = "URI";

    private static final String OFFER_SHARE_HASHTAG = " #KnowPriceApp";
    public static final String TRANSITION_ANIMATION = "TRANSITION" ;

    private ShareActionProvider mShareActionProvider;
    private Uri mUri;



    private static final String[] OFFER_COLUMNS = {
            KnowPriceContract.CategoryEntry.TABLE_NAME +
                    "." +KnowPriceContract.ItemEntry._ID,
            KnowPriceContract.ItemEntry.COLUMN_ITEM_NAME,
            KnowPriceContract.ItemEntry.COLUMN_ITEM_IMAGE,
            KnowPriceContract.ItemEntry.COLUMN_ITEM_ACT_PRICE,
            KnowPriceContract.ItemEntry.COLUMN_ITEM_SALE_PRICE,
            KnowPriceContract.ItemEntry.COLUMN_ITEM_LOCATION,
            KnowPriceContract.ItemEntry.COLUMN_START_DATE,
            KnowPriceContract.ItemEntry.COLUMN_END_DATE
    } ;
    public static final int COL_ITEM_ID = 0;
    public static final int COL_ITEM_NAME = 1;
    public static final int COL_ITEM_IMAGE = 2;
    public static final int COL_ACT_PRICE = 3;
    public static final int COL_SALE_PRICE = 4;
    public static final int COL_LOCATION = 5;
    public static final int COL_START_DATE = 6;
    public static final int COL_END_DATE = 7;


    private static final int OFFER_LOADER = 0;
    private ItemAdapter mItemAdapter;

    private boolean mTransitionAnimation;

    public OfferActivityFragment() {
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(OfferActivityFragment.OFFER_URI);
            mTransitionAnimation = arguments.getBoolean(TRANSITION_ANIMATION, false);
        }



        mItemAdapter = new ItemAdapter(getActivity(),null,0);
        View rootView = inflater.inflate(R.layout.fragment_offer, container, false);
        AdView mAdView = (AdView) rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        ListView categoryListView = (ListView)rootView.findViewById(R.id.listview_offer);
        View emptyView = rootView.findViewById(R.id.listView_offer_empty);
        categoryListView.setEmptyView(emptyView);
        categoryListView.setAdapter(mItemAdapter);
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if ( null != mUri ) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    OFFER_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mItemAdapter.swapCursor(data);
        if ( mTransitionAnimation ) {
            getActivity().supportStartPostponedEnterTransition();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mItemAdapter.swapCursor(null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(OFFER_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    void onLocationChanged(String country, String city) {
        // replace the uri, since the location has changed
        Uri uri = mUri;
        if (null != uri) {
            Uri updatedUri = KnowPriceContract.ItemEntry.builditemCategoryUri(1);
            mUri = updatedUri;
            getLoaderManager().restartLoader(OFFER_LOADER, null, this);
        }
    }

}
