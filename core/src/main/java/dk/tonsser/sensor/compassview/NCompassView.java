package dk.tonsser.sensor.compassview;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import dk.tonsser.sensor.compassview.calculations.NCompassARData;
import dk.tonsser.sensor.compassview.calculations.NCompassMatrix;
import dk.tonsser.utils.TLog;

public class NCompassView extends View implements SensorEventListener, LocationListener {

    private Bitmap arrowBitmap;
    private Paint paint;
    private Bitmap canvasBitmap;
    private Location bearingLocation;
    private int arrowResource;
    private static final int MIN_TIME = 1 * 1000; // milliseconds
    private static final int MIN_DISTANCE = 1; // meters

    private static final float toRadians = (float) (Math.PI / 180);
    private static float Pi = (float) Math.PI;
    private static float TwoPi = (float) Math.PI * 2f;

    private float[] gyro = new float[3]; // angular speeds from gyro
    private float[] gyroMatrix = new float[9];// rotation matrix from gyro data
    private float[] accMagMatrix = new float[9];// rotation matrix from gyro data

    private float[] gyroOrientation = new float[3];// orientation angles from gyro matrix
    private float[] magnet = new float[3];// magnetic field vector
    private float[] accel = new float[3];// accelerometer vector
    private float[] accMagOrientation = new float[3];// orientation angles from accel and magnet
    private float[] fusedOrientation = new float[3];// final orientation angles from sensor fusion
    private float[] unfusedOrientation = new float[3];// final orientation angles from sensor fusion
    private float[] rotationMatrix = new float[9];// accelerometer and magnetometer based rotation matrix

    public static int sensorRate = SensorManager.SENSOR_DELAY_GAME; // 20ms between updates
    public static final float EPSILON = 0.000000001f;
    private static final float NS2S = 1.0f / 1000000000.0f;
    private boolean initState = true;
    private float timestamp;

    public static float aspectRatio = 1.0f;
    public static boolean portrait = true;
    public static boolean isTablet = false;

    private static final NCompassMatrix magneticCompensatedCoord = new NCompassMatrix();
    private static final NCompassMatrix xAxisRotation = new NCompassMatrix();
    private static final NCompassMatrix mageticNorthCompensation = new NCompassMatrix();
    private static final String TAG = NCompassView.class.getName();

    private static GeomagneticField gmf = null;
    private static SensorManager sensorMgr = null;
    private static List<Sensor> sensors = null;
    private static Sensor sensorGrav = null;
    private static Sensor sensorMag = null;
    private static Sensor sensorGyro = null;
    private static boolean sensorGravRunning, sensorMagRunning, sensorGyroRunning;
    private static LocationManager locationMgr = null;

    public static int accFilterOrder = 25; //25;
    private static float[] xHistAcc = new float[accFilterOrder];
    private static float[] yHistAcc = new float[accFilterOrder];
    private static float[] zHistAcc = new float[accFilterOrder];
    private static int indexHistAcc = 0;
    public static int magFilterOrder = 5; //5;
    private static float[] xHistMag = new float[magFilterOrder];
    private static float[] yHistMag = new float[magFilterOrder];
    private static float[] zHistMag = new float[magFilterOrder];
    private static int indexHistMag = 0;
    public static int gyroFilterOrder = 7; //9;
    private static float[] xHistGyro = new float[gyroFilterOrder];
    private static float[] yHistGyro = new float[gyroFilterOrder];
    private static float[] zHistGyro = new float[gyroFilterOrder];
    private static int indexHistGyro = 0;

    public static int UPDATE_RATE = 20; // Rate of calculations (frames per second)
    public static int TIME_CONSTANT = 1000 / UPDATE_RATE; //50; // milliseconds between updates
    public static float COMBINATION_FILTER_COEFFICIENT = 0.800f; // <-- Important: lower=faster=noisier
    public static float ONEMINUS_COMBINATION_COEFF = 1.0f - COMBINATION_FILTER_COEFFICIENT;

    private Timer fuseTimer;
    private Context mContext;
    private Timer timer;

    private Handler timerHandler = new Handler() {

        @Override
        public void handleMessage(Message arg0) {
            super.handleMessage(arg0);
            postInvalidate();

            if (mNCompassUpdateListener != null)
                mNCompassUpdateListener.onUpdate(currentBearing);
        }
    };
    private NCompassUpdateListener mNCompassUpdateListener;
    private double currentBearing;

    public NCompassView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    public NCompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public NCompassView(Context context) {
        super(context);
        mContext = context;
    }

    /**
     * Set the Location to point against
     *
     * @param location
     */
    public void setLocationToPointAgainst(Location location) {
        bearingLocation = location;
    }

    /**
     * Set the resource of the arrow, fx R.drawable.arrow
     *
     * @param mContext
     * @param arrowResource
     */
    public void setArrowResource(Context mContext, int arrowResource) {
        this.arrowResource = arrowResource;
        init(mContext);
    }

    private void init(Context mContext) {
        if (arrowResource != 0) {
            arrowBitmap = BitmapFactory.decodeResource(mContext.getResources(), arrowResource);
            canvasBitmap = arrowBitmap.copy(Bitmap.Config.ARGB_4444, true);
            canvasBitmap.eraseColor(0x00000000);
            paint = new Paint();
        }
    }

    private void setTimer(boolean b) {
        if (timer != null)
            timer.cancel();
        if (b) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {


                @Override
                public void run() {
                    timerHandler.sendEmptyMessage(0);
                }
            }, 1000, 100);
        }
    }

    private void getScreenInfo() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        aspectRatio = (float) metrics.widthPixels / metrics.heightPixels;

        int r = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            portrait = true;
            isTablet = r % 2 != 0;
        } else {
            portrait = false;
            isTablet = r % 2 == 0;
        }

        TLog.i(TAG, "Portrait: " + portrait + ", rot: " + r + ", isTablet: " + isTablet);
    }

    private void initAR() {
        getScreenInfo();

        gyroOrientation[0] = 0.0f;
        gyroOrientation[1] = 0.0f;
        gyroOrientation[2] = 0.0f;

        unfusedOrientation[0] = 0.0f;
        unfusedOrientation[1] = 0.0f;
        unfusedOrientation[2] = 0.0f;
        // initialise gyroMatrix with identity matrix
        gyroMatrix[0] = 1.0f;
        gyroMatrix[1] = 0.0f;
        gyroMatrix[2] = 0.0f;
        gyroMatrix[3] = 0.0f;
        gyroMatrix[4] = 1.0f;
        gyroMatrix[5] = 0.0f;
        gyroMatrix[6] = 0.0f;
        gyroMatrix[7] = 0.0f;
        gyroMatrix[8] = 1.0f;
    }

    /**
     * Call this onStart
     */
    public void onStart() {
        //Counter-clockwise rotation at -90 degrees around the x-axis
        float angleX = -90 * toRadians;
        xAxisRotation.set(1f, 0f, 0f,
                0f, FloatMath.cos(angleX), -FloatMath.sin(angleX),
                0f, FloatMath.sin(angleX), FloatMath.cos(angleX));
    }

    /**
     * Call this onResume
     */
    public void onResume() {
        initListeners();
        setTimer(true);
    }

    /**
     * Call this on pause
     */
    public void onPause() {
        deInitListners();
        setTimer(false);
    }

    /**
     * This will set NCompassUpdateListener, which will provide callback each time compass updates, which is every 100 ms
     *
     * @param mNCompassUpdateListener
     */
    public void setUpdateListener(NCompassUpdateListener mNCompassUpdateListener) {
        this.mNCompassUpdateListener = mNCompassUpdateListener;
    }

    /**
     * This function registers sensor listeners for the accelerometer, magnetometer and gyroscope.
     */
    private void initListeners() {
        sensorMgr = (SensorManager) mContext.getSystemService(Activity.SENSOR_SERVICE);
        fuseTimer = new Timer();

        sensors = sensorMgr.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensors.size() > 0) {
            sensorGravRunning = false;
            sensorGrav = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorMgr.registerListener(this, sensorGrav, sensorRate);
        }
        sensors = sensorMgr.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
        if (sensors.size() > 0) {
            sensorMagRunning = false;
            sensorMag = sensorMgr.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            sensorMgr.registerListener(this, sensorMag, sensorRate);
        }
        sensors = sensorMgr.getSensorList(Sensor.TYPE_GYROSCOPE);
        if (sensors.size() > 0) {
            sensorGyroRunning = false;
            sensorGyro = sensorMgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            sensorMgr.registerListener(this, sensorGyro, sensorRate);
            fuseTimer.scheduleAtFixedRate(new calculateFusedOrientationTask(), 1000, TIME_CONSTANT); // use the gyro
            TLog.d(TAG, "initListeners() - " + "Gyro found");
        } else {
            fuseTimer.scheduleAtFixedRate(new calculateUnfusedOrientationTask(), 1000, TIME_CONSTANT); // don't use the gyro
            TLog.d(TAG, "initListeners() - " + "No gyro found");
        }

        locationMgr = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        locationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);


        try {
            Location gps = locationMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location network = locationMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (gps != null)
                onLocationChanged(gps);
            else if (network != null)
                onLocationChanged(network);
            else
                onLocationChanged(NCompassARData.hardFix);
        } catch (Exception ex2) {
            onLocationChanged(NCompassARData.hardFix);
        }

        float angleY = -90 * toRadians;

        gmf = new GeomagneticField((float) NCompassARData.getCurrentLocation().getLatitude(),
                (float) NCompassARData.getCurrentLocation().getLongitude(),
                (float) NCompassARData.getCurrentLocation().getAltitude(),
                System.currentTimeMillis());
        angleY = -gmf.getDeclination() * toRadians;

        synchronized (mageticNorthCompensation) {
            mageticNorthCompensation.toIdentity();

            //Counter-clockwise rotation at negative declination around the y-axis
            //note2: declination is the difference between true north and magnetic north
            mageticNorthCompensation.set(FloatMath.cos(angleY), 0f, FloatMath.sin(angleY),
                    0f, 1f, 0f,
                    -FloatMath.sin(angleY), 0f, FloatMath.cos(angleY));

            //Rotate the matrix to match the orientation
            mageticNorthCompensation.prod(xAxisRotation);
        }
    }

    private void deInitListners() {
        TLog.d(TAG, "deInitListeners()");
        fuseTimer.cancel();

        try {
            if (sensorMgr != null) {
                sensorMgr.unregisterListener(this);
                sensorMgr = null;
            }
            if (locationMgr != null) {
                locationMgr.removeUpdates(this);
                locationMgr = null;
            }
        } catch (Exception ex2) {
            ex2.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent evt) {

        if (evt.sensor.getType() == Sensor.TYPE_ACCELEROMETER) { // Accelerometer uses a combined mean/averaging filter
            float[] filteredAcc = filterAcc(evt.values);
            accel[0] = filteredAcc[0];
            accel[1] = filteredAcc[1];
            accel[2] = filteredAcc[2];
            calculateAccMagOrientation();
        }
        if (evt.sensor.getType() == Sensor.TYPE_GYROSCOPE) { // Gyroscope uses a mean filter
            gyroFunction(evt);
        }
        if (evt.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) { // Magnetic sensor uses a mean filter combined with ramping
            float[] filteredMag = filterMag(evt.values);
            // ramp the filtered values
            float ramping = 0.07f;
            magnet[0] = ramping * filteredMag[0] + (1 - ramping) * magnet[0];
            magnet[1] = ramping * filteredMag[1] + (1 - ramping) * magnet[1];
            magnet[2] = ramping * filteredMag[2] + (1 - ramping) * magnet[2];

            if (!sensorMagRunning) { // dont ramp untill the sensor is up and running - avoids the startup delay
                magnet[0] = filteredMag[0];
                magnet[1] = filteredMag[1];
                magnet[2] = filteredMag[2];
            }
        }
    }

    private float[] filterAcc(float[] in) {
        int filterOrder = xHistAcc.length;
        if (++indexHistAcc >= filterOrder) {
            indexHistAcc = 0;
            if (!sensorGravRunning) {
                sensorGravRunning = true;
            }
        }
        xHistAcc[indexHistAcc] = in[0];
        yHistAcc[indexHistAcc] = in[1];
        zHistAcc[indexHistAcc] = in[2];

        if (!sensorGravRunning) {
            return in;
        }
        float[] out = {arrayMedianAverage(xHistAcc), arrayMedianAverage(yHistAcc), arrayMedianAverage(zHistAcc)};
        return out;
    }

    private float[] filterMag(float[] in) {
        int filterOrder = xHistMag.length;
        if (++indexHistMag >= filterOrder) {
            indexHistMag = 0;
            if (!sensorMagRunning) {
                sensorMagRunning = true;
            }
        }
        xHistMag[indexHistMag] = in[0];
        yHistMag[indexHistMag] = in[1];
        zHistMag[indexHistMag] = in[2];

        if (!sensorMagRunning) {
            return in;
        }
        float[] out = {arrayMedian(xHistMag), arrayMedian(yHistMag), arrayMedian(zHistMag)};
        return out;
    }

    private float[] filterGyro(float[] in) {
        int filterOrder = xHistGyro.length;
        if (++indexHistGyro >= filterOrder) {
            indexHistGyro = 0;
            if (!sensorGyroRunning) {
                sensorGyroRunning = true;
            }
        }
        xHistGyro[indexHistGyro] = in[0];
        yHistGyro[indexHistGyro] = in[1];
        zHistGyro[indexHistGyro] = in[2];

        if (!sensorGyroRunning) {
            return in;
        }
        float[] out = {arrayMedian(xHistGyro), arrayMedian(yHistGyro), arrayMedian(zHistGyro)};
        return out;
    }

    /**
     * return the median value of the array
     */
    private float arrayMedian(float[] in) {
        float[] sort = new float[in.length];
        System.arraycopy(in, 0, sort, 0, sort.length);
        Arrays.sort(sort);
        return sort[sort.length / 2];
    }

    /**
     * calculate the average of the array, but using only the values within the standard deviation
     * ie.: throw away the biggest and the smallest values before calculating
     */
    private float arrayMedianAverage(float[] in) {
        float[] sort = new float[in.length];
        System.arraycopy(in, 0, sort, 0, sort.length);
        Arrays.sort(sort);

        int medianPos = sort.length / 2;
        int quartileLength = (medianPos - 1);
        int quartilePos = medianPos - (quartileLength / 2);

        float sum = 0;
        for (int i = quartilePos; i < quartilePos + quartileLength; i++) {
            sum += sort[i];
        }
        return (sum / quartileLength);
    }

    /**
     * calculates orientation angles from accelerometer and magnetometer output
     */
    private void calculateAccMagOrientation() {
        if (SensorManager.getRotationMatrix(rotationMatrix, null, accel, magnet)) {
            float[] tempMatrix = new float[9];
            if (portrait) {
                // dont remap in portrait
                if (isTablet) {
                    SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, tempMatrix);
                } else {
                    SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Y, tempMatrix);
                }
            } else {
                // remap in landscape
                if (isTablet) {
                    SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Y, tempMatrix);
                } else {
                    SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, tempMatrix);
                }
            }
            SensorManager.getOrientation(tempMatrix, accMagOrientation);
            rotationMatrix = tempMatrix;
        }
    }

    /**
     * This function performs the integration of the gyroscope data.
     * It writes the gyroscope based orientation into gyroOrientation.
     */
    private void gyroFunction(SensorEvent event) {
        // don't start until first accelerometer/magnetometer orientation has been acquired
        if (accMagOrientation == null)
            return;

        // initialisation of the gyroscope based rotation matrix
        if (initState) {
            float[] initMatrix = new float[9];
            initMatrix = getRotationMatrixFromOrientation(accMagOrientation);

            float[] test = new float[3];
            SensorManager.getOrientation(initMatrix, test);
            gyroMatrix = matrixMultiplication(gyroMatrix, initMatrix);
            initState = false;
        }

        // copy the new gyro values into the gyro array
        // convert the raw gyro data into a rotation vector
        float[] deltaVector = new float[4];
        if (timestamp != 0) {
            final float dT = (event.timestamp - timestamp) * NS2S;
            System.arraycopy(filterGyro(event.values), 0, gyro, 0, 3);

            getRotationVectorFromGyro(gyro, deltaVector, dT / 2.0f);
        }

        // measurement done, save current time for next interval
        timestamp = event.timestamp;

        // convert rotation vector into rotation matrix
        float[] deltaMatrix = new float[9];
        getRotationMatrixFromVector(deltaMatrix, deltaVector); // all apis

        // apply the new rotation interval on the gyroscope based rotation matrix
        gyroMatrix = matrixMultiplication(gyroMatrix, deltaMatrix);

        // get the gyroscope based orientation from the rotation matrix
        SensorManager.getOrientation(gyroMatrix, gyroOrientation);
    }

    // copied from SensorManager source-file which is only available since API-9
    private void getRotationMatrixFromVector(float[] R, float[] rotationVector) {
        float q0 = (float) Math.sqrt(1 - rotationVector[0] * rotationVector[0] - rotationVector[1] * rotationVector[1] - rotationVector[2] * rotationVector[2]);
        float q1 = rotationVector[0];
        float q2 = rotationVector[1];
        float q3 = rotationVector[2];

        float sq_q1 = 2 * q1 * q1;
        float sq_q2 = 2 * q2 * q2;
        float sq_q3 = 2 * q3 * q3;
        float q1_q2 = 2 * q1 * q2;
        float q3_q0 = 2 * q3 * q0;
        float q1_q3 = 2 * q1 * q3;
        float q2_q0 = 2 * q2 * q0;
        float q2_q3 = 2 * q2 * q3;
        float q1_q0 = 2 * q1 * q0;

        if (R.length == 9) {
            R[0] = 1 - sq_q2 - sq_q3;
            R[1] = q1_q2 - q3_q0;
            R[2] = q1_q3 + q2_q0;

            R[3] = q1_q2 + q3_q0;
            R[4] = 1 - sq_q1 - sq_q3;
            R[5] = q2_q3 - q1_q0;

            R[6] = q1_q3 - q2_q0;
            R[7] = q2_q3 + q1_q0;
            R[8] = 1 - sq_q1 - sq_q2;
        }
    }

    private float[] getRotationMatrixFromOrientation(float[] o) {
        float[] xM = new float[9];
        float[] yM = new float[9];
        float[] zM = new float[9];

        float sinX = FloatMath.sin(o[1]);
        float cosX = FloatMath.cos(o[1]);
        float sinY = FloatMath.sin(o[2]);
        float cosY = FloatMath.cos(o[2]);
        float sinZ = FloatMath.sin(o[0]);
        float cosZ = FloatMath.cos(o[0]);

        // rotation about x-axis (pitch)
        xM[0] = 1.0f;
        xM[1] = 0.0f;
        xM[2] = 0.0f;
        xM[3] = 0.0f;
        xM[4] = cosX;
        xM[5] = sinX;
        xM[6] = 0.0f;
        xM[7] = -sinX;
        xM[8] = cosX;

        // rotation about y-axis (roll)
        yM[0] = cosY;
        yM[1] = 0.0f;
        yM[2] = sinY;
        yM[3] = 0.0f;
        yM[4] = 1.0f;
        yM[5] = 0.0f;
        yM[6] = -sinY;
        yM[7] = 0.0f;
        yM[8] = cosY;

        // rotation about z-axis (azimuth)
        zM[0] = cosZ;
        zM[1] = sinZ;
        zM[2] = 0.0f;
        zM[3] = -sinZ;
        zM[4] = cosZ;
        zM[5] = 0.0f;
        zM[6] = 0.0f;
        zM[7] = 0.0f;
        zM[8] = 1.0f;

        // rotation order is y, x, z (roll, pitch, azimuth)
        float[] resultMatrix = matrixMultiplication(xM, yM);
        resultMatrix = matrixMultiplication(zM, resultMatrix);
        return resultMatrix;
    }

    /**
     * This function is borrowed from the Android reference
     * at http://developer.android.com/reference/android/hardware/SensorEvent.html#values
     * It calculates a rotation vector from the gyroscope angular speed values.
     */
    private void getRotationVectorFromGyro(float[] gyroValues, float[] deltaRotationVector, float timeFactor) {
        float[] normValues = new float[3];

        // Calculate the angular speed of the sample
        float omegaMagnitude = FloatMath.sqrt(gyroValues[0] * gyroValues[0] + gyroValues[1] * gyroValues[1] + gyroValues[2] * gyroValues[2]);

        // Normalize the rotation vector if it's big enough to get the axis
        if (omegaMagnitude > EPSILON) {
            normValues[0] = gyroValues[0] / omegaMagnitude;
            normValues[1] = gyroValues[1] / omegaMagnitude;
            normValues[2] = gyroValues[2] / omegaMagnitude;
        }

        // Integrate around this axis with the angular speed by the timestep
        // in order to get a delta rotation from this sample over the timestep
        // We will convert this axis-angle representation of the delta rotation
        // into a quaternion before turning it into the rotation matrix.
        float thetaOverTwo = omegaMagnitude * timeFactor;
        float sinThetaOverTwo = FloatMath.sin(thetaOverTwo);
        float cosThetaOverTwo = FloatMath.cos(thetaOverTwo);
        deltaRotationVector[0] = sinThetaOverTwo * normValues[0];
        deltaRotationVector[1] = sinThetaOverTwo * normValues[1];
        deltaRotationVector[2] = sinThetaOverTwo * normValues[2];
        deltaRotationVector[3] = cosThetaOverTwo;
    }

    private float[] matrixMultiplication(float[] A, float[] B) {
        float[] result = new float[9];

        result[0] = A[0] * B[0] + A[1] * B[3] + A[2] * B[6];
        result[1] = A[0] * B[1] + A[1] * B[4] + A[2] * B[7];
        result[2] = A[0] * B[2] + A[1] * B[5] + A[2] * B[8];

        result[3] = A[3] * B[0] + A[4] * B[3] + A[5] * B[6];
        result[4] = A[3] * B[1] + A[4] * B[4] + A[5] * B[7];
        result[5] = A[3] * B[2] + A[4] * B[5] + A[5] * B[8];

        result[6] = A[6] * B[0] + A[7] * B[3] + A[8] * B[6];
        result[7] = A[6] * B[1] + A[7] * B[4] + A[8] * B[7];
        result[8] = A[6] * B[2] + A[7] * B[5] + A[8] * B[8];

        return result;
    }

    /**
     * Ignore
     */
    @Override
    public void onProviderDisabled(String provider) {
        //Ignore
    }

    /**
     * Ignore
     */
    @Override
    public void onProviderEnabled(String provider) {
        //Ignore
    }

    /**
     * Ignore
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //Ignore
    }

    @Override
    public void onLocationChanged(Location location) {
        NCompassARData.setCurrentLocation(location);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (sensor == null) throw new NullPointerException();

        if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD && accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            TLog.w(TAG, "Compass data unreliable");
        }
    }

    private class calculateUnfusedOrientationTask extends TimerTask {
        public void run() {
            accMagMatrix = getRotationMatrixFromOrientation(accMagOrientation);

            //Convert from float[9] to Matrix
            NCompassMatrix mat = new NCompassMatrix();
            mat.set(accMagMatrix[0], accMagMatrix[1], accMagMatrix[2], accMagMatrix[3], accMagMatrix[4], accMagMatrix[5], accMagMatrix[6], accMagMatrix[7], accMagMatrix[8]);

            // Find position relative to magnetic north //
            magneticCompensatedCoord.toIdentity(); //Identity matrix

            synchronized (mageticNorthCompensation) {
                magneticCompensatedCoord.prod(mageticNorthCompensation); //Cross product the matrix with the magnetic north compensation
            }

            magneticCompensatedCoord.prod(mat); //Cross product with the world coordinates
            magneticCompensatedCoord.invert(); //Invert the matrix

            NCompassARData.setRotationMatrix(magneticCompensatedCoord); //Set the rotation matrix (used to translate all object from lat/lon to x/y/z)
        }
    }

    private class calculateFusedOrientationTask extends TimerTask {
        public void run() {
            /* Fix for 180 <--> -180 transition problem:
			 * Check whether one of the two orientation angles (gyro or accMag) is negative while the other one is positive.
			 * If so, add 360 (2 * math.PI) to the negative value, perform the sensor fusion, and remove the 360 from the result
			 * if it is greater than 180. This stabilizes the output in positive-to-negative-transition cases.
			 */

            // azimuth
            if (gyroOrientation[0] < -0.5f * Pi && accMagOrientation[0] > 0f) {
                fusedOrientation[0] = COMBINATION_FILTER_COEFFICIENT * (gyroOrientation[0] + TwoPi) + ONEMINUS_COMBINATION_COEFF * accMagOrientation[0];
                fusedOrientation[0] -= (fusedOrientation[0] > Pi) ? TwoPi : 0f;
            } else if (accMagOrientation[0] < -0.5f * Pi && gyroOrientation[0] > 0f) {
                fusedOrientation[0] = COMBINATION_FILTER_COEFFICIENT * gyroOrientation[0] + ONEMINUS_COMBINATION_COEFF * (accMagOrientation[0] + TwoPi);
                fusedOrientation[0] -= (fusedOrientation[0] > Pi) ? TwoPi : 0f;
            } else {
                fusedOrientation[0] = COMBINATION_FILTER_COEFFICIENT * gyroOrientation[0] + ONEMINUS_COMBINATION_COEFF * accMagOrientation[0];
            }

            // pitch
            if (gyroOrientation[1] < -0.5f * Pi && accMagOrientation[1] > 0f) {
                fusedOrientation[1] = COMBINATION_FILTER_COEFFICIENT * (gyroOrientation[1] + TwoPi) + ONEMINUS_COMBINATION_COEFF * accMagOrientation[1];
                fusedOrientation[1] -= (fusedOrientation[1] > Pi) ? TwoPi : 0f;
            } else if (accMagOrientation[1] < -0.5f * Pi && gyroOrientation[1] > 0f) {
                fusedOrientation[1] = COMBINATION_FILTER_COEFFICIENT * gyroOrientation[1] + ONEMINUS_COMBINATION_COEFF * (accMagOrientation[1] + TwoPi);
                fusedOrientation[1] -= (fusedOrientation[1] > Pi) ? TwoPi : 0f;
            } else {
                fusedOrientation[1] = COMBINATION_FILTER_COEFFICIENT * gyroOrientation[1] + ONEMINUS_COMBINATION_COEFF * accMagOrientation[1];
            }

            // roll
            if (gyroOrientation[2] < -0.5f * Pi && accMagOrientation[2] > 0f) {
                fusedOrientation[2] = COMBINATION_FILTER_COEFFICIENT * (gyroOrientation[2] + TwoPi) + ONEMINUS_COMBINATION_COEFF * accMagOrientation[2];
                fusedOrientation[2] -= (fusedOrientation[2] > Pi) ? TwoPi : 0f;
            } else if (accMagOrientation[2] < -0.5f * Pi && gyroOrientation[2] > 0f) {
                fusedOrientation[2] = COMBINATION_FILTER_COEFFICIENT * gyroOrientation[2] + ONEMINUS_COMBINATION_COEFF * (accMagOrientation[2] + TwoPi);
                fusedOrientation[2] -= (fusedOrientation[2] > Pi) ? TwoPi : 0f;
            } else {
                fusedOrientation[2] = COMBINATION_FILTER_COEFFICIENT * gyroOrientation[2] + ONEMINUS_COMBINATION_COEFF * accMagOrientation[2];
            }

            // overwrite gyro matrix and orientation with fused orientation to compensate gyro drift
            gyroMatrix = getRotationMatrixFromOrientation(fusedOrientation);
            System.arraycopy(fusedOrientation, 0, gyroOrientation, 0, 3);

            //Convert from float[9] to Matrix
            NCompassMatrix mat = new NCompassMatrix();
            mat.set(gyroMatrix[0], gyroMatrix[1], gyroMatrix[2], gyroMatrix[3], gyroMatrix[4], gyroMatrix[5], gyroMatrix[6], gyroMatrix[7], gyroMatrix[8]);

            // Find position relative to magnetic north
            magneticCompensatedCoord.toIdentity(); //Identity matrix

            synchronized (mageticNorthCompensation) {
                magneticCompensatedCoord.prod(mageticNorthCompensation); //Cross product the matrix with the magnetic north compensation
            }

            magneticCompensatedCoord.prod(mat); //Cross product with the world coordinates
            magneticCompensatedCoord.invert(); //Invert the matrix

            NCompassARData.setRotationMatrix(magneticCompensatedCoord); //Set the rotation matrix (used to translate all object from lat/lon to x/y/z)
        }
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Matrix rotateMatrix = new Matrix();
        if (bearingLocation != null) {
            currentBearing = NCompassARData.getBearingTo(bearingLocation);
        } else
            currentBearing = NCompassARData.getBearingTo(NCompassARData.getCurrentLocation());
        if (arrowBitmap != null) {
            rotateMatrix.setRotate((float) currentBearing, arrowBitmap.getWidth() / 2, arrowBitmap.getHeight() / 2);
            rotateMatrix.postTranslate(getWidth() / 2 - arrowBitmap.getWidth() / 2, getHeight() / 2 - arrowBitmap.getHeight() / 2);
            canvas.drawBitmap(arrowBitmap, rotateMatrix, null);
        }
    }

    public interface NCompassUpdateListener {
        void onUpdate(double angle);
    }
}
