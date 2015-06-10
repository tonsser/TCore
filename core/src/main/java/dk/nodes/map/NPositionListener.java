package dk.nodes.map;
/**
 * @author Casper Rasmussen 2012
 */
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class NPositionListener implements LocationListener {
	private boolean first = true;
	private int accuracyCounter = 0;
	
	private NPositionListenerListener mNPositionListenerListener;
	public NPositionListener(NPositionListenerListener mNPositionListenerListener){
		this.mNPositionListenerListener = mNPositionListenerListener;
	}
	
	@Override
	public void onLocationChanged(Location location) {
		if(location==null)
			return;

		if(first){
			if(mNPositionListenerListener!=null)
				mNPositionListenerListener.onLocationChanged(location);
			first = false;
			accuracyCounter=0;
		}
		else{
			if(location.getAccuracy()< 25){
				mNPositionListenerListener.onLocationChanged(location);
			}
			else{
				accuracyCounter++;
				
				if(accuracyCounter >3){
					mNPositionListenerListener.onLocationChanged(location);
					accuracyCounter=0;
				}
			}
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

	public interface NPositionListenerListener{
		public void onLocationChanged(Location mLocation);
	}
}
