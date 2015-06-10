/*
 *XXX Use this if you have a MapView in you layout. You will not be able to get touch-events from
 *the MapView's onTouchListener for some reason. If you need to be able to read touch-events, use NBaseMapFragment instead,
 *which uses a touchable wrapper and SupportMapFragment instead of a MapView.
 *
 *
 * -THNI 11/10/2013
 * 
 * 
 * */
package dk.nodes.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;

public abstract class NBaseMapViewFragment extends NBaseFragment implements OnMyLocationChangeListener {
	protected View parrentView;
	protected GoogleMap mMap;
	protected MapView mapView;

	protected abstract void setUpMap(boolean init) ;
	protected abstract void clearCurrentSearch();
	protected abstract int getMapLayoutId();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		parrentView = inflater.inflate(getLayoutResource(), container, false);
 
 		// Gets the MapView from the XML layout and creates it
		mapView = (MapView) parrentView.findViewById(getMapLayoutId());
		mapView.onCreate(savedInstanceState);
 
		// Gets to GoogleMap from the MapView
		mMap = mapView.getMap();
 
		try {
			MapsInitializer.initialize(this.getActivity());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (mMap != null) {
			setUpMap(true);
		}
		
		initResources(parrentView);
 
		return parrentView;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}
 
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mapView.onLowMemory();
	}
	
	@Override
	public void onResume(){
		mapView.onResume();
		super.onResume();
	}
	
	protected void resetMap() {
		if (!checkReady()) {
			return;
		}
		// Clear the map because we don't want duplicates of the markers.
		mMap.clear();
	}

	protected boolean checkReady() {
		if (mMap == null) {
			Toast.makeText(getActivity(), "MAP NOT READY", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
}
