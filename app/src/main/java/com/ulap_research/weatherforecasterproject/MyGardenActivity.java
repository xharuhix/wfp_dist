package com.ulap_research.weatherforecasterproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.Toast;

import com.ulap_research.weatherforecasterproject.CustomAdapter.CustomAchievementAdapter;
import com.ulap_research.weatherforecasterproject.CustomAdapter.CustomUserCropAdapter;
import com.ulap_research.weatherforecasterproject.Resources.SharedPrefResources;
import com.ulap_research.weatherforecasterproject.RestHelper.RestClient;
import com.ulap_research.weatherforecasterproject.RestHelper.RestResources;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MyGardenActivity extends Activity {
    private static final String TAG = "MyGardenActivity";

    private SharedPreferences sharedPref;
    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreflistener;

    // views
    private ListView lvCrop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_garden);

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
        ArrayList<String> cropName = new ArrayList<String>();
        ArrayList<String> cropType = new ArrayList<String>();
        ArrayList<String> cropLevel = new ArrayList<String>();
        ArrayList<String> cropLevelRequire = new ArrayList<String>();
        ArrayList<Integer> cropRequireLevelUp = new ArrayList<Integer>();
        int[] cropImageId = {R.drawable.kayaitch, R.drawable.stratocus, R.drawable.pyroms,
            R.drawable.lenticules, R.drawable.rollarcs, R.drawable.virgarils, R.drawable.shafalds,
            R.drawable.malmalus, R.drawable.noctils, R.drawable.nacres};

        try {
            JSONObject jUserCropObject = new JSONObject(sharedPref.getString(SharedPrefResources.PREFERENCE_KEY_JSON_USER_CROPS, ""));

            JSONArray jUserCrops = jUserCropObject.getJSONArray("crops");
            if(jUserCrops.length() > 0) {
                for (int i = 0; i < jUserCrops.length(); i++) {
                    JSONObject jUserCrop = jUserCrops.getJSONObject(i);
                    cropId.add(jUserCrop.getInt("cropId"));
                    cropName.add(jUserCrop.getString("name"));
                    cropType.add(jUserCrop.getString("type"));
                    cropLevel.add(jUserCrop.getString("level"));
                    cropLevelRequire.add(jUserCrop.getString("toNextLevel"));
                    cropRequireLevelUp.add(jUserCrop.getInt("rainRequireLvUp"));
                }
                CustomUserCropAdapter adapter = new CustomUserCropAdapter(this, cropId, cropName, cropType,
                        cropLevel, cropLevelRequire, cropRequireLevelUp, cropImageId);

                lvCrop.setAdapter(adapter);
            }
            else {
                String[] str = { getString(R.string.garden_no_crop) };
                lvCrop.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, str));
                lvCrop.setSelector(new ColorDrawable(0));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        lvCrop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1
                    , int arg2, long arg3) {

            }
        });
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

                if (userCrops == null) {
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

}
