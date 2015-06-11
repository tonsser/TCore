package dk.nodes.controllers;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import dk.nodes.utils.NLog;

@Deprecated
public class NTwitter {

	private static String TAG = NTwitter.class.getSimpleName();

	/**
	 * This method open a browser intent and do a tweet to twitter.
	 * If Twitter app is installed, it will auto deeplink into app and make the tweet through it.
	 * @param mActivity
	 * @param tweet
	 */
	public static void makeTweet(Activity mActivity, String tweet){
		makeTweet(mActivity, tweet, null);
	}
	
	public static void makeTweet(Activity mActivity, String tweet, boolean showChooseApp){
		makeTweet(mActivity, tweet, null, showChooseApp);
	}
	
	public static void makeTweet(Activity mActivity, String tweet, String url){
		makeTweet(mActivity, tweet, url, false);
	}
	
	/**
	 * 	 * This method open a browser intent and do a tweet to twitter.
	 * If Twitter app is installed, it will auto deeplink into app and make the tweet through it.
	 * 
	 * @param mActivity
	 * @param tweet The message
	 * @param url AN url to be posted with the message
	 * @param showChooseApp Show the app-chooser dialog or not.
	 */
	public static void makeTweet(Activity mActivity, String tweet, String url, boolean showChooseApp){
		try {
			StringBuilder sb = new StringBuilder();
			
			if(showChooseApp)
				sb.append("https://www.twitter.com/intent/tweet?text=" + tweet);
			else
				sb.append("https://twitter.com/intent/tweet?text=" + tweet);
			
			if(url != null){
				sb.append("?url="+url);
			}
			Intent twitterIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(sb.toString()));
			mActivity.startActivity(twitterIntent);
		} catch (Exception e) {
			NLog.e(TAG + " makeTweet",e);
		} 
	}
	
	/**
	 * Opens a tweet in the Twitter app if possible. Falls back to browser.
	 * 
	 * @param mActivity
	 * @param tweetId 
	 * @param screenName Necessary since it's needed if the user doesn't have the native Twitter-app installed.
	 */
	public static void showTweetInTwitterApp(Activity mActivity, String tweetId, String screenName){
		if(mActivity == null){
			NLog.e(TAG + " openTweetInTwitter","Unable to open Twitter app. 'mActivity' is NULL");
			return;
		}
		try {
		    // Get the Twitter app if possible
			mActivity.getPackageManager().getPackageInfo("com.twitter.android", 0);
			mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://status?status_id=" + tweetId)));
		} catch (Exception e) {
		    // No Twitter app, revert to browser
			mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + screenName + "/status/" + tweetId)));
		}
	}
	
	/**
	 * Opens the Twitter app if it's installed and goes shows the user provided from screenName. Falls back to browser.
	 * 
	 * @param mActivity
	 * @param screenName
	 */
	public static void showUserInTwitterApp(Activity mActivity, String screenName){
		if(mActivity == null){
			NLog.e(TAG + " openUserInTwitter","Unable to open Twitter app. 'mActivity' is NULL");
			return;
		}
		try {
		    // Get the Twitter app if possible
			mActivity.getPackageManager().getPackageInfo("com.twitter.android", 0);
			mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + screenName)));
		} catch (Exception e) {
		    // No Twitter app, revert to browser
			mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/#!/" + screenName)));
		}
	}
}
