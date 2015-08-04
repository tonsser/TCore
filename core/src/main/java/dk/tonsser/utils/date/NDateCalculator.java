package dk.tonsser.utils.date;

import java.util.Calendar;
import java.util.Date;

import dk.tonsser.utils.NLog;

public class NDateCalculator {
	private static String TAG = NDateCalculator.class.getName();

	/**
	 * Will trim the Calendar object from hours, minutes, seconds and milisecs
	 * Fallback is a Calendar.getInstance();
	 * @param inputDate
	 * @return Calendar
	 */
	public static Calendar removeTime(Calendar mCalendar){
		if(mCalendar == null){
			NLog.e(TAG  + " removeTime", "Input mCalendar is null, " +
					"continueing with Calendar.getInstance()");
			mCalendar = Calendar.getInstance();
		}
		
		// set the calendar to start of today
		mCalendar.set(Calendar.HOUR_OF_DAY, 0);
		mCalendar.set(Calendar.MINUTE, 0);
		mCalendar.set(Calendar.SECOND, 0);
		mCalendar.set(Calendar.MILLISECOND, 0);

		return mCalendar;
	}
	
	/**
	 * Will trim the Date object hours, minutes, seconds and milisecs
	 * @param mDate
	 * @return Date
	 */
	public static Date removeTime(Date mDate){
		if(mDate == null){
			NLog.e(TAG + "removeTime", "mDate is null, continuing with currentTime");
			mDate = new Date();
		}
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTime(mDate);
		mCalendar = removeTime(mCalendar);
		return mCalendar.getTime();
	}
	
	/**
	 * Will return a Date object with today Date, without any hours, min, sec or ms
	 * @return
	 */
	public static Date getDateTrimmedToToday(){
		return removeTime(Calendar.getInstance()).getTime();
	}
	
	/**
	 * Add days to a Date object, minus will decrement
	 * Can return null in case Date input is null
	 * Will skip if days is 0
	 * @param mDate
	 * @param days
	 * @return Date or null
	 */
	public static Date incrementByDays(Date mDate, int days) {
		if(mDate == null){
			NLog.e(TAG + " addDays", "mDate is null returning null");
			return null;
		}
		
		if(days == 0){
			return mDate;
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(mDate);
		cal.add(Calendar.DATE, days); //minus number would decrement the days
		return cal.getTime();
	}
	
	/**
	 * Will return if the inputted Date-object is today according to system time
	 * If mDate is null, fallback is false
	 * @param mDate
	 * @return boolean
	 */
	public static boolean isDateToday(Date mDate){
		return (mDate != null && getDateTrimmedToToday().equals(mDate)) ? true : false;
	}
}
