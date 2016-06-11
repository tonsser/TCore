package com.tonsser.utils;

import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

public class TToast {
    private static final String TAG = TToast.class.getName();

    public static void execute(Context mContext, String msg) {
        if (mContext != null) {
            if (TBuild.DEBUG) {
                TLog.s("TToast: " + msg);
            }
            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
        } else {
            TLog.e(TAG + " execute", "Context is null, can't make the toast");
        }
    }

    public static void execute(Context mContext, @StringRes int stringResId) {
        if (mContext != null) {
            if (TBuild.DEBUG) {
                TLog.s("TToast: " + mContext.getString(stringResId));
            }
            Toast.makeText(mContext, stringResId, Toast.LENGTH_LONG).show();
        } else {
            TLog.e(TAG + " execute", "Context is null, can't make the toast");
        }
    }


    public static void executeShort(Context mContext, String msg) {
        if (mContext != null) {
            if (TBuild.DEBUG) {
                TLog.s("TToast: " + msg);
            }
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        } else {
            TLog.e(TAG + " execute", "Context is null, can't make the toast");
        }
    }

    public static void executeShort(Context mContext, @StringRes int stringResId) {
        if (mContext != null) {
            if (TBuild.DEBUG) {
                TLog.s("TToast: " + mContext.getString(stringResId));
            }
            Toast.makeText(mContext, stringResId, Toast.LENGTH_SHORT).show();
        } else {
            TLog.e(TAG + " execute", "Context is null, can't make the toast");
        }
    }
}
