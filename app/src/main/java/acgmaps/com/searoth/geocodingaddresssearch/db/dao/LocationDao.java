package acgmaps.com.searoth.geocodingaddresssearch.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import acgmaps.com.searoth.geocodingaddresssearch.db.entity.LocationEntity;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<LocationEntity> products);

    @Insert(onConflict = REPLACE)
    Long insertResult(LocationEntity listItem);

    @Query("SELECT * FROM LocationEntity")
    LiveData<List<LocationEntity>> getAll();

    @Query("select * from LocationEntity where placeId = :productId")
    LiveData<LocationEntity> loadProduct(String productId);

    @Query("DELETE FROM LocationEntity where placeId = :productId")
    void deleteLocation(String productId);

    @Query("DELETE FROM LocationEntity where latitude = :lat AND longitude = :lon")
    void deleteLocation(Double lat, Double lon);

    @Query("DELETE FROM LocationEntity")
    void deleteAll();
}
