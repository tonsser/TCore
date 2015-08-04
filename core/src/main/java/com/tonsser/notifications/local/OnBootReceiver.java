package com.tonsser.notifications.local;
/**
 * @author Casper Rasmussen 2012
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnBootReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, TRunningService.class));
    }
}