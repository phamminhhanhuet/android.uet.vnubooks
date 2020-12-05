package com.uet.android.mouspad.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.maps.android.PolyUtil;
import com.uet.android.mouspad.Model.DetailLocation;
import com.uet.android.mouspad.Model.RepoLocation;
import com.uet.android.mouspad.Model.Story;
import com.uet.android.mouspad.Model.User;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Service.APIService;
import com.uet.android.mouspad.Service.Map.Direction.DirectionRoot;
import com.uet.android.mouspad.Utils.ActivityUtils;
import com.uet.android.mouspad.Utils.Constants;
import com.uet.android.mouspad.Utils.WidgetsUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback , GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener{

    private FirebaseFirestore mFirebaseFirestore;
    private SearchView mSearchView;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Marker mCurrentMarker;
    private ProgressDialog mProgressDialog;
    private Retrofit mRetrofit;
    private DetailLocation mCurrentLocation;
    private DetailLocation mYourLocation;
    private RepoLocation mRepoLocation;
    private User mUser;
    private Story mStory;
    private String mUserId ;
    private int mRequestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initView();
        initData();
        initRetrofit();
        mProgressDialog= WidgetsUtils.initProgressDialog(this, mProgressDialog);
        ActivityUtils.initRetrofit(mRetrofit, Constants.GOOGLE_MAP_BASE_URL);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mCurrentLocation != null){
            Map<String,Object> mapData = new HashMap<>();
            GeoPoint geoPoint = new GeoPoint(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            mapData.put("contain", geoPoint);
            if(description.isEmpty() || description.equals("") || description == null) {
                mapData.put("description", mRepoLocation.getDescription());
            } else mapData.put("description", description);
            if(mRequestCode == Constants.MAP_REQUEST_USER_CODE){
                mFirebaseFirestore.collection("locations").document(mUser.getUser_id()).set(mapData);
            } else if(mRequestCode == Constants.MAP_REQUEST_STORY_CODE){
                mFirebaseFirestore.collection("locations").document(mStory.getStory_id()).set(mapData);
            }
            Intent intent = new Intent();
            setResult(RESULT_OK , intent);
            finish();
        }
    }

    private String description = "";
    private void initView() {
        mSearchView = findViewById(R.id.searchViewMap);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String keyword) {
                description = mSearchView.getQuery().toString();
                requestSearch();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void initData() {
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mRequestCode = getIntent().getIntExtra(Constants.MAP_REQUEST, 0);
        if(mRequestCode == Constants.MAP_REQUEST_USER_CODE){
            mUser = (User) getIntent().getSerializableExtra(Constants.USER_ID);
            mFirebaseFirestore.collection("locations/").document(mUser.getUser_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    GeoPoint geoPoint = task.getResult().getGeoPoint("contain");
//                    Log.d("Geopoint", geoPoint.getLatitude() + " " + geoPoint.getLongitude());
//                    mCurrentLocation = new DetailLocation();
//                    mCurrentLocation.setLatitude(geoPoint.getLatitude());
//                    mCurrentLocation.setLongitude(geoPoint.getLongitude());
                    mRepoLocation = task.getResult().toObject(RepoLocation.class);
                    mCurrentLocation = new DetailLocation(mRepoLocation.getContain().getLatitude(), mRepoLocation.getContain().getLongitude());
                }
            });
        } else if(mRequestCode == Constants.MAP_REQUEST_STORY_CODE){
            mStory = (Story) getIntent().getSerializableExtra(Constants.STORY_INDEX);
            mFirebaseFirestore.collection("locations/").document(mStory.getUser_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    GeoPoint geoPoint = task.getResult().getGeoPoint("contain");
//                    Log.d("Geopoint", geoPoint.getLatitude() + " " + geoPoint.getLongitude());
//                    mCurrentLocation = new DetailLocation();
//                    mCurrentLocation.setLatitude(geoPoint.getLatitude());
//                    mCurrentLocation.setLongitude(geoPoint.getLongitude());
                    mRepoLocation = task.getResult().toObject(RepoLocation.class);
                    mCurrentLocation = new DetailLocation(mRepoLocation.getContain().getLatitude(), mRepoLocation.getContain().getLongitude());
                }
            });
        }
        //mRepoLocation = (RepoLocation) getIntent().getSerializableExtra(Constants.GOOGLE_MAP_BASE_URL);
    }

    private void initRetrofit (){
        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constants.GOOGLE_MAP_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private void requestSearch(){
        String user_location = mSearchView.getQuery().toString();
        List<Address> addressList = null;
        if(user_location != null || !user_location.equals("")){
            Geocoder geocoder = new Geocoder(MapsActivity.this);
            try{
                addressList = geocoder.getFromLocationName(user_location, 1);
            }catch (Exception e){
            }
            Address address = addressList.get(0);
            mCurrentLocation.setLatitude(address.getLatitude());
            mCurrentLocation.setLongitude(address.getLongitude());
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.clear();
            if(mRequestCode == Constants.MAP_REQUEST_USER_CODE){
                mMap.addMarker(new MarkerOptions().position(latLng).title(mUser.getAccount()));
            } else if(mRequestCode == Constants.MAP_REQUEST_STORY_CODE){
                mMap.addMarker(new MarkerOptions().position(latLng).title(mStory.getTitle()));
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
    }

    private void buildApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void displayPlacesOnMap(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.dismiss();
                LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                if(mRequestCode == Constants.MAP_REQUEST_USER_CODE){
                    mMap.addMarker(new MarkerOptions().position(latLng).title(mUser.getAccount()));
                } else if(mRequestCode == Constants.MAP_REQUEST_STORY_CODE){
                    mMap.addMarker(new MarkerOptions().position(latLng).title(mStory.getTitle()));
                }
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                mMap.setMaxZoomPreference(17); }
        }, 3000);
    }

    private void getDirection(LatLng orgin, LatLng destination  ){
        APIService serviceAPI = mRetrofit.create(APIService.class);
        String originAddress = String.valueOf(orgin.latitude)+ "," + String.valueOf(orgin.longitude);
        String desAdrress = String.valueOf(destination.latitude) +"," +String.valueOf(destination.longitude);
        Call<DirectionRoot> call = serviceAPI.getDirection(originAddress, desAdrress);
        call.enqueue(new Callback<DirectionRoot>() {
            @Override
            public void onResponse(Call<DirectionRoot> call, Response<DirectionRoot> response) {
                DirectionRoot directionRoot = response.body();
                String polylines = directionRoot.getRoutes().get(0).getOverview_polyline().getPoints();
                List<LatLng> decodePath = PolyUtil.decode(polylines);
                mMap.addPolygon(new PolygonOptions().addAll(decodePath));
            }
            @Override
            public void onFailure(Call<DirectionRoot> call, Throwable t) {

            }
        });

    }

    private void getLocation(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, new LocationListener() {
                @Override
                public void onLocationChanged(android.location.Location location) {
                   mYourLocation = new DetailLocation(location.getLatitude(), location.getLongitude());
                    if(mGoogleApiClient !=null){
                        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                    }
                }
            });
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        displayPlacesOnMap();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            buildApiClient();
            mMap.setMyLocationEnabled(true);
        } else {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.GPS_PERMISSION_REQUEST_CODE);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case Constants.GPS_PERMISSION_REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==
                            PackageManager.PERMISSION_GRANTED){
                        if(mGoogleApiClient == null){
                            buildApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    } else {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }    }
    @Override
    protected void onDestroy() {
        if(mCurrentLocation != null){
            Map<String,Object> mapData = new HashMap<>();
            GeoPoint geoPoint = new GeoPoint(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
           // String description = mSearchView.getQuery().toString();
            mapData.put("contain", geoPoint);
            if(description.isEmpty() || description.equals("") || description == null) {
                mapData.put("description", mRepoLocation.getDescription());
            } else mapData.put("description", description);
            if(mRequestCode == Constants.MAP_REQUEST_USER_CODE){
                mFirebaseFirestore.collection("locations").document(mUser.getUser_id()).set(mapData);
            } else if(mRequestCode == Constants.MAP_REQUEST_STORY_CODE){
                mFirebaseFirestore.collection("locations").document(mStory.getStory_id()).set(mapData);
            }
        }
        super.onDestroy();

    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        getLocation();
    }
    @Override
    public void onConnectionSuspended(int i) {

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        LatLng currLatLng = new LatLng(mYourLocation.getLatitude(), mYourLocation.getLongitude());
        getDirection(currLatLng,marker.getPosition());
        return false;
    }
}