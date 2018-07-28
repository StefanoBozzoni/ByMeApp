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
import com.mastersoft.steb.bymeapp.Constants;
import com.mastersoft.steb.bymeapp.OfferFormActivity;
import com.mastersoft.steb.bymeapp.OfferListActivity;
import com.mastersoft.steb.bymeapp.R;
import com.mastersoft.steb.bymeapp.ServiceReqForm;
import com.mastersoft.steb.bymeapp.model.ServiceReq;

public class fbServiceReqAdapter  extends FirebaseRecyclerAdapter<ServiceReq,fbServiceReqAdapter.VHServiceReq>{

    //private Context rcContext;
    private int mCallerParam;

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

    public class VHServiceReq extends RecyclerView.ViewHolder {

        final TextView shortDescriptionTv;
        final TextView largedescriptionTv;
        final Button   actionButton;

        VHServiceReq(final View view) {
            super(view);
            shortDescriptionTv = view.findViewById(R.id.descr_small_tv);
            largedescriptionTv = view.findViewById(R.id.descr_large_tv);
            actionButton       = view.findViewById(R.id.action_button_1);

            if (mCallerParam== Constants.SR_ADD_OFFER) {
                actionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = getAdapterPosition();
                        Context cntx = view.getContext();
                        //Toast.makeText(cntx, getRef(pos).getKey(), Toast.LENGTH_SHORT).show();

                        Intent anIntent = new Intent(cntx, OfferFormActivity.class);
                        String key = getRef(pos).getKey();
                        anIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        anIntent.putExtra(Constants.SERVICE_KEY, key);
                        cntx.startActivity(anIntent);
                    }
                });
            }
            else if (mCallerParam== Constants.SR_SEE_OFFER) {
                actionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = getAdapterPosition();
                        Context cntx = view.getContext();
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
        if (mCallerParam== Constants.SR_ADD_OFFER)
            holder.actionButton.setText("add offer");
        else
            holder.actionButton.setText("see offers");
    }


}


