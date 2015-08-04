package dk.tonsser.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class NBuild {
	private static String TAG;

	/**
	 * This method will return packageName, can also return null on error
	 * @author Casper Rasmussen
	 * @param mContext
	 * @return String
	 */
	public static String getPackageName(Context mContext){
		try{
			return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).packageName;
		}
		catch(Exception e){
			NLog.e("NUtils getPackageName", e);
			NLog.w("NUtils getPackageName", "Returning null");
			return null;
		}
	}
	
	/**
	 * This method will return versionName, can return null on error
	 * @author Casper Rasmussen
	 * @param mContext
	 * @return String versionName
	 */
	public static String getVersionName(Context mContext){
		try{
			return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
		}
		catch(Exception e){
			NLog.e("NUtil getVersionName",e);
			return null;
		}
	}

	/**
	 * This method will return versionCode, can return 0 on error
	 * @param mContext
	 * @return int versionCode
	 */
	public static int getVersionCode(Context mContext){
		try{
			return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
		}
		catch(Exception e){
			NLog.e("NUtil getVersionCode",e);
			return 0;
		}
	}
	
	/**
	 * Will return the "SHA" key, used for facebook apps and more
	 * @param mContext
	 * @return String
	 */
	public static String getHashKey(Context mContext){
		try {
			PackageInfo info = mContext.getPackageManager().getPackageInfo(getPackageName(mContext), PackageManager.GET_SIGNATURES);

			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				return Base64.encodeToString(md.digest(), Base64.DEFAULT);
			}
		}
		catch (NameNotFoundException e) {
			NLog.e(TAG + " getHashKey",e);
			return null;
		} 
		catch (NoSuchAlgorithmException e) {
			NLog.e(TAG + " getHashKey",e);
			return null;
		}
		return null;
	}
}
