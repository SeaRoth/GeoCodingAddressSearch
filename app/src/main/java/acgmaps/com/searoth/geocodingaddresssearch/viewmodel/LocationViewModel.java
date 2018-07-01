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
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import acgmaps.com.searoth.geocodingaddresssearch.DataRepository;
import acgmaps.com.searoth.geocodingaddresssearch.GeoMapApp;
import acgmaps.com.searoth.geocodingaddresssearch.db.entity.LocationEntity;

public class LocationViewModel extends AndroidViewModel {

    private final MediatorLiveData<List<LocationEntity>> mLocations;
    private DataRepository repository;

    public LocationViewModel(Application application) {
        super(application);
        repository = ((GeoMapApp) application).getRepository();
        mLocations = new MediatorLiveData<>();
        mLocations.setValue(null);
        LiveData<List<LocationEntity>> products = ((GeoMapApp) application).getRepository()
                .getResults();
        mLocations.addSource(products, mLocations::setValue);
    }

    public LiveData<List<LocationEntity>> getLocations() {
        return mLocations;
    }
    public void setLocations(List<LocationEntity> list){
        mLocations.setValue(list);
    }

    /**
     * ADD ONE ITEM
     *
     * @param listItem
     */
    public void addNewItemToDatabase(LocationEntity listItem){
        new AddLocationTask(repository).execute(listItem);
    }

    private static class AddLocationTask extends AsyncTask<LocationEntity, Void, Void> {
        DataRepository mRepository;
        AddLocationTask(DataRepository mRepository) {
            this.mRepository = mRepository;
        }

        @Override
        protected Void doInBackground(LocationEntity... item) {
            mRepository.insertNewItem(item[0]);
            return null;
        }
    }

    /**
     * DELETE ONE ITEM
     *
     */
    public void removeOneLocationFromDatabase(LatLng latLng){
        new RemoveLocation(repository).execute(latLng.latitude, latLng.longitude);
    }

    private static class RemoveLocation extends AsyncTask<Double, Double, Void>{

        DataRepository mRepository;
        RemoveLocation(DataRepository mRepository) {
            this.mRepository = mRepository;
        }

        @Override
        protected Void doInBackground(Double... doubles) {
            mRepository.removeOneItem(doubles[0], doubles[1]);
            return null;
        }
    }

    /**
     * DELETE ALL
     *
     */
    public void removeAllLocations(){
        new RemoveAllLocations(repository).execute();
    }

    private static class RemoveAllLocations extends AsyncTask<Void, Void, Void> {
        DataRepository mRepository;
        RemoveAllLocations(DataRepository repository) {
            mRepository = repository;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mRepository.deleteAllItems();
            return null;
        }
    }

    /**
     * ADD RANDOM ITEMS
     *
     */
    public void addRandomLocations(){
        LocationEntity entity = new LocationEntity();
        entity.setPlaceId("ChIJP3Sa8ziYEmsRUKgyFmh9AQM");
        entity.setAddress("Sydney AU");
        entity.setShortName("Sydney");
        entity.setLatitude(-34.0);
        entity.setLongitude(151.0);
        new AddLocationTask(repository).execute(entity);

        entity = new LocationEntity();
        entity.setPlaceId("ChIJH7jkCCzm5okRvaq5QdoIGB0");
        entity.setAddress("Springfield, MA, USA");
        entity.setLatitude(42.1014831);
        entity.setLongitude(-72.589811);
        entity.setShortName("Springfield, MA!");
        new AddLocationTask(repository).execute(entity);

        entity = new LocationEntity();
        entity.setPlaceId("1");
        entity.setAddress("Springfield, MA, USA");
        entity.setLatitude(43.1014831);
        entity.setLongitude(-71.589811);
        entity.setShortName("Random place 1");
        new AddLocationTask(repository).execute(entity);

        entity = new LocationEntity();
        entity.setPlaceId("12");
        entity.setAddress("Springfield, MA, USA");
        entity.setLatitude(40.1014831);
        entity.setLongitude(-79.589811);
        entity.setShortName("Random place 2");
        new AddLocationTask(repository).execute(entity);

        entity = new LocationEntity();
        entity.setPlaceId("123");
        entity.setAddress("Springfield, MA, USA");
        entity.setLatitude(32.1014831);
        entity.setLongitude(-32.589811);
        entity.setShortName("Random place 3");
        new AddLocationTask(repository).execute(entity);

        entity = new LocationEntity();
        entity.setPlaceId("1234");
        entity.setAddress("Springfield, MA, USA");
        entity.setLatitude(32.1014831);
        entity.setLongitude(-92.589811);
        entity.setShortName("Random place 4");
        new AddLocationTask(repository).execute(entity);
    }
}
