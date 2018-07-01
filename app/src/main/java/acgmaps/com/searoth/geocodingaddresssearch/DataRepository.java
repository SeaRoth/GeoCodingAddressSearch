package acgmaps.com.searoth.geocodingaddresssearch;



import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import java.util.List;

import acgmaps.com.searoth.geocodingaddresssearch.db.AppDatabase;
import acgmaps.com.searoth.geocodingaddresssearch.db.entity.LocationEntity;

/**
 * Repository handling the work with products and comments.
 */
public class DataRepository {

    private static DataRepository sInstance;

    private final AppDatabase mDatabase;
    private MediatorLiveData<List<LocationEntity>> mObservableProducts;

    private DataRepository(final AppDatabase database) {
        mDatabase = database;
        mObservableProducts = new MediatorLiveData<>();

        mObservableProducts.addSource(mDatabase.resultDao().getAll(),
                productEntities -> {
                    if (mDatabase.getDatabaseCreated().getValue() != null) {
                        mObservableProducts.postValue(productEntities);
                    }
                });
    }

    public static DataRepository getInstance(final AppDatabase database) {
        if (sInstance == null) {
            synchronized (DataRepository.class) {
                if (sInstance == null) {
                    sInstance = new DataRepository(database);
                }
            }
        }
        return sInstance;
    }

    public LiveData<List<LocationEntity>> getResults() {
        return mObservableProducts;
    }

    public LiveData<LocationEntity> loadResult(final String productId) {
        return mDatabase.resultDao().loadProduct(productId);
    }

    public void insertNewItem(LocationEntity locationEntity){
        mDatabase.resultDao().insertResult(locationEntity);
    }

    public void removeOneItem(String placeId){
        mDatabase.resultDao().deleteLocation(placeId);
    }

    public void removeOneItem(Double lat, Double lon){
        mDatabase.resultDao().deleteLocation(lat, lon);
    }

    public void deleteAllItems(){
        mDatabase.resultDao().deleteAll();
    }

    public void insertAll(List<LocationEntity> resultEntities){
        mDatabase.resultDao().insertAll(resultEntities);
    }


}
