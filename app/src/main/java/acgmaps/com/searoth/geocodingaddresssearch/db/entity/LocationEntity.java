package acgmaps.com.searoth.geocodingaddresssearch.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import acgmaps.com.searoth.geocodingaddresssearch.model.LocationModel;
import se.arbitur.geocoding.Result;

@Entity(tableName = "locationentity")
public class LocationEntity implements LocationModel {
    @PrimaryKey
    @NonNull
    private String placeId;
    private String shortName;
    private String address;
    private Double latitude;
    private Double longitude;


    @Override
    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String s){
        this.placeId = s;
    }

    @Override
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    @Override
    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public LocationEntity() {
    }

    public LocationEntity(@NonNull String placeId, String name, String address, Double latitude, Double longitude) {
        this.placeId = placeId;
        this.shortName = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LocationEntity(Result result){
        this.placeId = result.getPlaceId();
        this.shortName = result.getAddressComponents()[0].getShortName();
        this.address = result.getFormattedAddress();
        this.latitude = result.getGeometry().getLocation().getLatitude();
        this.longitude = result.getGeometry().getLocation().getLongitude();
    }

    public LocationEntity(LocationModel product) {
        this.placeId = product.getPlaceId();
        this.shortName = product.getShortName();
        this.address = product.getAddress();
        this.latitude = product.getLatitude();
        this.longitude = product.getLongitude();
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}
