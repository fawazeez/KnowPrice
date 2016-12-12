package com.adupp.aduppil.knowprice.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.adupp.aduppil.knowprice.R;
import com.adupp.aduppil.knowprice.ShoppingListActivity;
import com.adupp.aduppil.knowprice.data.KnowPriceContract;
import com.adupp.aduppil.knowprice.utils.Utility;

/**
 * Created by fawaz on 12/8/2016.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class WidgetRemoteViewsService extends RemoteViewsService {

    public final String LOG_TAG = WidgetRemoteViewsService.class.getSimpleName();
    private static final String[] SHOPPINGLIST_COLUMNS = {
            KnowPriceContract.ShoppingListEntry.TABLE_NAME + "." + KnowPriceContract.ShoppingListEntry._ID,
            KnowPriceContract.ShoppingListEntry.COLUMN_ITEM_NAME,
            KnowPriceContract.ShoppingListEntry.COLUMN_ITEM_UOM,
            KnowPriceContract.ShoppingListEntry.COLUMN_ITEM_QTY,
            KnowPriceContract.ShoppingListEntry.COLUMN_ITEM_PUR_DATE
    } ;
    public static final int COL_ENTRY_ID = 0;
    public static final int COL_ITEM_NAME = 1;
    public static final int COL_ITEM_UOM = 2;
    public static final int COL_ITEM_QTY = 3;
    public static final int COL_ITEM_PUR_DATE = 4;


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new RemoteViewsFactory() {
            private Cursor data;
            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }

                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(KnowPriceContract.ShoppingListEntry.CONTENT_URI,
                        SHOPPINGLIST_COLUMNS,
                        KnowPriceContract.ShoppingListEntry.COLUMN_ITEM_PUR_DATE + " < " + Utility.dateAdd(2),
                        null,
                        KnowPriceContract.ShoppingListEntry.COLUMN_ITEM_PUR_DATE + " ASC");
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int i) {
                if (i == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(i)) {
                    return null;
                }
                Resources res = getResources();

                String UXformat = String.format(res.getString(R.string.shopping_list),data.getString(ShoppingListActivity.COL_ITEM_NAME),data.getString(ShoppingListActivity.COL_ITEM_QTY),data.getString(ShoppingListActivity.COL_ITEM_UOM)," ");

                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);
                remoteViews.setTextViewText(R.id.appwidget_text, UXformat);
                final Intent fillInIntent = new Intent();
                fillInIntent.setData(KnowPriceContract.ShoppingListEntry.CONTENT_URI);
                remoteViews.setOnClickFillInIntent(R.id.widget_list,fillInIntent);

                return remoteViews;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int i) {
                if (data.moveToPosition(i))
                    return data.getLong(COL_ENTRY_ID);
                return i;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        }
                ;
    }
}
