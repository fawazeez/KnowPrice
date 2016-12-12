package com.adupp.aduppil.knowprice;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class OfferActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);

        if (savedInstanceState == null) {

            Bundle arguments = new Bundle();
            arguments.putParcelable(OfferActivityFragment.OFFER_URI, getIntent().getData());
            arguments.putBoolean(OfferActivityFragment.TRANSITION_ANIMATION, true);

            OfferActivityFragment fragment = new OfferActivityFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.offer_detail_container, fragment)
                    .commit();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        supportPostponeEnterTransition();
    }

}
