package com.tonsser.base;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.tonsser.controllers.ScreenParameters;
import com.tonsser.filehandler.NFileHandler;
import com.tonsser.utils.TBuild;
import com.tonsser.utils.TLog;

public abstract class TBaseApplication extends Application {

    private static String TAG = TBaseApplication.class.getName();
    public static String PACKAGE_WITHOUT_FLAVOUR;

    //SETTINGS
    public String WEBSERVICE_URL_LIVE;
    public String WEBSERVICE_URL_DEBUG;
    public String WEBSERVICE_URL;
    public static String FILE_NAME;

    //STATES
    public boolean IS_APPLICATION_LOADED = false;

    protected static TBaseApplication instance;

//    public static synchronized TBaseApplication getInstance() {
//        return instance;
//    }

    public static void broadcastFinishAll(Context mContext) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(TBuild.getPackageName(mContext) + ".finish_all");
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(broadcastIntent);
    }

    public static void broadcastAction(Context mContext, String action) {
        if (mContext == null) {
            TLog.e(TAG + " broadcastAction", "Context is null");
            return;
        }
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(action);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(broadcastIntent);
    }

    @Override
    public void onCreate() {
        instance = this;
        initApplication();
        ScreenParameters.setScreenParameters(getBaseContext());

        if (FILE_NAME != null)
            NFileHandler.setInstance(getBaseContext(), FILE_NAME);

        loadInitData();
        onAfterLoadInitData();

        IS_APPLICATION_LOADED = true;
        super.onCreate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    protected void onAfterLoadInitData() {
    }

    protected abstract void initApplication();

    protected abstract void loadInitData();

    public static synchronized TBaseApplication getInstance() {
        return instance;
    }
}
