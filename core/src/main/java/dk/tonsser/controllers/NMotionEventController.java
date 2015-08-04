package dk.tonsser.controllers;

import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;

public class NMotionEventController {

    /**
     * This method will return a fake MotionEvent
     *
     * @param v,           view
     * @param x,           x-cord
     * @param y,           y-crd
     * @param motionEvent, fx MotionEvent.ACTION_DOWN
     * @return MotionEvent
     */
    public static MotionEvent simulateMotionEvent(View v, long x, long y, int motionEvent) {
        MotionEvent mMotionEvent = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), motionEvent, x, y, 0);
        v.dispatchTouchEvent(mMotionEvent);
        return mMotionEvent;
    }
}
