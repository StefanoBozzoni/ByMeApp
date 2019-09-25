package com.mastersoft.steb.bymeapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mastersoft.steb.bymeapp.controllers.OfferController;
import com.mastersoft.steb.bymeapp.controllers.ServiceReqController;
import com.mastersoft.steb.bymeapp.adapters.fbOffersAdapter;
import com.mastersoft.steb.bymeapp.adapters.fbServiceReqAdapter;
import com.mastersoft.steb.bymeapp.model.Offer;
import com.mastersoft.steb.bymeapp.model.ServiceReq;
import com.mastersoft.steb.bymeapp.utils.MaskWatcher;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OfferFormActivity extends AppCompatActivity implements fbServiceReqAdapter.Completation, fbOffersAdapter.Completation {
    String mKeyService;
    @BindView(R.id.deliveryDate_et)  EditText          deliveryDate_et;
    @BindView(R.id.deliveryTime_et)  EditText          deliveryTime_et;
    @BindView(R.id.deliveryPlace_et) EditText          deliveryPlace_et;
    @BindView(R.id.proposedGain_et)  EditText          proposedGain_et;
    @BindView(R.id.serviceDescr_lbl) TextView          serviceDescr_lbl;
    @BindView(R.id.notes_et)         EditText          notes_et;
    @BindView(R.id.clOfferForm)      CoordinatorLayout coordinatorLayout;
    @BindView(R.id.insertButton)     Button            insertButton;

    private                          MaskWatcher       mDateWatcher;
    private                          MaskWatcher       mTimeWatcher;
    private                          DatabaseReference mDbOffer;
    private                          String            mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //prova
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_form);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDateWatcher = new MaskWatcher("##/##/##");
        mTimeWatcher = new MaskWatcher("##:##");
        deliveryDate_et.addTextChangedListener(mDateWatcher);
        deliveryTime_et.addTextChangedListener(mTimeWatcher);

        if (getIntent().hasExtra(Constants.SERVICE_KEY)) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) mKeyService = extras.getString(Constants.SERVICE_KEY);
        }

        if (mKeyService!=null)
            ServiceReqController.getServiceReqAtKey(mKeyService, this);

        FirebaseDatabase dbInstance=FirebaseDatabase.getInstance();
        mDbOffer = dbInstance.getReference("Offers");
        Intent callerIntent = getIntent();

        int mCallerParam=0;
        String mOfferKey="";
        //activity called in insert mode
        if ((callerIntent.hasExtra(Constants.OFFER_FORM_PARAM)) && (callerIntent.getExtras()!=null)) {
            mCallerParam = callerIntent.getExtras().getInt(Constants.OFFER_FORM_PARAM);
            mOfferKey    = callerIntent.getExtras().getString(Constants.OFFER_KEY);
            mUserId      = callerIntent.getExtras().getString(Constants.USER_ID,"");
        }

        //activity called in view mode
        if (mCallerParam==Constants.OF_VIEW_OFFER_FORM) {
            DisableEdits();
            if (mOfferKey!=null)
                loadDbValues(mOfferKey);
            insertButton.setVisibility(View.INVISIBLE);
        }
    }

    private void DisableEdits() {
            disableEditText(deliveryDate_et);
            disableEditText(deliveryTime_et);
            disableEditText(deliveryPlace_et);
            disableEditText(proposedGain_et);
            disableEditText(notes_et);
    }

    private void loadDbValues(String key) {
            if (!key.trim().equals("")) {
                OfferController.getOfferAtKey(key, this);
            }
    }

    @Override
    public void onComplete(ServiceReq sr) {
        TextView serviceDescr_lbl=findViewById(R.id.serviceDescr_lbl);
        if (sr!=null) {
            serviceDescr_lbl.setText(sr.getShortDescr());
        }
    }

    @Override
    public void onComplete(Offer of) {
        TextView serviceDescr_lbl=findViewById(R.id.serviceDescr_lbl);
        if (of!=null) {
            deliveryDate_et.setText(of.getDeliveryDate());
            deliveryTime_et.setText(of.getDeliveryTime());
            deliveryPlace_et.setText(of.getDeliveryPlace());
            proposedGain_et.setText(String.valueOf(of.getPropGain()));
            notes_et.setText(of.getNotes());
            if (mKeyService==null) {
                mKeyService = of.getSerReqID();
                if (mKeyService!=null)
                    ServiceReqController.getServiceReqAtKey(mKeyService, this);
            }
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        deliveryDate_et.removeTextChangedListener(mDateWatcher);
        deliveryTime_et.removeTextChangedListener(mTimeWatcher);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void InsertDB_BtnClick(View view) {

        String deliveryPlace = deliveryPlace_et.getText().toString();
        String offerDate     = deliveryDate_et.getText().toString();
        String offerTime     = deliveryTime_et.getText().toString();
        String serviceDescr  = serviceDescr_lbl.getText().toString();
        String notes         = notes_et.getText().toString();

        double propGain;
        if (!proposedGain_et.getText().toString().equals(""))
            propGain           = Double.parseDouble(proposedGain_et.getText().toString());
        else
            propGain           = 0;

        /*
        Date cdate=new Date();
        DateFormat df2 = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.ITALY);
        try {
            cdate = df2.parse(dateSrvReq+timeSrvReq);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long servReqDateTime = cdate.getTime();
        */

        Date d= new Date();
        long timestamp = d.getTime();
        Offer sr = new Offer(mUserId,
                             mKeyService,
                             serviceDescr,
                             deliveryPlace,
                             offerDate,
                             offerTime,
                             propGain,
                             notes,
                            "");

        OfferController of=(new OfferController());
        OfferController.Error se = of.validate(sr);

        if (se.getErrorCode()!=0) {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, se.getErrorDescription(), Snackbar.LENGTH_LONG);
            View viewSb = snackbar.getView();
            TextView tv = (TextView) viewSb.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snackbar.show();

            return;
        }

        //Validazione e snackbar nel caso dati non validi
        String id = mDbOffer.push().getKey();
        if (id!=null) mDbOffer.child(id).setValue(sr);
        Toast.makeText(this, R.string.Offer_added_toast_msg, Toast.LENGTH_LONG).show();
        onBackPressed();

    }

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
        editText.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
    }

}

