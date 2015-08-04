package com.tonsser.sensor;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.OrientationEventListener;
import android.view.WindowManager;

import com.tonsser.utils.TUtils;

/**
 * Landscape-portrait calculator.
 * Must be enabled to get call-backs, else the static methods: isPortrait, isLandscape, isTablet can be used.
 * - these does not require sensors so are less powerhungry.
 *
 * @code RotationDetector rotator = new RotationDetector(this, new RotationDetector.RotationListener() {
 * @public void onChangedToPortrait() {
 * Log.v(TAG, "Portrait!");
 * }
 * @public void onChangedToLandscape() {
 * Log.v(TAG, "Landscape!");
 * }
 * });
 * <p/>
 * rotator.enable();
 */
public class RotationDetector extends OrientationEventListener {

    private static final String TAG = RotationDetector.class.getSimpleName();

    private RotationListener rotationListener;

    private long enabledTimeStamp;

    private long delayBeforeFirstCallbackMs;

    private int landscapeTippingThreshold = 30;//30 is default angle to each side
    private static boolean isPortrait = true;
    private static boolean isTablet = false;


    public RotationDetector(Context context, RotationListener rotationListener) {
        super(context);

        measure(context); // do an initial measure to check for tablets
        this.rotationListener = rotationListener;
    }

    /**
     * Returns true if this is called by an activity that is in portrait (is relatively tall)
     */
    public static boolean isPortrait(Context context) {
        measure(context);
        return isPortrait;
    }

    /**
     * Returns true if this is called by an activity that is in landscape (is relatively wide)
     */
    public static boolean isLandscape(Context context) {
        return !isPortrait(context);
    }

    /**
     * Returns true if this is called by an activity that run on a tablet-like device (a relatively wide screen)
     */
    public static boolean isTablet(Context context) {
        measure(context);
        return isTablet;
    }

    private static void measure(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager win = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        win.getDefaultDisplay().getMetrics(metrics);


        int r = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            isPortrait = true;
            isTablet = r % 2 != 0;
        } else {
            isPortrait = false;
            isTablet = r % 2 == 0;
        }
    }


    @Override
    public void onOrientationChanged(int orientation) {
        if (orientation == ORIENTATION_UNKNOWN) {
            return;
        }
        if (!TUtils.isOutdated(delayBeforeFirstCallbackMs, enabledTimeStamp))
            return;
        if (isPortrait) {
            if (((90 - landscapeTippingThreshold) < orientation && orientation < (90 + landscapeTippingThreshold)) || ((270 - landscapeTippingThreshold) < orientation && orientation < (270 + landscapeTippingThreshold))) {
                isPortrait = false;
                if (isTablet) {
                    rotationListener.onChangedToPortrait();
                } else {
                    rotationListener.onChangedToLandscape();
                }
            }
        } else {
            if (((330 + landscapeTippingThreshold) < orientation || orientation < (30 - landscapeTippingThreshold)) || ((180 - landscapeTippingThreshold) < orientation && orientation < (180 + landscapeTippingThreshold))) {
                isPortrait = true;
                if (isTablet) {
                    rotationListener.onChangedToLandscape();
                } else {
                    rotationListener.onChangedToPortrait();
                }
            }
        }
    }


    /**
     * Sets the width of the tipping-threshold in landscape mode (for phones, portrait for tablets).
     *
     * @param landscapeTippingThreshold The width of the angle that will trigger an orientation change. Visualize it like this: When holding the
     *                                  device in landscape, it has [landscapeAngleWidth] of wiggle-room to each side before it changes to portrait. Default is 30 degrees, which means
     *                                  the device can be tilted 30 degrees to either side, before changing from landscape to portrait.
     */
    public void setLandscapeTippingThreshold(int landscapeTippingThreshold) {
        this.landscapeTippingThreshold = landscapeTippingThreshold;
    }

    /**
     * Gets the width of the tipping-threshold in landscape mode (for phones, portrait for tablets).
     *
     * @param landscapeTippingThreshold The width of the angle that will trigger an orientation change. Visualize it like this: When holding the
     *                                  device in landscape, it has [landscapeAngleWidth] of wiggle-room to each side before it changes to portrait. Default is 30 degrees, which means
     *                                  the device can be tilted 30 degrees to either side, before changing from landscape to portrait.
     */
    public int getLandscapeTippingThreshold() {
        return landscapeTippingThreshold;
    }

    public void setDelayBeforeFirstCallback(long delayBeforeFirstCallbackMs) {
        this.delayBeforeFirstCallbackMs = delayBeforeFirstCallbackMs;
    }

    @Override
    public void disable() {
        super.disable();
    }

    @Override
    public void enable() {
        super.enable();
        enabledTimeStamp = System.currentTimeMillis();
    }

    public interface RotationListener {
        /**
         * Called whenever the orientation changes to Portrait - excluding a 30-degree dead-zone
         */
        void onChangedToPortrait();

        /**
         * Called whenever the orientation changes to Landscape - excluding a 30-degree dead-zone
         */
        void onChangedToLandscape();
    }
}
