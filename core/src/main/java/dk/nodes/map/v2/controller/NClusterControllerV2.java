package dk.nodes.map.v2.controller;

import android.graphics.Point;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

import dk.nodes.map.v2.NLocationCalculatorV2;
import dk.nodes.map.v2.model.NClusterContainerV2;
import dk.nodes.map.v2.model.NMarkerList;
import dk.nodes.map.v2.model.NMarkerV2;

public class NClusterControllerV2 {
	public static String[] getStringArrayFromNOverlayList(NMarkerList list){
		
		String[] output = new String[list.getList().size()];
		for(int i = 0 ; i < list.getList().size() ; i++)
			output[i] = list.getList().get(i).getTitle();
		return output;
	}
	
	public static NMarkerList getClusterListFromItem(NMarkerV2 item, NClusterContainerV2 clusterContainer){
		for(int i = 0 ; i < clusterContainer.getGroupMarkerList().size() ; i++){
			for(int j = 0 ; j < clusterContainer.getGroupMarkerList().get(i).getList().size() ; j++)
				if(clusterContainer.getGroupMarkerList().get(i).getList().get(j).compareTo(item))
					return clusterContainer.getGroupMarkerList().get(i);
		}
		return null;
	}
	public static boolean isSingle(NMarkerV2 item, NClusterContainerV2 clusterContainer){
		for(int i = 0 ; i < clusterContainer.getSingleMarkerList().getList().size() ; i++){
			String title = clusterContainer.getSingleMarkerList().getList().get(i).getTitle();
			LatLng point = clusterContainer.getSingleMarkerList().getList().get(i).getPosition();
			if(title.equals(item.getTitle()) && NLocationCalculatorV2.isLatLngsEqual(point, item.getPosition()))
				return true;
		}
		return false;
	}
	public static NClusterContainerV2 mergeClusterIndexs(int index1, int index2, Projection proj, NClusterContainerV2 container){
		NMarkerList list1 = container.getGroupMarkerList().get(index1);
		NMarkerList list2 = container.getGroupMarkerList().get(index2);

		list1.getList().addAll(list2.getList());
		list1.setCenter();
		list1.setCenterPoint(proj);

		container.getGroupMarkerList().remove(index2);

		return container;
	}
	
	public static NClusterContainerV2 mergeClustersTooClose(NClusterContainerV2 tempClusterContainer, Projection mProjection, int mergeDistance) {

		while(true){
			boolean breakWhile = false;
			if(tempClusterContainer.getGroupMarkerList().size()>1){
				for(int i = 0 ; i <  tempClusterContainer.getGroupMarkerList().size(); i++){
					tempClusterContainer.getGroupMarkerList().get(i).setCenter();
					tempClusterContainer.getGroupMarkerList().get(i).setCenterPoint(mProjection);
					boolean breakI = false;
					for(int j = 0 ; j < tempClusterContainer.getGroupMarkerList().size(); j++){
						int dist= NLocationCalculatorV2.getDistanceBetweenPoints(tempClusterContainer.getGroupMarkerList().get(i).getCenterPoint(),tempClusterContainer.getGroupMarkerList().get(j).getCenterPoint());
						if(dist<mergeDistance && dist>0){
							tempClusterContainer = NClusterControllerV2.mergeClusterIndexs(i,j,mProjection,tempClusterContainer);
							breakI = true;
							break;
						}
					}
					if(breakI)
						break;

					if(i==tempClusterContainer.getGroupMarkerList().size()-1){
						breakWhile=true;
					}
				}
				if(breakWhile)
					break;
			}
			else
				break;
		}

		return tempClusterContainer;
	}
	
	public static NClusterContainerV2 mergePointsToClustersTooClose(NClusterContainerV2 tempClusterContainer, Projection mProjection, int mergeDistance) {
		if(tempClusterContainer.getSingleMarkerList().getList().size()==0 || tempClusterContainer.getGroupMarkerList().size()==0)
			return tempClusterContainer;


		for(int i = 0 ; i <  tempClusterContainer.getGroupMarkerList().size() ; i++){
			for(int j = 0 ; j < tempClusterContainer.getSingleMarkerList().getList().size(); j++){	
				int dist = NLocationCalculatorV2.getDistanceBetweenPoints(mProjection.toScreenLocation( tempClusterContainer.getSingleMarkerList().getList().get(j).getPosition() ),tempClusterContainer.getGroupMarkerList().get(i).getCenterPoint());
				if(dist<mergeDistance && dist>0){
					tempClusterContainer.getGroupMarkerList().get(i).getList().add(tempClusterContainer.getSingleMarkerList().getList().get(j));
					tempClusterContainer.getSingleMarkerList().getList().remove(j);
					break;
				}
			}
		}
		return tempClusterContainer;
	}
	
	public static NClusterContainerV2 mergePointsTooClose(NClusterContainerV2 tempClusterContainer, Projection mProjection, int mergeDistance) {
	
		for(int i = 0 ; i < tempClusterContainer.getSingleMarkerList().getList().size() ; i++){
			for(int j = 0 ; j < tempClusterContainer.getSingleMarkerList().getList().size() ; j++){
				if(j>=tempClusterContainer.getSingleMarkerList().getList().size()-1 || i>=tempClusterContainer.getSingleMarkerList().getList().size()-1 )
					break;

				int dist = NLocationCalculatorV2.getDistanceBetweenPoints(mProjection.toScreenLocation( tempClusterContainer.getSingleMarkerList().getList().get(j).getPosition() ),mProjection.toScreenLocation( tempClusterContainer.getSingleMarkerList().getList().get(i).getPosition() ));
				if(dist<mergeDistance && dist>0){

					NMarkerList newCluster = new NMarkerList();
					newCluster.getList().add(tempClusterContainer.getSingleMarkerList().getList().get(i));
					newCluster.getList().add(tempClusterContainer.getSingleMarkerList().getList().get(j));	
					newCluster.setCenter();
					tempClusterContainer.getGroupMarkerList().add(newCluster);

					if(i>j){
						tempClusterContainer.getSingleMarkerList().getList().remove(i);
						tempClusterContainer.getSingleMarkerList().getList().remove(j);	
					}
					else if(j>i){
						tempClusterContainer.getSingleMarkerList().getList().remove(j);	
						tempClusterContainer.getSingleMarkerList().getList().remove(i);					
					}
					else{
						tempClusterContainer.getSingleMarkerList().getList().remove(i);
					}
				}
			}
		}
		return tempClusterContainer;
	}
	
	public static boolean isWithin(LatLng l, GoogleMap mapView) {
		LatLngBounds bounds = mapView.getProjection().getVisibleRegion().latLngBounds;
		
		return bounds.contains( l );
	}
	
	public static boolean isWithin(Point p, Projection projection) {
		if( projection == null ) {
			Log.e("", "projection was null...");
			return false;
		}
		
		LatLngBounds bounds = projection.getVisibleRegion().latLngBounds;
		LatLng l = projection.fromScreenLocation( p );
		
		return bounds.contains( l );
	}
	
	public static NClusterContainerV2 recalculatePOI(int mergeGridDensity,int mergeDistance, Projection projection,NMarkerList mapOverlay, int viewHeight, int viewWidth) throws Exception {
		int densityX = mergeGridDensity;
		int densityY = mergeGridDensity;

		if(mergeGridDensity==0)
			throw new NullPointerException("Density = 0, might be cause of NScreenParameters is not initialized");
		

		NClusterContainerV2 tempClusterContainer = new NClusterContainerV2();
		
		List<List<List<NMarkerV2>>> grid = new ArrayList<List<List<NMarkerV2>>>(
				densityX); 

		for(int i = 0; i<densityX; i++){
			ArrayList<List<NMarkerV2>> column = new ArrayList<List<NMarkerV2>>(densityY);
			for(int j = 0; j < densityY; j++){
				column.add(new ArrayList<NMarkerV2>());
			}
			grid.add(column);
		}

		for (int a = 0 ; a < mapOverlay.getList().size() ; a++) {
			int binX;
			int binY;

			if(projection == null || mapOverlay == null)
				return tempClusterContainer;
			
			Point p = projection.toScreenLocation( mapOverlay.getList().get(a).getPosition() );

			if( p.x < viewWidth && p.x > 0 && p.y < viewHeight && p.y > 0 && NClusterControllerV2.isWithin(p, projection)) {
				double fractionX = ((double)p.x / (double)viewWidth);
				binX = (int) (Math.floor(densityX * fractionX));
				double fractionY = ((double)p.y / (double)viewHeight);
				binY = (int) (Math.floor(densityX * fractionY));
				
				grid.get(binX).get(binY).add(mapOverlay.getList().get(a));
			}
		}


		for (int i = 0; i < densityX; i++) {
			for (int j = 0; j < densityY; j++) {
				List<NMarkerV2> markerList = grid.get(i).get(j);
				if (markerList.size() > 1) {
					NMarkerList tempList = new NMarkerList();
					ArrayList<String> temp = new ArrayList<String>();
					for(int a = 0 ; a < markerList.size() ; a++ ){
						temp.add(markerList.get(a).getTitle());
						tempList.getList().add(markerList.get(a));
					}
					tempList.setCenter();
					tempList.setCenterPoint(projection);
					tempClusterContainer.getGroupMarkerList().add(tempList);
				} 
				else {
					for(int a = 0 ; a < markerList.size() ; a++ ){
						tempClusterContainer.getSingleMarkerList().getList().add(markerList.get(a));
					}            	  
				}
			}
		}	
		try{
//			//Disabled these to get more performance
			tempClusterContainer = NClusterControllerV2.mergePointsTooClose(tempClusterContainer, projection,mergeDistance);
			tempClusterContainer = NClusterControllerV2.mergePointsToClustersTooClose(tempClusterContainer,projection,mergeDistance);
			tempClusterContainer= NClusterControllerV2.mergeClustersTooClose(tempClusterContainer,projection,mergeDistance);
		}
		catch(Exception e){
			Log.e("Merging error", e.toString());
		}

		return tempClusterContainer;
	}
}

