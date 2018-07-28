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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.mastersoft.steb.bymeapp.Constants;
import com.mastersoft.steb.bymeapp.OfferFormActivity;
import com.mastersoft.steb.bymeapp.R;
import com.mastersoft.steb.bymeapp.model.Offer;

public class fbOffersAdapter  extends FirebaseRecyclerAdapter<Offer,fbOffersAdapter.VHOffer>{

    private Context rcContext;

    public interface Completation {
        void onComplete(Offer of);
    }

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public fbOffersAdapter(@NonNull FirebaseRecyclerOptions<Offer> options) {
        super(options);
    }

    public class VHOffer extends RecyclerView.ViewHolder {

        public final TextView serviceDescr_tv;
        public final TextView deliveryPlace_tv;
        public final TextView deliveryTime_tv;
        public final TextView proposedGain_tv;
        public final Button status_btn;


        public VHOffer(final View view) {
            super(view);
            serviceDescr_tv = view.findViewById(R.id.serviceDescr_tv);
            deliveryPlace_tv = view.findViewById(R.id.deliveryPlace_tv);
            deliveryTime_tv = view.findViewById(R.id.deliveryTime_tv);
            proposedGain_tv = view.findViewById(R.id.proposedGain_tv);
            status_btn      = (Button) view.findViewById(R.id.status_btn);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context cntx = view.getContext();
                    Intent anIntent = new Intent(cntx, OfferFormActivity.class);
                    anIntent.putExtra(Constants.OFFER_FORM_PARAM,Constants.OF_VIEW_OFFER_FORM);
                    //int pos = getAdapterPosition();
                    //String key = getRef(pos).getKey();
                    //anIntent.putExtra(Constants.SERVICE_KEY,key);
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
        holder.serviceDescr_tv.setText(model.getService_descr());
        holder.deliveryPlace_tv.setText(model.getDeliveryPlace());
        holder.deliveryTime_tv.setText(String.valueOf(model.getDeliveryTime()));
        holder.proposedGain_tv.setText(String.valueOf(model.getPropGain()));
        holder.status_btn.setText("accepted");
    }


}


