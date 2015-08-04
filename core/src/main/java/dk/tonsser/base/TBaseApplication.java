package dk.tonsser.base;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import dk.tonsser.controllers.ScreenParameters;
import dk.tonsser.controllers.heap.NHeapController;
import dk.tonsser.filehandler.NFileHandler;
import dk.tonsser.utils.TBuild;
import dk.tonsser.utils.TLog;
import dk.tonsser.utils.math.TUnits;

public abstract class TBaseApplication extends Application {

    private static String TAG = TBaseApplication.class.getName();

    //MODES
    public boolean DEBUG = false;
    public boolean BLACK_BERRY = false;
    public boolean IS_STATUSBAR_ON = true;
    public boolean IS_TRANSULENT_STATUS_BAR = false;
    public boolean IS_TRANSULENT_NAVIGATION_BAR = false;

    //KEYS
    public String HOCKEY_API;
    public String GOOGLE_ANALYTICS_API;

    //SETTINGS
    public long CACHE_SMALL = 5 * TUnits.MIN_IN_MS;
    public long CACHE_BIG = TUnits.DAY_IN_MS;
    public int SPLASH_SCREEN_DELAY = 2500;
    public int DEFINITION_OF_SMALL_HEAP = 31; // below this amount is lowheap devices
    public String WEBSERVICE_URL_LIVE;
    public String WEBSERVICE_URL_DEBUG;
    public String WEBSERVICE_URL;
    public String WEBSERVICE_TRANSLATION_URL;
    public static String FILE_NAME;
    public boolean AUTO_FEED_BACK_ON_DEBUG = true;
    public boolean AUTO_FEED_BACK_ON_LIVE = false;

    //STATES
    public boolean IS_APPLICATION_LOADED = false;

    private static TBaseApplication instance;

    public static TBaseApplication getInstance() {
        return instance;
    }

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
        NHeapController.logHeap();
        ScreenParameters.setScreenParameters(getBaseContext(), IS_STATUSBAR_ON, IS_TRANSULENT_STATUS_BAR, IS_TRANSULENT_NAVIGATION_BAR);

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
}
