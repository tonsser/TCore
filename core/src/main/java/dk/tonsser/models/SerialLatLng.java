package dk.tonsser.models;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class SerialLatLng implements Serializable {

    private double lat;
    private double lng;

    public SerialLatLng(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public SerialLatLng(LatLng latLng) {
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
