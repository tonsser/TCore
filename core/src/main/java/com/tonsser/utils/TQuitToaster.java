package com.tonsser.utils;

import android.content.Context;

import com.tonsser.base.TBaseApplication;

public class TQuitToaster {

    private static TQuitToaster instance;
    private long lastClickedBackUnix;
    private String msg = "Press back again to exit";


    public static TQuitToaster getInstance() {
        if (instance == null)
            instance = new TQuitToaster();

        return instance;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void onBackClicked(Context mContext) {
        if (TUtils.isOutdated(2000, lastClickedBackUnix)) {
            TToast.executeShort(mContext, msg);
            lastClickedBackUnix = System.currentTimeMillis();
        } else
            TBaseApplication.broadcastFinishAll(mContext);
    }
}
