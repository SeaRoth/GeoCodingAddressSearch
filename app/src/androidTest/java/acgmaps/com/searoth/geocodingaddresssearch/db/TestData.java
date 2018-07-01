package acgmaps.com.searoth.geocodingaddresssearch.db;


import java.util.Arrays;
import java.util.Date;
import java.util.List;

import acgmaps.com.searoth.geocodingaddresssearch.db.entity.LocationEntity;

/**
 * Utility class that holds values to be used for testing.
 */
public class TestData {

    static final LocationEntity PRODUCT_ENTITY = new LocationEntity("ddff", "name", "desc",
            3.0,3.0);
    static final LocationEntity PRODUCT_ENTITY2 = new LocationEntity("123", "name", "desc",
            34.0,34.0);

    static final List<LocationEntity> PRODUCTS = Arrays.asList(PRODUCT_ENTITY, PRODUCT_ENTITY2);




}
