package com.example.mylocation.Screens.Common.MvpViews;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mylocation.R;


public class RootViewMvpImpl implements ViewMvp {
    private static final String TAG = "RootViewMvpImpl";
    private View mRootView;

    public RootViewMvpImpl(Context context, ViewGroup container) {
        mRootView = LayoutInflater.from(context).inflate(R.layout.mvp_root_view, container);

        initialize();
    }

    private void initialize() {
        Toolbar toolbar = mRootView.findViewById(R.id.appToolbar);
        //setSupportActionBar(toolbar);
    }

    @Override
    public View getRootView() {
        return mRootView;
    }

    @Override
    public Bundle getViewState() {
        return null;   // This MVP view has no state that could be retrieved
    }
}
