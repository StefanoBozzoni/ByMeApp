package com.mastersoft.steb.bymeapp;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mastersoft.steb.bymeapp.Controllers.OfferController;
import com.mastersoft.steb.bymeapp.Controllers.ServiceReqController;
import com.mastersoft.steb.bymeapp.adapters.ServiceReqAdapter;
import com.mastersoft.steb.bymeapp.model.Offer;
import com.mastersoft.steb.bymeapp.model.ServiceReq;
import com.mastersoft.steb.bymeapp.utils.MaskWatcher;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.mastersoft.steb.bymeapp.Controllers.ServiceReqController.getServiceReqAtKey;

public class OfferFormActivity extends AppCompatActivity implements ServiceReqAdapter.Completation {
    String mKeyService;
    @BindView(R.id.deliveryDate_et)  TextView deliveryDate_et;
    @BindView(R.id.deliveryTime_et)  TextView deliveryTime_et;
    @BindView(R.id.deliveryPlace_et) TextView deliveryPlace_et;
    @BindView(R.id.proposedGain_et)  TextView proposedGain_et;
    @BindView(R.id.serviceDescr_lbl) TextView serviceDescr_lbl;
    @BindView(R.id.notes_et)         TextView notes_et;
    @BindView(R.id.clOfferForm)      CoordinatorLayout coordinatorLayout;
    private DatabaseReference mDbOffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_form);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        deliveryDate_et.addTextChangedListener(new MaskWatcher("##/##/##"));
        deliveryTime_et.addTextChangedListener(new MaskWatcher("##:##"));

        if (getIntent().hasExtra(Constants.OFFER_PARAM_KEY)) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) mKeyService = extras.getString(Constants.OFFER_PARAM_KEY);
        }
        if (mKeyService!=null)
            getServiceReqAtKey(mKeyService, this);

        FirebaseDatabase dbInstance=FirebaseDatabase.getInstance();
        mDbOffer = dbInstance.getReference("Offers");
    }

    @Override
    public void onComplete(ServiceReq sr) {
        TextView serviceDescr_lbl=findViewById(R.id.serviceDescr_lbl);
        if (sr!=null) {
            serviceDescr_lbl.setText(sr.getShortDescr());
        }
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
        Offer of = new Offer("userIdProva",
                mKeyService,
                serviceDescr,
                deliveryPlace,
                offerDate+offerTime,
                propGain,
                notes);

        OfferController ofc=(new OfferController());

        OfferController.Error se = ofc.validate(of);

        if (se.getErrorCode()!=0) {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, se.getErrorDescription(), Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        //Validazione e snackbar nel caso dati non validi
        String id = mDbOffer.push().getKey();
        if (id!=null) mDbOffer.child(id).setValue(of);
        Toast.makeText(this, "Offer added", Toast.LENGTH_LONG).show();

    }
}

