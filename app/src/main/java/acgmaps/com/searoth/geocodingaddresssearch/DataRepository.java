package acgmaps.com.searoth.geocodingaddresssearch;



import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import java.util.List;

import acgmaps.com.searoth.geocodingaddresssearch.db.AppDatabase;
import acgmaps.com.searoth.geocodingaddresssearch.db.entity.ResultEntity;

/**
 * Repository handling the work with products and comments.
 */
public class DataRepository {

    private static DataRepository sInstance;

    private final AppDatabase mDatabase;
    private MediatorLiveData<List<ResultEntity>> mObservableProducts;

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

    /**
     * Get the list of products from the database and get notified when the data changes.
     */
    public LiveData<List<ResultEntity>> getResults() {
        return mObservableProducts;
    }

    public LiveData<ResultEntity> loadResult(final int productId) {
        return mDatabase.resultDao().loadProduct(productId);
    }

    public void insertNewItem(ResultEntity resultEntity){
        mDatabase.resultDao().insertResult(resultEntity);
    }

    public void removeOneItem(String placeId){
        mDatabase.resultDao().deleteLocation(placeId);
    }

    public void deleteAllItems(){
        mDatabase.resultDao().deleteAll();
    }

    public void insertAll(List<ResultEntity> resultEntities){
        mDatabase.resultDao().insertAll(resultEntities);
    }


}
