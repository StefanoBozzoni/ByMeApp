package com.mastersoft.steb.bymeapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mastersoft.steb.bymeapp.adapters.fbServiceReqAdapter;
import com.mastersoft.steb.bymeapp.model.Offer;
import com.mastersoft.steb.bymeapp.model.Prova;
import com.mastersoft.steb.bymeapp.model.ServiceReq;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference mDbOffers;
    private DatabaseReference mDbServReq;

    private ArrayList<Offer>      offers;
    private ServiceReq[]          srvReqList;
    private ArrayList<Prova>      provas;
    private RecyclerView          myRecyclerView;
    fbServiceReqAdapter           mServiceReqAdapter;
    LinearLayoutManager           linearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseDatabase dbInstance=FirebaseDatabase.getInstance();
        mDbOffers  = dbInstance.getReference("Offers");
        mDbServReq = dbInstance.getReference("ServiceReq");

        offers = new ArrayList<Offer>();
        provas = new ArrayList<Prova>();
        srvReqList=null;

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

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myRecyclerView.setLayoutManager(linearLayoutManager);
        myRecyclerView.setAdapter(mServiceReqAdapter);
        //mServiceReqAdapter.setData(srvReqList);

    }

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
    protected void onStop() {
        super.onStop();
        mServiceReqAdapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mServiceReqAdapter.startListening();

        /*
        mDbServReq.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //ServiceReq sr = dataSnapshot.getValue(ServiceReq.class);
                //srvReqList.add(sr);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        }
        );

        mDbServReq.addValueEventListener(new ValueEventListener() {
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //
            }

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myRecyclerView.setAdapter(mServiceReqAdapter);
            }
        });

        */

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
            startActivity(intent);
            return true;
        }

        if (id == R.id.menu_myOffers) {
            //Invoca activity che mostra i servizi dell'utente
            Intent intent = new Intent(this,OfferListActivity.class);
            intent.putExtra(Constants.OFFER_LIST_PARAM,Constants.OF_USER_OFFER);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
