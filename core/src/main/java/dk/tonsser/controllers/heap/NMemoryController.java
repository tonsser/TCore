package dk.tonsser.controllers.heap;

import android.os.Environment;
import android.os.StatFs;

import dk.tonsser.utils.NLog;

@Deprecated
public class NMemoryController {


    public static double totalSize;
    public static double freeSize;
    public static double usedSize;
    public static double usedPercent;
    public static double freePercent;

    public static void logMemory() {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
        long blockSize = statFs.getBlockSize();
        totalSize = statFs.getBlockCount() * blockSize;
        freeSize = statFs.getFreeBlocks() * blockSize;
        usedSize = totalSize - freeSize;
        usedPercent = ((int) (freeSize / (totalSize) * 100f));
        freePercent = ((int) (usedSize / (totalSize) * 100f));
        NLog.d("ExternalStorage Memory", "totalSize: " + totalSize + " freeSize: " + freeSize + " usedSize: " + usedSize + " usedPercent: " + usedPercent + "% freePercent: " + freePercent + "%");
    }

    public static boolean isMinimumFree(float percent) {
        return percent <= freePercent;
    }

    public static boolean isMinimumUsed(float percent) {
        return percent <= freePercent;
    }

    public static boolean isMinimumFree(double mByte) {
        return mByte <= freeSize;
    }

    public static boolean isMinimumUsed(double mByte) {
        return mByte <= usedSize;
    }

    public static boolean isMaximumFree(float percent) {
        return percent >= freePercent;
    }

    public static boolean isMaximumUsed(float percent) {
        return percent >= freePercent;
    }

    public static boolean isMaximumFree(double mByte) {
        return mByte >= freeSize;
    }

    public static boolean isMaximumUsed(double mByte) {
        return mByte >= usedSize;
    }
}
