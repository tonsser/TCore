package com.tonsser.controllers;
/**
 * @author Casper Rasmussen 2012
 */

import com.tonsser.utils.TLog;

import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class SpamController {
    private static SpamController instance;
    private Timer timer;
    private boolean globalSpamBoolean = true;
    public static boolean loggingEnabled = false;
    private HashMap<String, Boolean> data = new HashMap<String, Boolean>();
    public static final int SPAM_CONTROLL_TIMER_DOUBLECLICK = 50;
    public static final int SPAM_CONTROLL_TIMER_SHORT = 250;
    public static final int SPAM_CONTROLL_TIMER_LONG = 2500;


    @Deprecated
    /**
     * Use getInstance();
     */
    public SpamController() {
    }

    public static SpamController getInstance() {
        if (instance == null)
            instance = new SpamController();

        return instance;
    }

    /**
     * Use this method to start a global spam lock-down, use isReady() to check when it's over.
     * The lock-down is time pased on int input
     *
     * @param spamDelay
     */
    public void spamControll(long spamDelayMs) {

        if (timer != null)
            timer.cancel();

        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            public void run() {
                globalSpamBoolean = true;
            }
        };
        timer.schedule(timerTask, new Date(System.currentTimeMillis() + spamDelayMs));
        globalSpamBoolean = false;
    }

    /**
     * Use this method to start a key based  spam lock-down, use isReady(key) to check when it's over.
     * The lock-down is time pased on int input
     *
     * @param spamDelayMs
     * @param key
     */
    public void spamControll(long spamDelayMs, final String key) {

        if (timer != null)
            timer.cancel();

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            public void run() {
                if (data.containsKey(key)) {
                    if (loggingEnabled) {
                        TLog.i("spamControl run remove " + data.containsKey(key) + " " + key);
                    }
                    data.remove(key);
                } else {
                    if (loggingEnabled) {
                        TLog.w("SpamController spamControl time has passed", "Key: " + key + " was deleted, which it should not be, adding it");
                    }
                }
            }
        };
        timer.schedule(timerTask, new Date(System.currentTimeMillis() + spamDelayMs));

        if (data.containsKey(key)) {
            data.remove(key);
            if (loggingEnabled) {
                TLog.w("SpamController spamControl", "Key: " + key + " was already existing, deleting it and adding new");
            }
        }
        if (loggingEnabled) {
            TLog.i("spamControl run put " + key);
        }
        data.put(key, false);
    }

    /**
     * Use this method to check if you global spam-lock-down is over
     *
     * @return
     */
    public boolean isReady() {
        return globalSpamBoolean;
    }

    /**
     * Use this method to check your if your key-based lock-down is over, will return true if key does not exist
     *
     * @param key
     * @return Boolean
     */
    public boolean isReady(String key) {
        if (data.containsKey(key))
            return data.get(key);
        else {
            TLog.d(" SpamController isReady(key)", "Key: " + key + " was not found returning true");
            return true;
        }
    }

    /**
     * Use this method to check your if your key-based lock-down is over, will return false if key does not exist
     *
     * @param key
     * @return Boolean
     */
    public boolean isReadyDefaultFalse(String key) {
        if (data.containsKey(key))
            return data.get(key);
        else {
            return false;
        }
    }

    public void clearSpamController() {
        globalSpamBoolean = true;
    }

    public void clearSpamController(String key) {
        if (data.containsKey(key)) {
            data.remove(key);
            data.put(key, true);
        }
    }
}
