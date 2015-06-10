package dk.nodes.map.geocoder;
/**
 * @author Casper Rasmussen 2012
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import org.apache.http.message.BasicHeader;
import org.json.JSONException;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dk.nodes.utils.NLog;
import dk.nodes.webservice.NWebserviceController;
import dk.nodes.webservice.models.NResponse;
import dk.nodes.webservice.parser.NJSONArray;
import dk.nodes.webservice.parser.NJSONObject;

public class NGeocoder {
	public static List<Address> getAddressFromLocationWithFallback(Context mContext, Location mLocation, int maxAmount){
		List<Address> output = null;

		if(isGeoCoderPresent())
			output = getAddressFromLocation(mContext, mLocation, maxAmount);

		if(output!=null && output.size()>0)
			return output;

		else{
			try {
				output = getAddressFromLocationGoogleAPI(mLocation,maxAmount);
			} catch ( Exception e ) {
				NLog.e("NGeocoder getAddressFromLocationWithFallback",e);
			}
		}

		return output;	
	}

	/**
	 * Use this method to get a list of address from geocoder by String search, with google-api as fallback, remember to call this async
	 * @param mContext
	 * @param search
	 * @return
	 */
	public static List<Address> getAddressFromSearchWithFallback(Context mContext, String search,int maxAmount){
		List<Address> output = null;

		if(isGeoCoderPresent())//most likely not working anyways :)
			output = getAddressFromSearch(mContext,search,maxAmount);

		if(output!=null && output.size()>0){
			return output;
		}
		else{
			try {
				output = getAddressFromSearchGoogleAPI(search,maxAmount);
				return output;
			} catch (Exception e) {
				NLog.e("getAddressFromSearchWithFallback",e.toString());
				e.printStackTrace();
				return null;
			}
		}	
	}

	/**
	 * Use this method to get a list of address from geocoder by String search, with google-api as fallback, remember to call this async
	 * @param mContext
	 * @param search
	 * @return
	 */
	public static List<Address> getAddressFromSearchNonFormattedAddressWithFallback(Context mContext, String search,int maxAmount){
		List<Address> output = null;

		if(isGeoCoderPresent())//most likely not working anyways :)
			output = getAddressFromSearch(mContext,search,maxAmount);

		if(output!=null && output.size()>0){
			return output;
		}
		else{
			try {
				output = getAddressFromSearchGoogleAPINonFormattedAddress(search,maxAmount, null);
				return output;
			} catch (Exception e) {
				NLog.e("getAddressFromSearchNonFormattedAddressWithFallback",e.toString());
				e.printStackTrace();
				return null;
			}
		}	
	}

	/**
	 * Use this method to get a list of address from geocoder by String search, with google-api only for proper eseperation of address lines, remember to call this async
	 * @param mContext
	 * @param search
	 * @return
	 */
	public static List<Address> getAddressFromSearchNonFormattedAddressGoogleAPIOnly(Context mContext, String search,int maxAmount, String language){
		List<Address> output = null;

		try {
			output = getAddressFromSearchGoogleAPINonFormattedAddress(search,maxAmount, language);
			return output;
		} catch (Exception e) {
			NLog.e("getAddressFromSearchNonFormattedAddressGoogleAPIOnly",e.toString());
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * Use this method to get list of address from geocoder by String search
	 * @param context
	 * @param search
	 * @return
	 */
	public static List<Address> getAddressFromSearch(Context context,String search,int maxAmount){
		if(context == null){
			NLog.e("NGeocoder getAddressFromSearch","Returning 'null' because context was null");
			return null;
		}
		
		Geocoder mGeoCoder = new Geocoder(context, Locale.getDefault());
		List<Address> result = null;
		try {
			search = URLEncoder.encode(search, "utf-8");
			result = mGeoCoder.getFromLocationName(search, maxAmount);
			return result;
		} catch (Exception e) {
			NLog.e("getAddressFromSearch", e.toString());
			return null;
		}	
	}
	/** Use this method to get a list of address from Google-api by String search. Call async
	 * getAddressLine(0) returns [Streetname, Postalcode Cityname, Country]
	 * Use this method to get 
	 * @param search
	 * @param maxAmount 
	 * @return List<Address>
	 * @throws JSONException
	 */
	public static List<Address> getAddressFromSearchGoogleAPI(String search, int maxAmount) throws Exception {
		ArrayList<Address> output = new ArrayList<Address>();
		search = URLEncoder.encode(search, "utf-8");
		NResponse response = googleApiSearchWithString(search,maxAmount);
		NJSONArray results = response.getResponseJson().getJSONArray("results");
		for(int i = 0 ; i < results.length() ; i++){
			NJSONObject object = results.getJSONObject(i);
			NJSONObject location = object.getJSONObject("geometry").getJSONObject("location");

			Address item = new Address(null);
			item.setAddressLine(0, object.getString("formatted_address"));
			item.setLatitude(location.getDouble("lat"));
			item.setLongitude(location.getDouble("lng"));

			NJSONArray addressComponents = object.getJSONArray("address_components");

			for(int ii = 0 ;ii < addressComponents.length() ; ii++){
				NJSONObject types = addressComponents.getJSONObject(ii);

				if(types.getJSONArray("types").getString(0).equals("country")){
					item.setCountryName(types.optString("long_name"));
					item.setCountryCode(types.optString("short_name"));
				}
			}
			output.add(item);
		}
		return output;
	}

	/** Use this method to get a list of address from Google-api by String search. Call async
	 * This method splits the address lines the same was as the Geocoder-class:
	 * getAddressLine(0) returns [Streetname]
	 * getAddressLine(1) returns [Postalcode Cityname] 
	 * getAddressLine(2) returns [Streetname, Postalcode Cityname, Country]
	 * Use this method to get 
	 * @param search
	 * @param maxAmount 
	 * @return List<Address>
	 * @throws JSONException
	 */
	public static List<Address> getAddressFromSearchGoogleAPINonFormattedAddress(String search, int maxAmount, String language) throws Exception {
		ArrayList<Address> output = new ArrayList<Address>();
		search = URLEncoder.encode(search, "utf-8");
		NResponse response = googleApiSearchWithString(search,maxAmount, language);
		NJSONArray results = response.getResponseJson().getJSONArray("results");
		for(int i = 0 ; i < results.length() ; i++){
			NJSONObject object = results.getJSONObject(i);
			NJSONObject location = object.getJSONObject("geometry").getJSONObject("location");

			Address item = new Address(null);

			item.setLatitude(location.getDouble("lat"));
			item.setLongitude(location.getDouble("lng"));

			NJSONArray addressComponents = object.getJSONArray("address_components");

			for(int ii = 0 ;ii < addressComponents.length() ; ii++){
				NJSONObject types = addressComponents.getJSONObject(ii);
				if(types.getJSONArray("types").getString(0).equals("route")){
					item.setAddressLine(0, types.optString("long_name"));
				}

				if(types.getJSONArray("types").getString(0).equals("locality")){
					item.setAddressLine(1, types.optString("long_name"));
				}

				if(types.getJSONArray("types").getString(0).equals("postal_code")){
					item.setAddressLine(2, types.optString("long_name")+" "+item.getAddressLine(1));
				}


				if(types.getJSONArray("types").getString(0).equals("country")){
					item.setCountryName(types.optString("long_name"));
					item.setCountryCode(types.optString("short_name"));
				}
			}

			item.setAddressLine(2, object.getString("formatted_address"));

			output.add(item);
		}
		return output;
	}

	public static List<Address> getAddressFromLocationGoogleAPI(Location mLocation, int maxAmount) throws JSONException {
		ArrayList<Address> output = new ArrayList<Address>();

		NResponse response = googleApiSearchWithLocation(mLocation);
		NJSONArray results = response.getResponseJson().getNJSONArray("results");
		for(int i = 0 ; i < results.length() ; i++){
			NJSONObject object = results.getJSONObject(i);
			NJSONObject location = object.getNJSONObject("geometry").getNJSONObject("location");

			Address item = new Address(null);
			item.setAddressLine(0, object.getString("formatted_address"));
			item.setLatitude(location.getDouble("lat"));
			item.setLongitude(location.getDouble("lng"));

			NJSONArray addressComponents = object.getJSONArray("address_components");

			for(int ii = 0 ;ii < addressComponents.length() ; ii++){
				NJSONObject types = addressComponents.getJSONObject(ii);

				if(types.getJSONArray("types").getString(0).equals("country")){
					item.setCountryName(types.optString("long_name"));
					item.setCountryCode(types.optString("short_name"));
				}
			}
			output.add(item);
		}
		return output;
	}
	/**
	 * Use this method to call the google-api with a String input
	 * @param search
	 * @param maxAmount 
	 * @return NResponse
	 */
	public static NResponse googleApiSearchWithString(String search, int maxAmount){
		try{
			NResponse response = new NWebserviceController().curlHttpGet("http://maps.google.com/maps/api/geocode/json?address="+search+"&sensor=false&max="+maxAmount);

			NLog.d("googleApiSearchWithStrin", response.getResponseJson().toString());

			return response;
		}
		catch(Exception e){
			NLog.e("googleApiSearchWithStrin",e.toString());
			return null;
		}
	}

	/**
	 * Use this method to call the google-api with a String input and chosen language for the response
	 * @param search
	 * @param maxAmount 
	 * @return NResponse
	 */
	public static NResponse googleApiSearchWithString(String search, int maxAmount, String language){
		try{

			ArrayList<BasicHeader> myHeaders = new ArrayList<BasicHeader>();

			if(language!=null){
				myHeaders.add(new BasicHeader("Accept-Language", language));
			}

			NWebserviceController mNWebserviceController = new NWebserviceController();
			if(myHeaders.size()>0)
				mNWebserviceController.setMyHeaderArrayList(myHeaders);

			NResponse response = mNWebserviceController.curlHttpGet("http://maps.google.com/maps/api/geocode/json?address="+search+"&sensor=false&max="+maxAmount);
			mNWebserviceController.getMyHeaderArrayList();

			return response;
		}
		catch(Exception e){
			NLog.e("googleApiSearchWithStrin",e.toString());
			return null;
		}
	}

	/**
	 * Use this method to call the google-api with a Location input
	 * @param mLocation
	 * @return NResponse
	 */
	public static NResponse googleApiSearchWithLocation(Location mLocation){
		try{
			NResponse response = new NWebserviceController().curlHttpGet("http://maps.google.com/maps/api/geocode/json?latlng="+mLocation.getLatitude()+","+mLocation.getLongitude()+"&sensor=true&max=1");

			NLog.d("googleApiSearchWithLocation", response.getResponseJson().toString());

			return response;
		}
		catch(Exception e){
			NLog.e("googleApiSearchWithLocation",e.toString());
			return null;
		}
	}


	/**
	 * Use this method to get an adress from a location input.
	 * @param context
	 * @param mLocation
	 * @return Address
	 */
	public static List<Address> getAddressFromLocation(Context context, Location mLocation,int maxAmount){

		if(context == null){
			NLog.e("NGeocoder getAddressFromLocation","Returning 'null' because context was null");
			return null;
		}
		Geocoder gcd = new Geocoder(context, Locale.getDefault());
		List<Address> result = null;
		try {
			result = gcd.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), maxAmount);
			return result;
		}
		catch(Exception e){
			return null;
		}
	}


	/**
	 * Use this to parse a address object, to a more usefull string
	 * @param address
	 * @param details
	 * @return String
	 */
	public static String getAddress(Address address,boolean details){
		if(address!=null){
			String add1 = address.getAddressLine(0);
			String add2 = address.getAddressLine(1);
			String coun = address.getCountryName();
			String subL = address.getSubLocality();


			if(details && add2 != null && coun!=null)
				return add1+" "+add2 +" ("+coun+")";
			else if(details && add2 != null)
				return add1+" "+add2;
			else if(!details && coun!=null && subL!=null )
				return subL+" ("+coun+")";
			else if(!details && coun!=null && add2!=null)
				return add2+" ("+coun+")";
			else if(!details && coun!=null && add1!=null)
				return add1+" ("+coun+")";
			else if(!details && subL!=null)
				return subL;	
			else if(!details && add1!=null)
				return add1;
			else if(!details && add2!=null)
				return add2;
			else if(!details && coun!=null)
				return "("+coun+")";
			else
				return "";
		}
		return "";
	}

	public static List<String> convertAddressListToStringList(List<Address> input){
		ArrayList<String> output = new ArrayList<String>();
		for(Address mAddress : input){
			output.add(getAddress(mAddress,false));
		}
		return output;
	}

	/**
	 * Check if GeoCoder is present
	 * @return boolean
	 */
	@SuppressLint("NewApi")
	public static boolean isGeoCoderPresent(){
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;

		if (currentapiVersion > android.os.Build.VERSION_CODES.FROYO){
			return Geocoder.isPresent();
		} else{
			return true;
		}		
	}
}
