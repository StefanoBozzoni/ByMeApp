package com.mastersoft.steb.bymeapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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
import com.mastersoft.steb.bymeapp.OfferStatusFormActivity;
import com.mastersoft.steb.bymeapp.R;
import com.mastersoft.steb.bymeapp.model.Offer;

public class fbOffersAdapter  extends FirebaseRecyclerAdapter<Offer,fbOffersAdapter.VHOffer>{

    private Context rcContext;
    private int mCallerParam;

    public interface Completation {
        void onComplete(Offer of);
    }

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
    */
    public fbOffersAdapter(@NonNull FirebaseRecyclerOptions<Offer> options, int callerParam) {
        super(options);
        mCallerParam=callerParam;
    }

    public class VHOffer extends RecyclerView.ViewHolder {

        public final TextView serviceDescr_tv;
        public final TextView deliveryPlace_tv;
        public final TextView deliveryDate_tv;
        public final TextView deliveryTime_tv;
        public final TextView proposedGain_tv;
        public final Button status_btn;


        public VHOffer(final View view) {
            super(view);
            final Context cntx=view.getContext();
            serviceDescr_tv = view.findViewById(R.id.serviceDescr_tv);
            deliveryPlace_tv = view.findViewById(R.id.deliveryPlace_tv);
            deliveryDate_tv = view.findViewById(R.id.deliveryDate_tv);

            deliveryTime_tv = view.findViewById(R.id.deliveryTime_tv);
            proposedGain_tv = view.findViewById(R.id.proposedGain_tv);
            status_btn      = (Button) view.findViewById(R.id.status_btn);
            if (mCallerParam==Constants.OF_USER_OFFER) {
                status_btn.setText(view.getResources().getString(R.string.set_status));
                status_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(cntx, OfferStatusFormActivity.class);
                        intent.putExtra(Constants.OFFER_FORM_PARAM, Constants.OF_VIEW_OFFER_FORM);
                        String key = getRef(getAdapterPosition()).getKey();
                        intent.putExtra(Constants.OFFER_KEY, key);
                        cntx.startActivity(intent);
                       // Toast.makeText(view.getContext(), "Ciao", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else
                status_btn.setVisibility(View.GONE);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context cntx = view.getContext();
                    Intent anIntent = new Intent(cntx, OfferFormActivity.class);
                    anIntent.putExtra(Constants.OFFER_FORM_PARAM,Constants.OF_VIEW_OFFER_FORM);
                    String key = getRef(getAdapterPosition()).getKey();
                    anIntent.putExtra(Constants.OFFER_KEY,key);
                    anIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    cntx.startActivity(anIntent);
                }
            });

        }
    }

    @Override
    @NonNull
    public VHOffer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate the layout in the view and return the viewholder
        Context context = parent.getContext();
        rcContext=context;
        int layoutId = R.layout.recview_offer;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutId, parent, false);
        return new VHOffer(view);
    }

    /*
    @Override
    public ServiceReq getItem(int position) {
        return super.getItem(getItemCount() - (position + 1));
    }
    */

    @Override
    protected void onBindViewHolder(@NonNull VHOffer holder, int position, @NonNull Offer model) {
        //Get the data[position] and load it in the viewholder
        String status=model.getStatus();

        if ((status!=null) && (!model.getStatus().equals("none"))) status=" : "+status; else status="";
        holder.serviceDescr_tv.setText(String.format("%s%s",model.getService_descr(),status));
        holder.deliveryPlace_tv.setText(model.getDeliveryPlace());
        holder.deliveryDate_tv.setText(String.valueOf(model.getDeliveryDate()));
        holder.deliveryTime_tv.setText(String.valueOf(model.getDeliveryTime()));
        holder.proposedGain_tv.setText(String.valueOf(model.getPropGain()));
    }

}


