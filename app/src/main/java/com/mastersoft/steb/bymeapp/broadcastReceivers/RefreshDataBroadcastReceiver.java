package com.mastersoft.steb.bymeapp.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mastersoft.steb.bymeapp.Constants;
import com.mastersoft.steb.bymeapp.MyRecyclerView;
import com.mastersoft.steb.bymeapp.R;
import com.mastersoft.steb.bymeapp.adapters.ServiceReqAdapter;
import com.mastersoft.steb.bymeapp.model.ServiceReq;

import java.util.ArrayList;

public class RefreshDataBroadcastReceiver extends BroadcastReceiver {
    MyRecyclerView        mRecyclerView;
    ServiceReqAdapter     mServiceReqAdapter;
    ArrayList<ServiceReq> mData;


    public RefreshDataBroadcastReceiver(MyRecyclerView myRecyclerView, ServiceReqAdapter serviceReqAdapter, ArrayList<ServiceReq> serviceReqData) {
        mRecyclerView      = myRecyclerView;
        mServiceReqAdapter = serviceReqAdapter;
        mData=serviceReqData;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(Constants.REFRESHDATA_JOB_TAG,context.getString(R.string.broadcasat_log_received_data_str));
        String action = intent.getAction();
        if (action != null) {
            if (action.equals(Constants.REFRESHDATA_JOB_ACTION)) {
                mServiceReqAdapter.setData(mData);
                mRecyclerView.restoreScrollPosition();
            }
        }
    }

}
