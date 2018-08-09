package com.mastersoft.steb.bymeapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created stefa on 26/02/2018.
 */

public class MyRecyclerView extends RecyclerView {
    static final String SAVED_SUPER_STATE   ="SAVED_SUPER_STATE";
    static final String SAVED_LAYOUT_MANAGER="SAVED_LAYOUT_MANAGER";
    static final String PREF_TAG_PRFX       ="MyRecyclerViewScrollPos";

    private Parcelable mLayoutManagerSavedState;

    public MyRecyclerView(Context context) {
        super(context);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void  resetScrollPosition() {
        LayoutManager lm=this.getLayoutManager();
        if (lm instanceof GridLayoutManager) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
            prefs.edit().putInt(getRVTag(), 0).apply();
        }
    }

    public void  storeScrollPosition() {
        LayoutManager lm=this.getLayoutManager();
        if (lm instanceof GridLayoutManager) {
            int lastFirstVisiblePosition = ((GridLayoutManager) this.getLayoutManager()).findFirstVisibleItemPosition();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
            prefs.edit().putInt(getRVTag(), lastFirstVisiblePosition).apply();

        } else if (lm instanceof LinearLayoutManager) {
            int lastFirstVisiblePosition = ((LinearLayoutManager) this.getLayoutManager()).findFirstVisibleItemPosition();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
            prefs.edit().putInt(getRVTag(), lastFirstVisiblePosition).apply();

        }
    }
    private String getRVTag() {
        Object obj=this.getTag(); String rvTag="";
        if ((obj!=null) && (obj instanceof String )) { rvTag = (String) obj;}
        rvTag=PREF_TAG_PRFX+rvTag;
        return rvTag;
    }



    public void  restoreScrollPosition() {
        LayoutManager lm = this.getLayoutManager();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        int lastFirstVisiblePosition = prefs.getInt(getRVTag(), -1);
        if (lastFirstVisiblePosition != -1) {
            //lastFirstVisiblePosition=15;
            if (lm instanceof GridLayoutManager)
                ((GridLayoutManager) this.getLayoutManager()).scrollToPositionWithOffset(lastFirstVisiblePosition, 0);
            else
            if (lm instanceof LinearLayoutManager)
                ((LinearLayoutManager) this.getLayoutManager()).scrollToPositionWithOffset(lastFirstVisiblePosition, 0);

        }
    }

}
