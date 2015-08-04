package dk.tonsser.utils;

import java.util.Date;

import dk.tonsser.utils.math.TUnits;

public class TTimeFormater {

    /**
     * Will format the timeunix to a string with days, hours, minuttes and seconds.
     * fx 1 day 10:03:15 || 2 days 23:11:32
     *
     * @param timeInUnix
     * @param dayName
     * @param daysName
     * @return String
     */
    public static String toDayHourMinSec(long timeInUnix, String dayName, String daysName) {
        timeInUnix = timeInUnix / 1000;
        long days, hours, minutes, seconds;
        String daysT = "", restT = "";

        days = (Math.round(timeInUnix) / 86400);
        hours = (Math.round(timeInUnix) / 3600) - (days * 24);
        minutes = (Math.round(timeInUnix) / 60) - (days * 1440) - (hours * 60);
        seconds = Math.abs(timeInUnix) % 60;

        if (days == 1) daysT = String.format("%d " + dayName + " ", days);
        if (days > 1) daysT = String.format("%d " + daysName + " ", days);

        restT = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        return daysT + restT;
    }

    /**
     * Will return mm:ss of the given delta time
     *
     * @param deltaTime
     * @return
     */
    public static String toMinSec(long deltaTime) {
        if (deltaTime < 0)
            deltaTime = 0;

        int minutes = (int) (deltaTime / 1000 / 60);
        int second = (int) ((deltaTime / 1000) % 60);

        String s = null;
        if (second < 10)
            s = "0" + second;
        else
            s = "" + second;

        String m = null;
        if (minutes < 10)
            m = "0" + minutes;
        else
            m = "" + minutes;

        return m + ":" + s;
    }

    /**
     * Will return mm:ss since startTime
     *
     * @param startTime
     * @return String
     */
    public static String toMinAndSecUsed(long startTime) {
        long deltaTime = System.currentTimeMillis() - startTime;
        return toMinSec(deltaTime);
    }

    /**
     * Will return mm:ss left after startTime and amountMs
     *
     * @param startTime
     * @param amountOfMs
     * @return
     */
    public static String toMinAndSecLeft(long startTime, int amountOfMs) {
        long deltaTime = (startTime + amountOfMs) - System.currentTimeMillis();
        return toMinSec(deltaTime);
    }

    /**
     * This will return fitting string in english
     * just now
     * a minute ago
     * x minute ago
     * an hour ago
     * x hour ago
     * yesterday
     * x days ago
     *
     * @param timeSince
     * @return
     */
    public static String toTimeSincePretty(Date timeSince) {
        return toTimeSincePretty(timeSince, "just now", "a minute ago", "minutes ago", "an hour ago", "hours ago", "yesterday", "days ago");
    }

    /**
     * This will return fitting string, with the inputted Strings
     * just now
     * a minute ago
     * x minute ago
     * an hour ago
     * x hour ago
     * yesterday
     * x days ago
     *
     * @param timeSince
     * @param justNow
     * @param aMinuteAgo
     * @param minutesAgo
     * @param anHourAgo
     * @param hoursAgo
     * @param yesterday
     * @param daysAgo
     * @return
     */
    public static String toTimeSincePretty(Date timeSince, String justNow, String aMinuteAgo, String minutesAgo, String anHourAgo,
                                           String hoursAgo, String yesterday, String daysAgo) {
        long now = System.currentTimeMillis();
        long time = timeSince.getTime();
        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now - time;
        if (diff < TUnits.MIN_IN_MS) {
            return justNow;
        } else if (diff < 2 * TUnits.MIN_IN_MS) {
            return aMinuteAgo;
        } else if (diff < 50 * TUnits.MIN_IN_MS) {
            return diff / TUnits.MIN_IN_MS + " " + minutesAgo;
        } else if (diff < 90 * TUnits.MIN_IN_MS) {
            return anHourAgo;
        } else if (diff < 120 * TUnits.MIN_IN_MS) {
            return "2 " + hoursAgo;
        } else if (diff < 24 * TUnits.HOUR_IN_MS) {
            return diff / TUnits.HOUR_IN_MS + " " + hoursAgo;
        } else if (diff < 48 * TUnits.HOUR_IN_MS) {
            return yesterday;
        } else {
            return diff / TUnits.DAY_IN_MS + " " + daysAgo;
        }
    }
}
