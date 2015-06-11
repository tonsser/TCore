package dk.nodes.controllers;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;

import dk.nodes.utils.NLog;

@Deprecated
public class NServiceController {

	public static void startServiceIfNotRunning(Context mContext,Intent mIntent,String serviceClassName){
		if(!isMyServiceRunning(mContext,serviceClassName)) {
			startService(mContext,mIntent);
			NLog.d("startServiceIfNotRunning started", serviceClassName);
		}
		else
			NLog.d("startServiceIfNotRunning already running",serviceClassName);
	}

	public static void startService(Context mContext,Intent mIntent){
		mContext.startService(mIntent);  
	}

	public static boolean isMyServiceRunning(Context mContext,String serviceClassName) {
		ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo mService : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClassName.equals(mService.service.getClassName())) {		     	
				return true;
			}
		}
		return false;
	}
}
