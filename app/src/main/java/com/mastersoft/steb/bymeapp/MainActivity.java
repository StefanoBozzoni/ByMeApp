package com.mastersoft.steb.bymeapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

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
    private DatabaseReference mDbOffers;
    private DatabaseReference mDbServReq;

    private ArrayList<Offer>  offers;
    private ServiceReq[]      srvReqList;
    private RecyclerView      myRecyclerView;
    fbServiceReqAdapter       mServiceReqAdapter;
    LinearLayoutManager       mLinearLayoutManager;
    private                   FirebaseAuth mFirebaseAuth;
    private                   FirebaseAuth.AuthStateListener mAuthStateListener;
    private                   FirebaseUser mFbUser;
    private static final int  RC_SIGN_IN = 1;


    /*
    String id = mDatabaseOffers.push().getKey();

    //creating an Artist Object
    Prova aProva = new Prova(id, "prova");

    //Saving the Artist
    mDatabaseOffers.child(id).setValue(aProva);

    //displaying a success toast
    Toast.makeText(this, "Prova added", Toast.LENGTH_LONG).show();
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseDatabase dbInstance=FirebaseDatabase.getInstance();
        mFirebaseAuth       =FirebaseAuth.getInstance();

        mDbOffers  = dbInstance.getReference("Offers");
        mDbServReq = dbInstance.getReference("ServiceReq");

        offers = new ArrayList<Offer>();
        srvReqList=null;

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("ServiceReq")
                .orderByChild("timestamp").limitToLast(10);

        FirebaseRecyclerOptions<ServiceReq> options = new FirebaseRecyclerOptions.Builder<ServiceReq>()
                .setQuery(query, ServiceReq.class)
                .build();
        mServiceReqAdapter = new fbServiceReqAdapter(options,Constants.SR_ADD_OFFER);

        myRecyclerView=findViewById(R.id.serviceReq_rv);
        myRecyclerView.setHasFixedSize(true);


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
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myRecyclerView.setLayoutManager(mLinearLayoutManager);
        myRecyclerView.setAdapter(mServiceReqAdapter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mServiceReqAdapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mServiceReqAdapter.startListening();

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
}
