package dk.tonsser.utils;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

/**
 * @author Casper Rasmussen 2013
 */
public class TBattery {
    private static String TAG = TBattery.class.getName();

    /**
     * Returning boolean dependent if battery level is lower than 15%
     *
     * @param mContext
     * @return
     */
    public static boolean isBatteryLow(Context mContext) {
        return isBatteryLowerThan(mContext, 0.15f);
    }

    /**
     * Returning boolean dependent if battery level is lower than param batteryLevel
     * batteryLevel should be a float between 0-1
     *
     * @param context
     * @return Boolean
     */
    public static boolean isBatteryLowerThan(Context mContext, float batteryLevel) {
        return getBatteryLevelNormalized(mContext) < batteryLevel;
    }


    /**
     * Will return a float between 0-1, which indicates the level of the battery
     *
     * @param mContext
     * @return float
     */
    public static float getBatteryLevelNormalized(Context mContext) {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent intent = null;
        try {
            intent = mContext.registerReceiver(null, intentFilter);
        } catch (Exception e) {
            TLog.e(TAG + " isBatteryLow", e);
        }
        int maxLevel = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);

        return ((level / maxLevel));
    }
}
