package com.adupp.aduppil.knowprice;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.TextView;

import com.adupp.aduppil.knowprice.adapter.CategoryAdapter;
import com.adupp.aduppil.knowprice.data.KnowPriceContract;
import com.adupp.aduppil.knowprice.sync.KnowPriceSyncAdapter;
import com.adupp.aduppil.knowprice.utils.Utility;


/**
 * Created by fawaz on 11/13/2016.
 */
public class CategoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = "CategoryFragment";

    private static final String SELECTED_KEY = "selected_position";
    private int mChoiceMode;
    private String mFilter;
    private boolean mAutoSelectView;
    private int mPane=1;
    private static final int CATEGORY_LOADER = 0;
    private int mPosition = RecyclerView.NO_POSITION;
    private CategoryAdapter mCategoryAdapter;
    private RecyclerView mRecyclerView;

    private static final String[] CATEGORY_COLUMNS = {
            KnowPriceContract.CategoryEntry._ID,
            KnowPriceContract.CategoryEntry.COLUMN_CATEGORY_NAME,
            KnowPriceContract.CategoryEntry.COLUMN_CATEGORY_IMAGE
    } ;
    public static final int COL_CATEGORY_ID = 0;
     public static final int COL_CATEGORY_NAME = 1;
    public static final int COL_CATEGORY_IMAGE = 2;


    public CategoryFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_category, container, false);
        View emptyView =  rootView.findViewById(R.id.recycleview_category_empty);

        mCategoryAdapter = new CategoryAdapter(getActivity(), new CategoryAdapter.CategoryAdapterOnClickHandler() {

            @Override
            public void onClick(Long categoryId, CategoryAdapter.ViewHolder vh) {

                    ((Callback) getActivity()).onItemSelected(KnowPriceContract.ItemEntry.builditemCategoryUri(categoryId));
mPosition = vh.getAdapterPosition();
            }
        },emptyView,mChoiceMode);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.category_recycleview);
//        int mNoOfColumns = Utility.calculateNoOfColumns(getActivity());
        int mNoOfColumns = getResources().getInteger(R.integer.grid_columns);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),mNoOfColumns));
        mRecyclerView.setAdapter(mCategoryAdapter);


        if (savedInstanceState != null ) {
            mCategoryAdapter.onRestoreInstanceState(savedInstanceState);
        }
        return rootView;

    }

    private void updateEmptyView() {
        if (mCategoryAdapter.getItemCount() == 0) {
            TextView tv = (TextView) getView().findViewById(R.id.recycleview_category_empty);
            int message = R.string.empty_category_list;
            boolean networkAvailable = Utility.isNetworkAvailable(getActivity());
            if (null != tv) {
                if (mFilter != null)
                    tv.setText(String.format(getString(R.string.empty_category_search), mFilter));
                else {
                    if (!networkAvailable) {
                        message = R.string.empty_category_no_network;
                    } else {
                        Utility.setPreferredLocation(getContext(), getString(R.string.pref_country_default), getString(R.string.pref_city_default));
                        onLocationChanged(null);
                    }
                    tv.setText(message);
                }
            }
        }
    }

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CategoryFragment,
                0, 0);
        mChoiceMode = a.getInt(R.styleable.CategoryFragment_android_choiceMode, AbsListView.CHOICE_MODE_SINGLE);
        mAutoSelectView = a.getBoolean(R.styleable.CategoryFragment_autoSelectView, false);
        a.recycle();
    }

    public void setmPane(boolean b) {
        if(b)
            mPane=2;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(CATEGORY_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    void onLocationChanged( String filter) {
        mFilter = filter;
        if (filter == null)
        updateCategory();
        getLoaderManager().restartLoader(CATEGORY_LOADER, null, this);
    }

    private void updateCategory() {
        KnowPriceSyncAdapter.syncImmediately(getActivity());

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        mCategoryAdapter.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri CategoryUri;
        if (mFilter != null)
            CategoryUri = KnowPriceContract.CategoryEntry.buildCategoryFilerUri(mFilter);
        else
            CategoryUri = KnowPriceContract.CategoryEntry.buildCategoryFullUri();
        Log.d(LOG_TAG,CategoryUri.toString() );
        return new CursorLoader(getActivity(),
                CategoryUri,
                CATEGORY_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCategoryAdapter.swapCursor(data);
        data.moveToPosition(2);
        updateEmptyView();

      if ( data.getCount() > 0 )
        {mRecyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (mRecyclerView.getChildCount() > 0) {
                    mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                    int itemPosition = mCategoryAdapter.getSelectedItemPosition();
                    if (RecyclerView.NO_POSITION == itemPosition) itemPosition = 0;
                    RecyclerView.ViewHolder vh = mRecyclerView.findViewHolderForAdapterPosition(itemPosition);
                    if (null != vh && mAutoSelectView) {
                        mCategoryAdapter.selectView(vh);
                    }
                    return true;
                }
                return false;
            }
        });

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    mCategoryAdapter.swapCursor(null);

    }

    public interface Callback {
         void onItemSelected(Uri dateUri);
    }
}

