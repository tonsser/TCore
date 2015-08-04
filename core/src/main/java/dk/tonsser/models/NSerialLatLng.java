package dk.tonsser.models;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class NSerialLatLng implements Serializable {

    private double lat;
    private double lng;

    public NSerialLatLng(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public NSerialLatLng(LatLng latLng) {
        setLatLng(latLng);
    }

    public LatLng getLatLng() {
        return new LatLng(lat, lng);
    }

    public void setLatLng(LatLng latLng) {
        this.lat = latLng.latitude;
        this.lng = latLng.longitude;
    }
}
