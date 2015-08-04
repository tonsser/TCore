package dk.tonsser.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;

import dk.tonsser.controllers.SpamController;

public class ShakeListener implements SensorEventListener {
    private String TAG = ShakeListener.class.getName();

    private SensorManager mSensorManager;
    private float mAccelCurrent = 9.8f; // current acceleration
    private float mAccelLast = 9.8f; // last acceleration
    private float shakeSensitivity = 1.5f;
    private int shakes = 0;
    private int spamControlDelay = 300;
    private OnNShakeListener mOnNShakeListener;
    private SpamController mSpamController;
    private boolean handlerRunning = false;
    private static ShakeListener instance;

    public static ShakeListener getInstance(Context mContext) {
        if (instance == null)
            instance = new ShakeListener(mContext);
        return instance;
    }

    public ShakeListener(Context mContext) {
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mSpamController = SpamController.getInstance();
    }

    /**
     * Will return shakeSensitivity
     *
     * @return float How many g's is needed to be applied to the phone for it to register as a shake
     * default is 1.5
     */
    public float getShakeSensitivity() {
        return shakeSensitivity;
    }

    /**
     * Will set shakeSensitivity
     *
     * @param How many g's is needed to be applied to the phone for it to register as a shake
     *            default is 1.5
     */
    public void setShakeSensitivity(float shakeSensitivity) {
        this.shakeSensitivity = shakeSensitivity;
    }

    /**
     * Will return spamControlDelay. (The time between a shake to one side and another side). Default is 300
     *
     * @return int
     */
    public int getSpamControlDelay() {
        return spamControlDelay;
    }

    /**
     * Will set spamControlDelay in ms. (The time between a shake to one side and another side). Default is 300ms
     *
     * @param spamControlDelay in ms
     */
    public void setSpamControlDelay(int spamControlDelay) {
        this.spamControlDelay = spamControlDelay;
    }

    public void onSensorChanged(SensorEvent se) {
        float x = se.values[0];
        float y = se.values[1];
        float z = se.values[2];

        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
        mAccelCurrent = mAccelLast * 0.7f + mAccelCurrent * 0.3f; // perform

        if (mAccelCurrent > (SensorManager.GRAVITY_EARTH * shakeSensitivity)) {
            if (mOnNShakeListener != null && mSpamController.isReady()) {
                shakes++;

                mSpamController.spamControll(spamControlDelay);
                if (shakes > 1) {
                    mOnNShakeListener.onShake();
                    shakes = 0;
                }

                if (!handlerRunning) {
                    handlerRunning = true;
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            shakes = 0;
                            handlerRunning = false;
                        }
                    }, 1000);
                }
            }
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * This will register the shakeListener
     *
     * @param mOnNShakeListener
     */
    public void register(OnNShakeListener mOnNShakeListener) {
        this.mOnNShakeListener = mOnNShakeListener;
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    /**
     * This will unregister the shakeListener.
     */
    public void unregister() {
        if (mSensorManager != null)
            mSensorManager.unregisterListener(this);
        mOnNShakeListener = null;
    }

    public interface OnNShakeListener {
        void onShake();
    }
}
