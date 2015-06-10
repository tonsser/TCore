package dk.nodes.map;
/**
 * @author Casper Rasmussen 2012
 */

import android.content.Context;
import android.location.Address;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import dk.nodes.map.geocoder.NGeoCoderLocationToAddressAsync;
import dk.nodes.map.geocoder.NGeoCoderLocationToAddressAsync.NGeoCoderLocationToAddressAsyncListener;
import dk.nodes.map.v2.NLocationCalculatorV2;
import dk.nodes.utils.NUtils;

public class NMyLocation{

	private LatLng mLatLng;
	private Location mLocation;
	private Address mAddress;
	private float mAccurracy;
	private NMyLocation mNMyLocation;
	private long lastSavedLocationUnix;

	public  void setMyLocation(final Context mContext, final Location location,boolean getAddress) {
		mNMyLocation = this;
		if(location==null)
			return;
		mLocation = location;
		mAccurracy = location.getAccuracy();
		mLatLng = NLocationCalculatorV2.toLatLng(location);
		lastSavedLocationUnix = System.currentTimeMillis();
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
		if(mLocation == null)
			return false;
		else
			return true;
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
}
