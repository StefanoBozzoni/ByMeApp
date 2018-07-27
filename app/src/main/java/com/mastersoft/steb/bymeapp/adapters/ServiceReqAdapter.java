package com.mastersoft.steb.bymeapp.adapters;

import android.content.Context;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mastersoft.steb.bymeapp.R;
import com.mastersoft.steb.bymeapp.model.ServiceReq;

public class ServiceReqAdapter extends RecyclerView.Adapter<ServiceReqAdapter.PMViewHolder> {

    public interface Completation {
        void onComplete(ServiceReq sr);
    }

    private ServiceReq[] adapterData;
    private Context rcContext;
    private final AdapterOnClickHandler mClickHandler;

    public interface AdapterOnClickHandler {
        void onClick(View v, ServiceReq aMovie);
    }

    public ServiceReqAdapter(AdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class PMViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

        public final TextView shortDescriptionTv;
        public final TextView largedescriptionTv;


        public PMViewHolder(View view) {
            super(view);
            shortDescriptionTv = view.findViewById(R.id.descr_small_tv);
            largedescriptionTv = view.findViewById(R.id.descr_large_tv);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            ServiceReq thisMovie = adapterData[adapterPosition];
            //ImageView iv = (ImageView) v.findViewById(R.id.imageView_pm);
            mClickHandler.onClick(v,thisMovie);
        }

    }

    @Override
    public PMViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the layout in the view and return the viewholder
        Context context = parent.getContext();
        rcContext=context;
        int layoutIdForMovies = R.layout.recview_servreq;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForMovies, parent, false);
        return new PMViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PMViewHolder holder, int position) {
        //Get the data[position] and load it in the viewholder
        holder.shortDescriptionTv.setText(adapterData[position].getShortDescr());
        holder.largedescriptionTv.setText(adapterData[position].getShortDescr());
    }

    @Override
    public int getItemCount() {
        int len=0;
        if (adapterData!=null)
            len = adapterData.length;
        return len;
    }

    public void setData(ServiceReq[] datas) {
        adapterData = datas;
        notifyDataSetChanged();
    }

}
