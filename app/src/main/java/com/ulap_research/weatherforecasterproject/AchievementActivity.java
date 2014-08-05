package com.ulap_research.weatherforecasterproject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ulap_research.weatherforecasterproject.CustomAdapter.CustomAchievementAdapter;
import com.ulap_research.weatherforecasterproject.CustomAdapter.CustomRankingAdapter;
import com.ulap_research.weatherforecasterproject.R;
import com.ulap_research.weatherforecasterproject.Resources.SharedPrefResources;
import com.ulap_research.weatherforecasterproject.RestHelper.RestClient;
import com.ulap_research.weatherforecasterproject.RestHelper.RestResources;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AchievementActivity extends Activity {
    private static final String TAG = "AchievementActivity";

    private SharedPreferences sharedPref;
    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreflistener;

    // views
    private ListView lvAchievement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement);

        // setup shared preferences
        sharedPref = this.getSharedPreferences(SharedPrefResources.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);

        // set OnSharedPreferenceChangeListener listener
        onSharedPreflistener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if (key.equals(SharedPrefResources.PREFERENCE_KEY_JSON_ACHIEVEMENT_LIST) ||
                        key.equals(SharedPrefResources.PREFERENCE_KEY_JSON_USER_ACHIEVEMENTS)) {
                    updateAchievementList();
                    Log.d(TAG, "SharedPref ACHIEVEMENTS has changed");
                }
            }
        };

        // start to fetch data
        GetAchievementTask task = new GetAchievementTask();
        task.execute((Void) null);

        // set up views
        lvAchievement = (ListView) findViewById(R.id.list_achievement);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // set onSharedPref changed listener
        sharedPref.registerOnSharedPreferenceChangeListener(onSharedPreflistener);

        updateAchievementList();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // unset onSharedPref changed listener
        sharedPref.unregisterOnSharedPreferenceChangeListener(onSharedPreflistener);
    }

    private void updateAchievementList() {
        ArrayList<String> achievementName = new ArrayList<String>();
        ArrayList<String> achievementDesc = new ArrayList<String>();
        ArrayList<Integer> achievementExp = new ArrayList<Integer>();
        ArrayList<Boolean> isUserAchieved = new ArrayList<Boolean>();

        try {
            JSONObject jAchievementList = new JSONObject(sharedPref.getString(SharedPrefResources.PREFERENCE_KEY_JSON_ACHIEVEMENT_LIST, ""));
            JSONObject jUserAchievements = new JSONObject(sharedPref.getString(SharedPrefResources.PREFERENCE_KEY_JSON_USER_ACHIEVEMENTS, ""));

            JSONArray jAchievement = jAchievementList.getJSONArray("achievements");
            JSONArray jUserAchievement = jUserAchievements.getJSONArray("achievements");
            for(int i = 0 ; i < jAchievement.length() ; i++) {
                JSONObject achievementObject = jAchievement.getJSONObject(i);
                achievementName.add(achievementObject.getString("name"));
                achievementDesc.add(achievementObject.getString("description"));
                achievementExp.add(achievementObject.getInt("gainExperience"));

                // check whether user already complete this achievement or not
                boolean achieved = false;
                for(int j = 0 ; j < jUserAchievement.length() ; j++) {
                    JSONObject userAchievementObject = jUserAchievement.getJSONObject(j);
                    if (achievementObject.getInt("achievementId") == userAchievementObject.getInt("achievementId")) {
                        achieved = true;
                        break;
                    }
                }
                isUserAchieved.add(achieved);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CustomAchievementAdapter adapter = new CustomAchievementAdapter(this, achievementName, achievementDesc,
                achievementExp, isUserAchieved);

        lvAchievement.setAdapter(adapter);
        lvAchievement.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1
                    , int arg2, long arg3) {

            }
        });
    }

    /**
     * An asynchronous get achievement data task
     */
    public class GetAchievementTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            String apiKey = sharedPref.getString(SharedPrefResources.PREFERENCE_KEY_APIKEY, "");

            /*
             *   Set up REST request
             */
            // Get all achievement list
            RestClient clientGetAchievementsList = new RestClient(RestResources.GET_ACHIEVEMENTS_LIST);
            clientGetAchievementsList.addHeader("Authorization", apiKey);

            // Get user's completed achievements
            RestClient clientGetUserAchievements = new RestClient(RestResources.GET_USERS_ACHIEVEMENTS);
            clientGetUserAchievements.addHeader("Authorization", apiKey);

            try {
                clientGetAchievementsList.execute(RestClient.RequestMethod.GET);
                clientGetUserAchievements.execute(RestClient.RequestMethod.GET);


                String achievementsList = clientGetAchievementsList.getResponse();
                String userAchievements = clientGetUserAchievements.getResponse();

                if (achievementsList == null || userAchievements == null) {
                    return true;
                }
                else {
                    // put data to shared preferences
                    sharedPref.edit().putString(SharedPrefResources.PREFERENCE_KEY_JSON_ACHIEVEMENT_LIST, achievementsList).commit();
                    sharedPref.edit().putString(SharedPrefResources.PREFERENCE_KEY_JSON_USER_ACHIEVEMENTS, userAchievements).commit();

                    // debug
                    Log.d(TAG, achievementsList);
                    Log.d(TAG, userAchievements);

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
