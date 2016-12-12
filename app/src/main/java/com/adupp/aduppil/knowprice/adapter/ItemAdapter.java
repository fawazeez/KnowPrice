package com.adupp.aduppil.knowprice.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adupp.aduppil.knowprice.CategoryFragment;
import com.adupp.aduppil.knowprice.OfferActivityFragment;
import com.adupp.aduppil.knowprice.R;
import com.squareup.picasso.Picasso;

/**
 * Created by fawaz on 11/16/2016.
 */

public class ItemAdapter extends CursorAdapter {
    public ItemAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public static class ViewHolder {
        public final ImageView itemImageView;
        public final TextView itemTextView;
        public final TextView actualPriceView;
        public final TextView offerPriceView;
        public final TextView locationTextView;
        public final ImageView locImageView;
        public ViewHolder(View view) {
            itemImageView = (ImageView) view.findViewById(R.id.itemImageView);
            itemTextView = (TextView) view.findViewById(R.id.itemTextView);
            actualPriceView = (TextView) view.findViewById(R.id.actualTextView);
            offerPriceView = (TextView) view.findViewById(R.id.offerTextView);
            locationTextView = (TextView) view.findViewById(R.id.locationTextView);
            locImageView = (ImageView) view.findViewById(R.id.locationImageView);

        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        int   layoutId = R.layout.list_item_products;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String itemName = cursor.getString(OfferActivityFragment.COL_ITEM_NAME);
        String location = cursor.getString(OfferActivityFragment.COL_LOCATION);
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.itemTextView.setText(itemName);
        viewHolder.itemTextView.setContentDescription(context.getString(R.string.a11y_item,itemName));
        viewHolder.itemImageView.setContentDescription(context.getString(R.string.a11y_item,itemName));
        viewHolder.actualPriceView.setText(cursor.getString(OfferActivityFragment.COL_ACT_PRICE));
        viewHolder.offerPriceView.setText(cursor.getString(OfferActivityFragment.COL_SALE_PRICE));
        viewHolder.locationTextView.setText(cursor.getString(OfferActivityFragment.COL_LOCATION));
        String imgItemURL = "http://test.mmivisionuae.com/item/"+cursor.getString(OfferActivityFragment.COL_ITEM_IMAGE)+".jpg";
        String imgLocURL = "http://test.mmivisionuae.com/location/"+location+".jpg";
        viewHolder.locImageView.setContentDescription(context.getString(R.string.a11y_location,location));
        Picasso.with(context).load(imgItemURL).placeholder(R.mipmap.ic_launcher).into(viewHolder.itemImageView);
        Picasso.with(context).load(imgLocURL).placeholder(R.mipmap.ic_launcher).into(viewHolder.locImageView);

    }

}
