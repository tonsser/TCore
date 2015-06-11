package dk.nodes.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;

import java.util.ArrayList;

import dk.nodes.controllers.feedback.NFeedback;
import dk.nodes.utils.NBuild;

public class NBaseActivity extends Activity {

	private static String TAG = NBaseActivity.class.getSimpleName();
	protected boolean SHAKE_FEEDBACK_ENABLED = true;
	private BroadcastReceiver finishReciever;
	private ArrayList<BroadcastReceiver> finishExtraReciever = new ArrayList<BroadcastReceiver>();
	private ArrayList<BroadcastReceiver> extraReciever = new ArrayList<BroadcastReceiver>();
	protected Activity mActivity;
	private boolean isResumed = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);  	
		mActivity = this;
		registerFinishBroadCastReciever();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onPause() {
		super.onPause();

		NFeedback.getInstance().unregisterShake();
		isResumed = false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(SHAKE_FEEDBACK_ENABLED)
			NFeedback.getInstance().registerShakeIfSettings(this);

		isResumed = true;
	}

	public boolean isResumedOnly(){
		return isResumed;
	}

	private void registerFinishBroadCastReciever(){
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(NBuild.getPackageName(getBaseContext())+".finish_all"); 
		finishReciever = new BroadcastReceiver() {
			@Override
			public void onReceive(Context mContext, Intent mIntent) {
				finish();
			} ;
		};			
		try {
			LocalBroadcastManager.getInstance(this).registerReceiver(finishReciever, intentFilter);
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	protected void registerExtraFinishBroadCastReciever(String action){
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(action); 
		finishExtraReciever.add(new BroadcastReceiver() {
			@Override
			public void onReceive(Context mContext, Intent mIntent) {
				finish();
			} ;
		});			
		try {
			LocalBroadcastManager.getInstance(this).registerReceiver(finishExtraReciever.get(finishExtraReciever.size()-1), intentFilter);
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	public void onDestroy(){
		super.onDestroy(); 
		if(finishReciever!= null)
			LocalBroadcastManager.getInstance(this).unregisterReceiver(finishReciever);
		unregisterFinishExtraRecievers();

	}

	public void unregisterFinishExtraRecievers(){
		for(BroadcastReceiver mBroadcastReceiver : finishExtraReciever)
			LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);

		finishExtraReciever.clear();
	}

	public void broadcastFinishAll(){
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(NBuild.getPackageName(getBaseContext()) + ".finish_all");
		LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
	}

	public void broadcastAction(String action){
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(action); 
		LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			onSettingsPressed();
			return true;
		}
		else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
			onSearchPressed();
			return true;
		}
		else if (keyCode == KeyEvent.KEYCODE_HOME) {
			onHomePressed();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Called when the physical key search is pressed, this is button is only available on some android models.
	 */
	public void onSearchPressed() {
	}

	/**
	 * Called when the physical key home is pressed. Overriding this method and do nothing, 
	 * will NOT prevent the phone from leaving app. 
	 */
	public void onHomePressed(){
	}

	/**
	 * Called when the physical key settings is pressed, this is button is only available on some android models.
	 */
	public void onSettingsPressed(){
	}

	/**
	 * Called when the physical key back is pressed
	 */
	@Override
	public void onBackPressed(){
		super.onBackPressed();
	}
}