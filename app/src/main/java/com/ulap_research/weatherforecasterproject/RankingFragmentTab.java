package com.ulap_research.weatherforecasterproject;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ulap_research.weatherforecasterproject.CustomAdapter.CustomRankingAdapter;
import com.ulap_research.weatherforecasterproject.Resources.SharedPrefResources;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by LouieZamora on 7/18/14.
 */
public class RankingFragmentTab extends Fragment {
    private static final String TAG = "RankingFragmentTab";

    private SharedPreferences sharedPref;

    // views
    private TextView tvUserRank;
    private ListView lvGlobalRank;

    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreflistener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ranking, container, false);
        Log.d(TAG, "createView");
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // setup shared preferences
        sharedPref = this.getActivity().getSharedPreferences(SharedPrefResources.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);

        // set OnSharedPreferenceChangeListener
        onSharedPreflistener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if (key.equals(SharedPrefResources.PREFERENCE_KEY_JSON_USER_RANK)) {
                    updateUserRank();
                    Log.d(TAG, "SharedPref has changed");
                }
                if (key.equals(SharedPrefResources.PREFERENCE_KEY_JSON_USER_GLOBAL_RANK)) {
                    updateListGlobalRank();
                    Log.d(TAG, "SharedPref has changed");
                }
            }
        };

        // set up views
        tvUserRank = (TextView) getView().findViewById(R.id.userRank);
        lvGlobalRank = (ListView) getView().findViewById(R.id.list_global_ranking);

    }

    private void updateUserRank() {
        try {
            // get ranking from JSON and update text view
            JSONObject jObject = new JSONObject(sharedPref.getString(SharedPrefResources.PREFERENCE_KEY_JSON_USER_RANK, ""));
            tvUserRank.setText(getString(R.string.user_rank_text) + " #" + jObject.getInt("userRank"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateListGlobalRank() {

        ArrayList<Integer> rankNum = new ArrayList<Integer>();
        ArrayList<String> username = new ArrayList<String>();
        ArrayList<Boolean> isUserRank = new ArrayList<Boolean>();

        try {
            // get current user rank
            JSONObject jObjectUserRank = new JSONObject(sharedPref.getString(SharedPrefResources.PREFERENCE_KEY_JSON_USER_RANK, ""));
            int userRank = jObjectUserRank.getInt("userRank");

            // get global rank
            JSONObject jObjectGlobalRank = new JSONObject(sharedPref.getString(SharedPrefResources.PREFERENCE_KEY_JSON_USER_GLOBAL_RANK, ""));
            JSONArray jArray = jObjectGlobalRank.getJSONArray("globalRank");
            for (int i=0; i < jArray.length(); i++)
            {
                JSONObject rankObject = jArray.getJSONObject(i);
                // Pulling items from the array
                username.add(rankObject.getString("username"));
                rankNum.add(rankObject.getInt("rank"));
                isUserRank.add(userRank == rankObject.getInt("rank"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CustomRankingAdapter adapter = new CustomRankingAdapter(getActivity(), username, rankNum, isUserRank);

        ListView listView = (ListView) getActivity().findViewById(R.id.list_global_ranking);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1
                    , int arg2, long arg3) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // set onSharedPref changed listener
        sharedPref.registerOnSharedPreferenceChangeListener(onSharedPreflistener);

        // update user rank using JSON values from shared preferences
        updateUserRank();
        updateListGlobalRank();
    }

    @Override
    public void onPause() {
        super.onPause();

        // unset onSharedPref changed listener
        sharedPref.unregisterOnSharedPreferenceChangeListener(onSharedPreflistener);
    }
}
