package dk.nodes.map.v2.model;
/**
 * @author Casper Rasmussen 2012
 */

import android.graphics.Point;

import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

public class NMarkerList{
	private ArrayList<NMarkerV2> list = new ArrayList<NMarkerV2>();
	private LatLng centerGeoPoint;
	private Point centerPoint;

	public NMarkerList(ArrayList<NMarkerV2> list){
		this.list = list;
	}
	public NMarkerList(){
	}

	public ArrayList<NMarkerV2> getList(){
		return list;
	}

	public void setList(ArrayList<NMarkerV2> list){
		this.list = list;
	}

	public void addItem(NMarkerV2 item){
		list.add(item);
	}

	public void clearList(){
		list.clear();
	}

	public void setCenter(){
		double lat = 0;
		double lng = 0;

		for(NMarkerV2 item : list){
			lat += item.getPosition().latitude;
			lng += item.getPosition().longitude;
		}

		lat = lat / list.size();
		lng = lng / list.size();

		centerGeoPoint = new LatLng( lat, lng );
	}
	public LatLng getCenterGeoPoint(){
		return centerGeoPoint;
	}
	public void setCenterGeoPoint( LatLng latlng ) {
		this.centerGeoPoint = latlng;
	}

	public Point getCenterPoint(){
		return centerPoint;
	}
	public void setCenterPoint(Projection proj){
		centerPoint = proj.toScreenLocation( centerGeoPoint );
	}

	public NMarkerV2 getMarker( Marker m ) {
		for( NMarkerV2 mNMarkerV2 : list ) {
			if(	mNMarkerV2.compareTo(m)) {
				return mNMarkerV2;
			}
		}

		return null;
	}
	public int getSize() {
		return list.size();
	}
}
