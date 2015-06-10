package dk.nodes.map.v2.model;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class NMarkerV2 {

	private MarkerOptions mMarkerOptions;
	private String id = "";
	private Marker mMarker = null;
	
	public NMarkerV2() {
		mMarkerOptions = new MarkerOptions();
	}
	
	public NMarkerV2( MarkerOptions options ) {
		mMarkerOptions = options;
	}
	
	public NMarkerV2 position( LatLng position ) {
		mMarkerOptions.position( position );
		return this;
	}
	
	public NMarkerV2 title( String title ) {
		mMarkerOptions.title( title );
		return this;
	}
	
	public NMarkerV2 snippet( String title ) {
		mMarkerOptions.snippet( title );
		return this;
	}
	
	public NMarkerV2 draggable( boolean isDraggable ) {
		mMarkerOptions.draggable( isDraggable );
		return this;
	}
	
	public NMarkerV2 anchor( float u, float v ) {
		mMarkerOptions.anchor( u, v );
		return this;
	}
	
	public NMarkerV2 visible( boolean isVisible ) {
		mMarkerOptions.visible( isVisible );
		return this;
	}
	
	public NMarkerV2 icon( BitmapDescriptor descriptor ) {
		mMarkerOptions.icon( descriptor );
		return this;
	}
	

	public boolean compareTo(NMarkerV2 options){
		if( getId().equals( options.getId() ) && mMarkerOptions.getTitle().equals(options.getTitle()) && mMarkerOptions.getSnippet().equals(options.getSnippet()))
			return true;
		else
			return false;
	}
	
	public boolean compareTo(Marker marker){
		if(mMarkerOptions == null || marker == null)
			return false;
		if(mMarkerOptions.getTitle() != null && marker.getTitle() != null && !mMarkerOptions.getTitle().equals(marker.getTitle()))
			return false;
		double deltaLat = Math.abs(marker.getPosition().latitude) - Math.abs(marker.getPosition().latitude);
		double deltaLng = Math.abs(marker.getPosition().longitude) - Math.abs(marker.getPosition().longitude);

		if(	 deltaLat > 0 &&  deltaLng > 0 ) {
			return false;
		}
		
		return true;
	}
	
	public String getTitle() {
		return mMarkerOptions.getTitle();
	}
	
	public String getSnippet() {
		return mMarkerOptions.getSnippet();
	}
	
	public LatLng getPosition() {
		return mMarkerOptions.getPosition();
	}

	public String getId() {
		return id;
	}

	public void setId( String id ) {
		this.id = id;
	}
	
	public MarkerOptions getOptions() {
		return mMarkerOptions;
	}

	public Marker getMarker() {
		return mMarker;
	}

	public void setMarker( Marker mMarker ) {
		this.mMarker = mMarker;
	}

	public void setBitmapOnMarker(Bitmap bitmap) {
		mMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
	}
}
