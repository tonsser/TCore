package dk.tonsser.controllers.heap;
/**
 * @author Casper Rasmussen 2012
 */
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.util.Log;

import java.text.DecimalFormat;

import dk.tonsser.base.NBaseApplication;
import dk.tonsser.controllers.NScreenParameters;
import dk.tonsser.utils.NLog;

@Deprecated
public class NHeapController {
	private static String TAG = NHeapController.class.getName();
	
	private static boolean DEBUG = false;
	private static int DEFINITION_OF_SMALL_HEAP = 31;
	public static float maxHeap ;

	/**
	 * Returns true if this is a low-RAM device. Exactly whether a device is low-RAM is ultimately up to 
	 * the device configuration, but currently it generally means something in the class of a 512MB device
	 * with about a 800x480 or less screen. This is mostly intended to be used by apps to determine whether
	 * they should turn off certain features that require more RAM.
	 * @param mActivity
	 * Requires API 19 - 4.4 Kitkat
	 * @return boolean
	 */
	@TargetApi(value = 19)
	public boolean isLowMemoryDevice(Activity mActivity){
		if(mActivity == null){
			NLog.e(TAG + " isLowMemoryDevice", "Activity is null, returning true");
			return true;
		}
		
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){
			NLog.e(TAG+ " isLowMemoryDevice", "Api level is not 19 or higher, returning false");
			return false;
		}
		
		return ((ActivityManager) mActivity.getSystemService(Context.ACTIVITY_SERVICE)).isLowRamDevice();
	}
	
	/**
	 * This method will return a boolean 
	 */
	public static boolean isCurrentHeapLow() {
		return isCurrentHeapLow(0.6f,0.4f);
	}
	
	public static boolean isCurrentHeapLow(float percent,float percentLowHeap) {

		float allocated = Debug.getNativeHeapAllocatedSize()/(1048576f);
		if(maxHeap==0)
			maxHeap = Runtime.getRuntime().maxMemory()/1048576f;
		float leftAmount = maxHeap - allocated;
		float heapPercent = allocated /maxHeap;

		if(DEBUG)
			NLog.d("heap",allocated+"/"+maxHeap + " " +heapPercent*100+"% "+leftAmount);

		return (heapPercent > percent && leftAmount < 15) || (maxHeap < 24 && heapPercent > percentLowHeap);
	}

	/**
	 * This method will return true if general heap is blow DEFINITION_OF_SMALL_HEAP and screenDeseity is above 160, else return false
	 * This should be used to check what size images the device should load etc
	 */
	public static boolean isHeapLowScreenTakenIntoAccount() {
		try{
			return maxHeap < DEFINITION_OF_SMALL_HEAP && NScreenParameters.screenDensityConstant > 1;
		}
		catch(Exception e){
			NLog.e("isHeapLowScreenTakenIntoAccount",e);
			return false;
		}
	}
	/**
	 * This method will return true if general heap is blow DEFINITION_OF_SMALL_HEAP, else return false
	 * This should be used to check if the phone can run more memory heavy code
	 */
	public boolean isHeapLow() {
		if(maxHeap==0){
			NLog.e("isHeapLow maxHeap is zero","Did you run logHeap() on application onCreate()?");
			return false;
		}
		return maxHeap < DEFINITION_OF_SMALL_HEAP;
	}
	public static void logHeap() {
		Double allocated = Double.valueOf(Debug.getNativeHeapAllocatedSize())/new Double((1048576));
		Double available = Double.valueOf(Debug.getNativeHeapSize())/1048576.0;
		Double free = Double.valueOf(Debug.getNativeHeapFreeSize())/1048576.0;
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);

		if(NBaseApplication.getInstance().DEBUG){
			Log.d("Heap", "Heap native: allocated " + df.format(allocated) + "MB of " + df.format(available) + "MB (" + df
					.format(free) + "MB free)");
			Log.d("Heap", "Memory: allocated: " + df.format(Double.valueOf(Runtime.getRuntime()
					.totalMemory() / 1048576)) + "MB of " + df.format(Double.valueOf(Runtime.getRuntime()
					.maxMemory() / 1048576)) + "MB (" + df.format(Double.valueOf(Runtime.getRuntime()
					.freeMemory() / 1048576)) + "MB free)");
		}
		maxHeap = Float.valueOf(Runtime.getRuntime().maxMemory() / 1048576);
	}

	/** 
	 * Wait 2500 ms to allow for garbage collection
	 * @param mLowHeapAsyncListener The listener whose callbacks will be invoked
	 * @edit Christian: Added ".execute" to the asynctask-constructors - you wanna run them, right?
	 * added the possibility to use your listener as trigger for flow-control. If you supply a listener, 
	 * it's cancel-method will be called whether or not the heap was actually cleared
	 */
	public static void takeCoolDownIfNeeded(NLowHeapASync.LowHeapAsyncListener mLowHeapAsyncListener){
		if(isCurrentHeapLow()) {
			new NLowHeapASync(2500, mLowHeapAsyncListener).execute();
		}
		else if( mLowHeapAsyncListener != null ) {
			mLowHeapAsyncListener.onProgressDialogCancel();
		}
	}
	/** 
	 * Wait for garbage collection
	 * @param ms the time to wait for garbage collection in milliseconds
	 * @param mLowHeapAsyncListener The listener whose callbacks will be invoked
	 * @edit Christian: Added ".execute" to the asynctask-constructors - you wanna run them, right?
	 * added the possibility to use your listener as trigger for flow-control. If you supply a listener, 
	 * it's cancel-method will be called whether or not the heap was actually cleared
	 */
	public static void takeCoolDownIfNeeded(int ms, NLowHeapASync.LowHeapAsyncListener mLowHeapAsyncListener){
		if(isCurrentHeapLow()) {
			new NLowHeapASync(ms, mLowHeapAsyncListener).execute();
		}
		else if( mLowHeapAsyncListener != null ) {
			mLowHeapAsyncListener.onProgressDialogCancel();
		}
	}
	
	public static float getHeapLeftPercent(){
		float allocated = Debug.getNativeHeapAllocatedSize()/(1048576f);
		if(maxHeap==0)
			maxHeap = Runtime.getRuntime().maxMemory()/1048576f;

		float heapPercent = allocated /maxHeap;
		return heapPercent;
	}
}
