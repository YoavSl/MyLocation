package com.example.mylocation.Screens.Map.MvpViews;

import android.view.View;

import com.example.mylocation.Screens.Common.MvpViews.ViewMvp;


public interface MapFragmentView extends ViewMvp {

    interface SearchListener {
        void geoLocateByName(String searchString);
    }

    void searchExecuted(String address);

    void setSearchListener(SearchListener listener);

    void setMyLocationButtonPosition(View mapView);

    void initializeCurrentLocationUI();

    void displayMyCurrentLocation(String city, String street);
}
