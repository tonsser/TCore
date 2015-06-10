package dk.nodes.map.geocoder;
/**
 * @author Casper Rasmussen 2012
 */

import android.content.Context;
import android.location.Address;
import android.location.Location;
import android.os.AsyncTask;

import java.util.List;

public class NGeoCoderLocationToAddressAsync extends AsyncTask<String, Void, List<Address> > {

	private NGeoCoderLocationToAddressAsyncListener mNGeoCoderLocationToAddressAsyncListener;
	private Location mLocation;
	private Context mContext;
	private int maxAmount;

	public NGeoCoderLocationToAddressAsync(Context mContext, Location mLocation, int maxAmount,NGeoCoderLocationToAddressAsyncListener mNGeoCoderLocationToAddressAsyncListener){
		this.mContext = mContext;
		this.mLocation = mLocation;
		this.mNGeoCoderLocationToAddressAsyncListener = mNGeoCoderLocationToAddressAsyncListener;
		this.maxAmount = maxAmount;
	}

	protected void onPreExecute (){
	}

	@Override
	protected void onPostExecute(List<Address> list) {
		if(mNGeoCoderLocationToAddressAsyncListener==null)
			return;

		if(list==null){
			mNGeoCoderLocationToAddressAsyncListener.onGeoCoderLocationToAddressError();
		}
		else{
			if(list.size()==0){
				mNGeoCoderLocationToAddressAsyncListener.onGeoCoderLocationToAddressSuccessEmpty();
			}
			else if(list.size()==1){
				mNGeoCoderLocationToAddressAsyncListener.onGeoCoderLocationToAddressSuccessSingle(list.get(0));
			}
			else{
				mNGeoCoderLocationToAddressAsyncListener.onGeoCoderLocationToAddressSuccessList(list);
			}
		}
	}

	@Override
	protected List<Address> doInBackground(String... params) {
		return NGeocoder.getAddressFromLocationWithFallback(mContext, mLocation,maxAmount);
	}
	
	public interface NGeoCoderLocationToAddressAsyncListener{
		public void onGeoCoderLocationToAddressSuccessEmpty();
		public void onGeoCoderLocationToAddressSuccessSingle(Address mAddress);
		public void onGeoCoderLocationToAddressSuccessList(List<Address> listAddress);
		public void onGeoCoderLocationToAddressError();
	}
}