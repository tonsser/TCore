package com.tonsser.models;

import android.content.Context;

import com.tonsser.controllers.caching.CacheManager.CacheCallback;
import com.tonsser.controllers.caching.CacheManager.CacheState;
import com.tonsser.filehandler.NFileHandler;
import com.tonsser.utils.TLog;

import java.util.HashMap;

public class TData {

    private HashMap<String, CacheState> mCacheStates = new HashMap<String, CacheState>();
    private Context mContext;
    public static long DEFAULT_CACHE_TIME = 1000 * 60 * 5;

    public void setContext(Context context) {
        mContext = context.getApplicationContext();
    }

    public <T> void saveData(String key, T t) {
        if (mContext == null) {
            TLog.e("CacheManager", "saveData: Context was null");
            return;
        }

        NFileHandler fileHandler = new NFileHandler(mContext, key);

        fileHandler.saveData(t);

        CacheState cacheState = mCacheStates.get(key);

        if (cacheState == null) {
            cacheState = new CacheState();
        }

        cacheState.time = System.currentTimeMillis();
        mCacheStates.put(key, cacheState);

        saveState();
    }

    public <T> void loadData(String key, CacheCallback<T> callback) {
        if (mContext == null) {
            TLog.e("CacheManager", "loadData: Context was null");
            return;
        }

        NFileHandler fileHandler = new NFileHandler(mContext, key);
        T t = null;

        // We should refactor filehandler so it handles generics instead
        try {
            t = (T) fileHandler.loadData();
        } catch (Exception e) {
            TLog.e("CacheManager", "Failed casting to generic type: " + t.getClass().toString());
            callback.onNotFound();
        }

        CacheState cacheState = mCacheStates.get(key);

        if (t == null) {
            TLog.i("CacheManager", "Cache is not found for key: " + key);
            callback.onNotFound();
        } else if (cacheState != null && cacheState.isExpired(DEFAULT_CACHE_TIME)) {
            TLog.i("CacheManager", "Cache is expired for key: " + key);
            callback.onExpired(t);
        } else {
            TLog.i("CacheManager", "Cache is valid for key: " + key);
            callback.onValid(t);
        }
    }

    public <T> void loadData(String key, long cacheTime, CacheCallback<T> callback) {
        if (mContext == null) {
            TLog.e("CacheManager", "loadData: Context was null");
            return;
        }

        NFileHandler fileHandler = new NFileHandler(mContext, key);
        T t = null;

        // We should refactor filehandler so it handles generics instead
        try {
            t = (T) fileHandler.loadData();
        } catch (Exception e) {
            TLog.e("CacheManager", "Failed casting to generic type: " + t.getClass().toString());
            callback.onNotFound();
        }

        CacheState cacheState = mCacheStates.get(key);

        if (t == null) {
            TLog.i("CacheManager", "Cache is not found for key: " + key);
            callback.onNotFound();
        } else if (cacheState != null && cacheState.isExpired(cacheTime)) {
            TLog.i("CacheManager", "Cache is expired for key: " + key);
            callback.onExpired(t);
        } else {
            TLog.i("CacheManager", "Cache is valid for key: " + key);
            callback.onValid(t);
        }
    }

    private void loadState() {
        if (mCacheStates == null) {
            NFileHandler stateFileHandler = new NFileHandler(mContext, "cacheStates");
            mCacheStates = (HashMap<String, CacheState>) stateFileHandler.loadData();
        }
    }

    private void saveState() {
        if (mCacheStates == null) {
            NFileHandler stateFileHandler = new NFileHandler(mContext, "cacheStates");
            stateFileHandler.saveDataAsync(mCacheStates);
        }
    }
}
