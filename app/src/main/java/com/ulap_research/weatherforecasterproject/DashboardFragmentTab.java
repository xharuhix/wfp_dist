package com.ulap_research.weatherforecasterproject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ulap_research.weatherforecasterproject.Resources.SharedPrefResources;
import com.ulap_research.weatherforecasterproject.RestHelper.RestClient;
import com.ulap_research.weatherforecasterproject.RestHelper.RestResources;

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

    private ProgressDialog progressDialog;

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
                if (key.equals(SharedPrefResources.PREFERENCE_KEY_UPLOAD_START)) {
                    setMenuListView();
                    Log.d(TAG, "SharedPref PREFERENCE_KEY_UPLOAD_START has changed");
                }
            }
        };

        // Set up progress dialog
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.upload_calculating_point_message)); // message
        progressDialog.setCancelable(false);

        // setup views
        tvUsername = (TextView) getView().findViewById(R.id.username);
        tvLevel = (TextView) getView().findViewById(R.id.level);
        tvExpRequire = (TextView) getView().findViewById(R.id.expRequire);
        tvCloudPoint = (TextView) getView().findViewById(R.id.cloudPoint);
        tvRainAmount = (TextView) getView().findViewById(R.id.rainAmount);
        levelProgressBar = (ProgressBar) getView().findViewById(R.id.levelProgressBar);

        // setup menu
        setMenuListView();
    }

    public void setMenuListView() {
        // load strings menu
        final String[] str = {"",
                getString(R.string.nav_menu_garden),
                getString(R.string.nav_menu_buy_rain),
                getString(R.string.nav_menu_achievements)};
        if(!sharedPref.getBoolean(SharedPrefResources.PREFERENCE_KEY_UPLOAD_START, false)) {
            str[0] = getString(R.string.nav_menu_start_upload_data);
        }
        else {
            str[0] = getString(R.string.nav_menu_stop_upload_data);
        }

        // set list view navigation menu
        menuListView = (ListView)getView().findViewById(R.id.list_menu);
        menuListView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, str));
        menuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0
                    , View arg1, int arg2, long arg3) {
                Intent intent;
                switch(arg2) {
                    case 0 :
                        // check network connection before start
                        if(isNetworkAvailable()) {
                            final Intent service = new Intent(getActivity(), UploadDataService.class);
                            // start uploading
                            if (!sharedPref.getBoolean(SharedPrefResources.PREFERENCE_KEY_UPLOAD_START, false)) {
                                // set stop text at menu
                                new AlertDialog.Builder(getActivity())
                                    .setTitle(R.string.upload_confirm_title)
                                    .setMessage(R.string.upload_confirm_detail)
                                    .setPositiveButton(R.string.upload_confirm_start, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                        str[0] = getString(R.string.nav_menu_stop_upload_data);
                                        ((BaseAdapter) menuListView.getAdapter()).notifyDataSetChanged();

                                        sharedPref.edit().putBoolean(SharedPrefResources.PREFERENCE_KEY_UPLOAD_START, true).commit();
                                        getActivity().startService(service);
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, null)
                                    .show();
                            }
                            // stop uploading
                            else {
                                // set start text at menu
                                str[0] = getString(R.string.nav_menu_start_upload_data);
                                ((BaseAdapter) menuListView.getAdapter()).notifyDataSetChanged();

                                sharedPref.edit().putBoolean(SharedPrefResources.PREFERENCE_KEY_UPLOAD_START, false).commit();
                                getActivity().stopService(service);

                                showProgress(true);

                                // calculate point
                                calculateCloudPointTask task = new calculateCloudPointTask();
                                task.execute((Void) null);
                            }
                        }
                        else {
                            Toast.makeText(getActivity(), R.string.error_network_connection, Toast.LENGTH_LONG).show();
                        }
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public void showProgress(final boolean show) {
        if(show) {
            progressDialog.show();
        }
        else if(progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
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

    /**
     * An asynchronous to calculate cloud point after user uploaded sensor data task
     */
    public class calculateCloudPointTask extends AsyncTask<Void, Void, Boolean> {
        private JSONObject jObject;

        @Override
        protected Boolean doInBackground(Void... params) {
            String apiKey = sharedPref.getString(SharedPrefResources.PREFERENCE_KEY_APIKEY, "");

            RestClient client = new RestClient(RestResources.UPDATE_CLOUD_POINT);
            client.addHeader("Authorization", apiKey);

            try {
                client.execute(RestClient.RequestMethod.POST);

                jObject = new JSONObject(client.getResponse());
                return jObject.getBoolean("error");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        protected void onPostExecute(Boolean error) {
            int numUploadedData = 0;
            int cloudPointGain = 0;
            try {
                numUploadedData = jObject.getInt("numUploadedData");
                cloudPointGain = jObject.getInt("cloudPointGain");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Toast.makeText(getActivity(), getString(R.string.upload_result) + " " + numUploadedData + " "
                    + getString(R.string.upload_result_unit) + " \n" + getString(R.string.upload_cloud_point_result) + " "
                    + cloudPointGain + " " + getString(R.string.upload_cloud_point_result_unit), Toast.LENGTH_LONG).show();
            RefreshUserInfoTask task = new RefreshUserInfoTask();
            task.execute((Void) null);
        }
    }

    /*
     *  Task for refreshing user info
     */
    public class RefreshUserInfoTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            String apiKey = sharedPref.getString(SharedPrefResources.PREFERENCE_KEY_APIKEY, "");

            /*
             *   Set up REST request
             */
            // Get user info
            RestClient clientGetUserInfo = new RestClient(RestResources.GET_USER_INFO_URL);
            clientGetUserInfo.addHeader("Authorization", apiKey);

            try {
                clientGetUserInfo.execute(RestClient.RequestMethod.GET);
                String userInfo = clientGetUserInfo.getResponse();

                if (userInfo ==  null) {
                    return true;
                }
                else {
                    // put data to shared preferences
                    sharedPref.edit().putString(SharedPrefResources.PREFERENCE_KEY_JSON_USER_INFO, userInfo).commit();
                    // debug
                    Log.d(TAG, userInfo);
                    return false;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        protected void onPostExecute(Boolean error) {
            showProgress(false);
            if(error){
                Toast.makeText(getActivity(), R.string.error_cannot_fetch, Toast.LENGTH_LONG).show();
            }
        }
    }
}
