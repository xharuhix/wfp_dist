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
import android.widget.Toast;

import com.ulap_research.weatherforecasterproject.CustomAdapter.CustomRainAdapter;
import com.ulap_research.weatherforecasterproject.R;
import com.ulap_research.weatherforecasterproject.Resources.SharedPrefResources;
import com.ulap_research.weatherforecasterproject.RestHelper.RestClient;
import com.ulap_research.weatherforecasterproject.RestHelper.RestResources;

import org.json.JSONObject;

public class RainShopActivity extends Activity {
    private static final String TAG = "RainShopActivity";

    private SharedPreferences sharedPref;

    private ListView lvRain;

    private ProgressDialog progressDialog;

    // rain amount and price
    private double[] rainAmount = {0.5, 1, 5, 10};
    private int[] rainPrice = {5, 7, 12 ,15};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rain_shop);

        // Set up progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.rain_buy_loading)); // message
        progressDialog.setCancelable(false);

        // setup shared preferences
        sharedPref = this.getSharedPreferences(SharedPrefResources.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);

        // setup
        lvRain = (ListView) findViewById(R.id.list_rain);

        // set list view
        CustomRainAdapter adapter = new CustomRainAdapter(this, rainAmount, rainPrice);

        lvRain.setAdapter(adapter);
        lvRain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long id) {
                // check network
                if (isNetworkAvailable()) {
                    // show dialog to ask user confirmation
                    new AlertDialog.Builder(RainShopActivity.this)
                            .setTitle(rainAmount[position] + " " + getString(R.string.rain_unit_desc))
                            .setMessage(R.string.rain_buy_confirm)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    showProgress(true);
                                    BuyRainTask task = new BuyRainTask(rainAmount[position]);
                                    task.execute((Void) null);
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .show();
                } else {
                    Toast.makeText(RainShopActivity.this, R.string.error_network_connection, Toast.LENGTH_LONG).show();
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

    /*
     *  Task for requesting to buy rain
     */
    public class BuyRainTask extends AsyncTask<Void, Void, Integer> {
        private double mRainAmount;

        private static final int SUCCESS = 0;
        private static final int FAIL = 1;
        private static final int ERROR = 2;

        public BuyRainTask(double rainAmount) {
            mRainAmount = rainAmount;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            String apiKey = sharedPref.getString(SharedPrefResources.PREFERENCE_KEY_APIKEY, "");

            // Send request to buy rain
            RestClient client = new RestClient(RestResources.BUY_RAIN);
            client.addHeader("Authorization", apiKey);
            client.addParam("rainAmount", mRainAmount+"");

            try {
                client.execute(RestClient.RequestMethod.POST);

                JSONObject jObject = new JSONObject(client.getResponse());
                if(!jObject.getBoolean("error")) {
                    return SUCCESS;
                }
                else {
                    return FAIL;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ERROR;
        }

        protected void onPostExecute(Integer errorCode) {
            if(errorCode == SUCCESS) {
                Toast.makeText(RainShopActivity.this, R.string.rain_brought_success, Toast.LENGTH_SHORT).show();
                // refresh user info
                RefreshUserInfoTask refreshTak = new RefreshUserInfoTask();
                refreshTak.execute((Void) null);
            }
            else if(errorCode == FAIL) {
                showProgress(false);
                Toast.makeText(RainShopActivity.this, R.string.rain_brought_fail, Toast.LENGTH_SHORT).show();
            }
            else if(errorCode == ERROR) {
                showProgress(false);
                Toast.makeText(RainShopActivity.this, R.string.error_something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
     *  Task for refreshing user info, if the user successfully brought rain
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
                Toast.makeText(RainShopActivity.this, R.string.error_cannot_fetch, Toast.LENGTH_LONG).show();
            }
        }
    }
}
