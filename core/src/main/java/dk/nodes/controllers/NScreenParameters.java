package dk.nodes.controllers;
/**
 * @author Casper Rasmussen 2012
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import dk.nodes.ncore.R;
import dk.nodes.utils.NLog;

public class NScreenParameters {
	private static String TAG = NScreenParameters.class.getName();

	public static int screenDensity;
	public static float screenDensityConstant;
	public static int screenWidth;
	public static int screenHeight;
	public static float screenRatio;
	public static float screenXCenter;
	public static float screenYCenter;
	public static int orientation;
	public static String screenTypeName; //when set can be small/normal/large/x-large or N/A
	public static int screenType;
	public static boolean isStatusBarVisisble;
	public static boolean isStatusBarTransulent;
	public static boolean isNavigationBarTransulent;

	public static final int SMALL_SCREEN = 1;
	public static final int NORMAL_SCREEN = 2;
	public static final int LARGE_SCREEN = 3;
	public static final int X_LARGE_SCREEN = 4;

	/**
	 * Use this method to calculate all screenparameters, and set initialize the public static values
	 * Run this in Application onCreate()
	 * @param mContext
	 * @param isStatusBarVisible
	 */
	public static void setScreenParameters(Context mContext,boolean isStatusBarVisible, boolean isStatusBarTransulent,
			boolean isNavigationBarTransulent) {

		NScreenParameters.isStatusBarVisisble = isStatusBarVisible;
		NScreenParameters.isStatusBarTransulent = isStatusBarTransulent;
		NScreenParameters.isNavigationBarTransulent = isNavigationBarTransulent;

		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		screenDensity = metrics.densityDpi;
		screenDensityConstant = (screenDensity ) / (160f);

		int statusBarHeight = getStatusBarHeight(mContext);
		int navigationBarHeight = getNavigationBarHeight(mContext);

		screenWidth = display.getWidth();  
		screenHeight = display.getHeight()-statusBarHeight + navigationBarHeight;
		screenRatio = (float)screenHeight/(float)screenWidth;
		screenXCenter = screenWidth / 2f;
		screenYCenter = screenHeight / 2f;

		setScreenSizeMask(mContext);

		NLog.d("Screen isStatusBarVisisble", String.valueOf(isStatusBarVisisble));
		NLog.d("Screen isStatusBarTransulent", String.valueOf(isStatusBarTransulent));
		NLog.d("Screen isNavigationBarTransulent", String.valueOf(isNavigationBarTransulent));
		NLog.d("Screen hasMenuKeysInScreen", String.valueOf(hasMenuKeysInScreen(mContext)));
		NLog.d("Screen statusBarHeight", String.valueOf(statusBarHeight));
		NLog.d("Screen actionBarHeight", String.valueOf(getActionBarHeight(mContext)));
		NLog.d("Screen navigationBarHeight",navigationBarHeight+"");
		NLog.d("Screen dimension", screenWidth+"x"+screenHeight);
		NLog.d("Screen dpi", screenDensity+" aka "+screenDensityConstant);
		NLog.d("Screen", "screenRatio: "+screenRatio);
		NLog.d("Screen type",screenTypeName);
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public static boolean hasMenuKeysInScreen(Context mContext){
		if(mContext == null) {
			NLog.e(TAG + " hasMenuKeysInScreen", "Context was null, returning false");
			return false;
		}



		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
			boolean hasMenuKey = ViewConfiguration.get(mContext).hasPermanentMenuKey();
			boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
			if(!hasMenuKey && !hasBackKey)
				return true;
			else
				return false;
		}
		else
			return false;
	}

	public static int getNavigationBarHeight(Context mContext){
		if(mContext == null) {
			NLog.e(TAG + " getNavigationBarHeight", "Context was null, returning 0");
			return 0;
		}

		if(isNavigationBarTransulent && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && hasMenuKeysInScreen(mContext))
			return getNavigationbarHeight(mContext);
		else
			return 0;
	}

	private static int getNavigationbarHeight(Context mContext){
		if(mContext == null) {
			NLog.e(TAG + " getNavigationbarHeight", "Context was null, returning 0");
			return 0;
		}

		Resources resources = mContext.getResources();
		int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
		if (resourceId > 0) {
			NLog.d("getNavigationBarHeight getDimensionPixelSize "+resources.getDimensionPixelSize(resourceId));
			return resources.getDimensionPixelSize(resourceId);
		}
		return 0;
	}
	/**
	 * Will return 0 if statusbar is transulent
	 * @param mContext
	 * @return
	 */
	public static int getStatusBarHeight(Context mContext){
		if(mContext == null) {
			NLog.e(TAG + " getStatusBarHeight", "Context was null, returning 0");
			return 0;
		}

		int statusBarHeight = 0;

		if(isStatusBarVisisble)
			statusBarHeight =  getStatusbarHeight(mContext);

		if(isStatusbarTransulent(mContext))
			statusBarHeight = 0;

		return statusBarHeight;
	}

    public static boolean isNavigationBarTranslucent(Context mContext){
        if(mContext == null) {
            NLog.e(TAG + " isNavigationBarTransulent", "Context was null, returning false");
            return false;
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && isNavigationBarTransulent)
            return true;
        else
            return false;
    }

	// A method to find height of the status bar
	public static int getStatusbarHeight(Context mContext){
		int result = 0;
		int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = mContext.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	public static boolean isStatusbarTransulent(Context mContext){
		if(mContext == null) {
			NLog.e(TAG + " isStatusbarTransulent", "Context was null, returning false");
			return false;
		}

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && isStatusBarTransulent)
			return true;
		else
			return false;
	}

	/**
	 * @param mContext
	 * @return
	 */
	public static int getStatusBarHeightPadding(Context mContext){
		if(mContext == null) {
			NLog.e(TAG + " getStatusBarHeight", "Context was null, returning 0");
			return 0;
		}

		int statusBarHeight = 0;
		if(isStatusBarVisisble && isStatusbarTransulent(mContext))
			statusBarHeight = getStatusbarHeight(mContext);
		else
			statusBarHeight = 0;

		return statusBarHeight;
	}

	public static int getActionBarHeight(Context mContext){
		if(mContext == null) {
			NLog.e(TAG + " getActionBarHeight", "Context was null, returning 48dp");
			return toPx(48);
		}

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return mContext.getResources().getDimensionPixelSize(R.dimen.abc_action_bar_default_height_material);
        }

		return NScreenParameters.toPx(48);
	}

/**
	 * Will return statusbar + actionbar height if transulent
	 * @param mContext
	 * @return
	 */
	public static int getActionBarAndStatusBarHeight(Context mContext){
		if(isStatusbarTransulent(mContext))
			return getActionBarHeight(mContext) + getStatusbarHeight(mContext);
		else
			return getActionBarHeight(mContext);
	}

	public static void setScreenSizeMask(Context mContext) {
		Configuration config = mContext.getResources().getConfiguration();
		if((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) ==  Configuration.SCREENLAYOUT_SIZE_SMALL) {
			screenTypeName = "small";
			screenType = SMALL_SCREEN;
		}
		else if((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) ==  Configuration.SCREENLAYOUT_SIZE_NORMAL) {
			screenTypeName = "normal";
			screenType = NORMAL_SCREEN;
		}
		else if((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) ==  Configuration.SCREENLAYOUT_SIZE_NORMAL) {
			screenTypeName = "large";
			screenType = LARGE_SCREEN;
		}
		else if((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) ==  Configuration.SCREENLAYOUT_SIZE_XLARGE) {
			screenTypeName = "x-large";
			screenType = X_LARGE_SCREEN;
		}
		else{
			screenTypeName = "N/A";
		}
	}

	/**
	 * This method will check if width is bigger than height and decide what orientation the screen got by default
	 * Use this method to decide land/portrait orientation if the app is both supported phone and tablet
	 * This method will set the public static int orientation for later use.
	 * @author Christian - 2012
	 * @param mActivity
	 * @return Configuration.ORIENTATION_PORTRAIT or Configuration.ORIENTATION_LANDSCAPE or Configuration.ORIENTATION_SQUARE
	 */
	public static int getAndSetScreenOrientation(Activity mActivity)
	{
		Display getOrient = mActivity.getWindowManager().getDefaultDisplay();
		orientation = Configuration.ORIENTATION_UNDEFINED;
		if(getOrient.getWidth()==getOrient.getHeight()){
			orientation = Configuration.ORIENTATION_SQUARE;
		} else{ 
			if(getOrient.getWidth() < getOrient.getHeight()){
				orientation = Configuration.ORIENTATION_PORTRAIT;
			}else { 
				orientation = Configuration.ORIENTATION_LANDSCAPE;
			}
		}
		return orientation;
	}

	public static int toPx(float dp){
		return  (int) (dp*screenDensityConstant);
	}
	public static int toDp(float pixels){
		return (int) (pixels/screenDensityConstant);
	}

	/**
	 * This method will return 320,480 or 720 depending on most fitting screenWidth
	 * @return String
	 */
	public static String getScreenWidthForApi(){
		if(screenWidth<480)
			return "320";
		else if(screenWidth<720)
			return "480";
		else
			return "720";	
	}

	/**
	 *  This method will return 320,480, 720 or 1080 depending on most fitting screenWidth
	 * @return String
	 */
	public static String getScreenWidthForApi1080(){
		if(screenWidth<480)
			return "320";
		else if(screenWidth<720)
			return "480";
		else if(screenWidth<1080)
			return "720";
		else
			return "1080";	
	}

	public static boolean isTablet(Context mContext){
		if(mContext == null) {
			NLog.e(TAG + " isTablet", "Context was null, returning false");
			return false;
		}

		boolean tabletSize = mContext.getResources().getBoolean(R.bool.isTablet);
		if (tabletSize) 
			return true;
		else 
			return false;
	}

	public static void forceActivityPotrait(Activity mActivity){
		mActivity.setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	public static void forceActivityLandscape(Activity mActivity){
		mActivity.setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

	public static void forceActivityLandscapeForTabletElsePotrait(Activity mActivity){
		if(isTablet(mActivity))
			forceActivityLandscape(mActivity);
		else
			forceActivityPotrait(mActivity);
	}
}
