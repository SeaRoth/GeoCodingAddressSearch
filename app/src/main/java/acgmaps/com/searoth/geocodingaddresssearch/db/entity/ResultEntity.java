package acgmaps.com.searoth.geocodingaddresssearch.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import acgmaps.com.searoth.geocodingaddresssearch.model.OurResult;

@Entity(tableName = "results")
public class ResultEntity implements OurResult {
    @PrimaryKey
    @NonNull
    private String placeId;
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

    public ResultEntity() {
    }

    public ResultEntity(String placeId, String address, Double latitude, Double longitude) {
        this.placeId = placeId;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public ResultEntity(OurResult product) {
        this.placeId = product.getPlaceId();
        this.address = product.getAddress();
        this.latitude = product.getLatitude();
        this.longitude = product.getLongitude();
    }
}
