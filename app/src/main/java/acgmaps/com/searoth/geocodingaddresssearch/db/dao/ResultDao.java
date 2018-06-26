package acgmaps.com.searoth.geocodingaddresssearch.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import acgmaps.com.searoth.geocodingaddresssearch.db.entity.ResultEntity;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface ResultDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ResultEntity> products);

    @Insert(onConflict = REPLACE)
    Long insertResult(ResultEntity listItem);

    @Query("SELECT * FROM results")
    LiveData<List<ResultEntity>> getAll();

    @Query("select * from results where placeId = :productId")
    LiveData<ResultEntity> loadProduct(int productId);

    @Query("DELETE FROM results")
    void deleteAll();
}
