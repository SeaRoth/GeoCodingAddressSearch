package acgmaps.com.searoth.geocodingaddresssearch.ui;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.support.v4.app.Fragment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import acgmaps.com.searoth.geocodingaddresssearch.R;
import acgmaps.com.searoth.geocodingaddresssearch.db.entity.LocationEntity;
import acgmaps.com.searoth.geocodingaddresssearch.model.LocationModel;
import acgmaps.com.searoth.geocodingaddresssearch.viewmodel.LocationViewModel;
import okhttp3.logging.HttpLoggingInterceptor;
import se.arbitur.geocoding.Callback;
import se.arbitur.geocoding.Geocoder;
import se.arbitur.geocoding.Geocoding;
import se.arbitur.geocoding.Response;
import acgmaps.com.searoth.geocodingaddresssearch.databinding.MyMapFragmentBinding;
import se.arbitur.geocoding.Result;

public class MapFragment extends Fragment implements OnMapReadyCallback, MenuInterface {

    public static final String TAG = "MapFrag";
    private ListAdapter mListAdapter;
    private MyMapFragmentBinding mBinding;
    Snackbar snackbar;

    private GoogleMap mMap;
    private Response lastNetworkResponse;
    private LocationEntity lastClickedMarker;
    private String lastSearchQuery = "not set";
    boolean isMarkerSelected = false;
    boolean doesMarkerExistInDb = false;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.my_map_fragment, container, false);
        mListAdapter = new ListAdapter(locationClickCallback);
        mBinding.resultsList.setAdapter(mListAdapter);

        mBinding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(lastNetworkResponse != null)
                    mBinding.resultsList.setVisibility(View.VISIBLE);
                mBinding.tvNoResults.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mBinding.etSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(lastNetworkResponse != null)
                    mBinding.resultsList.setVisibility(View.VISIBLE);
                return false;
            }
        });

        mBinding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = mBinding.etSearch.getText().toString();
                if(s.equals(lastSearchQuery)){
                    setSearchResults();
                }else if(!s.equals("")) {
                    lastSearchQuery = s;
                    addressSearch(s);
                }
            }
        });

        mBinding.resultsList.setHasFixedSize(true);
        mBinding.resultsList.setVisibility(View.GONE);
        mBinding.tvNoResults.setVisibility(View.GONE);
        initMap();
        return mBinding.getRoot();
    }

    private void initMap() {
        SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mMapFragment != null) {
            mMapFragment.getMapAsync(this);
        }
    }

    private LocationViewModel viewModel;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(LocationViewModel.class);
        subscribeUi(viewModel);
        Geocoder.loggingLevel = HttpLoggingInterceptor.Level.BASIC;
    }

    private void subscribeUi(LocationViewModel viewModel) {
        viewModel.getLocations().observe(this, new Observer<List<LocationEntity>>() {

            @Override
            public void onChanged(@Nullable List<LocationEntity> entities) {
                if (entities != null) {
                    mBinding.setIsLoading(false);
                    mListAdapter.setProductList(entities);
                    mBinding.resultsList.setAdapter(mListAdapter);
                    Objects.requireNonNull(getActivity()).invalidateOptionsMenu();
                } else {
                    mBinding.setIsLoading(true);
                }
                mBinding.executePendingBindings();
            }
        });
    }

    private final LocationClickCallback locationClickCallback = new LocationClickCallback() {
        @Override
        public void onClick(LocationModel product) {
            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                String mAddress = product.getAddress();

                if(mAddress != null && mAddress.equals(getString(R.string.show_all_on_map))){
                    isMarkerSelected = false;
                    hideSearch(true);
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    mMap.clear();
                    for(Result result : lastNetworkResponse.getResults()){
                        builder.include(setMarkerAndListener(new LocationEntity(result)));
                    }
                    LatLngBounds bounds = builder.build();
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 25,25,5);
                    mMap.resetMinMaxZoomPreference();
                    mMap.animateCamera(cu);
                }else {
                    isMarkerSelected = true;
                    mBinding.resultsList.setVisibility(View.GONE);
                    lastClickedMarker = new LocationEntity(product);
                    doesMarkerExistInDb = doesMarkerExistInDatabaseLatLng(new LatLng(lastClickedMarker.getLatitude(),lastClickedMarker.getLongitude()));
                    setSingleMarkerOnMap(lastClickedMarker);
                }
                getActivity().invalidateOptionsMenu();
            }
        }
    };

    private void setSingleMarkerOnMap(LocationEntity markerOnMap){
        mMap.clear();
        setMarkerAndListener(markerOnMap);
        mMap.setMinZoomPreference(12.0f);
        mMap.setMaxZoomPreference(14.0f);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(markerOnMap.getLatitude(), markerOnMap.getLongitude())));
    }

    private Map<Marker, LocationEntity> allMarkersMap = new HashMap<Marker, LocationEntity>();

    private LatLng setMarkerAndListener(LocationEntity locationEntity){
        LatLng latLng = new LatLng(locationEntity.getLatitude(), locationEntity.getLongitude());

        MarkerOptions marker = new MarkerOptions()
                .position(latLng)
                .title(getString(R.string.placeholder_marker,
                        locationEntity.getShortName(),
                        String.valueOf(locationEntity.getLatitude()),
                        String.valueOf(locationEntity.getLongitude())));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                lastClickedMarker = allMarkersMap.get(marker);
                isMarkerSelected = true;
                doesMarkerExistInDb = doesMarkerExistInDatabaseLatLng(marker.getPosition());
                getActivity().invalidateOptionsMenu();
                return false;
            }
        });

        Marker m = mMap.addMarker(marker);
        m.setTag(locationEntity);
        allMarkersMap.put(m,locationEntity);
        return marker.getPosition();
    }

    boolean doesMarkerExistInDatabaseLatLng(LatLng latLng){
        List<LocationEntity> mList = viewModel.getLocations().getValue();

        if(mList != null)
            for(LocationEntity entity : mList){
                if(entity.getLatitude().equals(latLng.latitude) &&
                        entity.getLongitude().equals(latLng.longitude))
                    return true;
            }
        return false;
    }

    private LocationEntity returnShowAllOnMap(){
        LocationEntity locationEntity = new LocationEntity();
        locationEntity.setPlaceId("1337");
        locationEntity.setAddress(getString(R.string.show_all_on_map));
        locationEntity.setShortName(getString(R.string.show_all_on_map));
        return locationEntity;
    }

    private void setSearchResults(){
        if(lastNetworkResponse == null || lastNetworkResponse.getResults().length == 0){
            setEmptySearchResults();
            return;
        }

        mBinding.resultsList.setVisibility(View.VISIBLE);

        List<LocationEntity> mList = new ArrayList<>();
        if(lastNetworkResponse.getResults().length > 1){
            mList.add(returnShowAllOnMap());
        }

        for(Result result : lastNetworkResponse.getResults()){
            mList.add(new LocationEntity(result));
        }
        mListAdapter.setProductList(mList);
        mBinding.resultsList.setAdapter(mListAdapter);
    }

    private void setEmptySearchResults(){
        mListAdapter.setProductList(new ArrayList<LocationModel>());
        mBinding.resultsList.setVisibility(View.VISIBLE);
        mBinding.tvNoResults.setVisibility(View.VISIBLE);
    }

    private void hideSearch(boolean tF){
        mBinding.resultsList.setVisibility(tF ? View.GONE : View.VISIBLE);
        mBinding.clSearch.setVisibility(tF ? View.GONE : View.VISIBLE);
        mBinding.tvNoResults.setVisibility(tF ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                isMarkerSelected = false;
                hideSearch(true);
                getActivity().invalidateOptionsMenu();
            }
        });
        LocationEntity locationEntity = new LocationEntity(
                "ChIJIQjAlYiAhYARoh4a6zegMvs",
                "Avenue Code",
                "26 O'Farrell St, San Francisco, CA 94108, USA",
                37.7869368,
                -122.4055448
        );
        setSingleMarkerOnMap(locationEntity);
    }

    Callback geoCallback = new Callback() {
        @Override
        public void onResponse(Response response) {
            lastNetworkResponse = response;
            enableSearch(true);
            setSearchResults();

            Log.d(TAG, "Status code: " + response.getStatus());
            Log.d(TAG, "Responses: " + response.getResults().length);

            for (Result result : response.getResults()) {
                Log.d(TAG, "   Short Name: " + result.getAddressComponents()[0].getShortName());
                Log.d(TAG, "   Place ID:   " + result.getPlaceId());
                Log.d(TAG, "   Address:    " + result.getFormattedAddress());
                Log.d(TAG, "   Latitude:   " + result.getGeometry().getLocation().getLatitude());
                Log.d(TAG, "   Longitude:  " + result.getGeometry().getLocation().getLatitude());
                Log.d(TAG, "               ");
            }
        }

        @Override
        public void onFailed(Response response, IOException exception) {
            setEmptySearchResults();
            lastNetworkResponse = null;
            enableSearch(true);
            if (response != null) Log.e(TAG, (response.getErrorMessage() == null) ? response.getStatus() : response.getErrorMessage());
            else Log.e(TAG, exception.getLocalizedMessage());
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if(!isMarkerSelected){
            menu.findItem(R.id.action_save).setVisible(false);
            menu.findItem(R.id.action_delete).setVisible(false);
        }else {
            menu.findItem(R.id.action_save).setVisible(!doesMarkerExistInDb);
            menu.findItem(R.id.action_delete).setVisible(doesMarkerExistInDb);
        }
        List<LocationEntity> list = viewModel.getLocations().getValue();
        if(list == null || list.size() == 0)
            menu.findItem(R.id.action_view_favorites).setVisible(false);
        else
            menu.findItem(R.id.action_view_favorites).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void menuSearchClicked() {
        if(mBinding.clSearch.getVisibility() == View.GONE) {
            mBinding.clSearch.setVisibility(View.VISIBLE);
        }else {
            mBinding.clSearch.setVisibility(View.GONE);
            mBinding.resultsList.setVisibility(View.GONE);
        }
    }

    @Override
    public void deleteClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    @Override
    public void saveClicked() {
        viewModel.addNewItemToDatabase(lastClickedMarker);
        doesMarkerExistInDb = true;
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void deleteAllClicked() {
        viewModel.removeAllLocations();
        mBinding.resultsList.setVisibility(View.GONE);
        doesMarkerExistInDb = false;
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void populateClicked() {
        viewModel.addRandomLocations();

        snackbar = Snackbar.make(getActivity().findViewById(R.id.myCoordinatorLayout),
                getString(R.string.locations_added), Snackbar.LENGTH_SHORT);
        snackbar.show();

        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void showFavoritesClicked() {
        List<LocationEntity> list = viewModel.getLocations().getValue();

        if(list != null && list.size() > 0) {
            mListAdapter.setProductList(list);
            mBinding.resultsList.setAdapter(mListAdapter);
            if(mBinding.resultsList.getVisibility() == View.VISIBLE)
                mBinding.resultsList.setVisibility(View.GONE);
            else
                mBinding.resultsList.setVisibility(View.VISIBLE);
        }
    }

    private void enableSearch(boolean yesNo){
        mBinding.btnSearch.setEnabled(yesNo);
    }

    private void addressSearch(String query) {
        enableSearch(false);
        new Geocoding(query,getString(R.string.google_maps_geocode_key))
                .fetch(geoCallback);
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    viewModel.removeOneLocationFromDatabase(new LatLng(lastClickedMarker.getLatitude(), lastClickedMarker.getLongitude()));
                    doesMarkerExistInDb = false;
                    getActivity().invalidateOptionsMenu();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };
}


