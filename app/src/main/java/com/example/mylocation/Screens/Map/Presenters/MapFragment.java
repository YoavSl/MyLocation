package com.example.mylocation.Screens.Map.Presenters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mylocation.R;
import com.example.mylocation.Screens.Common.Presenters.BaseFragment;
import com.example.mylocation.Screens.Map.MvpViews.MapFragmentView;
import com.example.mylocation.Screens.Map.MvpViews.MapFragmentViewImpl;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MapFragment extends BaseFragment implements MapFragmentView.SearchListener,
        OnMapReadyCallback {
    private static final String TAG = "MapFragment";
    private static final int LOCATION_MIN_UPDATE_TIME = 1000;
    private static final int LOCATION_MIN_UPDATE_DISTANCE = 4;
    private static final int REQUEST_LOCATION_CODE = 99;
    private static final int DEFAULT_ZOOM = 15;
    private static final double TEL_AVIV_LAT = 32.08088;
    private static final double TEL_AVIV_LNG = 34.78057;

    private MapFragmentViewImpl mViewMvp;
    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private boolean usingLocationManager, locationManagerEnabled;
    private Marker userMarker = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mViewMvp = new MapFragmentViewImpl(inflater, container);
        mViewMvp.setSearchListener(this);

        initializeMap();

        return mViewMvp.getRootView();
    }

    private void initializeMap() {
        mMapFragment = SupportMapFragment.newInstance();
        mMapFragment.getMapAsync(this);

        getChildFragmentManager().beginTransaction().replace(R.id.mapContainerFL, mMapFragment).commit();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setBuildingsEnabled(true);

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);

        checkForLocationPermission();

        //Set Custom InfoWindow Adapter
        //CustomInfoWindowAdapter customInfoWindowAdapter = new CustomInfoWindowAdapter(this);
        //mMap.setInfoWindowAdapter(customInfoWindowAdapter);

        // Add a marker in Toronto, Canada, and move the camera
        //LatLng torontoLatLng = new LatLng(43.6532, -79.3832);
       /* mMap.addMarker(new MarkerOptions()
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.home_flag))
                //.anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(torontoLatLng)
                .title("Toronto's marker"));*/

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng arg0) {
                if(userMarker != null) {
                    userMarker.remove();
                }
                MarkerOptions markerOption = new MarkerOptions();
                markerOption.position(arg0).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                userMarker = mMap.addMarker(new MarkerOptions()
                        .position(arg0)
                        .title("Your marker"));
            }
        });
    }

    @Override
    public void geoLocateByName(String searchString) {
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> addressList = new ArrayList<>();

        try {
            addressList = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException ioException){
            Log.e(TAG, "geoLocateByName, IOException: " + ioException.getMessage());
        }

        if (addressList.size() > 0) {
            Address address = addressList.get(0);
            mViewMvp.searchExecuted(address.getAddressLine(0));

            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
        }
    }

    private void geoLocateByLatLng(Location location) {
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> addressList = new ArrayList<>();

        try {
            addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        }catch (IOException ioException){
            Log.e(TAG, "geoLocateByLatLng, IOException: " + ioException.getMessage());
        }catch (IllegalArgumentException illegalArgumentException) {   //Catch invalid latitude or longitude values
            Log.e(TAG, "geoLocateByLatLng, IllegalArgumentException: " + illegalArgumentException.getMessage());
        }

        if (addressList.size() > 0) {
            Address address = addressList.get(0);
            String city = address.getLocality();
            String street = address.getAddressLine(0).split(",")[0];

            mViewMvp.displayMyCurrentLocation(city, street);
        }
    }

    private void checkForLocationPermission() {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) &&
        (ContextCompat.checkSelfPermission(mViewMvp.getRootView().getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED))
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
        else
            locationPermissionGranted();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case REQUEST_LOCATION_CODE:
                if((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    if (ContextCompat.checkSelfPermission(mViewMvp.getRootView().getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                        locationPermissionGranted();
                }
                else {
                    //Set the camera at the default location (Tel Aviv)
                    LatLng telAvivLatLng = new LatLng(TEL_AVIV_LAT, TEL_AVIV_LNG);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(telAvivLatLng, DEFAULT_ZOOM));
                }
        }
    }

    @SuppressLint("MissingPermission")  //Location permission already granted
    private void locationPermissionGranted() {
        mMap.setMyLocationEnabled(true);

        mViewMvp.setMyLocationButtonPosition(mMapFragment.getView());
        mViewMvp.initializeCurrentLocationUI();

        monitorCurrentLocation();
    }

    @SuppressLint("MissingPermission")  //Location permission already granted
    private void monitorCurrentLocation() {
        locationManager = (LocationManager) mViewMvp.getRootView().getContext().getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_MIN_UPDATE_TIME, LOCATION_MIN_UPDATE_DISTANCE, locationListener);
            Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            usingLocationManager = true;
            locationManagerEnabled = true;

            moveCameraToCurrentPosition(currentLocation);
        }
    }

    private void moveCameraToCurrentPosition(Location currentLocation) {
        LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM));
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            geoLocateByLatLng(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        if (usingLocationManager) {
            locationManager.removeUpdates(locationListener);
            locationManagerEnabled = false;
        }
    }

    @SuppressLint("MissingPermission")   //If usingLocationManager=true, then location permission already granted
    @Override
    public void onResume() {
        super.onResume();
        if ((usingLocationManager) && (!locationManagerEnabled)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_MIN_UPDATE_TIME, LOCATION_MIN_UPDATE_DISTANCE, locationListener);
            locationManagerEnabled = true;
        }
    }
}
