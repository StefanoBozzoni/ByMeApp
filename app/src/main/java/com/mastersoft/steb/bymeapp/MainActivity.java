package com.mastersoft.steb.bymeapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mastersoft.steb.bymeapp.adapters.fbServiceReqAdapter;
import com.mastersoft.steb.bymeapp.model.Offer;
import com.mastersoft.steb.bymeapp.model.ServiceReq;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {


    private MyRecyclerView    myRecyclerView;
    fbServiceReqAdapter       mServiceReqAdapter;
    LinearLayoutManager       mLinearLayoutManager;
    private                   FirebaseAuth mFirebaseAuth;
    private                   FirebaseAuth.AuthStateListener mAuthStateListener;
    private                   FirebaseUser mFbUser;
    private                   RecyclerView.AdapterDataObserver mDataObserver;
    private static final int  RC_SIGN_IN = 1;

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseDatabase dbInstance=FirebaseDatabase.getInstance();
        mFirebaseAuth       =FirebaseAuth.getInstance();

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("ServiceReq")
                .orderByChild("timestamp").limitToLast(10);

        FirebaseRecyclerOptions<ServiceReq> options = new FirebaseRecyclerOptions.Builder<ServiceReq>()
                .setQuery(query, ServiceReq.class)
                .setLifecycleOwner(this)
                .build();
        mServiceReqAdapter = new fbServiceReqAdapter(options,Constants.SR_ADD_OFFER);

        myRecyclerView=findViewById(R.id.serviceReq_rv);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myRecyclerView.setLayoutManager(mLinearLayoutManager);


        mDataObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                myRecyclerView.restoreScrollPosition();
            }
        };

        mServiceReqAdapter.registerAdapterDataObserver(mDataObserver);

        //Insert Offers
        /*
        String id = mDbOffers.push().getKey();
        Date d= new Date();
        long milliseconds = d.getTime();
        Offer anOffer = new Offer(id, "Roma",milliseconds,10.23);
        mDbOffers.child(id).setValue(anOffer);
        Toast.makeText(this, "Offer added", Toast.LENGTH_LONG).show();
        */
        /*
        Date d= new Date();
        long milliseconds = d.getTime();
        String id = mDbServReq.push().getKey();
        ServiceReq sr = new ServiceReq("userIdProva2","via del mare 111, roma","Roma","non lo so","è solo una prova di servizio","delivery time",30.10,milliseconds);
        mDbServReq.child(id).setValue(sr);
        Toast.makeText(this, "Service request added", Toast.LENGTH_LONG).show();
        */


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    onUserSignInInitialize(user);
                    //Toast.makeText(MainActivity.this, "You're now signed in. Welcome to ByMeApp.", Toast.LENGTH_SHORT).show();
                } else {
                    // User is signed out
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.PhoneBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

    }

    private void onUserSignInInitialize(FirebaseUser user) {
        mFbUser=user;
        mServiceReqAdapter.setUser(user);
        //mLinearLayoutManager = new LinearLayoutManager(this);
        //mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //myRecyclerView.setLayoutManager(mLinearLayoutManager);
        myRecyclerView.setAdapter(mServiceReqAdapter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        //mServiceReqAdapter.registerAdapterDataObserver(mDataObserver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        //mServiceReqAdapter.unregisterAdapterDataObserver(mDataObserver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_myServices) {
            //Invoca activity che mostra i servizi dell'utente
            Intent intent = new Intent(this,UserServicesReq.class);
            intent.putExtra(Constants.SERVICE_REQ_PARAM,Constants.SR_SEE_OFFER);
            intent.putExtra(Constants.USER_ID, mFbUser.getUid());
            startActivity(intent);
            return true;
        }

        if (id == R.id.menu_myOffers) {
            //Invoca activity che mostra i servizi dell'utente
            Intent intent = new Intent(this,OfferListActivity.class);
            intent.putExtra(Constants.OFFER_LIST_PARAM,Constants.OF_USER_OFFER);
            intent.putExtra(Constants.USER_ID         ,mFbUser.getUid());
            startActivity(intent);
            return true;
        }

        if (id == R.id.menu_signOut) {
            AuthUI.getInstance().signOut(this);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==RC_SIGN_IN) {
            if (resultCode==RESULT_OK) {
            }

        }
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
