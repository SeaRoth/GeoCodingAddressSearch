package acgmaps.com.searoth.geocodingaddresssearch.model;

public interface LocationModel {
    String getShortName();
    String getPlaceId();
    String getAddress();
    Double getLatitude();
    Double getLongitude();
}
