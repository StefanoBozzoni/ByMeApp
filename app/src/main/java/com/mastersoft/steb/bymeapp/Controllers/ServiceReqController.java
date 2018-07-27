package com.mastersoft.steb.bymeapp.Controllers;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mastersoft.steb.bymeapp.adapters.ServiceReqAdapter;
import com.mastersoft.steb.bymeapp.model.ServiceReq;

public class ServiceReqController {
    static private ServiceReq sr;

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

    public ServiceReqController() {};

    public static Error validate(ServiceReq sr){
        if (sr.getShortDescr().trim().equals("")) {
            return new Error(10,"short service description can't be void");
        }

        if (sr.getDescription().trim().equals("")) {
            return new Error(20,"service description can't be void");
        }

        if (sr.getPropGain()==0) {
            return new Error(30,"service payment should be provided");
        }

        return new Error(0,"OK");

    }

    public static ServiceReq getServiceReqAtKey(String key, final ServiceReqAdapter.Completation c) {
        FirebaseDatabase dbInstance=FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = dbInstance.getReference("ServiceReq");

        databaseRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                sr = snapshot.getValue(ServiceReq.class);
                c.onComplete(sr);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return sr;
    }
}

