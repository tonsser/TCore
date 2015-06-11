package dk.nodes.controllers.versionsandcrashes;
/**
 * @author Casper Rasmussen 2012 - modified 6/12 - 2013
 */
import android.app.Activity;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.CrashManagerListener;
import net.hockeyapp.android.UpdateManager;
import net.hockeyapp.android.UpdateManagerListener;

import dk.nodes.base.NBaseApplication;
import dk.nodes.controllers.versionsandcrashes.util.NVersionASync;
import dk.nodes.controllers.versionsandcrashes.util.NVersionASync.NVersioAsyncListener;
import dk.nodes.controllers.versionsandcrashes.util.NVersionAlertOptions;
import dk.nodes.controllers.versionsandcrashes.util.NVersionOptions;
import dk.nodes.controllers.versionsandcrashes.util.NVersionSharedPrefController;
import dk.nodes.controllers.versionsandcrashes.util.NVersionType;
import dk.nodes.utils.NBuild;

public class NVersionAndCrash {
	private final static String TAG = NVersionAndCrash.class.getName();

	//Instances
	private static NVersionAndCrash instance;
	private HockeyListener mHockeyListener;
	private NVersionListener mNVersionListener;
	private NVersionOptions mNVersionOptions = new NVersionOptions();

	private NVersionDialog mNVersionDialog;

	public static NVersionAndCrash getInstance(){
		if(instance == null)
			instance = new NVersionAndCrash();

		return instance;
	}

	public void setOptions(NVersionOptions mNVersionOptions){
		this.mNVersionOptions = mNVersionOptions;
	}

	public NVersionOptions getOptions(){
		return mNVersionOptions;
	}

	/**
	 * Uses the phones default language. Same as calling checkForEverything(mActivity, null, HOCKEY_API_KEY); 
	 * @param mActivity
	 * @param HOCKEY_API_KEY
	 */
	public void checkForEverything(Activity mActivity, String HOCKEY_API_KEY){
		checkForEverything(mActivity, null, HOCKEY_API_KEY);
	}
	
	/**
	 * @param mActivity
	 * @param locale Locale for the version-dialogs so we can return in a localized translation, e.g. "en_gb" for English or "da_dk" for Danish..
	 * @param HOCKEY_API_KEY
	 */
	public void checkForEverything(Activity mActivity, String locale, String HOCKEY_API_KEY){
		if(NBaseApplication.getInstance().DEBUG){
			checkForCrashesWithPopup(mActivity,HOCKEY_API_KEY);
			checkForUpdatesOnHockey(mActivity, HOCKEY_API_KEY);
		}
		else{
			checkForCrashesWithoutPopup(mActivity,HOCKEY_API_KEY);
			checkForUpdatesOnMarket(mActivity, locale);
		}
	}

	public void checkForCrashesWithoutPopup(final Activity mActivity,String HOCKEY_API){
		CrashManager.register(mActivity, HOCKEY_API, new CrashManagerListener() {
			public boolean onCrashesFound() {
				return true;
			}
		});
	}

	public void checkForCrashesWithPopup(Activity mActivity, String HOCKEY_API){
		CrashManager.register(mActivity, HOCKEY_API);
	}

	public void checkForUpdatesOnHockey(Activity mActivity,String HOCKEY_API) {
		UpdateManager.register(mActivity, HOCKEY_API,new UpdateManagerListener(){

			@Override
			public void onNoUpdateAvailable() {
				super.onNoUpdateAvailable();
				if(mHockeyListener!=null)
					mHockeyListener.onHockeyUpdateAvaiable();
			}

			@Override
			public void onUpdateAvailable() {
				super.onUpdateAvailable();
				if(mHockeyListener!=null)
					mHockeyListener.onHockeyUpdateNotAvaiable();
			}
		}, true);
	}


	public  void checkForUpdatesOnMarket(final Activity mActivity, String locale){
		new NVersionASync(mActivity, locale, new NVersioAsyncListener() {

			@Override
			public void onUpdateAvailable(String changelog, String version) {
				if(mNVersionListener != null)
					mNVersionListener.onNodesUpdate(false, changelog);
				else
					buildDialog(mActivity, mNVersionOptions, NVersionType.UPDATE, changelog);
				
				//mark 'version' as read
				NVersionSharedPrefController.setReadUpdateVersion(mActivity, version);
			}

			@Override
			public void onForcedUpdate(String changelog) {
				if(mNVersionListener != null)
					mNVersionListener.onNodesUpdate(true, changelog);
				else
					buildDialog(mActivity, mNVersionOptions, NVersionType.FORCE_UPDATE, changelog);
			}

			@Override
			public void onNoUpdate() {
				if(mNVersionListener != null)
					mNVersionListener.onNodesNoUpdate();
			}

			@Override
			public void onNewInThisVersion(String message) {
				if(mNVersionListener != null)
					mNVersionListener.oNodesNewThisVersion(message);
				else
					buildDialog(mActivity, mNVersionOptions, NVersionType.NEW_IN_VERSION, message);
				
				//mark 'new in version' as read
				NVersionSharedPrefController.setReadNewInThisVersion(mActivity, NBuild.getVersionName(mActivity));
			}

			@Override
			public void onAlert(String header, String message, String modified) {
				if(mNVersionListener != null)
					mNVersionListener.oNodesNewThisVersion(message);
				else{
					mNVersionOptions.setNVersionAlertOptions(new NVersionAlertOptions(header, message, modified));
					buildDialog(mActivity, mNVersionOptions, NVersionType.ALERT, message);
				}
				
				NVersionSharedPrefController.setReadAlert(mActivity, modified);
			}
		}).execute();
	}

	public void setNVersionListener(NVersionListener mNVersionListener){
		this.mNVersionListener = mNVersionListener;
	}

	public void removeNVersionListener(){
		this.mNVersionListener = null;
	}
	
	private void buildDialog(Activity mActivity, NVersionOptions mNVersionOptions, NVersionType type, String message){

		//Don't build dialog if already showing
		if(mNVersionDialog != null && mNVersionDialog.isShowing())
			return;

		mNVersionDialog = new NVersionDialog(mActivity, mNVersionOptions, type, message);
		mNVersionDialog.show();
	}

	public interface HockeyListener{
		public void onHockeyUpdateAvaiable();
		public void onHockeyUpdateNotAvaiable();
	}

	public interface NVersionListener{
		public void onNodesNoUpdate();
		public void onNodesUpdate(boolean isForced, String changelog);
		public void oNodesNewThisVersion(String message);
		public void onNodesAlert(String header, String message, String modified);
	}
}
