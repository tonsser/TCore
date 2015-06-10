package dk.nodes.utils.date;
/**
 * @author Casper Rasmussen 2012
 */
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import dk.nodes.utils.NLog;

public class NDateParser {
	private static String TAG = NDateParser.class.getName();
	
	/**
	 * Returning a Date object with the time of inputted time and timeZone
	 * Parsing input String like this : yyyy-MM-dd HH:mm:ss
	 * @param time
	 * @param timeZone (can be null);
	 * @return Date
	 */
	public static Date stringToDateDateAndTime(String time, String timeZone){
		if(time == null){
			NLog.e(TAG+" stringToDateDateAndTime","Input time is null, returning null");
			return null;
		}
		try{
		  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		  if(timeZone!=null)
			  formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
		  return (Date)formatter.parse(time);
		}
		catch(Exception e){
			return new Date(System.currentTimeMillis());
		}
	}
	
	/**
	 * Returning a Calendar object with the time of inputed time
	 * Parsing input String like this : yyyy-MM-dd HH:mm:ss
	 * 
	 * @param time
	 * @return
	 * @throws ParseException
	 */
	public static Calendar stringToCalendar(final String time)
			throws ParseException {
		Calendar calendar = GregorianCalendar.getInstance();
		Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
		calendar.setTime(date);
		return calendar;
	}
	
	/**
	 * Returning a Date object with the time of inputted time and timeZone
	 * Parsing input String like this : HH:mm:ss
	 * @param time (can be null);
	 * @param timeZone
	 * @return Date
	 */
	public static Date stringToDateTime(String time, String timeZone){
		if(time == null){
			NLog.e(TAG+" StringToDateTime","Input time is null, returning null");
			return null;
		}
		try{
		  SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		  if(timeZone!=null)
			  formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
		  return (Date)formatter.parse(time);
		}
		catch(Exception e){
			return new Date(System.currentTimeMillis());
		}
	}
	
	/**
	 * Returning a Date object with the time of inputted time
	 * Parsing input String like this : HH:mm:ss
	 * @param time (can be null);
	 * @return Date
	 */
	public static Date stringToDate(String time){
		if(time == null){
			NLog.e(TAG+" StringToDate","Input time is null, returning null");
			return null;
		}
		try{
		  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		  return (Date)formatter.parse(time);
		}
		catch(Exception e){
			return new Date(System.currentTimeMillis());
		}
	}
	
	/**
	 * Formatting like yyyy-MM-dd HH:mm:ss
	 * @return String
	 */
	public static String getCurrentDateTimeAsFormattedString(){
		try{
			Date date = new Date(System.currentTimeMillis());
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return formatter.format(date);
		}
		catch(Exception e){
			NLog.e(TAG + " getCurrentDateTimeAsFormattedString",e);
			return "1970-01-01 00:00:00";
		}	
	}
	
	/**
	 * @param date
	 * @return "yyyy-MM-dd HH:mm:ss" format
	 */
	public static String getDateTimeAsFormattedString(final Date date) {
		String formatted = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
		.format(date);
//		return formatted.substring(0, 22) + ":" + formatted.substring(22);
		return formatted;
	}
	
	/**
	 * @param calendar
	 * @return String "dd.MM.yyyy" format
	 */
	public static String dateMonthYearfromCalendar(final Calendar calendar) {
		String formatted = new SimpleDateFormat("dd.MM.yyyy")
		.format(calendar.getTime());
		return formatted;
	}

	/**
	 * @param calendar
	 * @return "HH:mm" format
	 */
	public static String hoursMinutesFromCalendar(final Calendar calendar) {
		String formatted = new SimpleDateFormat("HH:mm")
		.format(calendar.getTime());
		return formatted;
	}
	
	/**
	 * @param calendar
	 * @return "HH:mm:ss"
	 */
	public static String ISO8601HoursMinutesSecondsFromCalendar(final Calendar calendar) {
		String formatted = new SimpleDateFormat("HH:mm:ss")
		.format(calendar.getTime());
		return formatted;
	}
	
	/** Get current date and time formatted as ISO 8601 string. */
	public static String ISO8601now() {
		return ISO8601fromCalendar(GregorianCalendar.getInstance());
	}

	/** Transform ISO 8601 string to Calendar. */
	public static Calendar ISO8601ToCalendar(final String iso8601string)
			throws ParseException {
		Calendar calendar = GregorianCalendar.getInstance();
		String s = iso8601string.replace("Z", "+00:00");
		try {
			s = s.substring(0, 22) + s.substring(23);
		} catch (IndexOutOfBoundsException e) {
			throw new ParseException("Invalid length", 0);
		}
		Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(s);
		calendar.setTime(date);
		return calendar;
	}
	
	/**
	 * http://stackoverflow.com/questions/2201925/converting-iso8601-compliant-string-to-java-util-date
	 * 
	 * Helper class for handling ISO 8601 strings of the following format:
	 * "2008-03-01T13:00:00+01:00". It also supports parsing the "Z" timezone.
	 *
	 * Transform Calendar to ISO 8601 string.
	 *
	 * @param calendar
	 * @return "yyyy-MM-dd'T'HH:mm:ssZ" format
	 */
	public static String ISO8601fromCalendar(final Calendar calendar) {
		Date date = calendar.getTime();
		String formatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
		.format(date);
		return formatted.substring(0, 22) + ":" + formatted.substring(22);
	}
	
	/** Transform Calendar to ISO 8601 string. */
	/**
	 * @param date
	 * @return "yyyy-MM-dd'T'HH:mm:ssZ"
	 */
	public static String ISO8601fromDate(final Date date) {
		String formatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
		.format(date);
		return formatted.substring(0, 22) + ":" + formatted.substring(22);
	}

	/** Transform ISO 8601 string to Date. */
	public static Date ISO8601ToDate(final String iso8601string)
			throws ParseException {
		String s = iso8601string.replace("Z", "+00:00");
		try {
			s = s.substring(0, 22) + s.substring(23);
		} catch (IndexOutOfBoundsException e) {
			throw new ParseException("Invalid length", 0);
		}
		
		Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(s);
		
		return date;
	}
}
