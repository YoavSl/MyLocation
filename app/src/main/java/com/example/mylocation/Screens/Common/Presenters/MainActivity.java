package com.example.mylocation.Screens.Common.Presenters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.example.mylocation.R;
import com.example.mylocation.Screens.Common.MvpViews.RootViewMvpImpl;
import com.example.mylocation.Screens.Map.Presenters.MapFragment;


public class MainActivity extends AppCompatActivity implements BaseFragment.AbstractFragmentCallback {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RootViewMvpImpl mViewMVP = new RootViewMvpImpl(this, null);
        setContentView(mViewMVP.getRootView());

        //Show the default fragment if the application is not restored
        if (savedInstanceState == null)
            replaceFragment(MapFragment.class, false, null);
    }

    @Override
    public void replaceFragment(Class<? extends Fragment> claz, boolean addToBackStack, Bundle args) {
        Fragment newFragment;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        try {
            newFragment = claz.newInstance();
            if (args != null) newFragment.setArguments(args);
        } catch (InstantiationException e) {
            e.printStackTrace();
            return;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return;
        }

        if (addToBackStack) {
            ft.addToBackStack(null);
        }

        ft.replace(R.id.fragmentContainerFL, newFragment, claz.getClass().getSimpleName());
        ft.commit();
    }
}