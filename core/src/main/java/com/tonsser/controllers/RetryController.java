package com.tonsser.controllers;
/**
 * @author Casper Rasmussen - 2012
 */

import android.util.Log;

import com.tonsser.utils.TLog;

import java.util.HashMap;

public class RetryController {
    private int retryLimit = 3;
    private HashMap<String, Integer> data = new HashMap<String, Integer>();

    /**
     * Use this constructor to initialize with default retryLimit to 3, use setRetryLimit(int) to change
     */
    public RetryController() {
    }

    /**
     * Use this constructor to initialize retryLimit with a given integer
     *
     * @param retryLimit
     */
    public RetryController(int retryLimit) {
        this.retryLimit = retryLimit;
    }

    /**
     * Setting retryLimit, 3 is standard
     *
     * @param retryLimit
     */
    public void setRetryLimit(int retryLimit) {
        this.retryLimit = retryLimit;
    }

    /**
     * Use this method to register a key or clear if it already exists
     *
     * @param key
     */
    public void register(String key) {
        if (!data.containsKey(key))
            data.put(key, 1);
        else {
            data.remove(key);
            data.put(key, 1);
        }
    }

    /**
     * Use this method to count key one up, and return true if limit is hit, false if not
     *
     * @param key
     * @return boolean
     */
    public boolean retryBoolean(String key) {
        if (data.containsKey(key)) {
            Integer temp = data.get(key);
            temp++;
            data.remove(key);
            data.put(key, temp);
            return temp >= retryLimit;
        } else {
            Log.d("RetryController retry", key + " was not found, adding it");
            register(key);
            return false;
        }
    }

    /**
     * Use this method to count key one up, and amount of retries
     *
     * @param key
     * @return Integer
     */
    public int retryInteger(String key) {
        if (data.containsKey(key)) {
            Integer temp = data.get(key);
            temp++;
            data.remove(key);
            data.put(key, temp);
            return temp;
        } else {
            Log.d("RetryController retry", key + " was not found, adding it");
            data.put(key, 1);
            return 1;
        }
    }

    /**
     * Use this method to get the number of retry of a given key, will return -1 if key does not exist
     *
     * @param key
     * @return
     */
    public int getCurrentRetryAmount(String key) {
        if (data.containsKey(key))
            return data.get(key);
        else {
            TLog.e("RetryController getCurrentRetryState", "Key: " + key + " was not found, returning -1");
            return -1;
        }
    }

    /**
     * Use this method to delete a key with value
     *
     * @param key
     */
    public void delete(String key) {
        if (data.containsKey(key))
            data.remove(key);
        else
            TLog.w("RetryController delete", "Could not delete following key :" + key + " since it did not exist");
    }

    public void deleteNoLog(String key) {
        if (data.containsKey(key))
            data.remove(key);
    }
}
