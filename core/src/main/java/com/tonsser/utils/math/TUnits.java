package com.tonsser.utils.math;

/**
 * @author Casper Rasmussen - 2012
 */
public class TUnits {
    public final static int DAY_IN_HOUR = 24;
    public final static int HOUR_IN_MIN = 60;
    public final static int MIN_IN_SEC = 60;
    public final static int SEC_IN_MS = 1000;

    public final static int YEAR_IN_MONTH = 12;
    public final static int YEAR_IN_WEEK = 52;
    public final static int YEAR_IN_DAY = 365;
    public final static int YEAR_IN_HOUR = YEAR_IN_DAY * DAY_IN_HOUR;
    public final static long YEAR_IN_MIN = YEAR_IN_DAY * DAY_IN_HOUR * HOUR_IN_MIN;
    public final static long YEAR_IN_SEC = YEAR_IN_DAY * DAY_IN_HOUR * HOUR_IN_MIN * MIN_IN_SEC;
    public final static long YEAR_IN_MS = YEAR_IN_DAY * DAY_IN_HOUR * HOUR_IN_MIN * MIN_IN_SEC * SEC_IN_MS;

    public final static int MONTH_IN_DAYS = 31;
    public final static int MONTH_IN_HOURS = MONTH_IN_DAYS * DAY_IN_HOUR;
    public final static long MONTH_IN_MIN = MONTH_IN_DAYS * DAY_IN_HOUR * HOUR_IN_MIN;
    public final static long MONTH_IN_SEC = MONTH_IN_DAYS * DAY_IN_HOUR * HOUR_IN_MIN * MIN_IN_SEC;
    public final static long MONTH_IN_MS = MONTH_IN_DAYS * DAY_IN_HOUR * HOUR_IN_MIN * MIN_IN_SEC * SEC_IN_MS;

    public final static int WEEK_IN_DAY = 7;
    public final static int WEEK_IN_HOUR = WEEK_IN_DAY * DAY_IN_HOUR;
    public final static int WEEK_IN_MIN = WEEK_IN_DAY * DAY_IN_HOUR;
    public final static int WEEK_IN_SEC = WEEK_IN_DAY * DAY_IN_HOUR * HOUR_IN_MIN * MIN_IN_SEC;
    public final static long WEEK_IN_MS = WEEK_IN_DAY * DAY_IN_HOUR * HOUR_IN_MIN * MIN_IN_SEC * SEC_IN_MS;


    public final static int DAY_IN_MIN = DAY_IN_HOUR * HOUR_IN_MIN;
    public final static int DAY_IN_SEC = DAY_IN_HOUR * HOUR_IN_MIN * MIN_IN_SEC;
    public final static long DAY_IN_MS = DAY_IN_HOUR * HOUR_IN_MIN * MIN_IN_SEC * SEC_IN_MS;

    public final static int HOUR_IN_SEC = HOUR_IN_MIN * MIN_IN_SEC;
    public final static int HOUR_IN_MS = HOUR_IN_MIN * MIN_IN_SEC * SEC_IN_MS;

    public final static int MIN_IN_MS = MIN_IN_SEC * SEC_IN_MS;

    public final static int KILO_BYTE = 1024;
    public final static int MEGA_BYTE = 1024 * 1024;
    public final static int GIGA_BYTE = 1024 * 1024 * 1024;
    public final static int TERA_BYTE = 1024 * 1024 * 1024 * 1024;

    public static int byteToKiloByte(long mByte) {
        return (int) (mByte / KILO_BYTE);
    }

    public static int byteToMegaByte(long mByte) {
        return (int) (mByte / MEGA_BYTE);
    }

    public static int byteToGigaByte(long mByte) {
        return (int) (mByte / GIGA_BYTE);
    }

    public static String byteToPrettyString(long mByte) {
        String[] extendtions = {"B", "kB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};
        int extendtionPicker = 0;
        while (mByte > 1024) {
            mByte = mByte / 1024;
            extendtionPicker++;
        }
        if (extendtionPicker < extendtions.length)
            return mByte + extendtions[extendtionPicker];
        else
            return mByte + "10E" + extendtionPicker * 3;
    }

}
