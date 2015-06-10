package dk.nodes.controllers.feedback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import dk.nodes.base.NBaseApplication;
import dk.nodes.controllers.feedback.NFeedbackAskDialog.OnNFeedbackAskDialogListener;
import dk.nodes.controllers.feedback.util.NFeedbackOptions;
import dk.nodes.controllers.feedback.util.NFeedbackSharedPrefKey;
import dk.nodes.controllers.feedback.util.OnNFeedbackListener;
import dk.nodes.sensor.NShakeListener;
import dk.nodes.sensor.NShakeListener.OnNShakeListener;

public class NFeedback {
	
	private final static String TAG = NFeedback.class.getName();
	private static NFeedback instance;
	private NShakeListener mNShakeListener;
	private NFeedbackOptions mNFeedbackOptions = new NFeedbackOptions();
	private OnNFeedbackListener mOnNFeedbackListener;

	public static NFeedback getInstance(){
		if(instance == null)
			instance = new NFeedback();
		
		return instance;
	}
	
	/**
	 * Add custom Strings to the feedback
	 * @param mNFeedbackOptions
	 */
	public void setOptions(NFeedbackOptions mNFeedbackOptions){
		this.mNFeedbackOptions = mNFeedbackOptions;
	}
	
	public NFeedbackOptions getOptions(){
		return mNFeedbackOptions;
	}
	
	public void registerShake(final Activity mActivity){
		final SharedPreferences sharedPref = mActivity.getPreferences(Context.MODE_PRIVATE);
		boolean neverShowAgain = sharedPref.getBoolean(NFeedbackSharedPrefKey.key, false);
		
		if(neverShowAgain)
			return;
		
		mNShakeListener = new NShakeListener(mActivity);
		mNShakeListener.setShakeSensitivity(2f);
		mNShakeListener.register(new OnNShakeListener() {

			private NFeedbackAskDialog mNFeedbackAskDialog;

			@Override
			public void onShake() {
				boolean neverShowAgain = sharedPref.getBoolean(NFeedbackSharedPrefKey.key, false);
				if(neverShowAgain){
					unregisterShake();
					return;
				}
				
				if(mOnNFeedbackListener != null){
					mOnNFeedbackListener.onShowAskDialog();
					return;
				}
				
				if(mNFeedbackAskDialog == null || !mNFeedbackAskDialog.isShowing()){
					mNFeedbackAskDialog = new NFeedbackAskDialog(mActivity, mNFeedbackOptions, new OnNFeedbackAskDialogListener() {

						@Override
						public void onYes() {
							mActivity.startActivity(new Intent(mActivity, NFeedbackActivity.class));
						}
					});
					mNFeedbackAskDialog.show();
				}
			}
		});
	}
	
	/**
	 * If setting this listener, the normal dialog will not show on shake.
	 * @param mOnNFeedbackListener
	 */
	public void setOnFeedbackListener(OnNFeedbackListener mOnNFeedbackListener){
		this.mOnNFeedbackListener = mOnNFeedbackListener;
	}
	
	/**
	 * Will unregister shakefeedback
	 */
	public void unregisterShake(){
		if(mNShakeListener != null)
			mNShakeListener.unregister();
	}

	/**
	 * Will register for shake if settings is set!
	 * @param mActivity
	 */
	public void registerShakeIfSettings(Activity mActivity) {
		if(NBaseApplication.getInstance().AUTO_FEED_BACK_ON_DEBUG && NBaseApplication.getInstance().DEBUG){
			registerShake(mActivity);
		}
		else if(NBaseApplication.getInstance().AUTO_FEED_BACK_ON_LIVE && !NBaseApplication.getInstance().DEBUG){
			registerShake(mActivity);
		}
	}
}
