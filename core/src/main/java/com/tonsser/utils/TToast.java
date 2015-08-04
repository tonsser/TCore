package com.tonsser.utils;

import android.content.Context;
import android.widget.Toast;

public class TToast {
    private static final String TAG = TToast.class.getName();

    public static void execute(Context mContext, String msg) {
        if (mContext != null)
            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
        else
            TLog.e(TAG + " execute", "Context is null, can't make the toast");
    }

    public static void executeShort(Context mContext, String msg) {
        if (mContext != null)
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        else
            TLog.e(TAG + " execute", "Context is null, can't make the toast");
    }
}
