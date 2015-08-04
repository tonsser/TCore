package dk.tonsser.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import dk.tonsser.utils.NLog;

public abstract class NBaseFragment extends Fragment {

	private static final String TAG = NBaseFragment.class.getName();

	private ArrayList<BroadcastReceiver> extraReciever = new ArrayList<BroadcastReceiver>();
	protected boolean isResumed;
	protected View parentView;
	protected LayoutInflater inflater;

	public abstract void initResources(View v);
	public abstract void setFragment();
	public abstract int getLayoutResource();

	@Override
	public void onResume() {
		isResumed = true;
		super.onResume();
		setFragment();
	}

	public View getParentView(){
		return parentView;
	}
	/**
	 * This method will return the active Arguments, either through a global variable or through the super method
	 * @return Bundle
	 */
	public Bundle getCurrentArguments(){
		return getArguments();
	}

	/**
	 * This will try to set Bundle in superclass if fails, it will set a global variable instead
	 */
	@Override
	public void setArguments(Bundle mBundle) {
		try {
			super.setArguments(mBundle);
		} catch (Exception e) {
			if(getArguments() != null){
				getArguments().clear();
				getArguments().putAll(mBundle);
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		this.inflater = inflater;
		parentView = inflater.inflate(getLayoutResource(), null);
		initResources(parentView);
		return parentView;
	}

	protected void registerExtraBroadCastReciever(String action,BroadcastReceiver mBroadcastReceiver){
		try {
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(action); 
			extraReciever.add(mBroadcastReceiver);		
			LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(extraReciever.get(extraReciever.size()-1), intentFilter);
		} catch (Exception e) {
			NLog.e(TAG + " registerExtraBroadCastReciever", e);
		}	
	}

	/**
	 * Can return null
	 * @return Context
	 */
	protected Context getBaseContext(){
		if(getActivity() == null)
			return null;

		return getActivity().getBaseContext();
	}

	@Override
	public void onDetach() {
		super.onDetach();
		unregisterAllExtraRecievers();
	}

	public void unregisterAllExtraRecievers(){
		try {
			for(BroadcastReceiver item :extraReciever)
				LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(item);

			extraReciever.clear();
		} catch (Exception e) {
			NLog.e(TAG+" unregisterAllRecievers",e);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	/**
	 * Remember to call this method in fragmentActivity
	 * true to intercept // false to let go through
	 */
	public boolean onBackClicked(){
		return false;
	}

	@Override
	public void onPause() {
		super.onPause();
		isResumed = false;
	}

	/**
	 * This will return true, if the the fragment is in resumed state, and only resumed state
	 */
	public boolean isResumedOnly(){
		return isResumed;
	}

	/**
	 * Use isResumed or isResumedOnly
	 * @return
	 */
	@Deprecated
	public boolean isFragmentActive(){
		return isResumedOnly();
	}


	public boolean isActivityInitilized(){
		return (getActivity() != null) ? true : false;
	}
}