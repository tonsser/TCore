package dk.tonsser.notifications.local;
/**
 * @author Casper Rasmussen 2012
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import dk.tonsser.notifications.builder.prejellybeans.PreJellyBeansNotificationBuilder;

public class NotificationAlarmReceiver extends BroadcastReceiver {

    public void onReceive(Context mContext, Intent intent) {
        new PreJellyBeansNotificationBuilder().execute(mContext, "Notification", "message", 0, null);
    }
} 