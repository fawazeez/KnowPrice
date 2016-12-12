package com.adupp.aduppil.knowprice.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adupp.aduppil.knowprice.R;
import com.adupp.aduppil.knowprice.ShoppingListActivity;
import com.adupp.aduppil.knowprice.data.KnowPriceContract;
import com.adupp.aduppil.knowprice.utils.Utility;

import java.text.ParseException;

/**
 * Created by fawaz on 12/6/2016.
 */

public class ShoppingListAdapter extends CursorAdapter {


    public ShoppingListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    private String convertCursorRowToUXFormat(Cursor cursor) {

        String Days = "0" ;
        String daySeq = null;
        try {
            Days = Utility.getDateDiff(cursor.getLong(ShoppingListActivity.COL_ITEM_PUR_DATE));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (Days.contentEquals("0"))
             daySeq = "Today";
        else if (Days.contentEquals("1"))
            daySeq = "by Tomorrow";
        else
            daySeq = "by " + Days + " days";
        Resources res = mContext.getResources();
        String UXformat = String.format(res.getString(R.string.shopping_list),cursor.getString(ShoppingListActivity.COL_ITEM_NAME),cursor.getString(ShoppingListActivity.COL_ITEM_QTY),cursor.getString(ShoppingListActivity.COL_ITEM_UOM),daySeq);
        return UXformat;
//        return cursor.getString(ShoppingListActivity.COL_ITEM_NAME) +
//                " - " + cursor.getString(ShoppingListActivity.COL_ITEM_QTY)
//                +" " + cursor.getString(ShoppingListActivity.COL_ITEM_UOM) + " " + daySeq ;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_shoppinglist, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.shoppingListText.setText(convertCursorRowToUXFormat(cursor));

    }

    private static class ViewHolder {

        public final TextView shoppingListText;
        public ViewHolder(View view) {
            shoppingListText = (TextView) view.findViewById(R.id.listItemTextView);

        }
    }
}
