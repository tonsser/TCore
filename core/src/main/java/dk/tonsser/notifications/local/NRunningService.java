package dk.tonsser.notifications.local;
/**
 * @author Casper Rasmussen 2012
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Calendar;

import dk.tonsser.utils.NLog;

public class NRunningService extends Service {

	private AlarmManager alarmManager=null;
	private PendingIntent pendingIntent=null;
	private int delay=1000*60*60*24;
	private int hour = 12;
	private int min = 00;

	public void onCreate() {

	}

	public void onDestroy() {
		stopAlarms();
	}

	public void onStart(Intent intent, int startid) {
		startAlarms();

	}
	public IBinder onBind(Intent intent) {
		return null;
	}
	private void startAlarms() {			
		Calendar calorg= Calendar.getInstance();
		calorg.setTimeInMillis(System.currentTimeMillis());

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, min);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		if(calendar.getTimeInMillis()< System.currentTimeMillis())
			calendar.add(Calendar.DAY_OF_YEAR, delay);
		
		Intent alarm=new Intent(this,NNotificationAlarmReceiver.class);
		pendingIntent = PendingIntent.getBroadcast(this, 0, alarm, 0);
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),delay , pendingIntent);
	
		NLog.d("RunningService startAlarms", "Next Alarm is: " + calendar.getTime().toString());

	}

	private void stopAlarms() {
		if(alarmManager!=null)
			alarmManager.cancel(pendingIntent);
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}
}