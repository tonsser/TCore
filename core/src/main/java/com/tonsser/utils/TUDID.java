package com.tonsser.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import java.util.UUID;

public class TUDID {
    private static final String TAG = TUDID.class.getName();

    public static final String PREFS = "UTILS_PREFS";
    public static final String PREFS_ID = "MY_UNIQUE_ID";


    /**
     * This is not more unique than it can be deleted in clear data, but this is not requiring phone state permission
     *
     * @param context
     * @return
     */
    public synchronized static String getCachedUniqueID(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String id = prefs.getString(PREFS_ID, null);

        if (id == null) {
            id = UUID.randomUUID().toString();
            Editor edit = prefs.edit();
            edit.putString(PREFS_ID, id);
        }
        return id;
    }

    /**
     * Will generate a random UDID
     *
     * @return String
     */
    public static String getUniqueID() {
        return UUID.randomUUID().toString();
    }

    /**
     * Will return UDID of phone, this requires READ_PHONE_STATE permission
     *
     * @param mContext
     * @return
     */
    public static String getUDID(Context mContext) {
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String UDID = telephonyManager.getDeviceId();
        if (UDID == null) {
            UDID = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);
        }
        return UDID;
    }
}
