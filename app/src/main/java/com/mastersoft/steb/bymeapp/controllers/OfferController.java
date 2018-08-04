package com.mastersoft.steb.bymeapp.Controllers;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mastersoft.steb.bymeapp.adapters.fbOffersAdapter;
import com.mastersoft.steb.bymeapp.adapters.fbServiceReqAdapter;
import com.mastersoft.steb.bymeapp.model.Offer;
import com.mastersoft.steb.bymeapp.model.ServiceReq;

public class OfferController {

    public static class Error {
        private int errorCode;
        private String errorDescription;
        Error(int code, String description) {this.errorCode=code;this.errorDescription=description;};
        public int getErrorCode() {
            return errorCode;
        }
        public String getErrorDescription() {
            return errorDescription;
        }
    }

    public OfferController() {};

    public static OfferController.Error validate(Offer of){
        if (of.getServiceDescr().trim().equals("")) {
            return new OfferController.Error(10,"short service description can't be void in offer data");
        }

        if (of.getUserID().trim().equals("")) {
            return new OfferController.Error(20,"user ID can't be void");
        }

        if (of.getPropGain()==0) {
            return new OfferController.Error(30,"proposed offer gain should be provided");
        }

        if (of.getSerReqID().trim().equals("")) {
            return new OfferController.Error(20,"service Id can't be void in offer data");
        }


        return new OfferController.Error(0,"OK");

    }

    public static void getOfferAtKey(String key, final fbOffersAdapter.Completation c) {
        FirebaseDatabase dbInstance=FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = dbInstance.getReference("Offers");

        databaseRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Offer of = snapshot.getValue(Offer.class);
                c.onComplete(of);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
