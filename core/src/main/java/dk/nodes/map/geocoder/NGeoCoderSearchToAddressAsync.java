package dk.nodes.map.geocoder;
/**
 * @author Casper Rasmussen 2012
 */

import android.content.Context;
import android.location.Address;
import android.os.AsyncTask;

import java.util.List;

public class NGeoCoderSearchToAddressAsync extends AsyncTask<String, Void, List<Address> > {

	private NGeoCoderSearchToAddressAsyncListener mGeoCoderSearchToAddressAsyncListener;
	private String search;
	private Context mContext;
	private int maxAmount;

	public NGeoCoderSearchToAddressAsync(Context mContext, String search, int maxAmount, NGeoCoderSearchToAddressAsyncListener mGeoCoderSearchToAddressAsyncListener){
		this.mContext = mContext;
		this.search = search;
		this.maxAmount = maxAmount;
		this.mGeoCoderSearchToAddressAsyncListener = mGeoCoderSearchToAddressAsyncListener;
	}

	protected void onPreExecute (){
	}

	@Override
	protected void onPostExecute(List<Address> list) {
		if(mGeoCoderSearchToAddressAsyncListener==null)
			return;

		if(list==null){
			mGeoCoderSearchToAddressAsyncListener.onGeoCoderSearchToAddressError();
		}
		else{
			if(list.size()==0){
				mGeoCoderSearchToAddressAsyncListener.onGeoCoderSearchToAddressSuccessEmpty();
			}
			else if(list.size()==1){
				mGeoCoderSearchToAddressAsyncListener.onGeoCoderSearchToAddressSuccessSingle(list.get(0));
			}
			else{
				mGeoCoderSearchToAddressAsyncListener.onGeoCoderSearchToAddressSuccessList(list);
			}
		}
	}

	@Override
	protected List<Address> doInBackground(String... params) {
		return NGeocoder.getAddressFromSearchWithFallback(mContext, search,maxAmount);
	}
	
	public interface NGeoCoderSearchToAddressAsyncListener{
		public void onGeoCoderSearchToAddressSuccessEmpty();
		public void onGeoCoderSearchToAddressSuccessSingle(Address mAddress);
		public void onGeoCoderSearchToAddressSuccessList(List<Address> listAddress);
		public void onGeoCoderSearchToAddressError();
	}
}