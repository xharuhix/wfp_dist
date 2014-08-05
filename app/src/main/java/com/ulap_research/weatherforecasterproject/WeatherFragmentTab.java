package com.ulap_research.weatherforecasterproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ulap_research.weatherforecasterproject.Resources.SharedPrefResources;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class WeatherFragmentTab extends Fragment {
    private static final String TAG = "WeatherFragmentTab";

    private SharedPreferences sharedPref;
    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreflistener;

    private SupportMapFragment fragment;
    private GoogleMap map;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        Log.d(TAG, "createView");
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map_container);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_container, fragment).commit();
        }

        // setup shared preferences
        sharedPref = this.getActivity().getSharedPreferences(SharedPrefResources.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);

        // set OnSharedPreferenceChangeListener
        onSharedPreflistener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if (key.equals(SharedPrefResources.PREFERENCE_KEY_JSON_SENSORS_LIST)) {
                    updateMapMarker();
                    Log.d(TAG, "SharedPref PREFERENCE_KEY_JSON_SENSORS_LIST has changed");
                }
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        if (map == null) {
            map = fragment.getMap();
            map.setMyLocationEnabled(true);

            // set custom info windows
            map.setInfoWindowAdapter(new MyInfoWindowAdapter());

            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            if (location != null) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), 12.0f));
            }
            else {
                Toast.makeText(getActivity(), R.string.error_cannot_get_location, Toast.LENGTH_SHORT).show();
            }
        }

        // set onSharedPref changed listener
        sharedPref.registerOnSharedPreferenceChangeListener(onSharedPreflistener);

        updateMapMarker();

    }

    @Override
    public void onPause() {
        super.onPause();

        // unset onSharedPref changed listener
        sharedPref.unregisterOnSharedPreferenceChangeListener(onSharedPreflistener);
    }

    private void updateMapMarker() {
        try {
            // get ranking from JSON and update text view
            JSONObject jObjectSensors = new JSONObject(sharedPref.getString(SharedPrefResources.PREFERENCE_KEY_JSON_SENSORS_LIST, ""));
            JSONArray jArray = jObjectSensors.getJSONArray("sensorData");

            // clear map markers
            map.clear();

            for (int i=0; i < jArray.length(); i++) {
                JSONObject sensorObject = jArray.getJSONObject(i);
                // Pulling sensor data from the array
                double lad = sensorObject.getDouble("ladtitude");
                double lon = sensorObject.getDouble("longtitude");
                String time = sensorObject.getString("timestamp");

                map.addMarker(new MarkerOptions()
                        .position(new LatLng(lad, lon))
                        .title(time)
                        .snippet("Pressure: " + sensorObject.getString("pressure") + "\n" +
                                "Humidity: " + sensorObject.getString("humidity") + "\n" +
                                "Proximity: " + sensorObject.getString("proximity")));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private final View myContentsView;

        public MyInfoWindowAdapter() {
            myContentsView = getActivity().getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.title));
            tvTitle.setText(marker.getTitle());
            TextView tvSnippet = ((TextView)myContentsView.findViewById(R.id.snippet));
            tvSnippet.setText(marker.getSnippet());

            return myContentsView;
        }
    }
}
