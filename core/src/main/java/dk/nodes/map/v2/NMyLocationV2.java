package dk.nodes.map.v2;
/**
 * @author Casper Rasmussen 2012
 */

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import dk.nodes.map.geocoder.NGeoCoderLocationToAddressAsync;
import dk.nodes.map.geocoder.NGeoCoderLocationToAddressAsync.NGeoCoderLocationToAddressAsyncListener;
import dk.nodes.utils.NLog;
import dk.nodes.utils.NUtils;

public class NMyLocationV2{

	private LatLng mLatLng;
	private Location mLocation;
	private Address mAddress;
	private float mAccurracy;
	private NMyLocationV2 mNMyLocation;
	private long lastSavedLocationUnix;
	private String TAG = NMyLocationV2.class.getName();

	public  void setMyLocation(final Context mContext, final Location location,boolean getAddress) {
		mNMyLocation = this;
		if(location==null)
			return;

		lastSavedLocationUnix = System.currentTimeMillis();
		mLocation = location;
		mLatLng = NLocationCalculatorV2.toLatLng(location);
		mAccurracy = location.getAccuracy();
	
		if(getAddress){
			new NGeoCoderLocationToAddressAsync(mContext, location, 1, new NGeoCoderLocationToAddressAsyncListener(){

				@Override
				public void onGeoCoderLocationToAddressSuccessEmpty() {
				}

				@Override
				public void onGeoCoderLocationToAddressSuccessSingle(Address mAddress) {
					mNMyLocation.mAddress = mAddress;
				}

				@Override
				public void onGeoCoderLocationToAddressSuccessList(List<Address> listAddress) {
					mNMyLocation.mAddress = listAddress.get(0);
				}

				@Override
				public void onGeoCoderLocationToAddressError() {
	
				}
			}).execute();
		}
	}

	public boolean isLocationFound(){
		if(mLatLng != null)
			return true;
		else
			return false;
	}
	
	public LatLng getLatLng(){
		return mLatLng;
	}
	public Location getLocation(){
		return mLocation;
	}
	public Address getAddress(){
		return mAddress;
	}
	public float getAccuracy(){
		return mAccurracy;
	}
	
	public long getTimeSinceLastGps(){
		return System.currentTimeMillis() - lastSavedLocationUnix;
	}
	
	public boolean isGPSLocationOutdated(int cacheTime){
		return NUtils.isOutdated(cacheTime, lastSavedLocationUnix);
	}
	
	public void setLastKnownLocation(Context mContext){
		try{
			LocationManager lm = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
			Criteria crit = new Criteria();
			crit.setAccuracy(Criteria.NO_REQUIREMENT);
			String provider = lm.getBestProvider(crit, true);
			Location lastKnownLocation = lm.getLastKnownLocation(provider);
			
			if(lastKnownLocation != null)
				setMyLocation(mContext, lastKnownLocation, false);
		}
		catch(Exception e){
			NLog.e(TAG + " setLastKnownLocation", e);
		}
	}
}
