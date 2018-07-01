/*
 * Copyright (C) 2017 The Android Open Source Project
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

package acgmaps.com.searoth.geocodingaddresssearch.db;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;



import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import acgmaps.com.searoth.geocodingaddresssearch.LiveDataTestUtil;
import acgmaps.com.searoth.geocodingaddresssearch.db.dao.LocationDao;
import acgmaps.com.searoth.geocodingaddresssearch.db.entity.LocationEntity;

import static acgmaps.com.searoth.geocodingaddresssearch.db.TestData.PRODUCTS;
import static acgmaps.com.searoth.geocodingaddresssearch.db.TestData.PRODUCT_ENTITY;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Test the implementation of {@link LocationDao}
 */
@RunWith(AndroidJUnit4.class)
public class LocationDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase mDatabase;

    private LocationDao mLocationDao;

    @Before
    public void initDb() throws Exception {
        // using an in-memory database because the information stored here disappears when the
        // process is killed
        mDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                AppDatabase.class)
                // allowing main thread queries, just for testing
                .allowMainThreadQueries()
                .build();

        mLocationDao = mDatabase.resultDao();
    }

    @After
    public void closeDb() throws Exception {
        mDatabase.close();
    }

    @Test
    public void getProductsWhenNoProductInserted() throws InterruptedException {
        List<LocationEntity> products = LiveDataTestUtil.getValue(mLocationDao.getAll());

        assertTrue(products.isEmpty());
    }

    @Test
    public void getProductsAfterInserted() throws InterruptedException {
        mLocationDao.insertAll(PRODUCTS);

        List<LocationEntity> products = LiveDataTestUtil.getValue(mLocationDao.getAll());

        assertThat(products.size(), is(PRODUCTS.size()));
    }

    @Test
    public void getProductById() throws InterruptedException {
        mLocationDao.insertAll(PRODUCTS);

        LocationEntity product = LiveDataTestUtil.getValue(mLocationDao.loadProduct
                (PRODUCT_ENTITY.getPlaceId()));

        assertThat(product.getPlaceId(), is(PRODUCT_ENTITY.getPlaceId()));
        assertThat(product.getShortName(), is(PRODUCT_ENTITY.getShortName()));
        assertThat(product.getAddress(), is(PRODUCT_ENTITY.getAddress()));
        assertThat(product.getLatitude(), is(PRODUCT_ENTITY.getLatitude()));
        assertThat(product.getLongitude(), is(PRODUCT_ENTITY.getLongitude()));
    }

}
