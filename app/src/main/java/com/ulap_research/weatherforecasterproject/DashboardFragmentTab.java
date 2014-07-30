package com.ulap_research.weatherforecasterproject;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by LouieZamora on 7/18/14.
 */
public class DashboardFragmentTab extends Fragment {
    private static final String TAG = "DashboardFragmentTab";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        Log.d(TAG, "createView");
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "create");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "resume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "pause");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "destroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "destroy");
    }
}
