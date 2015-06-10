package dk.nodes.map.v2;
/**
 * @author Casper Rasmussen 2013
 */
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.LatLngBounds.Builder;

public class NZoomToSpanControllerV2  {

	private static final double EARTHRADIUS = 6366198;
	
	private final String TAG = NZoomToSpanControllerV2.class.getName();
	Builder bounds = new LatLngBounds.Builder();
	private int itemCounter;

	public void clearMinMax(){
		bounds = new LatLngBounds.Builder();
		itemCounter = 0;
	}
	public void addToMinMax(LatLng item){
		if(item==null)
			return;

		bounds.include(item);
		itemCounter ++;
	}

	/**
	 * @param padding Screen padding in pixels
	 * @return
	 */
	public CameraUpdate getBounds(int padding){
		return CameraUpdateFactory.newLatLngBounds(bounds.build(), padding);
	}
	
	public boolean hasValuesAdded(){
		if(itemCounter>1)
			return true;
		else
			return false;
	}
	
	/**
	 * @param minBoundsInMeters You will see at least X amount of meters from the center of the map.
	 * @param padding Screen padding in pixels
	 * @return
	 */
	public CameraUpdate getBoundsWithMinBoundsInMeters(int minBoundsInMeters, int padding) {

	    LatLngBounds tmpBounds = bounds.build();
	    /** Add 2 points 1000m northEast and southWest of the center.
	     * They increase the bounds only, if they are not already larger
	     * than this. 
	     * 1000m on the diagonal translates into about 709m to each direction. */
	    
	    float calculatedBoundsInMeters = minBoundsInMeters * 0.709f;
	    
	    LatLng center = tmpBounds.getCenter();
	    LatLng norhtEast = move(center, calculatedBoundsInMeters, calculatedBoundsInMeters);
	    LatLng southWest = move(center, -calculatedBoundsInMeters, -calculatedBoundsInMeters);
	    bounds.include(southWest);
	    bounds.include(norhtEast);
	    
	    return CameraUpdateFactory.newLatLngBounds(bounds.build(), padding);
	}
	
	/**
	 * Create a new LatLng which lies toNorth meters north and toEast meters
	 * east of startLL
	 */
	private static LatLng move(LatLng startLL, double toNorth, double toEast) {
	    double lonDiff = meterToLongitude(toEast, startLL.latitude);
	    double latDiff = meterToLatitude(toNorth);
	    return new LatLng(startLL.latitude + latDiff, startLL.longitude
	            + lonDiff);
	}

	private static double meterToLongitude(double meterToEast, double latitude) {
	    double latArc = Math.toRadians(latitude);
	    double radius = Math.cos(latArc) * EARTHRADIUS;
	    double rad = meterToEast / radius;
	    return Math.toDegrees(rad);
	}

	private static double meterToLatitude(double meterToNorth) {
	    double rad = meterToNorth / EARTHRADIUS;
	    return Math.toDegrees(rad);
	}
}