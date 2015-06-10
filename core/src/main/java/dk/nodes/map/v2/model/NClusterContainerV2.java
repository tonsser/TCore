package dk.nodes.map.v2.model;
/**
 * @author Casper Rasmussen 2012
 */

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class NClusterContainerV2 {
	private NMarkerList singleMarkerList = new NMarkerList();
	private CopyOnWriteArrayList<NMarkerList> groupMarkerList = new CopyOnWriteArrayList<NMarkerList>();
	
	public NClusterContainerV2(NMarkerList singleMarkerList, CopyOnWriteArrayList<NMarkerList> groupMarkerList){
		this.setSingleMarkerList(singleMarkerList);
		this.setGroupMarkerList(groupMarkerList);
	}
	public NClusterContainerV2(){
		
	}
	public NMarkerList getSingleMarkerList() {
		return singleMarkerList;
	}
	public void setSingleMarkerList(NMarkerList singleMarkerList) {
		this.singleMarkerList = singleMarkerList;
	}
	public CopyOnWriteArrayList<NMarkerList> getGroupMarkerList() {
		return groupMarkerList;
	}
	public void setGroupMarkerList(CopyOnWriteArrayList<NMarkerList> groupMarkerList) {
		this.groupMarkerList = groupMarkerList;
	}
	
	public ArrayList<NMarkerV2> getGroup( Marker marker ) {
		for( NMarkerList list : groupMarkerList ) {
			if( Double.compare(list.getCenterGeoPoint().latitude, marker.getPosition().latitude) == 0 &&
				Double.compare(list.getCenterGeoPoint().longitude, marker.getPosition().longitude) == 0 ) {
				return list.getList(); 
			}
		}
		
		return null;
	} 
	
	
}
