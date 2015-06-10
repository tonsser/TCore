/*
 *XXX If you don't need to be able to get touch-events of the map (via the NTouchableFrameLayout), consider using 
 *NBaseMapViewFragment instead, since it seems to be a bit less complicated to use. It is just a MapView in a layout
 *, and fworks a a view. However, you won't get proper touch-events from a MapView's onTouchListener for some reason.
 *That's why we have this NBaseMapFragment with a touchable wrapper when touch-events are needed. 
 *
 * -THNI 11/10/2013
 * 
 * 
 * */
package dk.nodes.base;

import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.SupportMapFragment;

import dk.nodes.widgets.framelayout.NTouchableFrameLayout;

public abstract class NBaseMapFragment extends NBaseFragment implements OnMyLocationChangeListener {
	protected GoogleMap mMap;
	protected NTouchableFrameLayout mNTouchableFrameLayout;

	protected abstract void setUpMap(boolean init) ;
	protected abstract void clearCurrentSearch();
	protected abstract int getMapLayoutId();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (parentView != null) {
			ViewGroup parent = (ViewGroup) parentView.getParent();
			if (parent != null)
				parent.removeView(parentView);
		}
		try {
			parentView = inflater.inflate(getLayoutResource(), container, false);
		} catch (InflateException e) {
			e.printStackTrace();
		}

		mNTouchableFrameLayout = new NTouchableFrameLayout(getActivity());
		mNTouchableFrameLayout.addView(parentView);
		
		initResources(parentView);
		
		return mNTouchableFrameLayout;
	}
	
	@Override
	public void onResume(){
		setUpMapIfNeeded();
		super.onResume();
	}
	
	protected void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(getMapLayoutId()))
					.getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				setUpMap(true);
			}
		}
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
