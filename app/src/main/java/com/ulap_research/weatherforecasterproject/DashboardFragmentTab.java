package com.ulap_research.weatherforecasterproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ulap_research.weatherforecasterproject.Resources.SharedPrefResources;

import org.json.JSONException;
import org.json.JSONObject;

public class DashboardFragmentTab extends Fragment {
    private static final String TAG = "DashboardFragmentTab";

    private SharedPreferences sharedPref;

    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreflistener;

    // views
    private TextView tvUsername;
    private TextView tvLevel;
    private TextView tvExpRequire;
    private TextView tvCloudPoint;
    private TextView tvRainAmount;
    private ProgressBar levelProgressBar;
    private ListView menuListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        Log.d(TAG, "createView");
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // setup shared preferences
        sharedPref = this.getActivity().getSharedPreferences(SharedPrefResources.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);

        // set OnSharedPreferenceChangeListener listener
        onSharedPreflistener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if (key.equals(SharedPrefResources.PREFERENCE_KEY_JSON_USER_INFO)) {
                    updateDashboard();
                    Log.d(TAG, "SharedPref PREFERENCE_KEY_JSON_USER_INFO has changed");
                }
            }
        };

        // setup views
        tvUsername = (TextView) getView().findViewById(R.id.username);
        tvLevel = (TextView) getView().findViewById(R.id.level);
        tvExpRequire = (TextView) getView().findViewById(R.id.expRequire);
        tvCloudPoint = (TextView) getView().findViewById(R.id.cloudPoint);
        tvRainAmount = (TextView) getView().findViewById(R.id.rainAmount);
        levelProgressBar = (ProgressBar) getView().findViewById(R.id.levelProgressBar);

        // load strings menu
        String[] str = { getString(R.string.nav_menu_upload_data),
                getString(R.string.nav_menu_garden),
                getString(R.string.nav_menu_buy_rain),
                getString(R.string.nav_menu_achievements)};

        // set list view navigation menu
        menuListView = (ListView)getView().findViewById(R.id.list_menu);
        menuListView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, str));
        menuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0
                    , View arg1, int arg2, long arg3) {
                Intent intent;
                switch(arg2) {
                    case 0 :
                        //TODO
                        Toast.makeText(getActivity(), "TODO: upload data", Toast.LENGTH_SHORT).show();
                        break;
                    case 1 :
                        intent = new Intent(getActivity(), MyGardenActivity.class);
                        startActivity(intent);
                        break;
                    case 2 :
                        intent = new Intent(getActivity(), RainShopActivity.class);
                        startActivity(intent);
                        break;
                    case 3 :
                        intent = new Intent(getActivity(), AchievementActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();

        // set onSharedPref changed listener
        sharedPref.registerOnSharedPreferenceChangeListener(onSharedPreflistener);

        // update dashboard using JSON values from shared preferences
        updateDashboard();
    }

    @Override
    public void onPause() {
        super.onPause();

        // unset onSharedPref changed listener
        sharedPref.unregisterOnSharedPreferenceChangeListener(onSharedPreflistener);
    }

    private void updateDashboard() {
        // parse JSON
        try {
            JSONObject jObject = new JSONObject(sharedPref.getString(SharedPrefResources.PREFERENCE_KEY_JSON_USER_INFO, ""));

            // set user dashboard
            String username = jObject.getString("username").substring(0, 1).toUpperCase() + jObject.getString("username").substring(1).toLowerCase();
            tvUsername.setText(username + " (" + jObject.getString("alias") +")");

            tvLevel.setText(getString(R.string.dashboard_level) + " " + jObject.getInt("level"));
            tvExpRequire.setText(jObject.getInt("toNextLevel") + " " + getString(R.string.dashboard_exp_next_level));
            tvCloudPoint.setText(getString(R.string.dashboard_cloud_point) + " " + jObject.getInt("cloudPoint") + " " + getString(R.string.dashboard_cloud_point_unit));
            tvRainAmount.setText(getString(R.string.dashboard_rain_amount) + " " + jObject.getDouble("rainAmount") + " " + getString(R.string.dashboard_rain_amount_unit));

            // set level progress bar
            int prevLevel = jObject.getInt("level") - 1;
            double prevExp =  30 + ((prevLevel-1)*50) + ((prevLevel-1)*(prevLevel-2)*10);
            int levelProgress = (int)((((jObject.getDouble("requiredExp") - prevExp)
                    - jObject.getDouble("toNextLevel")) / (jObject.getDouble("requiredExp") - prevExp)) * 100);
            levelProgressBar.setProgress(levelProgress);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
