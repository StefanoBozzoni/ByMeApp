package com.mastersoft.steb.bymeapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseUser;
import com.mastersoft.steb.bymeapp.Constants;
import com.mastersoft.steb.bymeapp.OfferFormActivity;
import com.mastersoft.steb.bymeapp.OfferListActivity;
import com.mastersoft.steb.bymeapp.R;
import com.mastersoft.steb.bymeapp.ServiceReqForm;
import com.mastersoft.steb.bymeapp.model.ServiceReq;

import butterknife.BindView;
import butterknife.ButterKnife;

public class fbServiceReqAdapter  extends FirebaseRecyclerAdapter<ServiceReq,fbServiceReqAdapter.VHServiceReq>{

    //private Context rcContext;
    private int     mCallerParam;
    private String  mUserId;
    private  Context cntx;

    public interface Completation {
        void onComplete(ServiceReq sr);
    }

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public fbServiceReqAdapter(@NonNull FirebaseRecyclerOptions<ServiceReq> options,int callerParam) {
        super(options);
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
                        String key = getRef(pos).getKey();
                        anIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        anIntent.putExtra(Constants.OFFER_FORM_PARAM, Constants.SR_ADD_OFFER);
                        anIntent.putExtra(Constants.SERVICE_KEY, key);
                        if (!mUserId.equals(""))
                            anIntent.putExtra(Constants.USER_ID, mUserId);
                        cntx.startActivity(anIntent);
                    }
                });
            }
            else if (mCallerParam== Constants.SR_SEE_OFFER) {
                actionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = getAdapterPosition();
                        String key = getRef(pos).getKey();

                        Intent anIntent = new Intent(cntx, OfferListActivity.class);
                        anIntent.putExtra(Constants.OFFER_LIST_PARAM, Constants.OF_SRV_OFFER);
                        anIntent.putExtra(Constants.SERVICE_KEY, key);
                        anIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        cntx.startActivity(anIntent);
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
                    String key = getRef(pos).getKey();
                    anIntent.putExtra(Constants.SERVICE_KEY,key);
                    anIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    cntx.startActivity(anIntent);
                }
            });

        }
    }

    @Override
    @NonNull
    public VHServiceReq onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate the layout in the view and return the viewholder
        final Context context = parent.getContext();
        //rcContext=context;
        int layoutId = R.layout.recview_servreq;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutId, parent, false);
        return new VHServiceReq(view);
    }


    /*
    @Override
    public ServiceReq getItem(int position) {
        return super.getItem(getItemCount() - (position + 1));
    }
    */

    @Override
    protected void onBindViewHolder(@NonNull VHServiceReq holder, int position, @NonNull ServiceReq model) {
        //Get the data[position] and load it in the viewholder
        holder.shortDescriptionTv.setText(model.getShortDescr());
        holder.largedescriptionTv.setText(model.getDescription());
        holder.deliveryPlaceTv.setText(model.getDeliveryPlace());
        holder.deliveryDateTv.setText(model.getDeliveryDate());
        holder.deliveryTimeTv.setText(model.getDeliveryTime());
        String currencySymbol=cntx.getResources().getString(R.string.currency_symbol);
        holder.gainTv.setText(String.valueOf(model.getPropGain()+" "+currencySymbol ));

        if (mCallerParam== Constants.SR_ADD_OFFER)
            holder.actionButton.setText("add offer");
        else
            holder.actionButton.setText("see offers");
    }

    public void setUser(FirebaseUser user) {
        mUserId = user.getUid();
    }


}


