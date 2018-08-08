package com.mastersoft.steb.bymeapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mastersoft.steb.bymeapp.controllers.ServiceReqController;
import com.mastersoft.steb.bymeapp.adapters.fbServiceReqAdapter;
import com.mastersoft.steb.bymeapp.model.ServiceReq;
import com.mastersoft.steb.bymeapp.utils.MaskWatcher;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ServiceReqForm extends AppCompatActivity implements fbServiceReqAdapter.Completation {
    @BindView(R.id.serviceShortDescrTv) EditText edtServiceShortDescrTv;
    @BindView(R.id.serviceDescrTv)      EditText edtServiceDescrTv;
    @BindView(R.id.dateSrvReqTv)        EditText edtDateSrvReqTv;
    @BindView(R.id.timeSrvReqTv)        EditText edtTimeSrvReqTv;
    @BindView(R.id.performPlaceTv)      EditText edtPerformPlaceTv;
    @BindView(R.id.deliveryPlaceTv)     EditText edtDeliveryPlaceTv;
    @BindView(R.id.propGainTv)          EditText edtPropGainTv;
    @BindView(R.id.contactInfoTv)       EditText edtContactInfoTv;
    @BindView(R.id.clServiceReqForm)    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.InsertDB_Btn)        Button insertButton;
    private                             DatabaseReference mDbServReq;
    private                             int               mCallerParam;
    private                             String            mServiceKey;
    private                             String            mUserId;
    private                             MaskWatcher mDateWatcher;
    private                             MaskWatcher mTimeWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_service_req_form);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent callerIntent = getIntent();
        mCallerParam=0;
        if ((callerIntent.hasExtra(Constants.SERVICE_REQ_PARAM)) && (callerIntent.getExtras()!=null)) {
            mCallerParam = callerIntent.getExtras().getInt(Constants.SERVICE_REQ_PARAM);
            mServiceKey  = callerIntent.getExtras().getString(Constants.SERVICE_KEY);
            mUserId      = callerIntent.getExtras().getString(Constants.USER_ID,"");
        }

        if (mCallerParam==Constants.SR_VIEW_SRV_FORM) {
            DisableEdits();
            LoadDbValues(mServiceKey);
            insertButton.setVisibility(View.INVISIBLE);
        }

        FirebaseDatabase dbInstance=FirebaseDatabase.getInstance();
        mDbServReq = dbInstance.getReference("ServiceReq");

        mDateWatcher = new MaskWatcher("##/##/##");
        mTimeWatcher = new MaskWatcher("##:##");

    }

    private void DisableEdits() {
        disableEditText(edtServiceShortDescrTv);
        disableEditText(edtServiceDescrTv);
        disableEditText(edtDateSrvReqTv);
        disableEditText(edtTimeSrvReqTv);
        disableEditText(edtPerformPlaceTv);
        disableEditText(edtDeliveryPlaceTv);
        disableEditText(edtPropGainTv);
        disableEditText(edtContactInfoTv);
    }

    private void LoadDbValues(String key) {
        ServiceReqController.getServiceReqAtKey(key, this);
    }

    public void InsertDB_BtnClick(View view) {

        String serviceShortDescr  = edtServiceShortDescrTv.getText().toString();
        String serviceDescr       = edtServiceDescrTv.getText().toString();
        String dateSrvReq         = edtDateSrvReqTv.getText().toString();
        String timeSrvReq         = edtTimeSrvReqTv.getText().toString();
        String performPlace       = edtPerformPlaceTv.getText().toString();
        String deliveryPlace      = edtDeliveryPlaceTv.getText().toString();
        double propGain;
        if (!edtPropGainTv.getText().toString().equals(""))
               propGain           = Double.parseDouble(edtPropGainTv.getText().toString());
        else
               propGain           = 0;
        String contactInfo        = edtContactInfoTv.getText().toString();

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
        long timestamp = -d.getTime();
        ServiceReq sr = new ServiceReq(mUserId,
                                       serviceShortDescr,
                                       serviceDescr,
                                       contactInfo,
                                       deliveryPlace,
                                       performPlace,
                                       dateSrvReq,
                                       timeSrvReq,
                                       propGain,
                                       timestamp);

        ServiceReqController sc=(new ServiceReqController());
        ServiceReqController.Error se = sc.validate(sr);
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
        String id = mDbServReq.push().getKey();
        if (id!=null) mDbServReq.child(id).setValue(sr);
        Toast.makeText(this, "Service request added", Toast.LENGTH_LONG).show();
        onBackPressed();

    }

    @Override
    public void onComplete(ServiceReq sr) {
        edtServiceShortDescrTv.setText(sr.getShortDescr());
        edtServiceDescrTv.setText(sr.getDescription());
        edtDateSrvReqTv.setText(sr.getDeliveryDate());
        edtTimeSrvReqTv.setText(sr.getDeliveryTime());
        edtPerformPlaceTv.setText(sr.getPerfPlace());
        edtDeliveryPlaceTv.setText(sr.getDeliveryPlace());
        edtPropGainTv.setText(String.valueOf(sr.getPropGain()));
        edtContactInfoTv.setText(sr.getContactInfos());
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mDateWatcher!=null) 
            edtDateSrvReqTv.addTextChangedListener(mDateWatcher);
        if (mTimeWatcher!=null)
            edtTimeSrvReqTv.addTextChangedListener(mTimeWatcher);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mDateWatcher!=null)
            edtDateSrvReqTv.removeTextChangedListener(mDateWatcher);
        if (mTimeWatcher!=null)
            edtTimeSrvReqTv.removeTextChangedListener(mTimeWatcher);
    }


    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
        editText.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
    }

}
