package acgmaps.com.searoth.geocodingaddresssearch.ui;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.support.v4.app.Fragment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import acgmaps.com.searoth.geocodingaddresssearch.R;
import acgmaps.com.searoth.geocodingaddresssearch.db.entity.ResultEntity;
import acgmaps.com.searoth.geocodingaddresssearch.model.OurResult;
import acgmaps.com.searoth.geocodingaddresssearch.viewmodel.ResultListViewModel;
import okhttp3.logging.HttpLoggingInterceptor;
import se.arbitur.geocoding.Callback;
import se.arbitur.geocoding.Geocoder;
import se.arbitur.geocoding.Geocoding;
import se.arbitur.geocoding.Response;
import acgmaps.com.searoth.geocodingaddresssearch.databinding.MyMapFragmentBinding;
import se.arbitur.geocoding.Result;

public class MapFragment extends Fragment implements OnMapReadyCallback, MenuInterface {

    public static final String TAG = "MapFrag";
    private ResultAdapter mResultAdapter;
    private MyMapFragmentBinding mBinding;

    private GoogleMap mMap;

    private RecyclerView.LayoutManager mLayoutManager;
    private Response lastNetworkResponse;
    private ResultEntity lastClickedMarker;
    boolean isMarkerSelected = false;
    boolean doesMarkerExistInDb = false;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.my_map_fragment, container, false);
        mResultAdapter = new ResultAdapter(mResultClickCallback);
        mBinding.resultsList.setAdapter(mResultAdapter);

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
                if(!s.equals(""))
                    addressSearch(s);
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

    private ResultListViewModel viewModel;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(ResultListViewModel.class);
        subscribeUi(viewModel);
        Geocoder.loggingLevel = HttpLoggingInterceptor.Level.BASIC;
    }

    private void subscribeUi(ResultListViewModel viewModel) {
        viewModel.getResults().observe(this, new Observer<List<ResultEntity>>() {
            @Override
            public void onChanged(@Nullable List<ResultEntity> searchResults) {
                if (searchResults != null) {
                    mBinding.setIsLoading(false);
                    mResultAdapter.setProductList(searchResults);
                } else {
                    mBinding.setIsLoading(true);
                }
                mBinding.executePendingBindings();
            }
        });
    }

    private final LocationClickCallback mResultClickCallback = new LocationClickCallback() {
        @Override
        public void onClick(OurResult product) {
            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                isMarkerSelected = true;
                mBinding.resultsList.setVisibility(View.GONE);
                lastClickedMarker = convert(product);
                doesMarkerExistInDb = checkIfMarkerExistsInDatabase(lastClickedMarker.getPlaceId());
                setGoogleMap(product.getAddress(), product.getLatitude(), product.getLongitude(), product.getPlaceId());
                getActivity().invalidateOptionsMenu();
            }
        }
    };

    private ResultEntity convert(OurResult ourResult){
        ResultEntity resultEntity = new ResultEntity();
        String placeId = ourResult.getPlaceId();
        String address = ourResult.getAddress();
        Double latitude = ourResult.getLatitude();
        Double longitude = ourResult.getLongitude();
        resultEntity.setPlaceId(placeId);
        resultEntity.setAddress(address);
        resultEntity.setLatitude(latitude);
        resultEntity.setLongitude(longitude);
        return resultEntity;
    }

    private void setGoogleMap(String title, Double lat, Double lon, String snippet){
        mMap.clear();
        LatLng sydney = new LatLng(lat, lon);
        mMap.addMarker(
                new MarkerOptions()
                .position(sydney)
                .title(title)
                .snippet(snippet)
        );
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                isMarkerSelected = true;
                doesMarkerExistInDb = checkIfMarkerExistsInDatabase(marker.getId());
                if(doesMarkerExistInDb)
                    getActivity().invalidateOptionsMenu();
                return false;
            }
        });

        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    boolean checkIfMarkerExistsInDatabase(String placeId){
        List<ResultEntity> mList = viewModel.getResults().getValue();

        assert mList != null;
        for(ResultEntity entity : mList){
            if(entity.getPlaceId().equals(placeId))
                return true;
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(12.0f);
        mMap.setMaxZoomPreference(14.0f);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                isMarkerSelected = false;
                mBinding.resultsList.setVisibility(View.GONE);
                mBinding.clSearch.setVisibility(View.GONE);
            }
        });

        setGoogleMap("Sydney!", -34.0, 151.0, null);
    }

    private void setSearchResults(){
        mBinding.resultsList.setVisibility(View.VISIBLE);

        List<ResultEntity> mList = new ArrayList<>();
        for(Result result : lastNetworkResponse.getResults()){
            ResultEntity resultEntity = new ResultEntity();
            String placeId = result.getPlaceId();
            String address = result.getFormattedAddress();
            Double latitude = result.getGeometry().getLocation().getLatitude();
            Double longitude = result.getGeometry().getLocation().getLongitude();

            resultEntity.setPlaceId(placeId);
            resultEntity.setAddress(address);
            resultEntity.setLatitude(latitude);
            resultEntity.setLongitude(longitude);
            mList.add(resultEntity);
        }
        mResultAdapter.setProductList(mList);
        mBinding.resultsList.setAdapter(mResultAdapter);
    }

    private void setEmptySearchResults(){
        mBinding.resultsList.setVisibility(View.VISIBLE);
        mBinding.tvNoResults.setVisibility(View.VISIBLE);
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
                Log.d(TAG, "   Formatted address: " + result.getFormattedAddress());
                Log.d(TAG, "   Place ID: " + result.getPlaceId());
                Log.d(TAG, "   Location: " + result.getGeometry().getLocation());
                Log.d(TAG, "       Location type: " + result.getGeometry().getLocationType());
                Log.d(TAG, "       SouthWest: " + result.getGeometry().getViewport().getSouthWest());
                Log.d(TAG, "       NorthEast: " + result.getGeometry().getViewport().getNorthEast());
                Log.d(TAG, "   Types:");
                for (int i = 0; i < result.getAddressTypes().length; i++)
                    Log.d(TAG, "       " + result.getAddressTypes()[i]);
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
        }else if(!doesMarkerExistInDb) {
            menu.findItem(R.id.action_save).setVisible(true);
            menu.findItem(R.id.action_delete).setVisible(false);
        }else if(doesMarkerExistInDb){
            menu.findItem(R.id.action_save).setVisible(false);
            menu.findItem(R.id.action_delete).setVisible(true);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void searchClicked() {
        if(mBinding.clSearch.getVisibility() == View.GONE) {
            mBinding.clSearch.setVisibility(View.VISIBLE);
        }else
            mBinding.clSearch.setVisibility(View.GONE);
    }

    @Override
    public void deleteClicked() {
        viewModel.removeOneItemFromDatabase(lastClickedMarker.getPlaceId());
    }

    @Override
    public void saveClicked() {
        viewModel.addNewItemToDatabase(lastClickedMarker);
        doesMarkerExistInDb = true;
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void deleteAllClicked() {
        viewModel.deleteAllFromDb();
    }

    @Override
    public void populateClicked() {
        viewModel.addRandomItems();
    }

    @Override
    public void showFavoritesClicked() {
        List<ResultEntity> list = viewModel.getResults().getValue();
        mResultAdapter.setProductList(list);
        mBinding.resultsList.setAdapter(mResultAdapter);
        mBinding.resultsList.setVisibility(View.VISIBLE);
    }

    private void enableSearch(boolean yesNo){
        mBinding.btnSearch.setEnabled(yesNo);
    }

    private void addressSearch(String query) {
        enableSearch(false);
        new Geocoding(query, getString(R.string.google_maps_key))
                .fetch(geoCallback);
    }
}
