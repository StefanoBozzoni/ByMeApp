package com.mastersoft.steb.bymeapp.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mastersoft.steb.bymeapp.MainActivity;
import com.mastersoft.steb.bymeapp.R;
import com.mastersoft.steb.bymeapp.model.ServiceReq;

import java.util.ArrayList;

/**
 * Implementation of App Widget functionality.
 */
public class ByMeAppWidget extends AppWidgetProvider {

    private static DatabaseReference  mDbServReq;
    private static FirebaseAuth       mFirebaseAuth;
    FirebaseDatabase                  dbInstance;
    private static ChildEventListener mChildEventListener;
    private static ValueEventListener mEventListner;


    static void updateAppWidget(Context context, final AppWidgetManager appWidgetManager, final int appWidgetId) {

        // Construct the RemoteViews object
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_byme_app);
        final ArrayList<ServiceReq> srvcReq = new ArrayList<ServiceReq>();

        FirebaseDatabase dbInstance = FirebaseDatabase.getInstance();
        mDbServReq      = dbInstance.getReference("ServiceReq");
        mFirebaseAuth   = FirebaseAuth.getInstance();
        Query q=null;
        if (mDbServReq!=null)
            q=mDbServReq.orderByChild("timestamp").limitToLast(5);


        if (mChildEventListener==null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    ServiceReq sr = dataSnapshot.getValue(ServiceReq.class);
                    srvcReq.add(sr);
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
            };
            if (q!=null)
               q.addChildEventListener(mChildEventListener);
        }

        if (mEventListner==null) {
            mEventListner=new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (srvcReq.size()>0) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder sb = new StringBuilder();
                                for (int i = 0; i < srvcReq.size(); i++) {
                                    ServiceReq sr = srvcReq.get(i);
                                    String descr = String.valueOf(i+1) + ") " + sr.getShortDescr();//.substring(10);
                                    sb.append(descr);
                                    sb.append("\r\n");
                                    String deliveryDate=sr.getDeliveryDate();
                                    if (deliveryDate==null) deliveryDate="";
                                    if (deliveryDate.equals("null")) deliveryDate="";
                                    String deliveryPlace=sr.getDeliveryPlace();
                                    if (deliveryPlace.length()>10) deliveryPlace=sr.getDeliveryPlace().substring(10);
                                    descr=String.format("    %s - %s",deliveryPlace,deliveryDate);
                                    sb.append(descr);
                                    sb.append("\r\n");
                                }
                                views.setTextViewText(R.id.appwidget_text, sb.toString());
                                appWidgetManager.updateAppWidget(appWidgetId, views);
                            }
                        }).start();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            if (q!=null)
               q.addValueEventListener(mEventListner);
        }

        Intent i= new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context,0,i,0);
        views.setOnClickPendingIntent(R.id.appwidget_text, pi);

        // Instruct the widget manager to update the widget
         appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);

        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        mChildEventListener=null;mEventListner=null;

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        if (mChildEventListener!=null) {
            mDbServReq.removeEventListener(mChildEventListener);
            mChildEventListener=null;
        }
        if (mEventListner!=null) {
            mDbServReq.removeEventListener(mEventListner);
            mEventListner=null;
        }
    }
}

