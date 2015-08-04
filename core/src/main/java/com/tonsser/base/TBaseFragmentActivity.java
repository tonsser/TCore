package com.tonsser.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;

import com.tonsser.utils.TBuild;
import com.tonsser.utils.TLog;

import java.util.ArrayList;

public abstract class TBaseFragmentActivity extends FragmentActivity {

    private String TAG = TBaseFragmentActivity.class.getName();
    private BroadcastReceiver finishReciever;
    public Activity mActivity;
    private ArrayList<BroadcastReceiver> finishExtraReciever = new ArrayList<BroadcastReceiver>();
    private boolean isResumed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        registerFinishBroadCastReciever();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isResumed = false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        isResumed = true;
    }

    public boolean isResumedOnly() {
        return isResumed;
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

    private void registerFinishBroadCastReciever() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TBuild.getPackageName(getBaseContext()) + ".finish_all");
        finishReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context mContext, Intent mIntent) {
                finish();
            }
        };
        try {
            LocalBroadcastManager.getInstance(this).registerReceiver(finishReciever, intentFilter);
        } catch (Exception e) {
            TLog.e(TAG + " registerFinishBroadCastReciever", e);
        }
    }

    protected void registerExtraFinishBroadCastReciever(String action) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(action);
        finishExtraReciever.add(new BroadcastReceiver() {
            @Override
            public void onReceive(Context mContext, Intent mIntent) {
                finish();
            }

        });
        try {
            LocalBroadcastManager.getInstance(this).registerReceiver(finishExtraReciever.get(finishExtraReciever.size() - 1), intentFilter);
        } catch (Exception e) {
            TLog.e(TAG + " registerExtraFinishBroadCastReciever", e);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (finishReciever != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(finishReciever);
    }


    public void broadcastFinishAll() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(TBuild.getPackageName(getBaseContext()) + ".finish_all");
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    public void broadcastAction(String action) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(action);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            onSettingsPressed();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            onSearchPressed();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
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
    public void onHomePressed() {
    }

    /**
     * Called when the physical key settings is pressed, this is button is only available on some android models.
     */
    public void onSettingsPressed() {
    }

    /**
     * Called when the physical key back is pressed
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        try {
            super.onSaveInstanceState(outState);
        } catch (Exception e) {
            TLog.e(TAG + " onSaveInstanceState", e);
        }
    }
}