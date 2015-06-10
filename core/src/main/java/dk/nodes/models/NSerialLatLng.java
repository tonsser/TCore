package dk.nodes.models;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

import dk.nodes.map.v2.NLocationCalculatorV2;

public class NSerialLatLng implements Serializable {

	private double lat;
	private double lng;

	public NSerialLatLng(double lat, double lng){
		this.lat = lat ;
		this.lng = lng;
	}
	
	public NSerialLatLng(LatLng latLng){
		setLatLng(latLng);
	}
	public LatLng getLatLng(){
		return NLocationCalculatorV2.toLatLng(lat, lng);
	}
	
	public void setLatLng(LatLng latLng){
		this.lat = latLng.latitude;
		this.lng = latLng.longitude;
	}
}
