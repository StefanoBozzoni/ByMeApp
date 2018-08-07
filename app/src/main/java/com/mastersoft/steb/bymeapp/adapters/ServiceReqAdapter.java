package com.mastersoft.steb.bymeapp.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseUser;
import com.mastersoft.steb.bymeapp.Constants;
import com.mastersoft.steb.bymeapp.OfferFormActivity;
import com.mastersoft.steb.bymeapp.OfferListActivity;
import com.mastersoft.steb.bymeapp.R;
import com.mastersoft.steb.bymeapp.ServiceReqForm;
import com.mastersoft.steb.bymeapp.model.ServiceReq;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ServiceReqAdapter extends RecyclerView.Adapter<ServiceReqAdapter.VHServiceReq> {

    private int     mCallerParam;
    private String  mUserId;
    private  Context cntx;
    static private int i=0;
    private ArrayList<ServiceReq> data = new ArrayList<>();

    public interface Completation {
        void onComplete(ServiceReq sr);
    }

    public ServiceReqAdapter(int callerParam, final Context rv_context) {
        mCallerParam=callerParam;
    }

    class VHServiceReq extends RecyclerView.ViewHolder {

        @BindView(R.id.descr_small_tv)   TextView shortDescriptionTv;
        @BindView(R.id.deliveryPlace_tv) TextView deliveryPlaceTv;
        @BindView(R.id.deliveryDate_tv)  TextView deliveryDateTv;
        @BindView(R.id.deliveryTime_tv)  TextView deliveryTimeTv;
        @BindView(R.id.gain_tv)          TextView gainTv;
        @BindView(R.id.descr_large_tv)   TextView largedescriptionTv;

        @BindView(R.id.action_button_1)  TextView actionButton;

        VHServiceReq(final View view) {
            super(view);
            ButterKnife.bind(this,view);
            cntx = view.getContext();

            if (mCallerParam== Constants.SR_ADD_OFFER) {
                actionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = getAdapterPosition();
                        Intent anIntent = new Intent(cntx, OfferFormActivity.class);
                        String key = data.get(pos).getKey();
                        anIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        anIntent.putExtra(Constants.OFFER_FORM_PARAM, Constants.SR_ADD_OFFER);
                        anIntent.putExtra(Constants.SERVICE_KEY, key);
                        if (!mUserId.equals(""))
                            anIntent.putExtra(Constants.USER_ID, mUserId);
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity)cntx,null);
                        cntx.startActivity(anIntent,options.toBundle());
                    }
                });
            }
            else if (mCallerParam== Constants.SR_SEE_OFFER) {
                actionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = getAdapterPosition();
                        String key = data.get(pos).getKey();
                        Intent anIntent = new Intent(cntx, OfferListActivity.class);
                        anIntent.putExtra(Constants.OFFER_LIST_PARAM, Constants.OF_SRV_OFFER);
                        anIntent.putExtra(Constants.SERVICE_KEY, key);
                        anIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity)cntx,null);
                        cntx.startActivity(anIntent,options.toBundle());
                    }
                });
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context cntx = view.getContext();
                    Intent anIntent = new Intent(cntx, ServiceReqForm.class);
                    anIntent.putExtra(Constants.SERVICE_REQ_PARAM,Constants.SR_VIEW_SRV_FORM);
                    int pos = getAdapterPosition();
                    String key = data.get(pos).getKey();
                    anIntent.putExtra(Constants.SERVICE_KEY,key);
                    anIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity)cntx,null);
                    cntx.startActivity(anIntent,options.toBundle());
                }
            });

        }
    }


    @NonNull
    @Override
    public VHServiceReq onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate the layout in the view and return the viewholder
        final Context context = parent.getContext();
        //rcContext=context;
        int layoutId = R.layout.recview_servreq;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutId, parent, false);
        return new VHServiceReq(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VHServiceReq holder, int position) {
        //Get the data[position] and load it in the viewholder
        if (getItemCount()!=0) {

            holder.shortDescriptionTv.setText(data.get(position).getShortDescr());
            holder.largedescriptionTv.setText(data.get(position).getDescription());
            holder.deliveryPlaceTv.setText(data.get(position).getDeliveryPlace());
            holder.deliveryDateTv.setText(data.get(position).getDeliveryDate());
            holder.deliveryTimeTv.setText(data.get(position).getDeliveryTime());
            String currencySymbol = cntx.getResources().getString(R.string.currency_symbol);
            holder.gainTv.setText(String.valueOf(data.get(position).getPropGain() + " " + currencySymbol));

            if (mCallerParam == Constants.SR_ADD_OFFER)
                holder.actionButton.setText(R.string.add_offer_str);
            else
                holder.actionButton.setText(R.string.see_offer_str);
        }
    }

    @Override
    public int getItemCount() {
        int len=0;
        if (data!=null)
            len = data.size();
        return len;
    }

    public void setData(ArrayList<ServiceReq> servicesrequests) {
        data = servicesrequests;
        notifyDataSetChanged();
    }

    public void setUser(FirebaseUser user) {
        mUserId = user.getUid();
    }

}
