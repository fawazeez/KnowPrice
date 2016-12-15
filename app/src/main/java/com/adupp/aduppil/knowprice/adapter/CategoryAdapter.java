package com.adupp.aduppil.knowprice.adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adupp.aduppil.knowprice.CategoryFragment;
import com.adupp.aduppil.knowprice.R;
import com.adupp.aduppil.knowprice.data.KnowPriceContract;
import com.adupp.aduppil.knowprice.utils.ItemChoiceManager;
import com.squareup.picasso.Picasso;

/**
 * Created by fawaz on 11/26/2016.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {


    private Cursor mCursor;
    final private CategoryAdapterOnClickHandler mClickHandler;
    final private View mEmptyView;

    final private ItemChoiceManager mICM;
    final private Context mContext;

    public CategoryAdapter(Context context, CategoryAdapterOnClickHandler dh, View emptyView, int choiceMode) {
        mContext = context;
        mEmptyView = emptyView;
        mClickHandler = dh;
        mICM = new ItemChoiceManager(this);
        mICM.setChoiceMode(choiceMode);

    }

    public static interface CategoryAdapterOnClickHandler {
        void onClick(Long id, ViewHolder vh);
    }



    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public Cursor getCursor() {
        return mCursor;
    }




    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if(parent instanceof RecyclerView) {
    int layoutId = R.layout.list_item_category;
    View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
    view.setFocusable(true);
    return new ViewHolder(view);
}
        else
{
    throw new RuntimeException("Not Bound to recyclerviewselection");
}
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String category = mCursor.getString(CategoryFragment.COL_CATEGORY_NAME);
        holder.categoryView.setText(category.substring(0,1).toUpperCase() + category.substring(1));
        holder.categoryView.setContentDescription(mContext.getString(R.string.a11y_category,category));

        holder.imageView.setContentDescription(mContext.getString(R.string.a11y_category,category));
        String imgURL = "http://test.mmivisionuae.com/category/"+mCursor.getString(CategoryFragment.COL_CATEGORY_IMAGE)+".jpg";
        Picasso.with(mContext).load(imgURL).placeholder(R.mipmap.ic_launcher).error(R.drawable.connection_error).resize(100,100).into(holder.imageView);
        mICM.onBindViewHolder(holder, position);
    }


    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mICM.onRestoreInstanceState(savedInstanceState);
    }

    public void onSaveInstanceState(Bundle outState) {
        mICM.onSaveInstanceState(outState);
    }

    public int getSelectedItemPosition() {
        return mICM.getSelectedItemPosition();
    }


    @Override
    public int getItemCount() {
        if ( null == mCursor ) return 0;
        return mCursor.getCount();
    }

    public   class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView categoryView;
        public final ImageView imageView;
        public ViewHolder(View view) {
            super(view);
            categoryView = (TextView) view.findViewById(R.id.categoryTextView);
            imageView= (ImageView)view.findViewById(R.id.catImageView);
            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            mClickHandler.onClick(mCursor.getLong(CategoryFragment.COL_CATEGORY_ID), this);
            mICM.onClick(this);
        }
    }


    public void selectView(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof ViewHolder) {
            ViewHolder vfh = (ViewHolder) viewHolder;
            vfh.onClick(vfh.itemView);
        }
    }
}
