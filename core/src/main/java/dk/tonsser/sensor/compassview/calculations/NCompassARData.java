package dk.tonsser.sensor.compassview.calculations;

import android.location.Location;

import dk.tonsser.utils.NLog;

public abstract class NCompassARData {
    private static final String TAG = NCompassARData.class.getName().toString();

    /*defaulting to our place*/
    public static final Location hardFix = new Location("NAR");

    static {
        //hardFix.setLatitude(55.6661); // nodes
        //hardFix.setLongitude(12.5819); // nodes
        hardFix.setLatitude(57.6661);
        hardFix.setLongitude(12.5819);
        hardFix.setAltitude(1);
    }

    public static final float Pi = (float) (Math.PI);

    private static Location currentLocation = hardFix;
    private static NCompassMatrix rotationMatrix = new NCompassMatrix();
    private static final Object azimuthLock = new Object();
    private static float azimuth = 0;
    private static final Object rollLock = new Object();
    private static float roll = 0;
    private static final Object pitchLock = new Object();
    private static float pitch = 0;

    /**
     * Set the current location.
     *
     * @param currentLocation Location to set.
     * @throws NullPointerException if Location param is NULL.
     */
    public static void setCurrentLocation(Location currentLocation) {
        if (currentLocation == null) throw new NullPointerException();

        NLog.d(TAG, "current location. location=" + currentLocation.toString());
        synchronized (currentLocation) {
            NCompassARData.currentLocation = currentLocation;
        }
    }

    /**
     * Get the current Location.
     *
     * @return Location representing the current location.
     */
    public static Location getCurrentLocation() {
        synchronized (NCompassARData.currentLocation) {
            return NCompassARData.currentLocation;
        }
    }

    public static double getBearingTo(Location destination) {
        double bearing = currentLocation.bearingTo(destination) - (180 / Math.PI) * getAzimuth();
        if (bearing < 0.0) bearing += 360.0;
        return bearing;
    }

    /**
     * Set the rotation matrix.
     *
     * @param rotationMatrix Matrix to use for rotation.
     */
    public static void setRotationMatrix(NCompassMatrix rotationMatrix) {
        synchronized (NCompassARData.rotationMatrix) {
            NCompassARData.rotationMatrix = rotationMatrix;
            //calcUpAngle();
            update();
        }
    }

    /**
     * Get the rotation matrix.
     *
     * @return Matrix representing the rotation matrix.
     */
    public static NCompassMatrix getRotationMatrix() {
        synchronized (NCompassARData.rotationMatrix) {
            return rotationMatrix;
        }
    }

    private static void update() {
        NCompassCalculator.calcPitchBearing(getRotationMatrix());
        float az;
        if (NCompassCalculator.getPitch() < 0) {
            az = NCompassCalculator.getAzimuth();
        } else if (Math.abs(NCompassCalculator.getRoll()) > (Pi / 4)) {
            az = (Pi + NCompassCalculator.getAzimuth()) % (2 * Pi);
        } else {
            az = NCompassCalculator.getAzimuth();
        }
        setAzimuth(az);
        //NLog.v(TAG, "Azimuth: " + getAzimuth());
    }

    /**
     * Set the current Azimuth.
     *
     * @param azimuth float representing the azimuth.
     */
    public static void setAzimuth(float azimuth) {
        synchronized (azimuthLock) {
            NCompassARData.azimuth = azimuth;
        }
    }

    /**
     * Get the current Azimuth.
     *
     * @return azimuth float representing the azimuth.
     */
    public static float getAzimuth() {
        synchronized (azimuthLock) {
            return NCompassARData.azimuth;
        }
    }

    /**
     * Set the current Roll.
     *
     * @param roll float representing the roll.
     */
    public static void setRoll(float roll) {
        synchronized (rollLock) {
            NCompassARData.roll = roll;
        }
    }

    /**
     * Get the current Roll.
     *
     * @return roll float representing the roll.
     */
    public static float getRoll() {
        synchronized (rollLock) {
            return NCompassARData.roll;
        }
    }

    /**
     * Get the current Pitch.
     *
     * @return pitch float representing the pitch.
     */
    public static float getPitch() {
        synchronized (pitchLock) {
            return NCompassARData.pitch;
        }
    }
}
