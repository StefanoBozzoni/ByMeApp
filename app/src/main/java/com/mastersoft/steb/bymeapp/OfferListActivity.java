package com.mastersoft.steb.bymeapp;

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
    private RecyclerView   myRecyclerView;
    fbOffersAdapter        mOffersAdapter;
    LinearLayoutManager    linearLayoutManager;

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


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Offers")
                .orderByChild("userID")
                .equalTo("userIdProva");

        FirebaseRecyclerOptions<Offer> options = new FirebaseRecyclerOptions.Builder<Offer>()
                .setQuery(query, Offer.class)
                .build();

        mOffersAdapter = new fbOffersAdapter(options);
        myRecyclerView = findViewById(R.id.offers_rv);
        myRecyclerView.setHasFixedSize(true);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myRecyclerView.setLayoutManager(linearLayoutManager);
        myRecyclerView.setAdapter(mOffersAdapter);
    }
}
