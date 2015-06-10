package dk.nodes.controllers.versionsandcrashes.util;

/**
 * @author Casper Rasmussen 2012, modified 12/6-2013
 */

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.message.BasicHeader;

import java.util.ArrayList;
import java.util.Locale;

import dk.nodes.utils.NBuild;
import dk.nodes.utils.NLog;
import dk.nodes.webservice.NQueryBuilder;
import dk.nodes.webservice.NWebserviceConstants;
import dk.nodes.webservice.NWebserviceController;
import dk.nodes.webservice.models.NResponse;
import dk.nodes.webservice.parser.NJSONObject;

public class NVersionASync extends AsyncTask<String, Void, NJSONObject> {
	private String TAG = NVersionASync.class.getName();
	private final boolean DEBUG = false;
	
	private String packageName;
	private NVersioAsyncListener mNVersioAsyncListener;
	private String currentUserVersion	= "0.0.0.0";
	private Activity mActivity;
	private String lastVisitedVersion;
	private String locale;

	public NVersionASync(Activity mActivity, String locale, NVersioAsyncListener mNVersioAsyncListener ) {
		this.mActivity = mActivity;
		this.locale = locale;
		this.packageName = NBuild.getPackageName(mActivity);
		this.currentUserVersion = NBuild.getVersionName(mActivity);
		this.lastVisitedVersion = NVersionSharedPrefController.getLastVisitedVersion(mActivity);
		this.mNVersioAsyncListener = mNVersioAsyncListener;
	}

	@Override
	protected void onPostExecute(NJSONObject json) {
		if(DEBUG)
			Log.d("response", json.toString());
		
		try{
			NJSONObject data = json.getJSONObject("data");
			NJSONObject update = data.optJSONObject("update");
			NJSONObject alert = data.optJSONObject("alert");
			String newInThisVersion = data.optString("new_in_version", null);

			// update
			if(update != null){
				if(DEBUG)
					Log.d("state", "update");
				
				boolean isForced = update.optBoolean("force_update", false);
				String changelog = update.optString("change_log", null);
				String version = update.optString("version", null);
				
				if(isForced){
					if(mNVersioAsyncListener != null){
						mNVersioAsyncListener.onForcedUpdate(changelog);
					}
				}
				else if(!isForced && !NVersionSharedPrefController.didReadUpdateVersion(mActivity, version)){
					if(mNVersioAsyncListener != null){
						mNVersioAsyncListener.onUpdateAvailable(changelog, version);
					}
				}
				else{
					if(DEBUG)
						Log.d("state", "update but already read");
					
					if(mNVersioAsyncListener != null)
						mNVersioAsyncListener.onNoUpdate();
				}
			}
			//Alert, check if exists and not already read
			else if(alert != null && !NVersionSharedPrefController.didReadThisAlert(mActivity, alert.getString("modified"))){
				if(DEBUG)
					Log.d("state", "alert");
				
				if(mNVersioAsyncListener != null)
					mNVersioAsyncListener.onAlert(alert.getString("header"), alert.getString("message"), alert.getString("modified"));
			}
			//new in this version and not read already
			else if(newInThisVersion != null && !NVersionSharedPrefController.didReadNewInThisVersion(mActivity, currentUserVersion)){
				if(DEBUG)
					Log.d("state", "new in version");
				
				if(mNVersioAsyncListener != null)
					mNVersioAsyncListener.onNewInThisVersion(newInThisVersion);
			}
			else{
				//No update
				if(DEBUG)
					Log.d("state", "no update");
				
				if(mNVersioAsyncListener != null)
					mNVersioAsyncListener.onNoUpdate();
			}
		}
		catch(Exception e){
			NLog.e(TAG + " onPostExecute",e);
			e.printStackTrace();
		}
	}

	@Override
	protected NJSONObject doInBackground(String... params) {
		NJSONObject object = getNewestVersion(packageName);
		return object;
	}

	public NJSONObject getNewestVersion(String packageName){
		NResponse response;

		try{
			NWebserviceController mNWebserviceController = new NWebserviceController();
			NJSONObject query = new NJSONObject();
			query.put("type", "android");
			query.put("bundle", packageName);
			query.put("version", currentUserVersion);

			if(lastVisitedVersion != null)
				query.put("old_version", lastVisitedVersion);
			
			ArrayList<BasicHeader> myHeaderArrayList = new ArrayList<BasicHeader>();
			if(locale == null)
				myHeaderArrayList.add(new BasicHeader("Accept-Language", Locale.getDefault().toString()));
			else
				myHeaderArrayList.add(new BasicHeader("Accept-Language", locale));
			mNWebserviceController.setMyHeaderArrayList(myHeaderArrayList);
			
			String url = "https://mobilev2.like.st/api/v2/version/" + NQueryBuilder.jsonToQuery(query);

			if(DEBUG)
				Log.d("querry", NQueryBuilder.jsonToQuery(query));
			
			response = mNWebserviceController.curlHttpGet(url, NWebserviceConstants.HTTP_SAFE_THREAD);
		}
		catch(Exception e){
			NLog.e(TAG + " getNewestVersion connection", e);
			return null;
		}
		try{
			if(NWebserviceConstants.isApiSuccess(response))
				return response.getResponseJson();
			else if(response.getResponseCode() == NWebserviceConstants.API_NOT_FOUND){ 
				NLog.e("TAG", "404, is the App added to Nodescamp and is the bundle added and version added? " + packageName);
				return null;
			}
			else{
				NLog.e(TAG +" parse", "Response code: " + response.getResponseCode());
				return null;
			}
		}
		catch(Exception e){
			NLog.e(TAG + " getNewestVersion parse", e);
			return null;
		}
	}

	public interface NVersioAsyncListener {
		public void onForcedUpdate(String changelog);
		public void onUpdateAvailable(String changelog, String version);
		public void onNewInThisVersion(String message);
		public void onAlert(String header, String message, String modified);
		public void onNoUpdate();
	}
}