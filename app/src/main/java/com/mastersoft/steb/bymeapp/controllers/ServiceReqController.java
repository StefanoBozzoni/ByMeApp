package com.mastersoft.steb.bymeapp.controllers;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mastersoft.steb.bymeapp.adapters.fbServiceReqAdapter;
import com.mastersoft.steb.bymeapp.model.ServiceReq;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ServiceReqController {

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

    public Error validate(ServiceReq sr){
        if (sr.getShortDescr().trim().equals("")) {
            return new Error(10,"short service description can't be void");
        }

        if (sr.getDescription().trim().equals("")) {
            return new Error(20,"service description can't be void");
        }

        if (sr.getPropGain()==0) {
            return new Error(30,"service payment should be provided");
        }

        if (!isValidDate(sr.getDeliveryDate(),"dd/mm/yyyy")) {
            return new Error(40,"delivery date is not a valid date");
        }

        if ((!isValidDate(sr.getDeliveryTime(),"hh:mm")) ||
            (!isValidDate(sr.getDeliveryTime(),"hh:mm a")))
        {
            return new Error(50,"delivery time is not a valid time");
        }


        return new Error(0,"OK");

    }

    public static void getServiceReqAtKey(String key, final fbServiceReqAdapter.Completation c) {
        FirebaseDatabase dbInstance=FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = dbInstance.getReference("ServiceReq");

        databaseRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ServiceReq sr = snapshot.getValue(ServiceReq.class);
                c.onComplete(sr);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public static boolean isValidDate(String input, String format) {
        boolean valid = false;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.ITALY);
            //dateFormat.setLenient(false) ;
            String output = dateFormat.format(dateFormat.parse(input));
            valid = input.equals(output);
        } catch (Exception ignore) {}

        return valid;
    }

}

