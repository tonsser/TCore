package dk.nodes.controllers.versionsandcrashes.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import dk.nodes.controllers.NStringController;

public class NVersionSharedPrefController {

	private final static String LAST_VISITED_VERSION = "nversion_last_read_version";
	//	private final static String LAST_LOADED_UNIX = "nversion_last_loaded_unix";

	private static String generateNewInThisVersionKey(String version){
		return "nversion_read_news_in_this_version_" + version;
	}

	private static String generateUpdateVersionKey(String version){
		return "nversion_read_update_version_" + version;
	}

	private static String generateAlertKey(String modified){
		if(NStringController.hasValue(modified)){
			return "nversion_read_alert_" + modified.split(" ")[0];
		}
		else{
			return "nversion_read_alert_" + modified;
		}
	}

	//	public static long getLastLoadedUnix(Activity mActivity){
	//		final SharedPreferences sharedPref = mActivity.getPreferences(Context.MODE_PRIVATE);
	//		return sharedPref.getLong(LAST_LOADED_UNIX, 0);
	//	}
	//	
	//	public static void setLastLoadedUnix(Activity mActivity){
	//		SharedPreferences sharedPref = mActivity.getPreferences(Context.MODE_PRIVATE);
	//		SharedPreferences.Editor editor = sharedPref.edit();
	//		editor.putLong(LAST_LOADED_UNIX, System.currentTimeMillis());
	//		editor.commit();
	//	}

	public static void setLastVisitedVersion(Activity mActivity, String lastVisitedVersion){
		SharedPreferences sharedPref = mActivity.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(LAST_VISITED_VERSION, lastVisitedVersion);
		editor.commit();
	}

	public static String getLastVisitedVersion(Activity mActivity){
		final SharedPreferences sharedPref = mActivity.getPreferences(Context.MODE_PRIVATE);
		return sharedPref.getString(LAST_VISITED_VERSION, null);
	}

	public static void setReadNewInThisVersion(Activity mActivity, String version){
		SharedPreferences sharedPref = mActivity.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putBoolean(NVersionSharedPrefController.generateNewInThisVersionKey(version), true);
		editor.commit();
	}

	public static void setReadUpdateVersion(Activity mActivity, String version){
		SharedPreferences sharedPref = mActivity.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putBoolean(NVersionSharedPrefController.generateUpdateVersionKey(version), true);
		editor.commit();
	}

	public static void setReadAlert(Activity mActivity, String modified){
		SharedPreferences sharedPref = mActivity.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putBoolean(NVersionSharedPrefController.generateAlertKey(modified), true);
		editor.commit();
	}
	public static boolean didReadUpdateVersion(Activity mActivity, String version){
		final SharedPreferences sharedPref = mActivity.getPreferences(Context.MODE_PRIVATE);
		return sharedPref.getBoolean(generateUpdateVersionKey(version), false);
	}

	public static boolean didReadNewInThisVersion(Activity mActivity, String version){
		final SharedPreferences sharedPref = mActivity.getPreferences(Context.MODE_PRIVATE);
		return sharedPref.getBoolean(generateNewInThisVersionKey(version), false);
	}


	public static boolean didReadThisAlert(Activity mActivity, String modified){
		final SharedPreferences sharedPref = mActivity.getPreferences(Context.MODE_PRIVATE);
		return sharedPref.getBoolean(generateAlertKey(modified), false);
	}
}
