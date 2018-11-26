package com.example.mylocation.Screens.Common.Presenters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;


public abstract class BaseFragment extends Fragment {
    private static final String TAG = "BaseFragment";
    private AbstractFragmentCallback mCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (AbstractFragmentCallback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement " + AbstractFragmentCallback.class.getCanonicalName());
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (AbstractFragmentCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement " + AbstractFragmentCallback.class.getCanonicalName());
        }
    }

    public void replaceFragment(Class<? extends Fragment> claz, boolean addToBackStack,
                                Bundle args) {
        mCallback.replaceFragment(claz, addToBackStack, args);
    }

    public interface AbstractFragmentCallback {
        void replaceFragment(Class<? extends Fragment> claz, boolean addToBackStack,
                             Bundle args);

    }

    /*
    protected MainThreadPoster getMainThreadPoster() {
        return ((MvcTutorialApplication)getActivity().getApplication()).getMainThreadPoster();
    }


    protected BackgroundThreadPoster getBackgroundThreadPoster() {
        return ((MvcTutorialApplication)getActivity().getApplication()).getBackgroundThreadPoster();
    }
    */
}