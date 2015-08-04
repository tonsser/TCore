package com.tonsser.utils;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.tonsser.widgets.dialogs.alert.TBasicAlertDialog;

public class TConnectivity {
    /*
     * HACKISH: These constants aren't yet available in my API level (7), but I need to handle these cases if they come up, on newer versions
     */
    public static final int NETWORK_TYPE_EHRPD = 14; // Level 11
    public static final int NETWORK_TYPE_EVDO_B = 12; // Level 9
    public static final int NETWORK_TYPE_HSPAP = 15; // Level 13
    public static final int NETWORK_TYPE_IDEN = 11; // Level 8
    public static final int NETWORK_TYPE_LTE = 13; // Level 11
    private static TBasicAlertDialog showNotConnectedPopupIfNotConnectionDialog;


    /**
     * Will return if the device is connected to any network providing internet
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null) {
            boolean isConnected = ni.isConnected();
            return isConnected;
        } else
            return false;
    }

    /**
     * @param mActivity
     */
    public static void showNotConnectedPopupIfNotConnection(final Activity mActivity) {
        if (!isConnected(mActivity) && (showNotConnectedPopupIfNotConnectionDialog == null || !showNotConnectedPopupIfNotConnectionDialog.isShowing())) {
            showNotConnectedPopupIfNotConnectionDialog = new TBasicAlertDialog(mActivity, null, "No internet connection",
                    "Ignore", "Go to settings", true, new TBasicAlertDialog.NBasicAlertDialogListener() {

                @Override
                public void onRightBtnClicked() {
                    TAndroidIntents.toWIFISettings(mActivity);
                }

                @Override
                public void onLeftBtnClicked() {

                }
            });

            showNotConnectedPopupIfNotConnectionDialog.show();
        }
    }

    /**
     * Will return of the device is connected to WIFI
     *
     * @param context
     * @return Boolean
     */
    public static boolean isOnWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        boolean isWifi = ni.getType() == ConnectivityManager.TYPE_WIFI;

        return isWifi;
    }

    /**
     * Will return if the device in connected to a mobile-network
     *
     * @param context
     * @return Boolean
     */
    public static boolean isOnMobileNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        boolean isOnMobileNetwork = ni.getType() == ConnectivityManager.TYPE_MOBILE;

        return isOnMobileNetwork;
    }

    /**
     * Check if there is fast connectivity
     *
     * @param context
     * @return
     */
    public static boolean isConnectedFast(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null && info.isConnected() && TConnectivity.isConnectionFast(info.getType(), info.getSubtype()));
    }

    public static boolean isGPSEnabled(Context mContext) {
        LocationManager service = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

        return enabled;
    }


    /**
     * Check if the connection is fast
     *
     * @param type
     * @param subType
     * @return
     */
    public static boolean isConnectionFast(int type, int subType) {
        if (type == ConnectivityManager.TYPE_WIFI) {
            System.out.println("CONNECTED VIA WIFI");
            return true;
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return false; // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return true; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return true; // ~ 400-7000 kbps
                // NOT AVAILABLE YET IN API LEVEL 7
                case TConnectivity.NETWORK_TYPE_EHRPD:
                    return true; // ~ 1-2 Mbps
                case TConnectivity.NETWORK_TYPE_EVDO_B:
                    return true; // ~ 5 Mbps
                case TConnectivity.NETWORK_TYPE_HSPAP:
                    return true; // ~ 10-20 Mbps
                case TConnectivity.NETWORK_TYPE_IDEN:
                    return false; // ~25 kbps
                case TConnectivity.NETWORK_TYPE_LTE:
                    return true; // ~ 10+ Mbps
                // Unknown
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

}
