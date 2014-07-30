package com.ulap_research.weatherforecasterproject;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener {
    private String[] tabs = { "Dashboard", "Ranking", "Weather" };
    private ViewPager viewPager;
    private MainPagerAdapter mPagerAdapter;
    private ActionBar actionBar;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
//    MainPagerAdapter mainPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
//    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity);
//        if (savedInstanceState == null) {
//            getFragmentManager().beginTransaction()
//                    .add(R.id.container, new PlaceholderFragment())
//                    .commit();
//
//        }

        // primary sections of the activity.
        // Create the adapter that will return a fragment for each of the three
        mPagerAdapter = new MainPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(mPagerAdapter);

        // Ask for default ActionBar element
        actionBar = getActionBar();

        // Specify that tabs should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setHomeButtonEnabled(false);

        // Add 3 tabs, specifying the tab's text and TabListener
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
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
            case R.id.action_settings:
                //setting activity
                Intent settings = new Intent(this, Settings.class);
                startActivity(settings);
                return true;
            case R.id.action_logout:
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

}
