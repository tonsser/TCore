package dk.nodes.map.v2;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import dk.nodes.controllers.NScreenParameters;
import dk.nodes.map.v2.controller.CalculatePOITaskV2;
import dk.nodes.map.v2.controller.NMarkerDrawerV2;
import dk.nodes.map.v2.model.NClusterContainerV2;
import dk.nodes.map.v2.model.NMarkerList;
import dk.nodes.map.v2.model.NMarkerV2;
import dk.nodes.utils.NLog;

public class NClusterV2 {

	private NMarkerList mMarkerList = new NMarkerList();
	private NClusterContainerV2 mClusterContainer = new NClusterContainerV2();
	private NMarkerDrawerV2 mDrawer = new NMarkerDrawerV2();
	private GoogleMap mMap = null;
	private CalculatePOITaskV2 mCalculateTask;

	public static int MERGE_GRID_DENSITY = NScreenParameters.toPx(5);
	public static int MERGE_DISTANCE = NScreenParameters.toPx(40);;
	public static int MIN_UPDATE_LIMIT = 300;

	private int mViewHeight = 0;
	private int mViewWidth = 0;
	private long mLastUpdate = 0;
	private boolean isCanceled;

	private static String TAG = NClusterV2.class.getName();

	public NClusterV2( GoogleMap map ) {
		mMap = map;
	}

	/**
	 *  Updates the markers via CalculatePOITask every onCameraChange, but at maximum every MIN_UPDATE_LIMIT
	 *  Add callbacks for complete and to set bitmaps on pins for singlepins
	 * @param viewHeight
	 * @param viewWidth
	 * @param mOnNClusterV2ClusterListner
	 * @param OnNClusterV2SetImageListener
	 */
	public void updateMarkers( int viewHeight, int viewWidth , final OnNClusterV2ClusterListner mOnNClusterV2ClusterListner, final OnNClusterV2SetImageListener OnNClusterV2SetImageListener) {
		if( !canUpdate() )
			return;
		isCanceled = false;

		mViewHeight = viewHeight;
		mViewWidth = viewWidth;

		if( mCalculateTask != null && mCalculateTask.getStatus() == AsyncTask.Status.RUNNING ) {
			return;
		}

		mCalculateTask = new CalculatePOITaskV2( MERGE_GRID_DENSITY, MERGE_DISTANCE, mMap, mMarkerList, mViewHeight, mViewWidth ) {
			@Override
			public void onPostExecute( NClusterContainerV2 container ) {
				if(container != null && !isCanceled){
					mClusterContainer = container;
					addMarkers(OnNClusterV2SetImageListener);
				}
				if(mOnNClusterV2ClusterListner != null)
					mOnNClusterV2ClusterListner.onCompleted();
			}
		};

		mCalculateTask.execute( mMap.getProjection() );

		mLastUpdate = System.currentTimeMillis();
	}
	/**
	 * This will cancel the updateMarkers Task
	 */
	public void cancel(){
		if(mCalculateTask != null)
			mCalculateTask.cancel(true);

		isCanceled = true;
	}

	/*
	 * Adds a marker to the clustering map
	 * 
	 */
	public void addMarker( NMarkerV2 marker ) {
		if( mMap == null ) {
			Log.e(TAG, "createMarker --> GoogleMap object was null");
			return;
		}

		mMarkerList.addItem( marker );
	}

	private void addMarkers(OnNClusterV2SetImageListener OnNClusterV2SetImageListener) {
		mMap.clear();

		addSingleMarkers(OnNClusterV2SetImageListener);
		addGroupMarkers();
	}

	public void clearLists(){
		mMarkerList.clearList();
	}

	private void addSingleMarkers(OnNClusterV2SetImageListener OnNClusterV2SetImageListener) {
		for( NMarkerV2 marker : mClusterContainer.getSingleMarkerList().getList() ) {
			if(OnNClusterV2SetImageListener != null){
				Bitmap bitmap = OnNClusterV2SetImageListener.setImageOnSinglePin(marker);
				if(bitmap != null)
					marker.setBitmapOnMarker(bitmap);
				else
					NLog.e(TAG+" addSingleMarkers","Bitmap was null, so didnt apply it");
			}
			try {
				mMap.addMarker(marker.getOptions());
			} catch (Exception e) {
				NLog.e(TAG+" addSingleMarkers",e);
			}
		}
	}

	private void addGroupMarkers() {
		for( NMarkerList group : mClusterContainer.getGroupMarkerList() ) {
			Marker centerGeoMarker = mMap.addMarker(
					new MarkerOptions()
					.position( group.getCenterGeoPoint() )
					.icon( BitmapDescriptorFactory.fromBitmap(mDrawer.drawGroupMarker(group.getList()
							.size())) )
					);
			group.setCenterGeoPoint( centerGeoMarker.getPosition() );
		}
	}

	public void setNMarkerDrawerV2(NMarkerDrawerV2 mNMarkerDrawerV2){
		mDrawer = mNMarkerDrawerV2;
	}

	public NMarkerDrawerV2 getNMarkerDrawerV2(){
		return mDrawer;
	}

	public void setOnMarkerClickListener( final OnNMarkerClickListener listener ) {
		mMap.setOnMarkerClickListener( new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick( Marker marker ) {

				// Cluster / Group
				ArrayList<NMarkerV2> list = mClusterContainer.getGroup( marker );
				if( list != null ) {
					listener.onClusterClick( list );

					// Single
				} else {
					NMarkerV2 nMarker = mMarkerList.getMarker( marker );

					if( nMarker != null ){
						nMarker.setMarker(marker);
						listener.onMarkerClick( nMarker );
					}
				}
				return true;
			}
		});
	}

	/*
	 * Only update every >250 ms
	 */
	private boolean canUpdate() {
		if( System.currentTimeMillis() - mLastUpdate > MIN_UPDATE_LIMIT || mLastUpdate == 0 ) {
			return true;
		}

		return false;
	}


	/*
	 * Interfaces
	 */

	public interface OnNMarkerClickListener {
		public void onMarkerClick(NMarkerV2 marker);
		public void onClusterClick(ArrayList<NMarkerV2> markers);
	}

	public interface OnNClusterV2ClusterListner{
		public void onCompleted();
	}

	public interface OnNClusterV2SetImageListener{
		public Bitmap setImageOnSinglePin(NMarkerV2 marker);
	}
}
