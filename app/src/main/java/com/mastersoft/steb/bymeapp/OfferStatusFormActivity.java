package com.mastersoft.steb.bymeapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OfferStatusFormActivity extends AppCompatActivity {

    private int    mCallerParam;
    private String mOfferKey;
    DatabaseReference mDbOffer;
    TextView deliveryDate_et;
    @BindView(R.id.offer_rg)     RadioGroup offer_rg;
    @BindView(R.id.accepted_rb)  RadioButton accepted_rb;
    @BindView(R.id.dismissed_rb) RadioButton dismissed_rb;
    @BindView(R.id.none_rb)      RadioButton none_rb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Explode trans1 = new Explode();
        //trans1.setDuration(2000);
        //getWindow().requestFeature(FEATURE_CONTENT_TRANSITIONS);
        Slide slide = (Slide) TransitionInflater.from(this).inflateTransition(R.transition.activity_sladein_right);
        getWindow().setReenterTransition(slide);
        getWindow().setExitTransition(slide);

        FirebaseDatabase dbInstance=FirebaseDatabase.getInstance();
        mDbOffer = dbInstance.getReference("Offers");

        setContentView(R.layout.activity_offer_status_form);
        ButterKnife.bind(this);

        Intent callerIntent = getIntent();

        if ((callerIntent.hasExtra(Constants.OFFER_FORM_PARAM)) && (callerIntent.getExtras()!=null)) {
            mCallerParam = callerIntent.getExtras().getInt(Constants.OFFER_FORM_PARAM);
            mOfferKey    = callerIntent.getExtras().getString(Constants.OFFER_KEY);
        }

    }

    public void updStatusClick(View view) {

        int selectID = offer_rg.getCheckedRadioButtonId();

        String status="";
        if (selectID==accepted_rb.getId())  status="accepted";
        if (selectID==dismissed_rb.getId()) status="discarded";
        if (selectID==none_rb.getId())      status="none";

        mDbOffer.child(mOfferKey).child("status").setValue(status);
        onBackPressed();

    }

}
