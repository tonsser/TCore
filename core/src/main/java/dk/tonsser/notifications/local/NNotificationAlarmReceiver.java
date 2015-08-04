package dk.tonsser.notifications.local;
/**
 * @author Casper Rasmussen 2012
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import dk.tonsser.notifications.builder.prejellybeans.NPreJellyBeansNotificationBuilder;

public class NNotificationAlarmReceiver extends BroadcastReceiver {

	public void onReceive(Context mContext, Intent intent) {
		new NPreJellyBeansNotificationBuilder().execute(mContext,"Notification","message",0,null);
	}
} 