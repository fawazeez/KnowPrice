package com.adupp.aduppil.knowprice;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.adupp.aduppil.knowprice.adapter.ShoppingListAdapter;
import com.adupp.aduppil.knowprice.data.KnowPriceContract;
import com.adupp.aduppil.knowprice.utils.Utility;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShoppingListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private ShoppingListAdapter mShoppingListAdapter;
    public static final String ACTION_DATA_UPDATED =
            "com.adupp.aduppil.knowprice.ACTION_DATA_UPDATED";
    private RadioGroup radioQtyGroup;
    private RadioButton radioQtyButton;
    private View userPrompt;
    private EditText itemText;
    private EditText qtyText;
    private EditText shopByText;
    private static final int SHOPPINGLIST_LOADER = 0;
    private static final String[] SHOPPINGLIST_COLUMNS = {
            KnowPriceContract.ShoppingListEntry.TABLE_NAME +
                    "." +KnowPriceContract.ItemEntry._ID,
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportLoaderManager().initLoader(SHOPPINGLIST_LOADER, null,this);

        mShoppingListAdapter =
                new ShoppingListAdapter(this,null,0);


        ListView listView = (ListView) findViewById(R.id.listview_shoppinglist);
        View emptyView = findViewById(R.id.listView_shoppingList_empty);
        listView.setEmptyView(emptyView);
        listView.setAdapter(mShoppingListAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                userPrompt = (LayoutInflater.from(ShoppingListActivity.this)).inflate(R.layout.prompt_shoppinglist,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(ShoppingListActivity.this);
                builder.setTitle(getString(R.string.title_activity_shopping_list));
                builder.setView(userPrompt).setPositiveButton(getString(R.string.add), null);
                itemText = (EditText)userPrompt.findViewById(R.id.itemText);
                qtyText = (EditText)userPrompt.findViewById(R.id.qtyText);
                shopByText = (EditText)userPrompt.findViewById(R.id.shopByText);
                radioQtyGroup = (RadioGroup) userPrompt.findViewById(R.id.radioQty);

                builder.setCancelable(true);
                final AlertDialog dialog =  builder.create();

                dialog.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(final DialogInterface dialog) {

                        Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        button.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                String itemName = itemText.getText().toString();
                                itemName = itemName.trim().toLowerCase();
                                String qty =  qtyText.getText().toString();
                                String shopBy = shopByText.getText().toString();
                                int selectedId = radioQtyGroup.getCheckedRadioButtonId();
                                String message =  getString(R.string.itemAdded);
                                if (itemName.contentEquals(""))
                                    message = getString(R.string.itemNull);
                                else if (qty.contentEquals(""))
                                    message = getString(R.string.qtyNull);
                                else if (shopBy.contentEquals(""))
                                    message = getString(R.string.shopByNull);
                                else  {
                                // find the radiobutton by returned id
                                long shopByDays = Long.parseLong(shopBy);

                                radioQtyButton = (RadioButton) userPrompt.findViewById(selectedId);

                                    Cursor shoppingListCursor = getContentResolver().query(
                                            KnowPriceContract.ShoppingListEntry.CONTENT_URI,
                                            new String[]{KnowPriceContract.ShoppingListEntry._ID},
                                            KnowPriceContract.ShoppingListEntry.COLUMN_ITEM_NAME + " = ?",
                                            new String[]{itemName},
                                            null);

                                    if (shoppingListCursor.moveToFirst()) {
                                        Toast.makeText(ShoppingListActivity.this, getString(R.string.item_exist_message), Toast.LENGTH_SHORT).show();
                                        shoppingListCursor.close();
                                    } else {
                                        ContentValues shoppingListValues = new ContentValues();
//                                        String[] strArray = itemName.split(" ");
//                                        StringBuilder builder = new StringBuilder();
//                                        for (String s : strArray) {
//                                            String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
//                                            builder.append(cap + " ");
//                                        }
                                        shoppingListValues.put(KnowPriceContract.ShoppingListEntry.COLUMN_ITEM_NAME, itemName);
                                        shoppingListValues.put(KnowPriceContract.ShoppingListEntry.COLUMN_ITEM_QTY, qty);
                                        shoppingListValues.put(KnowPriceContract.ShoppingListEntry.COLUMN_ITEM_PUR_DATE, Utility.dateAdd(shopByDays));
                                        shoppingListValues.put(KnowPriceContract.ShoppingListEntry.COLUMN_ITEM_UOM, radioQtyButton.getText().toString());


                                        Uri insertedUri = getContentResolver().insert(
                                                KnowPriceContract.ShoppingListEntry.CONTENT_URI,
                                                shoppingListValues);
                                        mShoppingListAdapter.notifyDataSetChanged();
                                        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED)
                                                .setPackage(getPackageName());
                                        sendBroadcast(dataUpdatedIntent);
                                    }
                                    //Dismiss once everything is OK.

                                    dialog.dismiss();
                                }
                                Toast toast = Toast.makeText(ShoppingListActivity.this,message,Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER,0,0);
                                toast.show();

                            }
                        });
                    }
                });
                dialog.show();
            }

        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = KnowPriceContract.ShoppingListEntry.COLUMN_ITEM_PUR_DATE + " ASC";
        return new CursorLoader(
              this,
                KnowPriceContract.ShoppingListEntry.CONTENT_URI,
                SHOPPINGLIST_COLUMNS,
                KnowPriceContract.ShoppingListEntry.COLUMN_ITEM_PUR_DATE + " > " + Utility.curDate(),
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mShoppingListAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
