package dk.nodes.map;
/**
 * @author Casper Rasmussen 2012
 */

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import dk.nodes.utils.NLog;

public class NGetMyPositionNow {
	private LocationHandler lh;
	private LocationManager locationManager;
	private NGetMyPositionNowListener mNGetMyPositionNowListener;
	private float lastAccuracy = Float.MAX_VALUE;
	private Location lastLocation;
	private Timer timer;
	private NGetMyPositionNowListener2 mNGetMyPositionNowListener2;
	
	public static boolean GPS = true;
	public static boolean NETWORK = true;


	/**
	 * Remember to take permissions
	 * @param mContext
	 * @param mNGetMyPositionNowListener
	 */
	public NGetMyPositionNow(Context mContext, final NGetMyPositionNowListener mNGetMyPositionNowListener){
		start(mContext, mNGetMyPositionNowListener);
	}
	
	/**
	 * Remember to take permissions
	 * @param mContext
	 * @param mNGetMyPositionNowListener
	 */
	public NGetMyPositionNow(final Activity mActivity, int timeBeforeShutDown, final NGetMyPositionNowListener2 mNGetMyPositionNowListener2){
		start(mActivity, mNGetMyPositionNowListener);

		timer = new Timer();
		TimerTask timerTask = new TimerTask(){
			public void run()
			{			
				if(lastLocation == null && mNGetMyPositionNowListener2 != null && mActivity != null){
					mActivity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							mNGetMyPositionNowListener2.onNoLocationFound();
							if(locationManager != null)
								locationManager.removeUpdates(lh);
						}
					});
				}
			}
		};	
		timer.schedule(timerTask, new Date(System.currentTimeMillis() + timeBeforeShutDown));
	}

	private void start(Context mContext, NGetMyPositionNowListener mNGetMyPositionNowListener){
		this.mNGetMyPositionNowListener = mNGetMyPositionNowListener;
		lh = new LocationHandler();
		locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

		if(mNGetMyPositionNowListener != null){
			if(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null){
				mNGetMyPositionNowListener.onFirstLocationFound(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
			}
			else if(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null){
				mNGetMyPositionNowListener.onFirstLocationFound(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
			}
		}

		try {
			if( GPS )
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1*1000L, 0.0f, lh);

			if( NETWORK )
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1*1000L, 0.0f, lh);
		} 
		catch( Exception e ) {
			NLog.e("NGetMyPositionNow", "Error requesting locations from GPS or NETWORK provider: " + e.toString());
		}
	}
	
	public class LocationHandler implements LocationListener {

		private int counter;

		@Override
		public void onLocationChanged(final Location location) {

			if(counter==0 && mNGetMyPositionNowListener!=null){
				if(mNGetMyPositionNowListener != null)
					mNGetMyPositionNowListener.onFirstLocationFound(location);
				if(mNGetMyPositionNowListener2 != null)
					mNGetMyPositionNowListener2.onFirstLocationFound(location);

				lastAccuracy = location.getAccuracy();
				lastLocation = location;
			}
			else{
				if(lastAccuracy>=location.getAccuracy() && mNGetMyPositionNowListener!=null){
					if(mNGetMyPositionNowListener != null)
						mNGetMyPositionNowListener.onOtherLocationsFound(location);
					if(mNGetMyPositionNowListener2 != null)
						mNGetMyPositionNowListener2.onOtherLocationsFound(location);
					lastAccuracy = location.getAccuracy();
					lastLocation = location;
				}
			}
			counter ++;
			if(counter >= 5){
				if(locationManager != null)
					locationManager.removeUpdates(lh);
				if(timer != null)
					timer.cancel();
			}		
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

	public void stop() { 
		if( lh == null || locationManager == null )
			return;

		locationManager.removeUpdates( lh );
		lh = null;
		locationManager = null;

		if(timer != null)
			timer.cancel();
	}

	public interface NGetMyPositionNowListener{
		public void onFirstLocationFound(Location location);
		public void onOtherLocationsFound(Location location);
	}

	public interface NGetMyPositionNowListener2{
		public void onFirstLocationFound(Location location);
		public void onOtherLocationsFound(Location location);
		public void onNoLocationFound();
	}
}
