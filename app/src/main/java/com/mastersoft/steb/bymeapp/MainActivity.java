package com.mastersoft.steb.bymeapp;

import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mastersoft.steb.bymeapp.BroadcastReceivers.RefreshDataBroadcastReceiver;
import com.mastersoft.steb.bymeapp.adapters.ServiceReqAdapter;
import com.mastersoft.steb.bymeapp.model.ServiceReq;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    MyRecyclerView                myRecyclerView;
    ServiceReqAdapter             mServiceReqAdapter;
    LinearLayoutManager           mLinearLayoutManager;
    private                       FirebaseAuth mFirebaseAuth;
    private                       FirebaseAuth.AuthStateListener mAuthStateListener;
    private                       FirebaseUser mFbUser;
    private                       RecyclerView.AdapterDataObserver mDataObserver;
    private static final int      RC_SIGN_IN = 1;
    static final ArrayList<ServiceReq> mData = new ArrayList<>();
    private Query                 mQuery;
    private ChildEventListener    mEventListner;
    private FirebaseJobDispatcher mFirebaseJobDispatcher;
    private BroadcastReceiver mRefreshReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseDatabase dbInstance=FirebaseDatabase.getInstance();
        mFirebaseAuth              =FirebaseAuth.getInstance();

        mQuery = FirebaseDatabase.getInstance()
                .getReference()
                .child("ServiceReq")
                .orderByChild("timeStamp").limitToFirst(10);

        mServiceReqAdapter = new ServiceReqAdapter(Constants.SR_ADD_OFFER,this);
        myRecyclerView=findViewById(R.id.serviceReq_rv);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myRecyclerView.setLayoutManager(mLinearLayoutManager);
        myRecyclerView.setAdapter(mServiceReqAdapter);

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
                    onUserSignOutCleanUp();
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
        attachFireBaseListener();

        mRefreshReceiver = new RefreshDataBroadcastReceiver(myRecyclerView,mServiceReqAdapter,mData);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.REFRESHDATA_JOB_ACTION);
        registerReceiver(mRefreshReceiver, filter);

        startJobCheduler();
    }

    private void startJobCheduler() {

        Driver driver = new GooglePlayDriver(this);
        mFirebaseJobDispatcher = new FirebaseJobDispatcher(driver);

        Job constraintReminderJob = mFirebaseJobDispatcher.newJobBuilder()
                .setService(RefresDataService.class)
                .setTag(Constants.REFRESHDATA_JOB_TAG)
                .setConstraints(Constraint.ON_UNMETERED_NETWORK)
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        20,
                        60
                ))
                .setReplaceCurrent(true)
                .build();

        mFirebaseJobDispatcher.schedule(constraintReminderJob);
    }

    private void attachFireBaseListener() {
        if (mEventListner==null) {
            mData.clear();
            mEventListner = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    ServiceReq sr = dataSnapshot.getValue(ServiceReq.class);
                    String key = dataSnapshot.getKey();
                    if (sr != null) {
                        sr.setKey(key);
                        mData.add(sr);
                    }
                    mServiceReqAdapter.setData(mData);
                    myRecyclerView.restoreScrollPosition();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    ServiceReq sr = dataSnapshot.getValue(ServiceReq.class);
                    if (sr!=null) {
                        //since there will only be max ten servReq in mData
                        for (int i=0;i<mData.size();i++) {
                            if (mData.get(i).getKey().equals(dataSnapshot.getKey()))
                                mData.set(i,sr);
                        }
                        mServiceReqAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    ServiceReq sr = dataSnapshot.getValue(ServiceReq.class);
                    if (sr!=null) {
                        //since there will only be max ten servReq in mData
                        for (int i=0;i<mData.size();i++) {
                            if (mData.get(i).getKey().equals(dataSnapshot.getKey()))
                                mData.remove(i);
                        }
                        mServiceReqAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };
            mQuery.addChildEventListener(mEventListner);
        }

    }

    private void onUserSignOutCleanUp() {
        if (mEventListner!=null) {
            mQuery.removeEventListener(mEventListner);
            mEventListner = null;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        if (mFirebaseJobDispatcher!=null)
            mFirebaseJobDispatcher.cancel(Constants.REFRESHDATA_JOB_TAG);
        if (mRefreshReceiver!=null) {
            unregisterReceiver(mRefreshReceiver);
            mRefreshReceiver=null;
        }
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
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
        //myRecyclerView.storeScrollPosition();

        if (id == R.id.menu_myServices) {
            //Invoca activity che mostra i servizi dell'utente
            Intent intent = new Intent(this,UserServicesReq.class);
            intent.putExtra(Constants.SERVICE_REQ_PARAM,Constants.SR_SEE_OFFER);
            intent.putExtra(Constants.USER_ID, mFbUser.getUid());
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivity(intent, options.toBundle());
            return true;
        }

        if (id == R.id.menu_myOffers) {
            //Invoca activity che mostra i servizi dell'utente
            Intent intent = new Intent(this,OfferListActivity.class);
            intent.putExtra(Constants.OFFER_LIST_PARAM,Constants.OF_USER_OFFER);
            intent.putExtra(Constants.USER_ID         ,mFbUser.getUid());
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivity(intent, options.toBundle());
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
                //nothing to do...
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        myRecyclerView.storeScrollPosition();
        super.onSaveInstanceState(outState);
    }

    public static class RefresDataService extends JobService {
        private Query  mQuery;

        @Override
        public boolean onStartJob(JobParameters job) {
            // Do some work here

            Intent intent = new Intent(this,RefreshDataBroadcastReceiver.class);
            intent.setAction(Constants.REFRESHDATA_JOB_ACTION);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

            FirebaseDatabase dbInstance=FirebaseDatabase.getInstance();
            mQuery = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("ServiceReq")
                    .orderByChild("timeStamp").limitToFirst(10);

            mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    mData.clear();
                    //iterating through all the nodes
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        ServiceReq sr = postSnapshot.getValue(ServiceReq.class);
                        if (sr!=null) {
                            sr.setKey(postSnapshot.getKey());
                            mData.add(sr);
                        }
                    }

                    //Call a broadcastreceiver to refresh data of the recyclerview and set data of the adapter
                    Intent intent = new Intent();
                    intent.setAction(Constants.REFRESHDATA_JOB_ACTION);
                    sendBroadcast(intent);
                    Log.d(Constants.REFRESHDATA_JOB_TAG,RefresDataService.this.getString(R.string.log_service_refresh_str));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            return false; // Answers the question: "Is there still work going on?"
        }

        @Override
        public boolean onStopJob(JobParameters job) {
            return false; // Answers the question: "Should this job be retried?"
        }
    }
}
