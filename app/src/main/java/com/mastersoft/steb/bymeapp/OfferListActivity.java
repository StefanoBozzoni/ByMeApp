package com.mastersoft.steb.bymeapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mastersoft.steb.bymeapp.adapters.fbOffersAdapter;
import com.mastersoft.steb.bymeapp.adapters.fbServiceReqAdapter;
import com.mastersoft.steb.bymeapp.model.Offer;
import com.mastersoft.steb.bymeapp.model.ServiceReq;

public class OfferListActivity extends AppCompatActivity {
    private RecyclerView            myRecyclerView;
    private fbOffersAdapter         mOffersAdapter;
    private LinearLayoutManager     linearLayoutManager;
    private int                     mCallerParam;
    private String                  mServiceKey;


    @Override
    protected void onStart() {
        super.onStart();
        mOffersAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mOffersAdapter.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offert_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.my_offer_list));
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        mCallerParam =0;
        Intent callerIntent=getIntent();
        if ((callerIntent.hasExtra(Constants.OFFER_LIST_PARAM)) && (callerIntent.getExtras()!=null)) {
            mCallerParam = callerIntent.getExtras().getInt(Constants.OFFER_LIST_PARAM);
            mServiceKey  = callerIntent.getExtras().getString(Constants.SERVICE_KEY,"");
        }

        FirebaseRecyclerOptions<Offer> options=null;

        if (mCallerParam==Constants.OF_USER_OFFER) {
            Query query = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Offers")
                    .orderByChild("userID")
                    .equalTo("userIdProva");

            options = new FirebaseRecyclerOptions.Builder<Offer>()
                    .setQuery(query, Offer.class)
                    .build();
        } else if (mCallerParam==Constants.OF_SRV_OFFER)
        {
            Query query = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Offers")
                    .orderByChild("SerReqID")
                    .equalTo(mServiceKey);

            options = new FirebaseRecyclerOptions.Builder<Offer>()
                    .setQuery(query, Offer.class)
                    .build();
        }

        if (options!=null) {
            mOffersAdapter = new fbOffersAdapter(options);
            myRecyclerView = findViewById(R.id.offers_rv);
            myRecyclerView.setHasFixedSize(true);

            linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            myRecyclerView.setLayoutManager(linearLayoutManager);
            myRecyclerView.setAdapter(mOffersAdapter);
        }
    }
}
