package dk.nodes.controllers.heap;

import android.os.Environment;
import android.os.StatFs;

import dk.nodes.utils.NLog;

public class NMemoryController {


	public static double totalSize;
	public static double freeSize;
	public  static double usedSize;
	public static double usedPercent;
	public static double freePercent;

	public static void logMemory(){
		StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
		long blockSize = statFs.getBlockSize();
		totalSize = statFs.getBlockCount()*blockSize;
		freeSize = statFs.getFreeBlocks()*blockSize;
		usedSize = totalSize-freeSize;
		usedPercent = ((int)(freeSize/(totalSize)*100f));
		freePercent = ((int)(usedSize/(totalSize)*100f));
		NLog.d("ExternalStorage Memory", "totalSize: "+totalSize+" freeSize: "+freeSize+" usedSize: "+usedSize+" usedPercent: "+usedPercent+"% freePercent: "+freePercent+"%");
	}
	
	public static boolean isMinimumFree(float percent){
		if(percent<=freePercent)
			return true;
		else
			return false;
	}
	public static boolean isMinimumUsed(float percent){
		if(percent<=freePercent)
			return true;
		else
			return false;
	}
	
	public static boolean isMinimumFree(double mByte){
		if(mByte<=freeSize)
			return true;
		else
			return false;
	}
	public static boolean isMinimumUsed(double mByte){
		if(mByte<=usedSize)
			return true;
		else
			return false;
	}	
	
	public static boolean isMaximumFree(float percent){
		if(percent>=freePercent)
			return true;
		else
			return false;
	}
	public static boolean isMaximumUsed(float percent){
		if(percent>=freePercent)
			return true;
		else
			return false;
	}
	
	public static boolean isMaximumFree(double mByte){
		if(mByte>=freeSize)
			return true;
		else
			return false;
	}
	public static boolean isMaximumUsed(double mByte){
		if(mByte>=usedSize)
			return true;
		else
			return false;
	}	
}
