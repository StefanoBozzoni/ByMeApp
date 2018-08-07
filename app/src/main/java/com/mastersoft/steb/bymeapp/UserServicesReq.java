package com.mastersoft.steb.bymeapp;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mastersoft.steb.bymeapp.adapters.fbServiceReqAdapter;
import com.mastersoft.steb.bymeapp.model.ServiceReq;

public class UserServicesReq extends AppCompatActivity {

    MyRecyclerView          myRecyclerView;
    fbServiceReqAdapter     mServiceReqAdapter;
    LinearLayoutManager     linearLayoutManager;
    int                     mCallerParam;
    String                  mUserId;
    private MyRecyclerView.AdapterDataObserver mDataObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_req_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("My services requests");
        setSupportActionBar(toolbar);

        mCallerParam =0;
        Intent callerIntent=getIntent();
        if ((callerIntent.hasExtra(Constants.SERVICE_REQ_PARAM)) && (callerIntent.getExtras()!=null)) {
            mCallerParam = callerIntent.getExtras().getInt(Constants.SERVICE_REQ_PARAM);
            mUserId      = callerIntent.getExtras().getString(Constants.USER_ID,"");
        }
        final Context cntx = this;
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserServicesReq.this, ServiceReqForm.class);
                intent.putExtra(Constants.SERVICE_REQ_PARAM,Constants.SR_ADD_SERVICE);
                intent.putExtra(Constants.USER_ID,mUserId);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity)cntx,null);
                startActivity(intent,options.toBundle());
            }
        });

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("ServiceReq")
                .orderByChild("userID")
                .equalTo(mUserId);

        FirebaseRecyclerOptions<ServiceReq> options = new FirebaseRecyclerOptions.Builder<ServiceReq>()
                                                          .setQuery(query, ServiceReq.class)
                                                          .setLifecycleOwner(this)
                                                          .build();

        mServiceReqAdapter = new fbServiceReqAdapter(options,Constants.SR_SEE_OFFER, this);

        mDataObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                myRecyclerView.restoreScrollPosition();
            }
        };

        mServiceReqAdapter.registerAdapterDataObserver(mDataObserver);
        myRecyclerView     = findViewById(R.id.serviceReq_rv);
        myRecyclerView.setHasFixedSize(true);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myRecyclerView.setLayoutManager(linearLayoutManager);
        myRecyclerView.setAdapter(mServiceReqAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        myRecyclerView.storeScrollPosition();
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        mServiceReqAdapter.unregisterAdapterDataObserver(mDataObserver);
        super.onDestroy();
    }
}
