package dk.nodes.map.v2.controller;

import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;

import dk.nodes.map.v2.model.NClusterContainerV2;
import dk.nodes.map.v2.model.NMarkerList;

public class CalculatePOITaskV2 extends AsyncTask<Projection, NClusterContainerV2, NClusterContainerV2> {
	private int MERGE_GRID_DENSITY;
	private int MERGE_DISTANCE;
	private NMarkerList markerList;
	private int viewHeight;
	private int viewWidth;
	
	public CalculatePOITaskV2( int MERGE_GRID_DENSITY, int MERGE_DISTANCE, GoogleMap map, NMarkerList markerList, int viewHeight, int viewWidth ) {
		this.MERGE_GRID_DENSITY = MERGE_GRID_DENSITY;
		this.MERGE_DISTANCE = MERGE_DISTANCE;
		this.markerList = markerList;
		this.viewHeight = viewHeight;
		this.viewWidth = viewWidth;
	}
	
	@Override
	protected NClusterContainerV2 doInBackground( Projection... params ) {
		Projection projection = params[0];
		try {
			return NClusterControllerV2.recalculatePOI( MERGE_GRID_DENSITY, MERGE_DISTANCE, projection, markerList, viewHeight, viewWidth );
		} catch (Exception e) {
			return null;
		}
	}
}
