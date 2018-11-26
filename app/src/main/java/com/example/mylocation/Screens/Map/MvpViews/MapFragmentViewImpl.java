package com.example.mylocation.Screens.Map.MvpViews;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mylocation.Adapters.PlaceAutoCompleteAdapter;
import com.example.mylocation.R;


public class MapFragmentViewImpl implements MapFragmentView {
    private static final String TAG = "MapFragmentViewImpl";
    private final Context mContext;
    private View mRootView;
    private AutoCompleteTextView searchACTV;
    private SearchListener mSearchListener;
    private FrameLayout myCurrentLocationFL;
    private TextView myCurrentCityTV, myCurrentStreetTV;

    public MapFragmentViewImpl(LayoutInflater inflater, ViewGroup container) {
        mContext = inflater.getContext();
        mRootView = inflater.inflate(R.layout.fragment_map, container, false);

        initializeSearch();
    }

    private void initializeSearch() {
        searchACTV = mRootView.findViewById(R.id.searchACTV);
        confMapSearch();
    }

    private void confMapSearch() {
        PlaceAutoCompleteAdapter placeAutoCompleteAdapter = new PlaceAutoCompleteAdapter(mContext);
        searchACTV.setAdapter(placeAutoCompleteAdapter);

        searchACTV.setThreshold(1);   //Minimum number of 1 characters for displaying the drop down list

        searchACTV.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    if (mSearchListener != null)
                        mSearchListener.geoLocateByName(searchACTV.getText().toString());
                }
                return false;
            }
        });

        searchACTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mSearchListener != null) {
                    mSearchListener.geoLocateByName(searchACTV.getText().toString());
                    hideSoftKeyboard();
                }
            }
        });
    }

    @Override
    public void searchExecuted(String address) {
        hideSoftKeyboard();
        searchACTV.setText(address);
        searchACTV.dismissDropDown();
    }

    @Override
    public void setSearchListener(SearchListener listener) {
        mSearchListener = listener;
    }

    @Override
    public void setMyLocationButtonPosition(View mapView) {
        //Align "My Location" button to bottom right
        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                locationButton.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        layoutParams.setMargins(0, 0, 0, 300);
    }

    @Override
    public void initializeCurrentLocationUI() {
        myCurrentLocationFL = mRootView.findViewById(R.id.myCurrentLocationFL);
        myCurrentCityTV = mRootView.findViewById(R.id.myCurrentCityTV);
        myCurrentStreetTV = mRootView.findViewById(R.id.myCurrentStreetTV);

        myCurrentLocationFL.setVisibility(View.VISIBLE);
    }

    @Override
    public void displayMyCurrentLocation(String city, String street) {
        myCurrentCityTV.setText(city);
        myCurrentStreetTV.setText(street);
    }

    @Override
    public View getRootView() {
        return mRootView;
    }

    @Override
    public Bundle getViewState() {
        return null;
    }

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(searchACTV.getWindowToken(), 0);
        }
    }
}
