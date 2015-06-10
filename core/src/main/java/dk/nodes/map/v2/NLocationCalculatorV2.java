package dk.nodes.map.v2;

import android.graphics.Point;
import android.location.Location;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

import dk.nodes.map.v2.model.NMarkerV2;
import dk.nodes.utils.NLog;

public class NLocationCalculatorV2 {
	private final static String TAG = NLocationCalculatorV2.class.getName();


	/**
	 * Will return meters between the 2 latLngs
	 * @param latLng1
	 * @param latLng2
	 * @return double
	 */
	public static double getDistanceBetweenLatLngs(LatLng latLng1, LatLng latLng2){
		if(latLng1 == null || latLng2 == null){
			NLog.e(TAG+" getDistanceBetweenLatLngs","LatLng input is null, returning MaxInteger");
			return Integer.MAX_VALUE;
		}

		return toLocation(latLng1).distanceTo(toLocation(latLng2));
	}

	/**
	 * Get distance between 2 points
	 * @param p1
	 * @param p2
	 * @return int
	 */
	public static int getDistanceBetweenPoints(Point p1, Point p2){
		if(p1==null || p2 == null)
			return 0;

		return (int) Math.sqrt((Math.pow((Math.abs(p1.x - p2.x)), 2) + Math.pow((Math.abs(p1.y - p2.y)), 2)));
	}

	/**
	 * Checking if the 2 LatLngs are equal
	 * @param latLng1
	 * @param latLng2
	 * @return Boolean
	 */
	public static boolean isLatLngsEqual(LatLng latLng1, LatLng latLng2){
		if(latLng1 == null || latLng2 == null)
			return false;
		
		if(latLng1.latitude == latLng2.latitude && latLng1.longitude==latLng2.longitude)
			return true;
		else 
			return false;
	}

	/**
	 * Checking if 2 locations are equal
	 * @param location1
	 * @param location2
	 * @return Boolean
	 */
	public static boolean isLocationsEqual(Location location1, Location location2) {
		if(location1.getLatitude() == location2.getLatitude() && location1.getLongitude() == location2.getLongitude())
			return true;
		else
			return false;
	}

	/**
	 * Will return a LatLng object with given input values
	 * @param lat
	 * @param lng
	 * @return LatLng
	 */
	public static LatLng toLatLng(double lat, double lng){
		return new LatLng(lat,lng);
	}

	/**
	 * Will return a LatLng object with same values as the Location input object
	 * @param location
	 * @return LatLng
	 */
	public static LatLng toLatLng(Location location){
		if(location!=null)
			return new LatLng(location.getLatitude(), location.getLongitude());
		else{
			NLog.e(TAG+" toLatLng","Input location was null, returning null");
			return null;
		}
	}

	/**
	 * Will return a Location object with given input values
	 * @param lat
	 * @param lng
	 * @return Location
	 */
	public static Location toLocation(double lat, double lng){
		Location location = new Location("new location");
		location.setLatitude(lat);
		location.setLongitude(lng);
		return location;
	}

	/**
	 * Will return a Location object, with same values as input LatLng object.
	 * @param latLng
	 * @return Location
	 */
	public static Location toLocation(LatLng latLng){
		if(latLng == null){
			NLog.e(TAG+" toLocation(LatLng latLng)","Input latLng is null returning null");
			return null;
		}

		Location location = new Location("new location");
		location.setLatitude(latLng.latitude);
		location.setLongitude(latLng.longitude);
		return location;
	}

	/**
	 * Returning a CameraPosition object, with the device current location, input zoom, tilt & bearing
	 * @param mGoogleMap
	 * @param zoom
	 * @param tilt
	 * @param bearing
	 * @return CameraPosition
	 */
	public static CameraPosition toMyCameraPosition(GoogleMap mGoogleMap, int zoom, float tilt, float bearing ){
		try{
			if(mGoogleMap!=null)
				return new CameraPosition(toLatLng(mGoogleMap.getMyLocation()),zoom, tilt,bearing);
			else{
				NLog.e(TAG+ "toMyCameraPosition","Input GoogleMap was null, returning null");
				return null;
			}
		}
		catch(Exception e){
			NLog.e(TAG+ " toMyCameraPosition",e);
			return null;
		}
	}

	/**
	 * Returning a CameraPositon object of the input LatLng, zoom, tilt & bearing
	 * @param latLng
	 * @param zoom
	 * @param tilt
	 * @param bearing
	 * @return CameraPosition
	 */
	public static CameraPosition toMyCameraPosition(LatLng latLng, int zoom, float tilt, float bearing ){
		if(latLng!=null)
			return new CameraPosition(latLng,zoom, tilt,bearing);
		else{
			NLog.e(TAG+ "toMyCameraPosition","Input LatLng was null, returning null");
			return null;
		}
	}

	/**
	 * Returning a CameraUpdate of the device current location, input zoom, tilt & bearing
	 * @param mGoogleMap
	 * @param zoom
	 * @param tilt
	 * @param bearing
	 * @return CameraUpdate
	 */
	public static CameraUpdate toMyCameraUpdate(GoogleMap mGoogleMap, int zoom, float tilt, float bearing ){
		try{
			if(mGoogleMap!=null)
				if(mGoogleMap.getMyLocation()!=null)
					return CameraUpdateFactory.newCameraPosition(new CameraPosition(toLatLng(mGoogleMap
							.getMyLocation()), zoom, tilt, bearing));
				else{
					NLog.e(TAG+ "toMyCameraPosition","Input GoogleMap.getMyLocation was null, returning null");
					return null;
				}
			else{
				NLog.e(TAG+ "toMyCameraPosition","Input GoogleMap was null, returning null");
				return null;
			}
		}
		catch(Exception e){
			NLog.e(TAG+ " toMyCameraUpdate",e);
			return null;
		}
	}

	/**
	 * Returning a CameraUpdate object of the input LatLng, zoom, tilt & bearing
	 * @param latLng
	 * @param zoom
	 * @param tilt
	 * @param bearing
	 * @return CameraUpdate
	 */
	public static CameraUpdate toMyCameraUpdate(LatLng latLng, int zoom, float tilt, float bearing ){
		if(latLng!=null)
			return CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, zoom, tilt, bearing));
		else{
			NLog.e(TAG+ "toMyCameraPosition","Input latLng was null, returning null");
			return null;
		}
	}
	
	/**
	 * @author Casper 
	 * Returning center LatLng from a marker List
	 * @param markerList
	 * @return LatLng
	 */
	public static LatLng getCenterLatLngFromMarkerList(ArrayList<Marker> markerList){

			double lat = 0;
			double lng = 0;

			for(Marker item : markerList){
				lat += item.getPosition().latitude;
				lng += item.getPosition().longitude;
			}

			lat = lat / markerList.size();
			lng = lng / markerList.size();

			return new LatLng( lat, lng );
		}
	
	/**
	 * @author Casper 
	 * Returning center LatLng from a marker List
	 * @param markerList
	 * @return LatLng
	 */
	public static LatLng getCenterLatLngFromNMakerV2List(ArrayList<NMarkerV2> markerList){
			double lat = 0;
			double lng = 0;

			for(NMarkerV2 item : markerList){
				lat += item.getPosition().latitude;
				lng += item.getPosition().longitude;
			}

			lat = lat / markerList.size();
			lng = lng / markerList.size();

			return new LatLng( lat, lng );
		}
}
