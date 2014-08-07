package com.ulap_research.weatherforecasterproject;

import android.app.ActionBar;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ulap_research.weatherforecasterproject.Resources.SharedPrefResources;
import com.ulap_research.weatherforecasterproject.RestHelper.RestClient;
import com.ulap_research.weatherforecasterproject.RestHelper.RestResources;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener {
    private static final String TAG = "MainActivity";

    private SharedPreferences sharedPref;

    private ViewPager viewPager;
    private MainPagerAdapter mPagerAdapter;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = this.getSharedPreferences(SharedPrefResources.PREFERENCE_FILE_KEY, MODE_PRIVATE);

        // primary sections of the activity.
        // Create the adapter that will return a fragment for each of the three
        mPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());

        // start to fetch data
        InitializeTask task = new InitializeTask();
        task.execute((Void) null);

        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(mPagerAdapter);

        // Ask for default ActionBar element
        actionBar = getActionBar();

        // Specify that tabs should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setHomeButtonEnabled(false);

        String[] tabs = { getString(R.string.main_tab_dashboard),
                getString(R.string.main_tab_ranking),
                getString(R.string.main_tab_weather)};

        // Add 3 tabs, specifying the tab's text and TabListener
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));
        }

        viewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        // When swiping between pages, select the
                        // corresponding tab.
                        actionBar.setSelectedNavigationItem(position);
                    }
                });
    }

    /*
     * TAB LISTENER
     */
    // Create a tab listener that is called when the user changes tabs.
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // show the given tab
        viewPager.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        // hide the given tab
    }

    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        // probably ignore this event
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
            case R.id.refresh:
                InitializeTask task = new InitializeTask();
                task.execute((Void) null);
                return true;
            case R.id.action_about:
                //dialog?
                Intent about = new Intent(this,About.class);
                startActivity(about);
                return true;
            case R.id.action_report:
                //report by email
                Intent report = new Intent(Intent.ACTION_VIEW);
                report.setData(Uri.parse("http://ulap-research.herokuapp.com/"));
                startActivity(report);
                return true;
            case R.id.action_logout:
                // clear all shared preferences
                sharedPref.edit().clear().commit();
                // set back accept TOS because a user already accepted it
                sharedPref.edit().putBoolean(SharedPrefResources.PREFERENCE_KEY_ACCEPT_TOS, true).commit();

                // reopen login screen
                Intent main_intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(main_intent);
                Toast.makeText(this, R.string.successfully_logged_out, Toast.LENGTH_SHORT).show();
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

       /**
        * A placeholder fragment containing a simple view.
        */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";


        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_activity, container, false);
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class MainPagerAdapter extends FragmentPagerAdapter {

        public MainPagerAdapter(FragmentManager fm) {super(fm); }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position){
                case 0:
                    return new DashboardFragmentTab();
                case 1:
                    return new RankingFragmentTab();
                case 2:
                    return new WeatherFragmentTab();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages
            return 3;
        }
    }

    /**
     * An asynchronous initialization task
     */
    public class InitializeTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            String apiKey = sharedPref.getString(SharedPrefResources.PREFERENCE_KEY_APIKEY, "");

            /*
             *   Set up REST request
             */

            // Get user info
            RestClient clientGetUserInfo = new RestClient(RestResources.GET_USER_INFO_URL);
            clientGetUserInfo.addHeader("Authorization", apiKey);

            // Get user rank
            RestClient clientGetUserRank = new RestClient(RestResources.GET_USER_RANK);
            clientGetUserRank.addHeader("Authorization", apiKey);

            // Get global rank
            RestClient clientGetUserGlobalRank = new RestClient(RestResources.GET_USER_GLOBAL_RANK);
            clientGetUserGlobalRank.addHeader("Authorization", apiKey);

            // Get lasted sensors list
            RestClient clientGetSensorsList = new RestClient(RestResources.GET_SENSORS_LIST);
            clientGetSensorsList.addHeader("Authorization", apiKey);

            try {
                clientGetUserInfo.execute(RestClient.RequestMethod.GET);
                clientGetUserRank.execute(RestClient.RequestMethod.GET);
                clientGetUserGlobalRank.execute(RestClient.RequestMethod.GET);
                clientGetSensorsList.execute(RestClient.RequestMethod.GET);

                String userInfo = clientGetUserInfo.getResponse();
                String userRank = clientGetUserRank.getResponse();
                String userGlobalRank = clientGetUserGlobalRank.getResponse();
                String sensorsList = clientGetSensorsList.getResponse();

                if (userInfo ==  null || userRank == null || userGlobalRank == null ||
                        sensorsList == null) {
                    return true;
                }
                else {
                    sharedPref.edit().putString(SharedPrefResources.PREFERENCE_KEY_JSON_USER_INFO, userInfo).commit();
                    sharedPref.edit().putString(SharedPrefResources.PREFERENCE_KEY_JSON_USER_RANK, userRank).commit();
                    sharedPref.edit().putString(SharedPrefResources.PREFERENCE_KEY_JSON_USER_GLOBAL_RANK, userGlobalRank).commit();
                    sharedPref.edit().putString(SharedPrefResources.PREFERENCE_KEY_JSON_SENSORS_LIST, sensorsList).commit();

                    Log.d(TAG, userInfo);
                    Log.d(TAG, userRank);
                    Log.d(TAG, userGlobalRank);
                    Log.d(TAG, sensorsList);

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
