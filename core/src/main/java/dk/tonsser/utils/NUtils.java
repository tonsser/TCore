package dk.tonsser.utils;
/**
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.location.Location;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class NUtils {
    private static Timer timer;
    private static String TAG = NUtils.class.getName();

    /**
     * @param mBoolean
     * @return int
     * @author Casper Rasmussen - 2012
     * Use this method to transform a boolean to a int, true = 1 , false = 0
     */

    public static int booleanToInt(boolean mBoolean) {
        if (mBoolean)
            return 1;
        else
            return 0;
    }

    /**
     * @param mInt
     * @return boolean
     * @author Thomas Nielsen - 2014
     * Use this method to transform an int to a boolean, 1 = true , 0 = false
     */
    public static boolean intToBoolean(int mInt) {
        return mInt == 1;
    }

    /**
     * @param mBoolean
     * @return String
     * @author Casper Rasmussen -2012
     * Use this method to transform a boolean to a String, true = "1" , false = "0"
     */
    public static String booleanToString(boolean mBoolean) {
        return String.valueOf(booleanToInt(mBoolean));
    }

    /**
     * @param filePath
     * @param compressRate    - 0-100%, 100% means no compression
     * @param mCompressFormat - CompressFormat.PNG, CompressFormat.JPEG etc.
     * @return byte[]
     * @author Casper Rasmussen - 2012
     * Use this method to get byte[] from a file of a given path.
     */
    public static byte[] filePathToByteArray(String filePath, int compressRate, CompressFormat mCompressFormat) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        Bitmap bitmapFromFile = BitmapFactory.decodeFile(filePath, options);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapFromFile.compress(mCompressFormat, compressRate, stream);
        byte[] byteArray = stream.toByteArray();

        return byteArray;
    }

    /**
     * @param filePath
     * @return byte[]
     * @author Casper Rasmussen - 2012
     * Use this method to get a byte[] from filepath of a PNG-file, compressed with 75.
     * Use filePathToByteArray for more variables
     */
    public static byte[] filePathToPNGByteArray(String filePath) {
        return filePathToByteArray(filePath, 75, CompressFormat.PNG);
    }

    /**
     * @param filePath
     * @return byte[]
     * @author Casper Rasmussen - 2012
     * Use this method to get a byte[] from filepath of a JPEG-file, compressed with 75.
     * Use filePathToByteArray for more variables
     */
    public static byte[] filePathToJPEGByteArray(String filePath) {
        return filePathToByteArray(filePath, 75, CompressFormat.JPEG);
    }

    /**
     * Use this method to check if you current data is outdated, just add cacheTime in ms and lastLoadedUnix as unixtimestamp in ms
     *
     * @param cacheTime
     * @param lastLoadedUnix
     * @return
     */
    public static boolean isOutdated(long cacheTimeMs, long lastLoadedUnix) {
        return System.currentTimeMillis() - lastLoadedUnix > cacheTimeMs;
    }

    /**
     * Will delete the folder and all files of the given path
     * Requires a write permission
     *
     * @param path
     */
    public static void deleteFiles(String path) {
        File file = new File(path);
        NLog.d("deleting", path);
        if (file.exists()) {
            String deleteCmd = "rm -r " + path;
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec(deleteCmd);
            } catch (IOException e) {
                NLog.e("deleteFiles", e);
            }
        }
    }

    /**
     * Will do a callback after the given delayMs, this will be in another thread. If you need to do UI stuff, please use
     * the method performTaskDelayWithCallbackInMainThread
     *
     * @param delayMS
     * @param mNCallbackListener
     */
    public static void performTaskDelay(long delayMS, final NCallbackListener mNCallbackListener) {
        Timer timer = new Timer();

        TimerTask timerTask = new TimerTask() {
            public void run() {
                if (mNCallbackListener != null)
                    mNCallbackListener.onCallback();
            }
        };
        timer.schedule(timerTask, new Date(System.currentTimeMillis() + delayMS));
    }


    public static void performTaskDelayWithCallbackInMainThread(final Activity mActivity, int delayMS, final NCallbackListener mNCallbackListener) {
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            public void run() {
                if (mActivity != null) {
                    mActivity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (mNCallbackListener != null) {
                                mNCallbackListener.onCallback();
                            }
                        }
                    });
                }
            }
        };
        timer.schedule(timerTask, new Date(System.currentTimeMillis() + delayMS));

    }

    /**
     * Use this method to parse a JSONObject only with key & value in main object to a hashMap of pairs
     *
     * @param object
     * @return HashMap<String, String>
     * @throws Exception
     */
    public static HashMap<String, String> jsonToHashMap(JSONObject object) throws Exception {
        if (object == null)
            return null;

        HashMap<String, String> data = new HashMap<String, String>();
        if (object != null) {
            Iterator<?> iter = object.keys();
            while (iter.hasNext()) {
                String key = (String) iter.next();
                String value = object.optString(key);
                data.put(key, value);
            }
        }
        return data;
    }

    /**
     * Will return a list of random location from the given position & radius
     *
     * @param amount
     * @param lat
     * @param lng
     * @param radius
     * @return ArrayList<Location>
     */
    public static ArrayList<Location> randomLocations(int amount, double lat, double lng, double radius) {
        ArrayList<Location> locations = new ArrayList<Location>(0);
        Location centerLocation = new Location("local");
        centerLocation.setLatitude(lat);
        centerLocation.setLongitude(lng);

        final double lngScale = Math.cos((Math.PI / 180.0) * lat);
        final double radiusDeg = radius / 111.2; // radius converted to degrees (square)

        while (locations.size() < amount) {
            Location l = new Location("local");
            double dLat = (Math.random() * radiusDeg * 2) - radiusDeg;
            double dLng = (Math.random() * radiusDeg * 2 / lngScale) - (radiusDeg / lngScale);

            l.setLatitude(centerLocation.getLatitude() + dLat);
            l.setLongitude(centerLocation.getLongitude() + dLng);
            double dist = l.distanceTo(centerLocation) / 1000.0;

            if (dist < (radius)) {
                locations.add(l);
            }
        }
        return locations;
    }


    public static void bitmapToFile(Context mContext, File mFile, Bitmap mBitmap) throws Exception {

        //Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        mBitmap.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

        //write the bytes in file
        FileOutputStream fos = new FileOutputStream(mFile);
        fos.write(bitmapdata);
    }

    public interface NCallbackListener {
        void onCallback();
    }

}
