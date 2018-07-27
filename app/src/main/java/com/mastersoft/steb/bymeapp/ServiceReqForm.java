package com.mastersoft.steb.bymeapp;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mastersoft.steb.bymeapp.Controllers.ServiceReqController;
import com.mastersoft.steb.bymeapp.model.ServiceReq;
import com.mastersoft.steb.bymeapp.utils.MaskWatcher;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ServiceReqForm extends AppCompatActivity {
    @BindView(R.id.serviceShortDescrTv) EditText edtServiceShortDescrTv;
    @BindView(R.id.serviceDescrTv)      EditText edtServiceDescrTv;
    @BindView(R.id.dateSrvReqTv)        EditText edtDateSrvReqTv;
    @BindView(R.id.timeSrvReqTv)        EditText edtTimeSrvReqTv;
    @BindView(R.id.performPlaceTv)      EditText edtPerformPlaceTv;
    @BindView(R.id.deliveryPlaceTv)     EditText edtDeliveryPlaceTv;
    @BindView(R.id.propGainTv)          EditText edtPropGainTv;
    @BindView(R.id.contactInfoTv)       EditText edtContactInfoTv;
    @BindView(R.id.clServiceReqForm)    CoordinatorLayout coordinatorLayout;
    private                             DatabaseReference mDbServReq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_req_form);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FirebaseDatabase dbInstance=FirebaseDatabase.getInstance();
        mDbServReq = dbInstance.getReference("ServiceReq");

        edtDateSrvReqTv.addTextChangedListener(new MaskWatcher("##/##/##"));
        edtTimeSrvReqTv.addTextChangedListener(new MaskWatcher("##:##"));

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
        long timestamp = d.getTime();
        ServiceReq sr = new ServiceReq("userIdProva2",
                                            serviceShortDescr,
                                            serviceDescr,
                                            contactInfo,
                                            deliveryPlace,
                                  dateSrvReq+' '+timeSrvReq,
                                            propGain,
                                            timestamp);

        ServiceReqController sc=(new ServiceReqController());
        ServiceReqController.Error se = sc.validate(sr);
        if (se.getErrorCode()!=0) {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, se.getErrorDescription(), Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        //Validazione e snackbar nel caso dati non validi
        String id = mDbServReq.push().getKey();
        if (id!=null) mDbServReq.child(id).setValue(sr);
        Toast.makeText(this, "Service request added", Toast.LENGTH_LONG).show();

    }
}
