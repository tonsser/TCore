package dk.tonsser.notifications.builder.prejellybeans;
/**
 * @author Casper Rasmussen 2012
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class PreJellyBeansNotificationBuilder {
    public static void execute(Context mContext, String title, String message, int resourceDrawable, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notifyDetails = new Notification(resourceDrawable, "", System.currentTimeMillis());
        long[] vibrate = {100, 100, 200, 300};
        notifyDetails.vibrate = vibrate;
        notifyDetails.defaults = Notification.DEFAULT_ALL;
        CharSequence contentTitle = title;
        CharSequence contentText = message;
        notifyDetails.flags = Notification.FLAG_AUTO_CANCEL;
        Intent notifyIntent = intent;
        PendingIntent intent1 = PendingIntent.getActivity(mContext, 0, notifyIntent, android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
        notifyDetails.setLatestEventInfo(mContext, contentTitle, contentText, intent1);
        notificationManager.notify(1, notifyDetails);
    }

    public static void execute(Context mContext, String title, String message, int smallResourceDrawable,
                               int largeResourceDrawable, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder noti = new NotificationCompat.Builder(mContext);
        noti.setContentTitle(title);
        noti.setContentText(message);
        noti.setSmallIcon(smallResourceDrawable);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
        noti.setContentIntent(pendingIntent);
        notificationManager.notify(1, noti.build());
    }
}
