package com.ulap_research.weatherforecasterproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ulap_research.weatherforecasterproject.CustomAdapter.CustomCropMenuAdapter;
import com.ulap_research.weatherforecasterproject.CustomAdapter.CustomUserCropAdapter;
import com.ulap_research.weatherforecasterproject.Resources.SharedPrefResources;
import com.ulap_research.weatherforecasterproject.RestHelper.RestClient;
import com.ulap_research.weatherforecasterproject.RestHelper.RestResources;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyGardenActivity extends Activity {
    private static final String TAG = "MyGardenActivity";

    private SharedPreferences sharedPref;
    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreflistener;

    // views
    private ListView lvCrop;

    // progress dialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_garden);

        // Set up progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        // setup shared preferences
        sharedPref = this.getSharedPreferences(SharedPrefResources.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);

        // set OnSharedPreferenceChangeListener listener
        onSharedPreflistener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if (key.equals(SharedPrefResources.PREFERENCE_KEY_JSON_USER_CROPS)) {
                    updateUserCrop();
                    Log.d(TAG, "SharedPref PREFERENCE_KEY_JSON_USER_CROPS has changed");
                }
            }
        };

        // start to fetch data
        GetUserCropsTask task = new GetUserCropsTask();
        task.execute((Void) null);

        // set up views
        lvCrop = (ListView) findViewById(R.id.list_user_crops);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // set onSharedPref changed listener
        sharedPref.registerOnSharedPreferenceChangeListener(onSharedPreflistener);

        updateUserCrop();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // unset onSharedPref changed listener
        sharedPref.unregisterOnSharedPreferenceChangeListener(onSharedPreflistener);
    }

    private void updateUserCrop() {

        ArrayList<Integer> cropId = new ArrayList<Integer>();
        final ArrayList<Integer> cropOwnedId = new ArrayList<Integer>();
        final ArrayList<String> cropName = new ArrayList<String>();
        ArrayList<String> cropType = new ArrayList<String>();
        ArrayList<String> cropLevel = new ArrayList<String>();
        ArrayList<String> cropLevelRequire = new ArrayList<String>();
        ArrayList<Integer> cropRequireLevelUp = new ArrayList<Integer>();

        int[] cropImageId = {R.drawable.kayaitch, R.drawable.stratocus, R.drawable.pyroms,
            R.drawable.lenticules, R.drawable.rollarcs, R.drawable.virgarils, R.drawable.shafalds,
            R.drawable.malmalus, R.drawable.noctils, R.drawable.nacres};

        try {
            JSONObject jUserCropObject = new JSONObject(sharedPref.getString(SharedPrefResources.PREFERENCE_KEY_JSON_USER_CROPS, ""));

            // read object crops
            JSONArray jUserCrops = jUserCropObject.getJSONArray("crops");
            if(jUserCrops.length() > 0) {
                for (int i = 0; i < jUserCrops.length(); i++) {
                    // read each value in object
                    JSONObject jUserCrop = jUserCrops.getJSONObject(i);
                    cropId.add(jUserCrop.getInt("cropId"));
                    cropOwnedId.add(jUserCrop.getInt("cropOwnedId"));
                    cropName.add(jUserCrop.getString("name"));
                    cropType.add(jUserCrop.getString("type"));
                    cropLevel.add(jUserCrop.getString("level"));
                    cropLevelRequire.add(jUserCrop.getString("toNextLevel"));
                    cropRequireLevelUp.add(jUserCrop.getInt("rainRequireLvUp"));
                }
                CustomUserCropAdapter adapter = new CustomUserCropAdapter(this, cropId, cropName, cropType,
                        cropLevel, cropLevelRequire, cropRequireLevelUp, cropImageId);

                lvCrop.setAdapter(adapter);

                // set on click for each crop
                lvCrop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> arg0, View arg1
                            , final int positionCrop, long id) {
                        // check network connection
                        if (isNetworkAvailable()) {
                            // set dialog
                            final Dialog cropMenuDialog = new Dialog(MyGardenActivity.this);
                            cropMenuDialog.requestWindowFeature(cropMenuDialog.getWindow().FEATURE_NO_TITLE);
                            cropMenuDialog.setContentView(R.layout.custom_dialog_crop);
                            cropMenuDialog.setCancelable(true);

                            // menu text
                            String[] menuText = {getString(R.string.crop_menu_water), getString(R.string.crop_menu_sell)};

                            // icon image
                            int[] iconImage = {R.drawable.rain, R.drawable.cloudpoint};

                            // custom adapter
                            CustomCropMenuAdapter cropMenuAdapter = new CustomCropMenuAdapter(MyGardenActivity.this, iconImage, menuText);

                            // set list view menu
                            ListView lvCropMenu = (ListView) cropMenuDialog.findViewById(R.id.list_crop_menu);
                            lvCropMenu.setAdapter(cropMenuAdapter);
                            lvCropMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                    cropMenuDialog.cancel();
                                    switch (arg2) {
                                        case 0:
                                        // user choose to water crop
                                            // set dialog
                                            final Dialog rainListDialog = new Dialog(MyGardenActivity.this);
                                            rainListDialog.requestWindowFeature(cropMenuDialog.getWindow().FEATURE_NO_TITLE);
                                            rainListDialog.setContentView(R.layout.custom_dialog_rain);
                                            rainListDialog.setCancelable(true);

                                            // set amount of water for a user to select
                                            final String[] rain = {0.5 + " " +getString(R.string.crop_water_unit),
                                                    1 + " " + getString(R.string.crop_water_unit),
                                                    2 + " " + getString(R.string.crop_water_unit),
                                                    3 + " " + getString(R.string.crop_water_unit),
                                                    4 + " " + getString(R.string.crop_water_unit),
                                                    5 + " " + getString(R.string.crop_water_unit),
                                                    10 + " " + getString(R.string.crop_water_unit)};

                                            ListView lvRainList = (ListView) rainListDialog.findViewById(R.id.list_rain_to_use);
                                            lvRainList.setAdapter(new ArrayAdapter<String>(MyGardenActivity.this, android.R.layout.simple_list_item_1, rain));
                                            lvRainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, final int positionRain, long id) {
                                                    rainListDialog.cancel();
                                                    new AlertDialog.Builder(MyGardenActivity.this)
                                                            .setTitle(R.string.crop_water_title)
                                                            .setMessage(getString(R.string.crop_water_confirm) + " "
                                                                    + rain[positionRain] + " ?")
                                                            .setPositiveButton(R.string.crop_water_button, new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                                    // set message
                                                                    progressDialog.setMessage(getString(R.string.crop_water_loading));
                                                                    showProgress(true);
                                                                    waterCropTask task = new waterCropTask(cropOwnedId.get(positionCrop)
                                                                            , Double.parseDouble(rain[positionRain].split(" ")[0]));
                                                                    task.execute((Void) null);
                                                                }
                                                            })
                                                            .setNegativeButton(android.R.string.no, null)
                                                            .show();
                                                }
                                            });
                                            rainListDialog.show();
                                            break;
                                        case 1:
                                        // user choose to sell crop
                                            // set message
                                            progressDialog.setMessage(getString(R.string.crop_get_price_loading));
                                            showProgress(true);
                                            // get sell price and let user desire to sell or not
                                            getCropSellPriceTask task = new getCropSellPriceTask(cropOwnedId.get(positionCrop));
                                            task.execute((Void) null);
                                            break;
                                    }
                                }
                            });
                            cropMenuDialog.show();
                        }
                        else {
                            Toast.makeText(MyGardenActivity.this, R.string.error_network_connection, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
            else {
                // show text if there is no crop in the garden
                String[] str = { getString(R.string.garden_no_crop) };
                lvCrop.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, str));
                lvCrop.setSelector(new ColorDrawable(0));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * An asynchronous to get crop sell price task
     */
    public class getCropSellPriceTask extends AsyncTask<Void, Void, Boolean> {
        private int cropOwnedId;
        // crop sell price
        private int cropSellPrice;

        public getCropSellPriceTask(int cropOwnedId) {
            this.cropOwnedId = cropOwnedId;
            this.cropSellPrice = 0;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String apiKey = sharedPref.getString(SharedPrefResources.PREFERENCE_KEY_APIKEY, "");

            // Send request to get crop sell price
            RestClient client = new RestClient(RestResources.GET_CROP_SELL_PRICE + cropOwnedId);
            client.addHeader("Authorization", apiKey);

            try {
                client.execute(RestClient.RequestMethod.GET);

                JSONObject jObject = new JSONObject(client.getResponse());
                cropSellPrice = jObject.getInt("sell_price");
                return jObject.getBoolean("error");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        protected void onPostExecute(Boolean error) {
            // after request was done
            showProgress(false);
            if(!error) {
                // show dialog to ask a user for selling confirmation
                new AlertDialog.Builder(MyGardenActivity.this)
                        .setTitle(R.string.crop_sell_title)
                        .setMessage(getString(R.string.crop_sell_confirm) + " " + cropSellPrice + " "
                                + getString(R.string.crop_shop_cloud_points))
                        .setPositiveButton(R.string.crop_sell_button, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                progressDialog.setMessage(getString(R.string.crop_sell_loading));
                                showProgress(true);
                                sellCropTask task = new sellCropTask(cropOwnedId);
                                task.execute((Void) null);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
            else {
                Toast.makeText(MyGardenActivity.this, R.string.error_something_went_wrong, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * An asynchronous to water crop task
     */
    public class waterCropTask extends AsyncTask<Void, Void, Integer> {
        private int cropOwnedId;
        private double rainToUse;

        private static final int SUCCESS = 0;
        private int FAIL_MAX_LEVEL = 1;
        private int FAIL_NOT_ENOUGH = 2;
        private int ERROR = 3;

        public waterCropTask(int cropOwnedId, double rainToUse) {
            this.cropOwnedId = cropOwnedId;
            this.rainToUse = rainToUse;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            String apiKey = sharedPref.getString(SharedPrefResources.PREFERENCE_KEY_APIKEY, "");

            // Send request to water crop
            RestClient client = new RestClient(RestResources.WATER_CROP);
            client.addHeader("Authorization", apiKey);
            client.addParam("cropOwnedId", cropOwnedId+"");
            client.addParam("rainToUse", rainToUse + "");

            try {
                client.execute(RestClient.RequestMethod.POST);

                JSONObject jObject = new JSONObject(client.getResponse());
                if(!jObject.getBoolean("error")) {
                    return SUCCESS;
                }
                else {
                    if(jObject.getString("message").contains("MAX")) {
                        return FAIL_MAX_LEVEL;
                    }
                    else {
                        return FAIL_NOT_ENOUGH;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ERROR;
        }

        protected void onPostExecute(Integer errorCode) {
            if(errorCode == SUCCESS) {
                Toast.makeText(MyGardenActivity.this, R.string.crop_watered_success, Toast.LENGTH_SHORT).show();
                // refresh user info & crops
                RefreshUserInfoTask refreshTask = new RefreshUserInfoTask();
                refreshTask.execute((Void) null);
            }
            else if(errorCode == FAIL_MAX_LEVEL){
                showProgress(false);
                Toast.makeText(MyGardenActivity.this, R.string.crop_watered_max_level, Toast.LENGTH_SHORT).show();
            }
            else if(errorCode == FAIL_NOT_ENOUGH){
                showProgress(false);
                Toast.makeText(MyGardenActivity.this, R.string.crop_watered_fail, Toast.LENGTH_SHORT).show();
            }
            else if(errorCode == ERROR){
                showProgress(false);
                Toast.makeText(MyGardenActivity.this, R.string.error_something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * An asynchronous to sell crop task
     */
    public class sellCropTask extends AsyncTask<Void, Void, Boolean> {
        private int cropOwnedId;

        public sellCropTask(int cropOwnedId) {
            this.cropOwnedId = cropOwnedId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String apiKey = sharedPref.getString(SharedPrefResources.PREFERENCE_KEY_APIKEY, "");

            // Send request to sell crop
            RestClient client = new RestClient(RestResources.SELL_CROP);
            client.addHeader("Authorization", apiKey);
            client.addParam("cropOwnedId", cropOwnedId+"");

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
                Toast.makeText(MyGardenActivity.this, R.string.crop_sold_success, Toast.LENGTH_SHORT).show();
                // refresh user info & crops
                RefreshUserInfoTask refreshTask = new RefreshUserInfoTask();
                refreshTask.execute((Void) null);
            }
            else {
                showProgress(false);
                Toast.makeText(MyGardenActivity.this, R.string.error_something_went_wrong, Toast.LENGTH_SHORT).show();
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

                if (new JSONObject(userInfo).getBoolean("error") ||
                        new JSONObject(userCrops).getBoolean("error")) {
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
                Toast.makeText(MyGardenActivity.this, R.string.error_cannot_fetch, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * An asynchronous get user crops task
     */
    public class GetUserCropsTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            String apiKey = sharedPref.getString(SharedPrefResources.PREFERENCE_KEY_APIKEY, "");

            /*
             *   Set up REST request
             */
            // Get user's crops
            RestClient clientGetUserCrops = new RestClient(RestResources.GET_USER_CROPS);
            clientGetUserCrops.addHeader("Authorization", apiKey);
            try {
                clientGetUserCrops.execute(RestClient.RequestMethod.GET);

                String userCrops = clientGetUserCrops.getResponse();

                if (new JSONObject(userCrops).getBoolean("error")) {
                    return true;
                }
                else {
                    // put data to shared preferences
                    sharedPref.edit().putString(SharedPrefResources.PREFERENCE_KEY_JSON_USER_CROPS, userCrops).commit();

                    // debug
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
            if(error){
                Toast.makeText(getApplicationContext(), R.string.error_cannot_fetch, Toast.LENGTH_LONG).show();
            }
        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_plant:
                Intent intent = new Intent(this, CropShopActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_garden_plant, menu);
        return super.onCreateOptionsMenu(menu);
    }


}
