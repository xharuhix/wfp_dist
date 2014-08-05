package com.ulap_research.weatherforecasterproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ulap_research.weatherforecasterproject.CustomAdapter.CustomCropAdapter;
import com.ulap_research.weatherforecasterproject.R;
import com.ulap_research.weatherforecasterproject.Resources.SharedPrefResources;
import com.ulap_research.weatherforecasterproject.RestHelper.RestClient;
import com.ulap_research.weatherforecasterproject.RestHelper.RestResources;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CropShopActivity extends Activity {
    private static final String TAG = "CropShopActivity";

    private SharedPreferences sharedPref;

    // progress dialog
    private ProgressDialog progressDialog;

    // views
    private ListView lvCropList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_shop);

        // Set up progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.crop_plant_loading)); // message
        progressDialog.setCancelable(false);

        // setup shared preferences
        sharedPref = this.getSharedPreferences(SharedPrefResources.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);

        // start to fetch data
        GetCropListTask task = new GetCropListTask();
        task.execute((Void) null);

        // setup view
        lvCropList = (ListView) findViewById(R.id.list_crops);

        updateCropList();
    }

    private void updateCropList() {
        final ArrayList<String> cropName = new ArrayList<String>();
        ArrayList<String> cropType = new ArrayList<String>();
        ArrayList<String> cropDesc = new ArrayList<String>();
        ArrayList<Integer> cropRarity = new ArrayList<Integer>();
        ArrayList<Integer> cropPrice = new ArrayList<Integer>();
        int[] cropImageId = {R.drawable.kayaitch, R.drawable.stratocus, R.drawable.pyroms,
                R.drawable.lenticules, R.drawable.rollarcs, R.drawable.virgarils, R.drawable.shafalds,
                R.drawable.malmalus, R.drawable.noctils, R.drawable.nacres};

        try {
            JSONObject jCropObject = new JSONObject(sharedPref.getString(SharedPrefResources.PREFERENCE_KEY_JSON_CROPS_LIST, ""));
            JSONArray jCrops = jCropObject.getJSONArray("crops");
            for (int i = 0; i < jCrops.length(); i++) {
                JSONObject jCrop = jCrops.getJSONObject(i);
                cropName.add(jCrop.getString("name"));
                cropType.add(jCrop.getString("type"));
                cropDesc.add(jCrop.getString("description"));
                cropRarity.add(jCrop.getInt("rarityRate"));
                cropPrice.add(jCrop.getInt("price"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CustomCropAdapter adapter = new CustomCropAdapter(this, cropName, cropType, cropDesc,
                cropRarity, cropPrice, cropImageId);

        lvCropList.setAdapter(adapter);
        lvCropList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long id) {

                if (isNetworkAvailable()) {
                    new AlertDialog.Builder(CropShopActivity.this)
                            .setTitle(cropName.get(position))
                            .setMessage(R.string.crop_plant_confirm)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    showProgress(true);
                                    plantCropTask task = new plantCropTask(position+1);
                                    task.execute((Void) null);
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .show();
                }
                else {
                    Toast.makeText(CropShopActivity.this, R.string.error_network_connection, Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public void showProgress(final boolean show) {
        if(show) {
            progressDialog.show();
        }
        else if(progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    /**
     * An asynchronous to plant crop list task
     */
    public class plantCropTask extends AsyncTask<Void, Void, Boolean> {
        private int cropId;

        public plantCropTask(int cropId) {
            this.cropId = cropId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String apiKey = sharedPref.getString(SharedPrefResources.PREFERENCE_KEY_APIKEY, "");

            // Send request to buy rain
            RestClient client = new RestClient(RestResources.PLANT_CROP);
            client.addHeader("Authorization", apiKey);
            client.addParam("cropId", cropId+"");

            try {
                client.execute(RestClient.RequestMethod.POST);

                JSONObject jObject = new JSONObject(client.getResponse());
                return jObject.getBoolean("error");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        protected void onPostExecute(Boolean error) {
            if(!error) {
                Toast.makeText(CropShopActivity.this, R.string.crop_planted_success, Toast.LENGTH_SHORT).show();
                // refresh user info & crops
                RefreshUserInfoTask refreshTak = new RefreshUserInfoTask();
                refreshTak.execute((Void) null);
            }
            else {
                showProgress(false);
                Toast.makeText(CropShopActivity.this, R.string.crop_planted_fail, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * An asynchronous get crop list task
     */
    public class GetCropListTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            String apiKey = sharedPref.getString(SharedPrefResources.PREFERENCE_KEY_APIKEY, "");

            /*
             *   Set up REST request
             */
            // Get all crops list
            RestClient clientGetCropsList = new RestClient(RestResources.GET_CROPS_LIST);
            clientGetCropsList.addHeader("Authorization", apiKey);

            try {
                clientGetCropsList.execute(RestClient.RequestMethod.GET);

                String cropsList = clientGetCropsList.getResponse();
                if (cropsList == null) {
                    return true;
                }
                else {
                    // put data to shared preferences
                    sharedPref.edit().putString(SharedPrefResources.PREFERENCE_KEY_JSON_CROPS_LIST, cropsList).commit();

                    // debug
                    Log.d(TAG, cropsList);

                    return false;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        protected void onPostExecute(Boolean error) {
            if(error){
                Toast.makeText(getApplicationContext(), R.string.error_cannot_fetch, Toast.LENGTH_LONG).show();
            }
        }
    }

    /*
     *  Task for refreshing user info & crops, if the user successfully planted a crop
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

            // Get user's crops
            RestClient clientGetUserCrops = new RestClient(RestResources.GET_USER_CROPS);
            clientGetUserCrops.addHeader("Authorization", apiKey);

            try {
                clientGetUserInfo.execute(RestClient.RequestMethod.GET);
                clientGetUserCrops.execute(RestClient.RequestMethod.GET);

                String userInfo = clientGetUserInfo.getResponse();
                String userCrops = clientGetUserCrops.getResponse();

                if (userInfo ==  null || userCrops == null) {
                    return true;
                }
                else {
                    // put data to shared preferences
                    sharedPref.edit().putString(SharedPrefResources.PREFERENCE_KEY_JSON_USER_INFO, userInfo).commit();
                    sharedPref.edit().putString(SharedPrefResources.PREFERENCE_KEY_JSON_USER_CROPS, userCrops).commit();
                    // debug
                    Log.d(TAG, userInfo);
                    Log.d(TAG, userCrops);

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
                Toast.makeText(CropShopActivity.this, R.string.error_cannot_fetch, Toast.LENGTH_LONG).show();
            }
        }
    }


}
