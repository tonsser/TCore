package dk.tonsser.sensor.compassview.calculations;

import android.content.res.Configuration;

import dk.tonsser.controllers.ScreenParameters;

public class NCompassCalculator {
    private static final String TAG = NCompassCalculator.class.getName().toString();

    private static final NCompassVector looking = new NCompassVector();
    private static final float[] lookingArray = new float[3];
    private static final float Pi = (float) (Math.PI);
    private static final float TwoPi = (float) (Math.PI * 2);

    private static volatile float azimuth = 0;
    private static volatile float pitch = 0;
    private static volatile float roll = 0;


    private NCompassCalculator() {
    }


    public static final float getAngle(float center_x, float center_y, float post_x, float post_y) {
        float tmpv_x = post_x - center_x;
        float tmpv_y = post_y - center_y;
        float d = (float) Math.sqrt(tmpv_x * tmpv_x + tmpv_y * tmpv_y);
        float cos = tmpv_x / d;
//        float angle = (float) Math.toDegrees(Math.acos(cos));
        float angle = (float) (Math.acos(cos));

        angle = (tmpv_y < 0) ? angle * -1 : angle;

        return angle;
    }

    /**
     * Azimuth the phone's camera is pointing. From 0 to 2Pi with magnetic north compensation.
     *
     * @return float representing the azimuth the phone's camera is pointing
     */
    public static synchronized float getAzimuth() {
        return NCompassCalculator.azimuth;
    }

    /**
     * Pitch of the phone's camera. From -Pi to Pi, where negative is pointing down and zero is level.
     *
     * @return float representing the pitch of the phone's camera.
     */
    public static synchronized float getPitch() {
        return NCompassCalculator.pitch;
    }

    /**
     * Roll of the phone's camera. From -Pi to Pi, where negative is rolled left and zero is level.
     *
     * @return float representing the roll of the phone's camera.
     */
    public static synchronized float getRoll() {
        return NCompassCalculator.roll;
    }

    public static synchronized void calcPitchBearing(NCompassMatrix rotationMatrix) {
        if (rotationMatrix == null) return;

        NCompassMatrix m = new NCompassMatrix();
        float[] r = new float[9];
        rotationMatrix.get(r);
        m.set(r[0], r[1], r[2], r[3], r[4], r[5], r[6], r[7], r[8]);
        m.transpose();

        boolean portrait = (ScreenParameters.orientation == Configuration.ORIENTATION_PORTRAIT);
        if (portrait) {
            looking.set(0, 1, 0);
        } else {
            looking.set(1, 0, 0);
        }
        looking.prod(m);
        looking.get(lookingArray);
        //Calculator.azimuth = ((getAngle(0, 0, lookingArray[0], lookingArray[2])  + Pi ) % TwoPi);
        NCompassCalculator.azimuth = ((getAngle(0, 0, lookingArray[0], lookingArray[2]) + TwoPi) % TwoPi);
        NCompassCalculator.roll = -(0.5f * Pi - Math.abs(getAngle(0, 0, lookingArray[1], lookingArray[2])));
        looking.set(0, 0, 1);
        looking.prod(m);
        looking.get(lookingArray);
        NCompassCalculator.pitch = -(0.5f * Pi - Math.abs(getAngle(0, 0, lookingArray[1], lookingArray[2])));
        //Log.i(TAG, "pitch, roll, azi: " + Calculator.pitch + ", " + Calculator.roll + ", " + Calculator.azimuth + ", ");
    }
}

