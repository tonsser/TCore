package dk.tonsser.utils;

import android.Manifest.permission;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Vibrator;

public class NVibrate {

	private static String TAG;

	public static void vibrateTimeBased(Activity mActivity, int ms){
		PackageManager pm = mActivity.getPackageManager();
	    if (pm.checkPermission(permission.VIBRATE, mActivity.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
	    	Vibrator v = (Vibrator) mActivity
					.getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(ms);
	    } else {
	        NLog.e(TAG+" vibrateTimeBased","<uses-permission android:name=\"android.permission.VIBRATE\"/> is missing");
	    }
	}
}
