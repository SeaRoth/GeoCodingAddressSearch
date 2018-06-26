/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package acgmaps.com.searoth.geocodingaddresssearch.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.database.Observable;
import android.databinding.ObservableList;
import android.os.AsyncTask;

import java.util.List;

import acgmaps.com.searoth.geocodingaddresssearch.DataRepository;
import acgmaps.com.searoth.geocodingaddresssearch.GeoMapApp;
import acgmaps.com.searoth.geocodingaddresssearch.db.entity.ResultEntity;

public class ResultListViewModel extends AndroidViewModel {

    private final MediatorLiveData<List<ResultEntity>> mObservableResults;
    private final LiveData<List<ResultEntity>> mCurrentList;
    private DataRepository repository;

    public ResultListViewModel(Application application) {
        super(application);

        repository = ((GeoMapApp) application).getRepository();

        mObservableResults = new MediatorLiveData<>();
        mObservableResults.setValue(null);

        LiveData<List<ResultEntity>> products = ((GeoMapApp) application).getRepository()
                .getResults();

        mObservableResults.addSource(products, mObservableResults::setValue);

        mCurrentList = repository.getResults();
    }

    public LiveData<List<ResultEntity>> getmCurrentList() {
        return mCurrentList;
    }

    public void setmCurrentList(List<ResultEntity> mCurrentList) {

    }

    public LiveData<List<ResultEntity>> getResults() {
        return mObservableResults;
    }

    public void addNewItemToDatabase(ResultEntity listItem){
        new AddItemTask().execute(listItem);
    }

    private class AddItemTask extends AsyncTask<ResultEntity, Void, Void> {

        @Override
        protected Void doInBackground(ResultEntity... item) {
            repository.insertNewItem(item[0]);
            return null;
        }
    }

    public void deleteAllFromDb(){
        repository.deleteAllItems();
    }


    public void addRandomItems(){
        ResultEntity resultEntity = new ResultEntity();
        resultEntity.setPlaceId("123");
        resultEntity.setAddress("21817 ne 20th way");
        resultEntity.set
    }

}
